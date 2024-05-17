package com.QuizBowl.users;

import com.QuizBowl.questions.ClientQuestionHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ClientQuestionRepository extends JpaRepository<ClientQuestionHistory, Long> {
    @Query(value = "select history from ClientQuestionHistory history where history.client.cid=?1 and history.question.id=?2")
    ClientQuestionHistory getClientQuestion(long cid, long qid);
}
