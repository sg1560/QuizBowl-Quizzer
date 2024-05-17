package com.QuizBowl.questions;

import com.QuizBowl.users.Client;
import com.QuizBowl.users.ClientQuestionController;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.persistence.*;
import org.json.JSONObject;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "Question")
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String question;
    private String answer;
    private int points;

    @JsonIgnoreProperties("questionSet")
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    @JoinColumn(name = "category_id", referencedColumnName = "cid")
    private Category category;

    @JsonIgnoreProperties("clientQuestionHistory, teams, questionSet")
    @ManyToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "client_id", referencedColumnName = "cid")
    private Client client;

    @JsonIgnore
    @OneToMany(orphanRemoval = true, fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<ClientQuestionHistory> clientQuestionHistories;

    public Question() {
    }

    public Question(String question, String answer, int points) {
        this.question = question;
        this.answer = answer;
        this.points = points;
        clientQuestionHistories = new HashSet<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public Category getCategory() {
        return category;
    }

    public void assignCategory(Category category) {
        this.category = category;
    }

    public Client getClient() {
        return client;
    }

    public void assignClient(Client c) {
        this.client = c;
    }


    public Set<ClientQuestionHistory> getClientQuestionHistories() {
        return clientQuestionHistories;
    }

    public void addClientQuestionHistory(ClientQuestionHistory clientQuestionHistory) {
        clientQuestionHistories.add(clientQuestionHistory);
    }

    public void removeClientQuestionHistory(ClientQuestionHistory clientQuestionHistory) {
        clientQuestionHistories.remove(clientQuestionHistory);
    }

    public static Question getQuestionFromJSON(String questionAsString) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper.readValue(questionAsString, Question.class);
    }

    public JSONObject toJSON() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        return new JSONObject(objectMapper.writeValueAsString(this));
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Question)) return false;
        return id == ((Question) o).id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
