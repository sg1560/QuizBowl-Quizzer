package com.QuizBowl;

import com.QuizBowl.questions.Category;
import com.QuizBowl.questions.ClientQuestionHistory;
import com.QuizBowl.questions.Question;
import com.QuizBowl.users.Client;
import com.QuizBowl.users.Team;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.json.JSONArray;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class OskarSystemTests {

    @LocalServerPort
    int port;

    @BeforeEach
    void setup() throws Exception {
        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost";
        getDefaultRequestSpec().delete("/users/deleteall");
        getDefaultRequestSpec().delete("/teams/deleteall");
        getDefaultRequestSpec().delete("/questions/deleteall");
    }

    @Test
    public void testUserCreation() throws JsonProcessingException {
        Response response = getDefaultRequestSpec().post("/users/post/MyNewUser?password=mypass").andReturn();
        assertEquals(200, response.statusCode());
        String returnString = response.getBody().asString();
        Client client = Client.getClientFromJSON(returnString);
        assertEquals("MyNewUser", client.getUserName());
        assertEquals("mypass", client.getPassword());

        response = getDefaultRequestSpec().get("/users/getbyid/" + client.getCid()).andReturn();
        assertEquals(200, response.statusCode());
        returnString = response.getBody().asString();
        Client client2 = Client.getClientFromJSON(returnString);
        assertEquals(client, client2);
        assertEquals(client.getUserName(), client2.getUserName());
        assertEquals(client.getPassword(), client2.getPassword());
    }

    @Test
    public void testUserQuestionsCreation() throws JsonProcessingException {
        Client client = Client.getClientFromJSON(getDefaultRequestSpec().post("/users/post/MyNewUser?password=mypass").andReturn().getBody().asString());
        Category category = Category.getCategoryFromJSON(getDefaultRequestSpec().post("/categories/post/mynewcategory").andReturn().getBody().asString());
        Question question = Question.getQuestionFromJSON(getDefaultRequestSpec().post("/questions/post/"+category.getId()+"/Here is my question/Here is my answer/99?owner=" + client.getCid()).andReturn().getBody().asString());

        Response response = getDefaultRequestSpec().get("/users/getQuestions/" + client.getCid()).andReturn();
        assertEquals(200, response.statusCode());
        String test = response.getBody().asString();
        JSONArray questions = new JSONArray(response.getBody().asString());
        assertEquals(1, questions.length());
        assertEquals(question, Question.getQuestionFromJSON(questions.getJSONObject(0).toString()));
    }

    @Test
    public void testUserQuestionsDeletion() throws JsonProcessingException {
        Client client = Client.getClientFromJSON(getDefaultRequestSpec().post("/users/post/MyNewUser?password=mypass").andReturn().getBody().asString());
        Category category = Category.getCategoryFromJSON(getDefaultRequestSpec().post("/categories/post/mynewcategory").andReturn().getBody().asString());
        Question question = Question.getQuestionFromJSON(getDefaultRequestSpec().post("/questions/post/"+category.getId()+"/Here is my question/Here is my answer/99?owner=" + client.getCid()).andReturn().getBody().asString());
        assertEquals(200, getDefaultRequestSpec().put("/users/getbyid/" + client.getCid() + "/savequestioncorrect/" + question.getId()).andReturn().statusCode());
        ClientQuestionHistory questionHistory = ClientQuestionHistory.getQuestionHistoryFromJSON(getDefaultRequestSpec().get("/users/getbyid/" + client.getCid() + "/getquestionhistory/" + question.getId()).andReturn().getBody().asString());

        assertEquals(200, getDefaultRequestSpec().delete("/users/deletebyid/" + client.getCid()).statusCode());
        assertEquals(0, new JSONArray(getDefaultRequestSpec().get("/users/getall").andReturn().getBody().asString()).length());
        JSONArray test = new JSONArray(getDefaultRequestSpec().get("/questions").andReturn().getBody().asString());
        assertEquals(0, new JSONArray(getDefaultRequestSpec().get("/questions").andReturn().getBody().asString()).length());
    }

    @Test
    public void testTeamDeletion() throws JsonProcessingException {
        Client client = Client.getClientFromJSON(getDefaultRequestSpec().post("/users/post/MyNewUser?password=mypass").andReturn().getBody().asString());
        Team team = Team.getTeamFromJSON(getDefaultRequestSpec().post("teams/createNewTeam/My new team!?coachName=" + client.getUserName() + "&coachPassword=" + client.getPassword()).andReturn().getBody().asString());
        client = Client.getClientFromJSON(getDefaultRequestSpec().get("/users/login?username=" + client.getUserName() + "&password=" + client.getPassword()).andReturn().getBody().asString());
        assertFalse(client.getTeams().isEmpty());
        assertTrue(client.getTeams().contains(team));
        assertTrue(team.memberInTeam(client));
        assertEquals(client, team.getCoach());
        assertEquals(200, getDefaultRequestSpec().delete("/teams/deletebyid/" + team.getTid()).andReturn().statusCode());
        client = Client.getClientFromJSON(getDefaultRequestSpec().get("/users/login?username=" + client.getUserName() + "&password=" + client.getPassword()).andReturn().getBody().asString());
        assertTrue(client.getTeams().isEmpty());
        assertFalse(client.getTeams().contains(team));
    }

    private static RequestSpecification getDefaultRequestSpec() {
        return RestAssured.given().header("Content-Type", "text/plain").header("charset","utf-8");
    }

}