package com.QuizBowl;

import com.QuizBowl.questions.Category;
import com.QuizBowl.questions.ClientQuestionHistory;
import com.QuizBowl.questions.Question;
import com.QuizBowl.users.Client;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class QuestionHistoryTests {
    @Autowired
    private MockMvc mockMvc;

    private Client client;
    private Category category;

    @BeforeEach
    void resetDatabase() throws Exception {
        mockMvc.perform(delete("/users/deleteall"));
        client = Client.getClientFromJSON(mockMvc.perform(post("/users/post/ClientQuestionHistory?password=mypassword")).andExpect(status().isOk()).andReturn().getResponse().getContentAsString());
        category = Category.getCategoryFromJSON(mockMvc.perform(post("/categories/post/CatQuestionHistory")).andExpect(status().isOk()).andReturn().getResponse().getContentAsString());
    }

    private void updateClient() throws Exception {
        client = Client.getClientFromJSON(mockMvc.perform(get("/users/getbyid/" + client.getCid())).andExpect(status().isOk()).andReturn().getResponse().getContentAsString());
    }

    private Question createTestQuestion(int points) throws Exception {
        return Question.getQuestionFromJSON(mockMvc.perform(post("/questions/post/" + category.getId() + "/testquestion/testanswer/" + points)).andExpect(status().isOk()).andReturn().getResponse().getContentAsString());
    }

    @Test
    void testQuestionCorrect() throws Exception {
        Question testQuestion = createTestQuestion(10);
        assertEquals(0, client.getTotalPoints());
        mockMvc.perform(put("/users/getbyid/" + client.getCid() + "/savequestioncorrect/" + testQuestion.getId()));
        updateClient();
        assertEquals(10, client.getTotalPoints());
        ClientQuestionHistory history = ClientQuestionHistory.getQuestionHistoryFromJSON(mockMvc.perform(get("/users/getbyid/" + client.getCid() + "/getquestionhistory/" + testQuestion.getId())).andExpect(status().isOk()).andReturn().getResponse().getContentAsString());
        assertEquals(1, history.getCorrectAnswers());
    }

    @Test
    void testQuestionIncorrect() throws Exception {
        Question testQuestion = createTestQuestion(10);
        assertEquals(0, client.getTotalPoints());
        mockMvc.perform(put("/users/getbyid/" + client.getCid() + "/savequestionincorrect/" + testQuestion.getId()));
        updateClient();
        assertEquals(0, client.getTotalPoints());
        ClientQuestionHistory history = ClientQuestionHistory.getQuestionHistoryFromJSON(mockMvc.perform(get("/users/getbyid/" + client.getCid() + "/getquestionhistory/" + testQuestion.getId())).andExpect(status().isOk()).andReturn().getResponse().getContentAsString());
        assertEquals(1, history.getIncorrectAnswers());
    }

    @Test
    void testMultipleQuestionRequests() throws Exception {
        Question testQuestion = createTestQuestion(10);
        assertEquals(0, client.getTotalPoints());
        mockMvc.perform(put("/users/getbyid/" + client.getCid() + "/savequestionincorrect/" + testQuestion.getId()));
        mockMvc.perform(put("/users/getbyid/" + client.getCid() + "/savequestioncorrect/" + testQuestion.getId()));
        mockMvc.perform(put("/users/getbyid/" + client.getCid() + "/savequestionincorrect/" + testQuestion.getId()));
        mockMvc.perform(put("/users/getbyid/" + client.getCid() + "/savequestioncorrect/" + testQuestion.getId()));
        mockMvc.perform(put("/users/getbyid/" + client.getCid() + "/savequestioncorrect/" + testQuestion.getId()));
        updateClient();
        assertEquals(30, client.getTotalPoints());
        ClientQuestionHistory history = ClientQuestionHistory.getQuestionHistoryFromJSON(mockMvc.perform(get("/users/getbyid/" + client.getCid() + "/getquestionhistory/" + testQuestion.getId())).andExpect(status().isOk()).andReturn().getResponse().getContentAsString());
        assertEquals(3, history.getCorrectAnswers());
        assertEquals(2, history.getIncorrectAnswers());
    }
}
