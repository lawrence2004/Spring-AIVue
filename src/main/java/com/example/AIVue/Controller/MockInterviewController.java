package com.example.AIVue.Controller;

import com.example.AIVue.Service.MockInterviewService;
import com.example.AIVue.dto.Feedback;
import com.example.AIVue.dto.MockInterview.ChatMessage;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/mockinterview")
@AllArgsConstructor
public class MockInterviewController {

    @Autowired
    private MockInterviewService mockInterviewService;

    // Step 1: Start Interview Session
    @PostMapping("/{id}")
    public ResponseEntity<?> startChat(@PathVariable Long id, HttpSession session) throws IOException {

        return mockInterviewService.startChatting(id,session);

    }

    // Step 2: Continue Interview
    @PostMapping
    public ResponseEntity<List<ChatMessage>> continueChat(@RequestBody String message, HttpSession session) {
        return mockInterviewService.continueChatting(message,session);
    }

    // Step 3: End Session Manually
    @PostMapping("/end")
    public ResponseEntity<Feedback> endChat(HttpSession session) {
        return mockInterviewService.endChatting(session);
    }

    // Optional: Get chat history
//    @GetMapping("/history")
//    public ResponseEntity<List<ChatMessage>> getHistory(HttpSession session) {
//        return mockInterviewService.gettingHistory(session);
//    }
}