package com.example.AIVue.Repository;

import com.example.AIVue.model.Interview;
import com.example.AIVue.model.QuestionAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionAnswerRepository extends JpaRepository<QuestionAnswer, Long> {

    QuestionAnswer findByInterview(Interview interview);
}
