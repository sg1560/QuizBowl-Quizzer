package Coms309.test.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class hello {

    @GetMapping("/")
    public String index() {
        return "Greetings from Spring Boot! Welcome to ComS309!!";
    }
}
