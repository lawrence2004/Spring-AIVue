package com.example.AIVue.Repository;

import com.example.AIVue.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.example.AIVue.model.Interview;
import java.util.*;

@Repository
public interface InterviewRepository extends JpaRepository<Interview, Long> {

    List<Interview> findByStudent(User user);

    @Query("SELECT i.resumeData FROM Interview i WHERE i.id = :id")
    String findResumeDataById(@Param("id") Long id);

    @Query("SELECT i.jobTitle FROM Interview i WHERE i.id = :id")
    String findJobTitleById(@Param("id") Long id);

    @Query("SELECT i.company FROM Interview i WHERE i.id = :id")
    String findCompanyById(@Param("id") Long id);

    @Query("SELECT i.jobDescription FROM Interview i WHERE i.id = :id")
    String findJobDescriptionById(@Param("id") Long id);

}
