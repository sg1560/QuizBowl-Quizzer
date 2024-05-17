package com.QuizBowl.demo.Questions;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@RestController
public class QuestionsController {
    @Autowired
    QuestionRepository questionRepository;

    //region getMappings
    @GetMapping("questions/all")
    List<Question> GetAllMessages() {
        return questionRepository.findAll();
    }

    @GetMapping("questions/byid/{id}")
    Question GetQuestionById(@PathVariable long id) {
        return questionRepository.findById(id).orElse(new Question("System", "There was no question by the id of " + id + ". Please try another id."));
    }

    @GetMapping("questions/bycreator/{creator}")
    List<Question> GetQuestionsByCreator(@PathVariable String creator) {
        return questionRepository.getQuestionsByCreator(creator);
    }

    @GetMapping("questions/beforedate/{time}")
    List<Question> GetQuestionsBeforeDate(@PathVariable LocalDateTime time) {
        return questionRepository.getQuestionsBeforeDate(time);
    }

    @GetMapping("questions/afterdate/{time}")
    List<Question> GetQuestionsAfterDate(@PathVariable LocalDateTime time) {
        return questionRepository.getQuestionsAfterDate(time);
    }

    @GetMapping("questions/arounddate/{time}/{offset}")
    List<Question> GetQuestionsAroundDate(@PathVariable LocalDateTime time, @PathVariable LocalDateTime offset) {
        // Ugly, but there isn't much of another way to do it. Each plus operation creates an entirely new object
        if (offset.isAfter(time)) {
            LocalDateTime delta = offset.minusYears(time.getYear()).minusDays(time.getDayOfYear()).minusHours(time.getHour()).minusMinutes(time.getMinute()).minusSeconds(time.getSecond()).minusNanos(time.getNano());
            LocalDateTime upperBound = offset;
            LocalDateTime lowerBound = time.minusYears(delta.getYear()).minusDays(delta.getDayOfYear() + 1).minusHours(delta.getHour()).minusMinutes(delta.getMinute()).minusSeconds(delta.getSecond()).minusNanos(delta.getNano());
            HashSet<Question> questionsBefore = new HashSet<Question>(questionRepository.getQuestionsBeforeDate(upperBound));
            return questionRepository.getQuestionsAfterDate(lowerBound).stream().filter(questionsBefore::contains).toList();
        } else {
            LocalDateTime delta = time.minusYears(offset.getYear()).minusDays(offset.getDayOfYear()).minusHours(offset.getHour()).minusMinutes(offset.getMinute()).minusSeconds(offset.getSecond()).minusNanos(offset.getNano());
            LocalDateTime upperBound = time.plusYears(delta.getYear()).plusDays(delta.getDayOfYear()).plusHours(delta.getHour()).plusMinutes(delta.getMinute()).plusSeconds(delta.getSecond()).plusNanos(delta.getNano());
            LocalDateTime lowerBound = offset;
            HashSet<Question> questionsBefore = new HashSet<Question>(questionRepository.getQuestionsBeforeDate(upperBound));
            return questionRepository.getQuestionsAfterDate(lowerBound).stream().filter(questionsBefore::contains).toList();
        }
    }

    @GetMapping("questions/betweendates/{start}/{end}")
    List<Question> GetQuestionsBetweenDates(@PathVariable LocalDateTime start, @PathVariable LocalDateTime end) {
        HashSet<Question> questionsBefore = new HashSet<Question>(questionRepository.getQuestionsBeforeDate(end));
        return questionRepository.getQuestionsAfterDate(start).stream().filter(questionsBefore::contains).toList();
    }
    //endregion

    //region postMappings
    @PostMapping("question")
    Question CreateQuestion(@RequestBody Question question) {
        questionRepository.save(new Question(question));
        return question;
    }

    @PostMapping("questions")
    Question CreateQuestion(@RequestParam(required = false, defaultValue = "Anonymous") String creator, @RequestBody String question) {
        Question newQuestion = new Question(creator, question);
        questionRepository.save(newQuestion);
        return newQuestion;
    }

    @PostMapping("questions/generate")
    List<Question> CreateQuestion(@RequestParam(required = false, defaultValue = "Anonymous") String creator, @RequestParam(required = false, defaultValue = "10") Integer count) {
        ArrayList<Question> newQuestions = new ArrayList<>(count);
        long fullCount = questionRepository.getMaxId();
        for (int i = 0; i < count; i++) {
            fullCount++;
            newQuestions.add(new Question(creator, "Generated Question " + fullCount));
        }
        questionRepository.saveAll(newQuestions);
        return newQuestions;
    }
    //endregion

    //region deleteMappings
    @DeleteMapping("questions/deleteall")
    List<Question> ClearAllQuestions() {
        List<Question> questions = questionRepository.findAll();
        questionRepository.deleteAll();
        return questions;
    }

    @DeleteMapping("questions/deletebyid/{id}")
    Question DeleteQuestionById(@PathVariable long id) {
        Optional<Question> question = questionRepository.findById(id);
        if (question.isPresent()) {
            questionRepository.deleteById(id);
            return question.get();
        }
        return new Question("System", "The question with id " + id + " was not found so it couldn't be deleted.");
    }

    @DeleteMapping("questions/deletebycreator/{creator}")
    List<Question> DeleteQuestionsByCreator(@PathVariable String creator) {
        List<Question> questions = questionRepository.getQuestionsByCreator(creator);
        questionRepository.deleteAll(questions);
        return questions;
    }
    //endregion
}