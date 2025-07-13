package com.example.AIVue.Service;

import com.example.AIVue.Exception.ResourceNotFoundException;
import com.example.AIVue.Repository.InterviewRepository;
import com.example.AIVue.Repository.QuestionAnswerRepository;
import com.example.AIVue.dto.Interview.DashboardInterviewResponse;
import com.example.AIVue.dto.QuestionAnswer.QARequest;
import com.example.AIVue.dto.QuestionAnswer.QAResponse;
import com.example.AIVue.model.Interview;
import com.example.AIVue.model.QuestionAnswer;
import com.example.AIVue.model.User;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class QuestionAnswerService {

    private final GeminiService geminiService;
    private final InterviewRepository interviewRepository;
    private final QuestionAnswerRepository questionAnswerRepository;

    public QAResponse generateQuestionAnswers(QARequest request) {

        String resumeData = interviewRepository.findResumeDataById(request.getInterviewId());
        String jobDescription = interviewRepository.findJobDescriptionById(request.getInterviewId());

        String prompt = "Give 10 interview questions and answers in the following JSON format only. " +
                "Example: [{\"question\": \"...\", \"answer\": \"...\"}]\n" +
                "Job company: " + request.getCompany() +
                "\nJob title: " + request.getJobTitle() +
                "\nJob description: " + jobDescription +
                "\nResume: " + resumeData;

        // Call Gemini API
        String responseData = geminiService.getGeminiResponse(prompt);
        System.out.println("Raw Gemini JSON:\n" + responseData);

        // Clean Markdown-style wrapping if present
        responseData = responseData
                .replaceFirst("(?i)^```json", "")  // Remove ```json from start
                .replaceFirst("^```", "")          // Remove plain ```
                .replaceFirst("```$", "")          // Remove ending ```
                .trim();

        System.out.println("Cleaned Gemini Response:\n" + responseData);

        // Parse JSON response
        List<Map<String, String>> qaList;
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            qaList = objectMapper.readValue(responseData, new TypeReference<>() {});
        } catch (Exception e) {
            System.out.println("Failed to parse Gemini response:\n" + responseData);
            throw new RuntimeException("Failed to parse Gemini response JSON", e);
        }

        // Convert to Map<String, String>
        Map<String, String> qaMap = new HashMap<>();
        for (Map<String, String> qa : qaList) {
            String question = qa.get("question");
            String answer = qa.get("answer");
            if (question != null && answer != null) {
                qaMap.put(question.trim(), answer.trim());
            }
        }

        Interview interview = interviewRepository.findById(request.getInterviewId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        QuestionAnswer questionAnswer = QuestionAnswer.builder()
                .questionAnswers(qaMap)
                .interview(interview)
                .build();

        QuestionAnswer savedQuestionAnswer = questionAnswerRepository.save(questionAnswer);

        return mapToQAResponse(savedQuestionAnswer);
    }

    private QAResponse mapToQAResponse(QuestionAnswer questionAnswer) {
        return QAResponse.builder()
                .questionAnswers(questionAnswer.getQuestionAnswers())
                .build();
    }

    public QAResponse isQuestionsExists(Interview interview) {
        QuestionAnswer questionAnswer = questionAnswerRepository.findByInterview(interview);
        return mapToQAResponse(questionAnswer);
    }

}
