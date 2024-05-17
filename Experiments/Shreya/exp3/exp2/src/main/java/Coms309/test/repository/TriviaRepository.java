package Coms309.test.repository;

import Coms309.test.model.trivia;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TriviaRepository extends JpaRepository<trivia, Long> {

}
