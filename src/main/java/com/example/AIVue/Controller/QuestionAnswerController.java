package com.example.AIVue.Controller;

import com.example.AIVue.Exception.ResourceNotFoundException;
import com.example.AIVue.Repository.InterviewRepository;
import com.example.AIVue.Service.QuestionAnswerService;
import com.example.AIVue.dto.QuestionAnswer.QARequest;
import com.example.AIVue.dto.QuestionAnswer.QAResponse;
import com.example.AIVue.model.Interview;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/questionAnswer")
@AllArgsConstructor
public class QuestionAnswerController {

    private final QuestionAnswerService questionAnswerService;
    private final InterviewRepository interviewRepository;

    @PostMapping
    public QAResponse generateQuestions(@RequestBody QARequest request) {
        return questionAnswerService.generateQuestionAnswers(request);
    }

    @GetMapping
    public QAResponse isQuestionsExist(Long id) {
        Interview interview = interviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Interview not found"));
        return questionAnswerService.isQuestionsExists(interview);
    }

}
