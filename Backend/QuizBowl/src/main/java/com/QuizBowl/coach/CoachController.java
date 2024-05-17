package com.QuizBowl.coach;

import com.QuizBowl.questions.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CoachController {

    @Autowired
    CoachRepository coachRepository;

    @GetMapping("/coach/getAll")
    public List<Coach> getAllCoaches(){
        return coachRepository.findAll();
    }

    @GetMapping("/coach/getById/{id}")
    public Coach getById(@PathVariable int id){
        return coachRepository.findById(id).orElseThrow();
    }

    @PostMapping("/coach/createCoach")
    public String createCoach(@RequestBody Coach c){
        if(c.getUsername().isEmpty()){
            return "unsuccessful";
        }else{
            coachRepository.save(c);
            return "Success!";
        }
    }

    @PutMapping("/coach/updateUsername/{username}{id}")
    public String updateUsername(@PathVariable String username, @PathVariable int id){
        Coach c = coachRepository.findById(id).orElseThrow();
        c.setUsername(username);
        coachRepository.save(c);
        return "Coach username updated to: " + username;
    }

    @DeleteMapping("/coach/delete/{id}")
    public String deleteCoach(@PathVariable int id){
        coachRepository.deleteById(id);
        return "Coach deleted";
    }
}
