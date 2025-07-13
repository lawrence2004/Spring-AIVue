package com.example.AIVue.dto.Interview;

import com.example.AIVue.model.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardInterviewResponse {

    private Long id;
    private String jobTitle;
    private String company;
    private Status status;

}
