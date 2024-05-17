package com.QuizBowl.users;

import com.QuizBowl.questions.ClientQuestionHistory;
import com.QuizBowl.questions.Question;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long cid;

    private String userName;

    private String password;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    @JoinTable(name = "client_team",
            joinColumns = @JoinColumn(name = "client_cid"),
            inverseJoinColumns = @JoinColumn(name = "team_tid"))
    @JsonIgnoreProperties(value = {"members", "coach"})
    private Set<Team> teams;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable(name = "client_client_question_history", joinColumns = @JoinColumn(name = "client_cid"),
            inverseJoinColumns = @JoinColumn(name = "client_question_history_hid"))
    @JsonIgnore
    private Set<ClientQuestionHistory> clientQuestionHistory;

    //One client owns many questions
    @JsonIgnore
    @OneToMany(mappedBy = "client", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<Question> questionSet = new HashSet<>();

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime joinDate;

    private long totalPoints;

    @JsonIgnore
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private ProfileImage profileImage;

    public Client() {
    }

    public Client(String userName, String password) {
        this.userName = userName;
        this.joinDate = LocalDateTime.now();
        this.password = password;
        teams = new HashSet<>();
        clientQuestionHistory = new HashSet<>();
        totalPoints = 0;
    }

    public long getCid() {
        return cid;
    }

    public void setCid(long id) {
        this.cid = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String name) {
        this.userName = name;
    }

    public Set<Team> getTeams() {
        return teams;
    }

    public void setTeams(Set<Team> teams) {
        this.teams = teams;
    }

    public void addTeam(Team team) {
        teams.add(team);
    }

    public void removeTeam(Team team) {
        teams.remove(team);
    }

    public LocalDateTime getJoinDate() {
        return joinDate;
    }

    public void setJoinDate(LocalDateTime joinDate) {
        this.joinDate = joinDate;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public long getTotalPoints() {
        return totalPoints;
    }

    public void addTotalPoints(long totalPoints) {
        this.totalPoints += totalPoints;
    }

    public Set<ClientQuestionHistory> getClientQuestionHistory() {
        return clientQuestionHistory;
    }

    public void addQuestionHistory(ClientQuestionHistory questionHistory) {
        clientQuestionHistory.add(questionHistory);
    }

    public Set<Question> getQuestionSet() {
        return questionSet;
    }

    public void addQuestion(Question q) {
        questionSet.add(q);
    }

    public void removeQuestion(Question question) {
        questionSet.remove(question);
    }

    public void removeQuestionHistory(ClientQuestionHistory questionHistory) {
        clientQuestionHistory.remove(questionHistory);
    }

    public static Client getClientFromJSON(String clientAsString) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper.readValue(clientAsString, Client.class);
    }

    public String toJSON() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper.writeValueAsString(this);
    }

    public ProfileImage getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(ProfileImage profileImage) {
        this.profileImage = profileImage;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Client)) return false;
        return cid == ((Client)o).cid;
    }

    @Override
    public int hashCode() {
        return Objects.hash(cid);
    }
}
