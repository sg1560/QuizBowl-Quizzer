package com.QuizBowl.questions;

import com.QuizBowl.users.Client;
import com.QuizBowl.users.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebInputException;

import java.util.Optional;
import java.util.Set;
import java.util.List;

@RestController
public class CategoryController {
    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    QuestionRepository questionRepository;

    @Autowired
    UserRepository userRepository;

    @GetMapping("/categories/getbyid/{id}")
    public Category getCategoryById(@PathVariable long id) {
        if (id == 0) return UniversalCategory.getInstance();
        return categoryRepository.findById(id).orElse(null);
    }

    @GetMapping("/categories")
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @GetMapping("/categories/getquestions/{id}")
    public List<Question> getQuestionsInCategory(@PathVariable long id, @RequestParam("owner") Optional<Long> ownerid) {
        Category c;
        if (id == 0) {
            c = UniversalCategory.getInstance();
        } else {
            c = categoryRepository.findById(id).orElseThrow();
        }
        List<Question> questions = c.getQuestionSet().stream().toList();
        if (ownerid.isPresent()) {
            Client client = userRepository.findById(ownerid.get()).get();
            questions = questions.stream().filter((q) -> client.equals(q.getClient())).toList();
        }
        return questions;
    }

    @GetMapping("/categories/universal/getAll")
    public List<Question> getAllQuestions() {
        return questionRepository.findAll();
    }

    @PostMapping("/categories/createCategory")
    public String createCategory(@RequestBody Category newC) {
        if (newC.getCategoryName().isEmpty()) {
            return "Unsuccessful";
        } else {
            categoryRepository.save(newC);
            return "Success!";
        }
    }

    @PostMapping("/categories/post/{name}")
    public Category createCategory(@PathVariable String name) {
        Category category = new Category(name);
        categoryRepository.save(category);
        return category;
    }

    @PutMapping("/categories/{id}/updateName/{name}")
    public String updateCategoryName(@PathVariable Long id, @PathVariable String name) {
        Category c = categoryRepository.findById(id).orElseThrow();
        c.setCategoryName(name);
        categoryRepository.save(c);
        return "Category name updated to: " + name;
    }

    @DeleteMapping("/categories/delete/{id}")
    public String deleteCategory(@PathVariable Long id) {
        categoryRepository.deleteById(id);
        return "Category deleted";
    }
}
