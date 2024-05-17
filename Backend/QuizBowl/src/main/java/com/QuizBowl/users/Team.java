package com.QuizBowl.users;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
public class Team {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long tid;

    private String name;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="client_cid")
    @JsonIgnoreProperties("teams")
    private Client coach;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    @JsonIgnoreProperties("teams")
    private Set<Client> members;

    private long teamPoints;

    public Team() {

    }

    public Team(String name, Client coach) {
        this.name = name;
        members = new HashSet<>();
        this.coach = coach;
        members.add(coach);
    }

    public void onDelete() {
        for (var member : members) {
            member.removeTeam(this);
        }
        members.clear();
    }

    public void setTid(long gid) {
        this.tid = gid;
    }

    public long getTid() {
        return tid;
    }

    public Set<Client> getMembers() {
        return members;
    }

    public void setMembers(Set<Client> members) {
        this.members = members;
    }

    public void addMember(Client member) {
        members.add(member);
    }
    public void removeMember(Client member) {
        members.remove(member);
    }

    public boolean memberInTeam(Client member){
        for(Client c: getMembers()){
            if(c.getCid() == member.getCid()){
                return true;
            }
        }
        return false;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Client getCoach() {
        return coach;
    }

    public void setCoach(Client coach) {
        this.coach = coach;
    }

    public long getTeamPoints() {
        return teamPoints;
    }

    public void addTeamPoints(long teamPoints) {
        this.teamPoints += teamPoints;
    }

    public static Team getTeamFromJSON(String teamAsString) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper.readValue(teamAsString, Team.class);
    }


    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Team)) return false;
        return tid == ((Team)o).tid;
    }

    @Override
    public int hashCode() {
        return Objects.hash(tid);
    }
}
