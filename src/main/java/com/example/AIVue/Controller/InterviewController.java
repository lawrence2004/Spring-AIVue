package com.example.AIVue.Controller;

import com.example.AIVue.Exception.ResourceNotFoundException;
import com.example.AIVue.Repository.UserRepository;
import com.example.AIVue.Service.InterviewService;
import com.example.AIVue.dto.Interview.DashboardInterviewResponse;
import com.example.AIVue.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/interview")
@RequiredArgsConstructor
@CrossOrigin("*")
public class InterviewController {

    private final InterviewService interviewService;
    private final UserRepository userRepo;

    @PostMapping
    public DashboardInterviewResponse createInterview(
            @RequestParam("resume") MultipartFile resume,
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("company") String company,
            @AuthenticationPrincipal UserDetails userDetails) throws Exception {

        User user = userRepo.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return interviewService.getGeminiResponse(resume, title, description, company, user);
    }

    @GetMapping
    public ResponseEntity<List<DashboardInterviewResponse>> getAllInterviewsById(
            @AuthenticationPrincipal UserDetails userDetails) {

        User user = userRepo.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return ResponseEntity.ok(interviewService.getAllInterviewsById(user));
    }
}
