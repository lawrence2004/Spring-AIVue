package com.example.AIVue.model;

import com.example.AIVue.dto.MapToStringConverter;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Entity
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    @Convert(converter = MapToStringConverter.class)
    @Column(columnDefinition = "LONGTEXT")
    private Map<String, String> questionAnswers;

    @OneToOne
    @JoinColumn(name = "interview_id")
    private Interview interview;

}

