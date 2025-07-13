package com.example.AIVue.dto.QuestionAnswer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
@AllArgsConstructor
public class QAResponse {

    private Map<String, String> questionAnswers;

}
