package com.example.AIVue.Service;

import com.example.AIVue.Repository.InterviewRepository;
import com.example.AIVue.dto.Feedback;
import com.example.AIVue.dto.MockInterview.ChatMessage;
import com.example.AIVue.dto.SessionData;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class MockInterviewService {

    private static final String SESSION_DATA_ATTRIBUTE = "interviewSessionData";
    private static final long SESSION_TIMEOUT_SECONDS = 600; // 10 minutes

    private final InterviewRepository interviewDetailsRepo;
    private final GemService gemService;

    public ResponseEntity<?> startChatting(Long id, HttpSession session) {
        String resumeData = interviewDetailsRepo.findResumeDataById(id);
        String title = interviewDetailsRepo.findJobTitleById(id);
        String description = interviewDetailsRepo.findJobDescriptionById(id);
        String company = interviewDetailsRepo.findCompanyById(id);

        String introMessage = """
            You are an intelligent and professional technical interviewer for an SDE (Software Development Engineer) role. 
            I will provide you with the candidate's resume and the job description.
            
            Resume:
            %s
            
            Job Title:
            %s
            
            Job Description:
            %s
            Company Name:
            %s
            
            
            Your task:
            - Conduct a mock technical interview with the candidate.
            - Start by introducing yourself briefly and asking one technical question at a time.
            - Wait for the candidate's response before asking the next question.
            - Ask a mix of technical, behavioral, and project-related questions relevant to the candidate's resume and the job role.
            - Adapt your questions based on previous answers.
            - Continue the interview for as long as the candidate responds with interest.
            - If the candidate says "stop", "end", "feedback", or "I am done", stop the interview and give a final feedback.
            
            Final Feedback Guidelines:
            - Assess the candidate's performance in the mock interview.
            - Provide a score out of 10 for technical skills, communication, confidence, and problem-solving.
            - Highlight strengths and suggest areas for improvement.
            - Keep the feedback structured, clear, and motivating.
            
            Begin the interview now by greeting the candidate and asking the first question.
            """.formatted(resumeData, title, description,company);

        List<ChatMessage> chatHistory = new ArrayList<>();
        chatHistory.add(new ChatMessage("user", introMessage));

        String response = gemService.chatWithModel(chatHistory);
        chatHistory.add(new ChatMessage("assistant", response));

        SessionData sessionData = new SessionData(chatHistory, Instant.now());
        session.setAttribute(SESSION_DATA_ATTRIBUTE, sessionData);

        return ResponseEntity.ok(response);
    }

    public ResponseEntity<List<ChatMessage>> continueChatting(String message, HttpSession session) {
        SessionData sessionData = (SessionData) session.getAttribute(SESSION_DATA_ATTRIBUTE);

        if (sessionData == null) {
            return ResponseEntity.badRequest().body(List.of(
                    new ChatMessage("system", "No active session. Please start a new interview session.")
            ));
        }

        // Check session timeout
        long elapsedSeconds = Duration.between(sessionData.getStartTime(), Instant.now()).getSeconds();
        if (elapsedSeconds > SESSION_TIMEOUT_SECONDS) {
            session.removeAttribute(SESSION_DATA_ATTRIBUTE);
            return ResponseEntity.ok(List.of(
                    new ChatMessage("system", "Session expired after 10 minutes of inactivity. Please start a new session.")
            ));
        }

        List<ChatMessage> chatHistory = sessionData.getChatHistory();
        chatHistory.add(new ChatMessage("user", message));

        String response = gemService.chatWithModel(chatHistory);
        chatHistory.add(new ChatMessage("assistant", response));

        // Update the session data with the new chat history
        session.setAttribute(SESSION_DATA_ATTRIBUTE, new SessionData(chatHistory, sessionData.getStartTime()));

        return ResponseEntity.ok(chatHistory);
    }

    public ResponseEntity<Feedback> endChatting(HttpSession session) {
        SessionData sessionData = (SessionData) session.getAttribute(SESSION_DATA_ATTRIBUTE);

        if (sessionData == null) {
            return ResponseEntity.badRequest().body(null);
        }

        List<ChatMessage> chatHistory = sessionData.getChatHistory();
        chatHistory.add(new ChatMessage("user", """
        Please provide the final feedback for the mock interview in the following strict JSON format:

        {
          "level": "Beginner | Intermediate | Advanced",
          "technical_score": "0-10",
          "communication_score": "0-10",
          "confidence_score": "0-10",
          "problem_solving_score": "0-10",
          "strengths": ["..."],
          "weak_areas": ["..."],
          "suggestions": ["..."]
        }

        Only return valid JSON. Do not include any explanation before or after the JSON.
        """));

        String feedbackResponse = gemService.chatWithModel(chatHistory);
        session.removeAttribute(SESSION_DATA_ATTRIBUTE);

        try {
            // Extract JSON block using regex
            String json = extractJsonFromResponse(feedbackResponse);

            ObjectMapper mapper = new ObjectMapper();
            Feedback feedback = mapper.readValue(json, Feedback.class);

            return ResponseEntity.ok(feedback);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(null);
        }
    }

    private String extractJsonFromResponse(String response) {
        int start = response.indexOf("{");
        int end = response.lastIndexOf("}");
        if (start != -1 && end != -1 && end > start) {
            return response.substring(start, end + 1);
        }
        throw new IllegalArgumentException("No valid JSON object found in response");
    }

}