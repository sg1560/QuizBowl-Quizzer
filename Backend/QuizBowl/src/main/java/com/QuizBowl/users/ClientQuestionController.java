package com.QuizBowl.users;

import com.QuizBowl.questions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
public class ClientQuestionController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    QuestionRepository questionRepository;

    @Autowired
    QuestionController questionController;

    @Autowired
    CategoryController categoryController;

    @Autowired
    ClientQuestionRepository questionHistoryRepository;

    //region getMappings

    @GetMapping("users/getbyid/{cid}/getquestionhistory/{qid}")
    public ClientQuestionHistory getUserQuestionHistory(@PathVariable long cid, @PathVariable long qid) {
        return questionHistoryRepository.getClientQuestion(cid, qid);
    }

    @GetMapping("users/getbyid/{cid}/getalluserquestionhistory")
    public List<ClientQuestionHistory> getAllUserQuestionHistory(@PathVariable long cid) {
        Client client = userRepository.findById(cid).orElseThrow();
        return client.getClientQuestionHistory().stream().toList();
    }

    @GetMapping("users/getbyid/{cid}/getquestionhistorystatistics")
    public double getUserQuestionHistoryStatistics(@PathVariable long cid, @RequestParam("category") Optional<Long> catid) {
        Client client = userRepository.findById(cid).orElseThrow();
        List<ClientQuestionHistory> questionHistoryList;
        if (catid.isPresent()) {
            Category category = categoryController.getCategoryById(catid.get());
            questionHistoryList = client.getClientQuestionHistory().stream().filter((h) -> h.getQuestion().getCategory().getId() == category.getId()).toList();
        } else {
            questionHistoryList = client.getClientQuestionHistory().stream().toList();
        }
        long correctCount = 0;
        long incorrectCount = 0;
        for (ClientQuestionHistory clientQuestionHistory : questionHistoryList) {
            if (clientQuestionHistory.getCorrectAnswers() > clientQuestionHistory.getIncorrectAnswers())
                correctCount++;
            else incorrectCount++;
        }
        return (double) correctCount / (correctCount + incorrectCount);
    }

    @GetMapping("users/getbyid/{cid}/getquestioncorrectpercentage")
    public double getUserQuestionCorrectPercentage(@PathVariable long cid, @RequestParam("category") Optional<Long> catid) {
        Client client = userRepository.findById(cid).orElseThrow();
        List<ClientQuestionHistory> questionHistoryList = new ArrayList<>();
        if (catid.isPresent()) {
            Category category = categoryController.getCategoryById(catid.get());
            for (var history : client.getClientQuestionHistory()) {
                Question question = questionRepository.findById(history.getQuestion().getId()).get();
                if (category.equals(question.getCategory()))
                    questionHistoryList.add(history);
            }
        } else {
            questionHistoryList = client.getClientQuestionHistory().stream().toList();
        }
        long correctPoints = 0;
        long incorrectPoints = 0;
        for (ClientQuestionHistory clientQuestionHistory : questionHistoryList) {
            long points = clientQuestionHistory.getQuestion().getPoints();
            if (clientQuestionHistory.getCorrectAnswers() > clientQuestionHistory.getIncorrectAnswers())
                correctPoints += points;
            else incorrectPoints += points;
        }
        return (double) correctPoints / (correctPoints + incorrectPoints);
    }

    //endregion

    //region putMappings
    @PutMapping("users/getbyid/{cid}/savequestioncorrect/{qid}")
    public void addUserQuestionCorrect(@PathVariable long cid, @PathVariable long qid) {
        Client client = userRepository.findById(cid).orElseThrow();
        Question question = questionController.getQuestionById(qid);
        ClientQuestionHistory questionHistory = questionHistoryRepository.getClientQuestion(cid, qid);
        if (questionHistory == null) {
            questionHistory = new ClientQuestionHistory(client, question);
            questionHistory = questionHistoryRepository.save(questionHistory);
            client.addQuestionHistory(questionHistory);
            question.addClientQuestionHistory(questionHistory);
            questionRepository.save(question);
        }
        questionHistory.addCorrectAnswer();
        client.addTotalPoints(question.getPoints());
        questionHistoryRepository.save(questionHistory);
        userRepository.save(client);
    }

    @PutMapping("users/getbyid/{cid}/savequestionincorrect/{qid}")
    public void addUserQuestionIncorrect(@PathVariable long cid, @PathVariable long qid) {
        Client client = userRepository.findById(cid).orElseThrow();
        Question question = questionController.getQuestionById(qid);
        ClientQuestionHistory questionHistory = questionHistoryRepository.getClientQuestion(cid, qid);
        if (questionHistory == null) {
            questionHistory = new ClientQuestionHistory(client, question);
            questionHistory = questionHistoryRepository.save(questionHistory);
            client.addQuestionHistory(questionHistory);
            question.addClientQuestionHistory(questionHistory);
            questionRepository.save(question);
            userRepository.save(client);
        }
        questionHistory.addIncorrectAnswer();
        questionHistoryRepository.save(questionHistory);
    }
    //endregion

    //region deleteMappings

    /**
     * Delete all the clients QuestionHistory related to the specified category.
     * If no category was given it will delete the history of all categories.
     *
     * @param cid   the client of which to delete the history from
     * @param catid the category of which to delete the history from, leave out to delete from every category
     */
    @DeleteMapping("users/getbyid/{cid}/deletequestionhistory")
    public void deleteUserQuestionHistory(@PathVariable long cid, @RequestParam("category") Optional<Long> catid) {
        Client client = userRepository.findById(cid).orElseThrow();
        List<ClientQuestionHistory> toRemove;
        if (catid.isPresent()) {
            Category category = categoryController.getCategoryById(catid.get());
            toRemove = client.getClientQuestionHistory().stream().filter((h) -> h.getQuestion().getCategory().getId() == category.getId()).toList();
        } else {
            toRemove = client.getClientQuestionHistory().stream().toList();
        }
        toRemove.forEach(client::removeQuestionHistory);
        questionHistoryRepository.deleteAll(toRemove);
        userRepository.save(client);
    }
    //endregion
}
