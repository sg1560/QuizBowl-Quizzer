package com.QuizBowl;

import com.QuizBowl.Session.StudySession;
import com.QuizBowl.users.Client;
import com.QuizBowl.users.Team;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class StudySessionTests {

    @Autowired
    private MockMvc mockMvc;

    private Client client;
    private Team team;
    private StudySession studySession;

    @BeforeEach
    void setup() throws Exception {
        mockMvc.perform(delete("/users/deleteall")).andExpect(status().isOk());
        mockMvc.perform(delete("/teams/deleteall")).andExpect(status().isOk());
        client = Client.getClientFromJSON(mockMvc.perform(post("/users/post/TestSessionClient?password=loginpassword")).andExpect(status().isOk()).andReturn().getResponse().getContentAsString());
        team = Team.getTeamFromJSON(mockMvc.perform(post("/teams/post/TestSessionTeam")).andExpect(status().isOk()).andReturn().getResponse().getContentAsString());
//        mockMvc.perform(MockMvcRequestBuilders.multipart("/session" + client.getCid() + "/size/10")).andExpect(status().isOk());
    }

    @Test
    void testPracticeSessionSetup() {
        assertNotNull(client);
        assertNotNull(team);
    }

    @Test
    void testSessionAddPoints() {

    }

}
