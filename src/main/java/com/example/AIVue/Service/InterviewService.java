package com.example.AIVue.Service;

import com.example.AIVue.Repository.InterviewRepository;
import com.example.AIVue.dto.Interview.DashboardInterviewResponse;
//import com.example.AIVue.dto.Interview.QADto;
import com.example.AIVue.model.Interview;
import com.example.AIVue.model.Status;
import com.example.AIVue.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InterviewService {

    private final ResumeParserService resumeParserService;
    private final InterviewRepository interviewRepository;

    @Value("${gemini.api.key}")
    private String apiKey;


    public DashboardInterviewResponse getGeminiResponse(MultipartFile file, String title, String description, String company, User user) throws IOException {

        String ResumeData =  resumeParserService.extractTextFromPDF(file);

        Interview interview = Interview.builder()
                .jobTitle(title)
                .company(company)
                .jobDescription(description)
                .resumeData(ResumeData)
                .status(Status.INPROGRESS)
                .student(user)
                .build();

        Interview savedInterview = interviewRepository.save(interview);

        return mapToDashboardInterviewResponse(savedInterview);

    }

    public DashboardInterviewResponse mapToDashboardInterviewResponse(Interview interview) {
        DashboardInterviewResponse response = DashboardInterviewResponse.builder()
                .id(interview.getId())
                .jobTitle(interview.getJobTitle())
                .company(interview.getCompany())
                .status(interview.getStatus())
                .build();
        return response;
    }

    public List<DashboardInterviewResponse> getAllInterviewsById(User user) {
        List<Interview> li = interviewRepository.findByStudent(user);
        return li.stream()
                .map(this::mapToDashboardInterviewResponse)
                .collect(Collectors.toList());
    }
}
