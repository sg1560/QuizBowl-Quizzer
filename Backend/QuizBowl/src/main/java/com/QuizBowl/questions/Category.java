package com.QuizBowl.questions;

import com.QuizBowl.users.Client;
import com.QuizBowl.users.ProfileImage;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "Category")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int cid;
    private String categoryName;

    @JsonIgnore
    @OneToMany(mappedBy = "category", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<Question> questionSet = new HashSet<>();
    public Category(){}

    public Category(String categoryName) {
        this.categoryName = categoryName;
    }

    public Set<Question> getQuestionSet() {
        return questionSet;
    }
    public void addQuestion(Question q) {
        questionSet.add(q);
    }
    public void removeQuestion(Question q) {
        questionSet.remove(q);
    }
    public void setQuestionSet(Set<Question> questionSet) {
        this.questionSet = questionSet;
    }

    public int getId() {
        return cid;
    }

    public void setId(int id) {
        this.cid = id;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public static Category getCategoryFromJSON(String categoryAsString) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper.readValue(categoryAsString, Category.class);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Category)) return false;
        return cid == ((Category)o).cid;
    }

    @Override
    public int hashCode() {
        return Objects.hash(cid);
    }
}
