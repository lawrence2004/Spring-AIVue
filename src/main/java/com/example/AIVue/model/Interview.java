package com.example.AIVue.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Interview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    private String jobTitle;

    private String company;

    @Column(columnDefinition = "LONGTEXT")
    private String resumeData;

    @Column(columnDefinition = "LONGTEXT")
    private String jobDescription;

    @Enumerated(EnumType.STRING)
    private Status status;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User student;

    @OneToOne(mappedBy = "interview")
    private QuestionAnswer questionAnswers;

}
