package com.example.AIVue.dto.Interview;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InterviewRequest {

    private String jobTitle;
    private String company;
    private String jobDescription;
//    private MultipartFile resume;

}
