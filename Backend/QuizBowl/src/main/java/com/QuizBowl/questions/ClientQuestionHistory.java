package com.QuizBowl.questions;

import com.QuizBowl.users.Client;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.persistence.*;

@Entity
public class ClientQuestionHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long hid;

    @JsonIgnore
    @ManyToOne(cascade = CascadeType.REMOVE)
    private Client client;

    @ManyToOne(cascade = CascadeType.REMOVE)
    private Question question;

    private int correctAnswers;
    private int incorrectAnswers;

    public ClientQuestionHistory() {
    }

    public ClientQuestionHistory(Client client, Question question) {
        this.client = client;
        this.question = question;
        correctAnswers = 0;
        incorrectAnswers = 0;
    }

    public long getHid() {
        return hid;
    }

    public Client getClient() {
        return client;
    }

    public Question getQuestion() {
        return question;
    }

    public int getIncorrectAnswers() {
        return incorrectAnswers;
    }

    public void addIncorrectAnswer() {
        incorrectAnswers++;
    }

    public int getCorrectAnswers() {
        return correctAnswers;
    }

    public void addCorrectAnswer() {
        correctAnswers++;
    }

    public static ClientQuestionHistory getQuestionHistoryFromJSON(String questionHistoryAsString) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper.readValue(questionHistoryAsString, ClientQuestionHistory.class);
    }
}
