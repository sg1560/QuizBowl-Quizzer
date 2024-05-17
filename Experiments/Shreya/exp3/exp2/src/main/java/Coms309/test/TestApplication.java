package Coms309.test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class TestApplication {

	public static void main(String[] args) {
		SpringApplication.run(TestApplication.class, args);
	}

}
@RestController
class HelloController {

	@GetMapping("/")
	public String index() {
		return "Greetings from Spring Boot! Welcome to ComS309!!";
	}
}