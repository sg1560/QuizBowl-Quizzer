package com.QuizBowl.demo;

import com.QuizBowl.demo.Questions.QuestionRepository;
import com.QuizBowl.demo.Questions.QuestionsController;
import org.json.JSONArray;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.Assert;

import java.util.Random;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@AutoConfigureMockMvc
@SpringBootTest
@ActiveProfiles("test")
class DemoApplicationTests {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void serverStatusOnline() throws Exception {
        mockMvc.perform(get("/status?requester=ServerStatusTest")).andExpectAll(
            status().isOk(),
            jsonPath("status").value("Online"),
            jsonPath("requester").value("ServerStatusTest"));
    }

    @BeforeEach
    void resetDatabase() throws Exception {
        mockMvc.perform(delete("/questions/deleteall"))
            .andExpect(status().isOk());
    }

    @Test
    void testEmptyGetAll() throws Exception {
        mockMvc.perform(get("/questions/all")).andExpectAll(
            status().isOk(),
            content().string("[]"));
    }

    @Test
    void testPost() throws Exception {
        mockMvc.perform(post("/questions").param("creator","unittest").content("Test Post Question"));
        mockMvc.perform(get("/questions/all")).andExpectAll(
            status().isOk(),
            jsonPath("[0].text").value("Test Post Question"));
    }

    @Test
    void testGenerate() throws Exception {
        int amount = new Random().nextInt(5,30);
        mockMvc.perform(post("/questions/generate").param("creator","unittest").param("count", Integer.toString(amount)));
        JSONArray content = new JSONArray(mockMvc.perform(get("/questions/all"))
            .andReturn()
            .getResponse()
            .getContentAsString());
        Assert.isTrue(content.length() == amount, "All the elements should be deleted");
    }

    @Test
    void testRemove() throws Exception {
        mockMvc.perform(post("/questions").param("creator","unittest").content("Test Remove Question"));
        JSONArray content = new JSONArray(mockMvc
            .perform(get("/questions/all"))
            .andReturn()
            .getResponse()
            .getContentAsString());
        int questionID = content.getJSONObject(0).getInt("id");
        mockMvc.perform(delete("/questions/deletebyid/" + questionID))
            .andExpect(status().isOk());
        content = new JSONArray(mockMvc.perform(get("/questions/all"))
            .andReturn()
            .getResponse()
            .getContentAsString());
        Assert.isTrue(content.length() == 0, "The element should be deleted");
    }

    @Test
    void testRemoveAll() throws Exception {
        mockMvc.perform(post("/questions/generate").param("creator","unittest").param("count", "20"));
        mockMvc.perform(delete("/questions/deleteall"))
            .andExpect(status().isOk());
        JSONArray content = new JSONArray(mockMvc.perform(get("/questions/all"))
            .andReturn()
            .getResponse()
            .getContentAsString());
        Assert.isTrue(content.length() == 0, "All the elements should be deleted");
    }

}
