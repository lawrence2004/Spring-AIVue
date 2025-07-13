package com.example.AIVue.dto;

import com.example.AIVue.dto.MockInterview.ChatMessage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

public class SessionData {
    private List<ChatMessage> chatHistory;
    private Instant startTime;

    public SessionData(List<ChatMessage> chatHistory, Instant startTime) {
        this.chatHistory = chatHistory;
        this.startTime = startTime;
    }

    public List<ChatMessage> getChatHistory() {
        return chatHistory;
    }

    public Instant getStartTime() {
        return startTime;
    }
}