package com.QuizBowl.users;

import com.QuizBowl.admin.AdminController;
import com.QuizBowl.admin.AdminRepository;
import com.QuizBowl.questions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.Set;

@RestController
public class ClientController {
    @Autowired
    UserRepository userRepository;

    @Autowired
    QuestionRepository questionRepository;

    @Autowired
    TeamRepository teamRepository;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    ClientQuestionRepository clientQuestionRepository;

    @Autowired
    AdminRepository adminRepository;

    @Autowired
    ProfileImageRepository profileImageRepository;

    //region getMappings
    @GetMapping("users/getbyid/{id}")
    public Client getUserById(@PathVariable long id) {
        return userRepository.findById(id).orElse(null);
    }

    @GetMapping("users/login")
    public Client getUserByLogin(@RequestParam String username, @RequestParam String password) {
        Client client = userRepository.getClientLogin(username, password);
        return client;
    }

    @GetMapping("users/getall")
    public List<Client> getAllUsers() {
        return userRepository.findAll();
    }

    @GetMapping("users/getQuestions/{cid}")
    public Set<Question> getQuestionsforClient(@PathVariable long cid) {
        Client c = userRepository.findById(cid).orElseThrow();
        return c.getQuestionSet();
    }
    //endregion

    //region postMappings
    @PostMapping("users/post/{name}")
    public Client postUser(@PathVariable String name, @RequestParam String password) {
        if (getAllUsers().stream().anyMatch((u) -> u.getUserName().equals(name)))
            throw new IllegalArgumentException("Trying to create a user with the same name!");
        Client newUser = new Client(name, password);
        userRepository.save(newUser);
        return newUser;
    }
    //endregion

    //region putMappings
    @PutMapping("users/rename")
    public Client renameUser(@RequestParam String username, @RequestParam String password, @RequestParam String newusername) {
        Client client = userRepository.getClientLogin(username, password);
        client.setUserName(newusername);
        userRepository.save(client);
        return client;
    }

    @PutMapping("users/changepassword")
    public Client changeUserPassword(@RequestParam String username, @RequestParam String password, @RequestParam String newpassword) {
        Client client = userRepository.getClientLogin(username, password);
        client.setPassword(newpassword);
        userRepository.save(client);
        return client;
    }

    @PutMapping("users/getbyid/{id}/addpoints/{points}")
    public void addUserPoints(@PathVariable long id, @PathVariable long points) {
        Client client = userRepository.findById(id).orElseThrow();
        client.addTotalPoints(points);
        userRepository.save(client);
    }
    //endregion

    //region deleteMappings
    @DeleteMapping("users/deletebyid/{id}")
    public Client deleteUserById(@PathVariable long id) {
        Client client = userRepository.findById(id).orElseThrow();
        userRepository.delete(client);
//        for (Team team : client.getTeams().stream().map((t) -> teamRepository.findById(t.getTid()).get()).toList()) {
//            team.removeMember(client);
//            teamRepository.save(team);
//        }
//        List<Question> questionList = client.getQuestionSet().stream().map((q) -> questionRepository.findById(q.getId()).get()).toList();
//        questionList.forEach(client::removeQuestion);
//        userRepository.save(client);
//        for (Question question : questionList) {
//            Category category = categoryRepository.findById((long)question.getCategory().getId()).get();
//            category.removeQuestion(question);
//            categoryRepository.save(category);
//            questionRepository.deleteById(question.getId());
//        }
//        List<ClientQuestionHistory> clientQuestionHistoryList = client.getClientQuestionHistory().stream().toList();
//        clientQuestionHistoryList.forEach(client::removeQuestionHistory);
//        userRepository.save(client);
//        clientQuestionRepository.deleteAll(clientQuestionHistoryList);
//
//        ProfileImage profileImage = client.getProfileImage();
//        client.setProfileImage(null);
//        userRepository.save(client);
//        if (profileImage != null) {
//            profileImageRepository.deleteById(profileImage.getIid());
//        }
//        userRepository.delete(client);
        return client;
    }

    @DeleteMapping("users/deleteall")
    public List<Client> deleteAllUsers() {
        List<Client> clients = userRepository.findAll();
        for (var client : clients) {
            deleteUserById(client.getCid());
        }
        return clients;
    }

    //endregion
}
