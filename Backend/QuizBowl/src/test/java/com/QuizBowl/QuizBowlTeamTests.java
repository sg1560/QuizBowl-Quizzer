package com.QuizBowl;

import com.QuizBowl.users.Client;
import com.QuizBowl.users.Team;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class QuizBowlTeamTests {
    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void resetDatabase() throws Exception {
        mockMvc.perform(delete("/teams/deleteall"));
        mockMvc.perform(delete("/users/deleteall"));
    }

    @Test
    void testEmptyGetAll() throws Exception {
        mockMvc.perform(get("/teams/getall")).andExpectAll(
                status().isOk(),
                content().string("[]")
        );
    }

    @Test
    void testCreateTeam() throws Exception {
        mockMvc.perform(post("/teams/post/TestCreateTeam")).andExpectAll(
                status().isOk()
        );
    }

    @Test
    void testDeleteTeam() throws Exception {
        mockMvc.perform(post("/teams/post/TestCreateTeam")).andExpectAll(
                status().isOk()
        );
    }

    @Test
    void testDeleteAll() throws Exception {
        mockMvc.perform(post("/teams/post/TestDeleteAll1")).andExpectAll(
                status().isOk()
        );
        mockMvc.perform(post("/teams/post/TestDeleteAll2")).andExpectAll(
                status().isOk()
        );
        mockMvc.perform(post("/teams/post/TestDeleteAll3")).andExpectAll(
                status().isOk()
        );

        String reponse = mockMvc.perform(get("/teams/getall")).andExpectAll(
                status().isOk()
        ).andReturn().getResponse().getContentAsString();

        String deleteAllResponse = mockMvc.perform(delete("/teams/deleteall"))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        assertTrue(deleteAllResponse.contains("TestDeleteAll1"));
        assertTrue(deleteAllResponse.contains("TestDeleteAll2"));
        assertTrue(deleteAllResponse.contains("TestDeleteAll3"));
        mockMvc.perform(get("/teams/getall")).andExpectAll(
                status().isOk(),
                content().string("[]")
        );
    }

    @Test
    void testCreateTeamWithCoach() throws Exception {
        mockMvc.perform(post("/users/post/TestCoach?password=verysecretpassword")).andExpect(status().isOk());
        String request = mockMvc.perform(post("/teams/createNewTeam/NewTestTeamWithCoach?coachName=TestCoach&coachPassword=verysecretpassword")).andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        Team team = Team.getTeamFromJSON(request);

        Client coach = Client.getClientFromJSON(mockMvc.perform(get("/users/login?username=TestCoach&password=verysecretpassword")).andExpectAll(
                status().isOk()
        ).andReturn().getResponse().getContentAsString());
        assertEquals(coach.getCid(), team.getCoach().getCid());
    }

    @Test
    void testAddMemberToTeam() throws Exception {
        long teamId = new JSONObject(mockMvc.perform(post("/teams/post/TestAddMemberTeam")).andExpect(
                status().isOk()).andReturn().getResponse().getContentAsString()).getInt("tid");

        long clientId = new JSONObject(mockMvc.perform(post("/users/post/TestAddMemberClient?password=password")).andExpect(
                status().isOk()).andReturn().getResponse().getContentAsString()).getInt("cid");

        JSONObject team = new JSONObject(mockMvc.perform(put("/teams/getbyid/" + teamId + "/adduser/" + clientId)).andExpect(status().isOk()).andReturn().getResponse().getContentAsString());
        assertEquals(clientId, team.getJSONArray("members").getJSONObject(0).getInt("cid"));
    }

    @Test
    void testTeamPoints() throws Exception {
        Team team = Team.getTeamFromJSON(mockMvc.perform(post("/teams/post/TestPointsTeam")).andExpect(
                status().isOk()).andReturn().getResponse().getContentAsString());
        assertEquals(0, team.getTeamPoints());
        mockMvc.perform(put("/teams/getbyid/" + team.getTid() + "/addpoints/1000")).andExpect(
                status().isOk());
        team = Team.getTeamFromJSON(mockMvc.perform(get("/teams/getbyid/" + team.getTid())).andExpect(
                status().isOk()).andReturn().getResponse().getContentAsString());
        assertEquals(1000, team.getTeamPoints());
    }
}
