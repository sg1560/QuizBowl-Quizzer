package Coms309.test.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class hello {

    @GetMapping("/")
    public String index() {
        return "Welcome to COMS 309 Shreya";
    }
}
