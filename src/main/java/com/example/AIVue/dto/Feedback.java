package com.example.AIVue.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Feedback {
    private String level;
    private int technical_score;
    private int communication_score;
    private int confidence_score;
    private int problem_solving_score;
    private List<String> strengths;
    private List<String> weak_areas;
    private List<String> suggestions;
}
