package com.QuizBowl.demo.Questions;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    @Query(value = "select coalesce(max(Question.id), 0) from Question", nativeQuery = true)
    long getMaxId();

    @Query(value = "select question from Question question where question.creator like %?1%")
    List<Question> getQuestionsByCreator(String creator);

    @Query(value = "select question from Question question where question.creationTime <= ?1")
    ArrayList<Question> getQuestionsBeforeDate(LocalDateTime localDateTime);

    @Query(value = "select question from Question question where question.creationTime >= ?1")
    ArrayList<Question> getQuestionsAfterDate(LocalDateTime localDateTime);

}
