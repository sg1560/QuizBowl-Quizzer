package com.QuizBowl.admin;

import com.QuizBowl.questions.*;
import com.QuizBowl.users.Client;
import com.QuizBowl.users.Team;
import com.QuizBowl.users.TeamController;
import com.QuizBowl.users.TeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebInputException;

import java.util.List;
import java.util.Optional;

@RestController
public class AdminController {
    @Autowired
    CategoryController categoryController;

    @Autowired
    TeamController teamController;

    @Autowired
    QuestionController questionController;

    @Autowired
    AdminRepository adminRepository;

    //Get all the admins
    @GetMapping("/admin/getAllAdmin")
    public List<Admin> getAllAdmin(){
        return adminRepository.findAll();
    }
    //Admin has access to all the teams in the database
    @GetMapping("/admin/getTeams")
    public List<Team> getAllTeams(){
        return teamController.getAllTeams();
    }

    //Admin has access to all the questions in the database
    @GetMapping("/admin/getQuestions")
    public List<Question> getAllQuestions(){return questionController.getAllQuestions();}

    //Admin has access to all the categories in the database
    @GetMapping("/admin/getCategories")
    public List<Category> getAllCategories(){return categoryController.getAllCategories();}

    @PostMapping("/admin/createAdmin")
    public Admin createAdmin(@RequestBody Admin admin){
        adminRepository.save(admin);
        return admin;
    }

    @PutMapping("admin/updateUsername/{username}/{id}")
    public String updateUsername(@PathVariable String username, @PathVariable Long id){
        Admin admin = adminRepository.findById(id).orElseThrow();
        admin.setName(username);
        adminRepository.save(admin);
        return "Username reset to: " + admin.getName();
    }
    //Admin can create team
    @PostMapping("/admin/createTeam/{name}/{coachName}/{coachPassword}")
    public Team createTeam(@PathVariable String name, @PathVariable String coachName, @PathVariable String coachPassword){
        return teamController.createTeamWithCoach(name, coachName, coachPassword);
    }

    //Admin can create question
    @PostMapping("/admin/createQuestion/{cid}")
    public Question createQuestion(@RequestBody Question newQ, @PathVariable Long cid){
        return questionController.createQuestion(newQ, newQ.getCategory().getId(), Optional.of(cid));
    }

    @DeleteMapping("/admin/deleteAdmin/{id}")
    public void deleteAdmin(@PathVariable long id){
        adminRepository.deleteById(id);
    }
    //Admin can remove a team
    @DeleteMapping("/admin/deleteTeam/{id}")
    public Team deleteTeam(@PathVariable long id){
        return teamController.deleteTeamById(id);
    }

    //Admin can remove a question
    @DeleteMapping("/admin/deleteQuestion/{id}")
    public Question deleteQuestion(@PathVariable Long id){
        return questionController.deleteQuestion(id);
    }
}
