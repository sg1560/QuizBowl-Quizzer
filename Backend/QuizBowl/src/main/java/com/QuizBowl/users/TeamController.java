package com.QuizBowl.users;

import com.QuizBowl.questions.Category;
import com.QuizBowl.questions.Question;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebInputException;

import java.util.List;
import java.util.Set;

@RestController
public class TeamController {
    @Autowired
    TeamRepository teamRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ClientController clientController;

    //region getMappings
    @GetMapping("teams/getbyid/{tid}")
    public Team getTeamById(@PathVariable long tid) {
        return teamRepository.findById(tid).orElse(null);
    }

    @GetMapping("teams/getall")
    public List<Team> getAllTeams() {
        return teamRepository.findAll();
    }

    //endregion

    //region postMappings
    @PostMapping("teams/post/{name}")
    public Team postTeam(@PathVariable String name) {
        Team newTeam = new Team(name, null);
        teamRepository.save(newTeam);
        return newTeam;
    }

    @PostMapping("teams/createNewTeam/{name}")
    public Team createTeamWithCoach(@PathVariable String name, @RequestParam String coachName, @RequestParam String coachPassword) {
        Client coach = clientController.getUserByLogin(coachName, coachPassword);
        if (coach == null) throw new ServerWebInputException("You did not give a valid user as the coach");
        Team newTeam = new Team(name, coach);
        teamRepository.save(newTeam);
        coach.addTeam(newTeam);
        userRepository.save(coach);
        return newTeam;
    }
    //endregion

    //region putMappings
    @PutMapping("teams/getbyid/{tid}/adduser/{uid}")
    public Team addUserToTeam(@PathVariable long tid, @PathVariable long uid) {
        Team team = teamRepository.findById(tid).orElseThrow();
        Client client = userRepository.findById(uid).orElseThrow();
        if (team.getMembers().contains(client))
            throw new ServerWebInputException("The user was already a part of the team!");
        team.addMember(client);
        client.addTeam(team);
        teamRepository.save(team);
        userRepository.save(client);
        return team;
    }

    @PutMapping("teams/getbyid/{tid}/removeuser/{uid}")
    public Team removeUserfromTeam(@PathVariable long tid, @PathVariable long uid) {
        Team team = teamRepository.findById(tid).orElseThrow();
        Client client = userRepository.findById(uid).orElseThrow();
        if (!team.getMembers().contains(client))
            throw new ServerWebInputException("The user was not a part of the team!");
        team.removeMember(client);
        client.removeTeam(team);
        userRepository.save(client);
        teamRepository.save(team);
        return team;
    }

    @GetMapping("/teams/getusers/{id}")
    public Set<Client> getUsersInTeam(@PathVariable long id){
        Team t = teamRepository.findById(id).orElseThrow();
        return t.getMembers();
    }
    @PutMapping("teams/getbyid/{tid}/addpoints/{points}")
    public void addPointsToTeam(@PathVariable long tid, @PathVariable long points) {
        Team team = teamRepository.findById(tid).orElseThrow();
        team.addTeamPoints(points);
        teamRepository.save(team);
    }
    //endregion

    //region deleteMappings
    @DeleteMapping("teams/deletebyid/{id}")
    public Team deleteTeamById(@PathVariable long id) {
        Team teams = teamRepository.findById(id).orElseThrow();
        teams.onDelete();
        teamRepository.deleteById(id);
        return teams;
    }

    @DeleteMapping("teams/deleteall")
    public List<Team> deleteAllTeams() {
        List<Team> teams = teamRepository.findAll();
        for (var team : teams) {
            team.onDelete();
        }
        teamRepository.deleteAll();
        return teams;
    }

    //endregion
}
