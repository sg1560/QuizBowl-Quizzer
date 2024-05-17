package com.QuizBowl.questions;

import com.QuizBowl.Session.StudySessionServer;
import com.QuizBowl.users.ClientController;
import com.QuizBowl.users.ClientQuestionController;
import com.QuizBowl.users.TeamController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class UniversalCategory extends Category {
    private static QuestionRepository questionRepository;

    private static UniversalCategory instance;

    private UniversalCategory(){

    }

    public static UniversalCategory getInstance(){
        if(instance == null){
            instance = new UniversalCategory();
        }
        return instance;
    }

    @Override
    public Set<Question> getQuestionSet(){
        return new HashSet<Question>(questionRepository.findAll());
    }

    @Override
    public int getId() {
        return 0;
    }

    @Autowired
    public void setControllers(QuestionRepository questionRepository) {
        UniversalCategory.questionRepository = questionRepository;
    }
}
