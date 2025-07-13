package com.example.AIVue.dto.QuestionAnswer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class QARequest {

    private String jobTitle;
    private String company;
    private Long interviewId;

}
