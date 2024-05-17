package Coms309.test.controller;

import Coms309.test.model.trivia;
import Coms309.test.repository.TriviaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class triviaController {
    @Autowired
    TriviaRepository triviaRepository;

    @GetMapping("trivia/all")
    List<trivia> getAll(){
        return triviaRepository.findAll();
    }

    @PostMapping("trivia/post/{q}/{a}")
    trivia PostTriviaByPath(@PathVariable String q, @PathVariable String a, @PathVariable int points){
        trivia newTrivia = new trivia();
        newTrivia.setQuestion(q);
        newTrivia.setAnswer(a);
        newTrivia.setPoints(points);
        triviaRepository.save(newTrivia);
        return newTrivia;
    }


    @PostMapping("trivia/post")
    trivia PostTriviaByPath(@RequestBody trivia newTrivia){
        triviaRepository.save(newTrivia);
        return newTrivia;
    }

}
