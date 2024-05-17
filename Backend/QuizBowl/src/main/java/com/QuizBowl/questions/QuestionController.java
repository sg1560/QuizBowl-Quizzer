package com.QuizBowl.questions;

import com.QuizBowl.users.Client;
import com.QuizBowl.users.ClientQuestionController;
import com.QuizBowl.users.ClientQuestionRepository;
import com.QuizBowl.users.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class QuestionController {
    @Autowired
    QuestionRepository questionRepository;

    @Autowired
    CategoryRepository categoryRepository;
    @Autowired
    UserRepository userRepository;

    @Autowired
    ClientQuestionRepository questionHistoryRepository;

    @GetMapping("/questions/getbyid/{id}")
    public Question getQuestionById(@PathVariable Long id) {
        return questionRepository.findById(id).orElse(null);
    }

    @GetMapping("/questions")
    public List<Question> getAllQuestions() {
        return questionRepository.findAll();
    }

    @GetMapping("/questions/{cid}")
    public List<Question> getAllQuestions(@PathVariable long cid) {
        Client client = userRepository.findById(cid).get();
        List<Question> questionList = questionRepository.findAll().stream().filter((q) -> client.equals(q.getClient())).toList();
        return questionList;
    }

    @GetMapping("/questions/getbydifficulty/{points}")
    public List<Question> getByDifficulty(@PathVariable int points) {
        return questionRepository.findByPoints(points);
    }

    //Post mappings to send data to server
    @PostMapping("/questions/createQuestion/{catid}")
    public Question createQuestion(@RequestBody Question newQ, @PathVariable long catid, @RequestParam Optional<Long> owner) {
        Question newQuestion = new Question(newQ.getQuestion(), newQ.getAnswer(), newQ.getPoints());
        if (owner.isPresent()) {
            Client client = userRepository.findById(owner.orElseThrow()).orElseThrow();
            client.addQuestion(newQuestion);
            newQuestion.assignClient(client);
            userRepository.save(client);
        }
        questionRepository.save(newQuestion);
        Category category = categoryRepository.findById(catid).get();
        newQuestion.assignCategory(category);
        category.addQuestion(newQuestion);
        categoryRepository.save(category);
        return questionRepository.findById(newQuestion.getId()).orElseThrow();
    }

    @PostMapping("/questions/post/{catid}/{question}/{answer}/{points}")
    public Question postQuestion(@PathVariable Long catid, @PathVariable String question, @PathVariable String answer, @PathVariable int points, @RequestParam Optional<Long> owner) {
        Question newQuestion = new Question(question, answer, points);
        if (owner.isPresent()) {
            Client client = userRepository.findById(owner.orElseThrow()).orElseThrow();
            client.addQuestion(newQuestion);
            newQuestion.assignClient(client);
            userRepository.save(client);
        }
        questionRepository.save(newQuestion);
        assignCategoryToQuestion(newQuestion.getId(), catid);
        return questionRepository.findById(newQuestion.getId()).orElseThrow();
    }

    @PutMapping("/questions/update/{id}/{catid}")
    public Question update(@RequestBody Question q, @PathVariable Long id, @PathVariable long catid) {
        Question question = questionRepository.findById(id).orElseThrow();
        question.setPoints(q.getPoints());
        question.setAnswer(q.getAnswer());
        question.setQuestion(q.getQuestion());
        question = questionRepository.save(question);
        Category newCategory = categoryRepository.findById(catid).orElseGet(() -> null);
        if (newCategory != null) {
            Category oldCategory = categoryRepository.findById((long) question.getCategory().getId()).get();
            if (!newCategory.equals(oldCategory)) {
                oldCategory.removeQuestion(question);
                categoryRepository.save(oldCategory);

                question.assignCategory(newCategory);
                question = questionRepository.save(question);

                newCategory.addQuestion(question);
                categoryRepository.save(newCategory);
            }
        }
        return question;
    }

    @PutMapping("/questions/{id}/updateQuestion/{name}")
    public String updateQuestion(@PathVariable Long id, @PathVariable String name) {
        Question q = questionRepository.findById(id).orElseThrow();
        q.setQuestion(name);
        questionRepository.save(q);
        return "Question updated to: " + name;
    }

    @PutMapping("/questions/{id}/updateAnswer/{name}")
    public String updateAnswer(@PathVariable Long id, @PathVariable String name) {
        Question q = questionRepository.findById(id).orElseThrow();
        q.setAnswer(name);
        questionRepository.save(q);
        return "Question's answer updated to: " + name;
    }

    @PutMapping("/questions/{id}/updateFullQuestion/{name}/{answer}")
    public String updateWholeQuestion(@PathVariable Long id, @PathVariable String name, @PathVariable String answer) {
        Question q = questionRepository.findById(id).orElseThrow();
        q.setQuestion(name);
        q.setAnswer(answer);
        questionRepository.save(q);
        return "Question's answer updated to: " + name;
    }

    @PutMapping("/question/{questionId}/category/{categoryId}")
    public void assignCategoryToQuestion(@PathVariable Long questionId, @PathVariable Long categoryId) {
        Question question = questionRepository.findById(questionId).get();
        Category category = categoryRepository.findById(categoryId).get();
        question.assignCategory(category);
        questionRepository.save(question);
    }

    @PutMapping("/question/{questionId}/client/{clientId}")
    public void assignClientToQuestion(@PathVariable Long questionId, @PathVariable Long clientId) {
        Question question = questionRepository.findById(questionId).get();
        Client client = userRepository.findById(clientId).get();
        question.assignClient(client);
        questionRepository.save(question);
    }

    @DeleteMapping("/questions/delete/{id}")
    public Question deleteQuestion(@PathVariable long id) {
        Question questionToDelete = questionRepository.findById(id).get();
        if (questionToDelete.getCategory() != null) {
            Category category = categoryRepository.findById((long) questionToDelete.getCategory().getId()).get();
            category.removeQuestion(questionToDelete);
            categoryRepository.save(category);
            questionToDelete.assignCategory(null);
            questionToDelete = questionRepository.save(questionToDelete);
        }
        if (questionToDelete.getClient() != null) {
            // Note: Does not delete the question history of all clients
            // It would be a very expensive operation unless we make it a many to many relationship
            Client client = userRepository.findById(questionToDelete.getClient().getCid()).get();
            client.removeQuestion(questionToDelete);
            userRepository.save(client);
        }
        List<ClientQuestionHistory> clientQuestionHistories = questionToDelete.getClientQuestionHistories().stream().toList();
        for (var history : clientQuestionHistories) {
            Client client = userRepository.findById(history.getClient().getCid()).get();
            client.removeQuestionHistory(history);
            userRepository.save(client);
            questionHistoryRepository.delete(history);
        }
        questionRepository.deleteById(id);
        return questionToDelete;
    }

    @DeleteMapping("/questions/deleteall")
    public List<Question> deleteAllQuestions() {
        List<Question> questionsToDelete = questionRepository.findAll();
        for (Question questionToDelete : questionsToDelete) {
            deleteQuestion(questionToDelete.getId());
        }
        return questionsToDelete;
    }
}
