package com.QuizBowl;

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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class QuizBowlClientTests {
    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void resetDatabase() throws Exception {
        mockMvc.perform(delete("/users/deleteall"));
    }

    @Test
    void testEmptyGetAll() throws Exception {
        mockMvc.perform(get("/users/getall")).andExpectAll(
                status().isOk(),
                content().string("[]")
        );
    }

    @Test
    void testCreateClient() throws Exception {
        mockMvc.perform(post("/users/post/TestCreateClient?password=mypassword")).andExpectAll(
                status().isOk()
        );
    }

    @Test
    void testClientLogin() throws Exception {
        mockMvc.perform(post("/users/post/TestLoginClient?password=loginpassword")).andExpectAll(
                status().isOk()
        );
        String response = mockMvc.perform(get("/users/login?username=TestLoginClient&password=loginpassword")).andExpectAll(
                status().isOk()
        ).andReturn().getResponse().getContentAsString();

        Client loginClient = Client.getClientFromJSON(response);
        assertNotNull(loginClient);
        assertEquals("TestLoginClient", loginClient.getUserName());
        assertEquals("loginpassword", loginClient.getPassword());
    }

    @Test
    void testRenameClient() throws Exception {
        mockMvc.perform(post("/users/post/TestRenameClient?password=clientpassword")).andExpect(
                status().isOk()
        );
        String response = mockMvc.perform(put("/users/rename?username=TestRenameClient&password=clientpassword&newusername=NewClientName"))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        Client client = Client.getClientFromJSON(response);
        assertEquals("NewClientName", client.getUserName());

        response = mockMvc.perform(get("/users/login?username=NewClientName&password=clientpassword")).andExpectAll(
                status().isOk()
        ).andReturn().getResponse().getContentAsString();

        Client loginClient = Client.getClientFromJSON(response);
        assertEquals("NewClientName", loginClient.getUserName());
    }

    @Test
    void testChangeClientPassword() throws Exception {
        mockMvc.perform(post("/users/post/TestRenameClient?password=clientpassword")).andExpect(
                status().isOk()
        );
        String response = mockMvc.perform(put("/users/changepassword?username=TestRenameClient&password=clientpassword&newpassword=newclientpassword"))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        Client client = Client.getClientFromJSON(response);
        assertEquals("newclientpassword", client.getPassword());

        response = mockMvc.perform(get("/users/login?username=TestRenameClient&password=newclientpassword")).andExpectAll(
                status().isOk()
        ).andReturn().getResponse().getContentAsString();

        Client loginClient = Client.getClientFromJSON(response);
        assertEquals("newclientpassword", loginClient.getPassword());
    }

    @Test
    void testClientNotEquivalency() throws Exception {
        mockMvc.perform(post("/users/post/TestEqualClient1?password=clientpassword")).andExpect(
                status().isOk()
        );
        mockMvc.perform(post("/users/post/TestEqualClient2?password=clientpassword")).andExpect(
                status().isOk()
        );
        String response = mockMvc.perform(get("/users/login?username=TestEqualClient1&password=clientpassword"))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        Client client1 = Client.getClientFromJSON(response);
        response = mockMvc.perform(get("/users/login?username=TestEqualClient2&password=clientpassword"))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        Client client2 = Client.getClientFromJSON(response);
        assertNotEquals(client1, client2);
        assertNotEquals(client2, client1);
    }

    @Test
    void testClientEquivalency() throws Exception {
        mockMvc.perform(post("/users/post/TestEqualClient?password=clientpassword")).andExpect(
                status().isOk()
        );
        String response = mockMvc.perform(get("/users/login?username=TestEqualClient&password=clientpassword"))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        Client client1 = Client.getClientFromJSON(response);
        response = mockMvc.perform(get("/users/login?username=TestEqualClient&password=clientpassword"))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        Client client2 = Client.getClientFromJSON(response);
        assertEquals(client1, client2);
        assertEquals(client2, client1);
    }

    @Test
    void testClientPoints() throws Exception {
        Client client = Client.getClientFromJSON(mockMvc.perform(post("/users/post/TestCreateClient?password=mypassword")).andExpect(
                status().isOk()).andReturn().getResponse().getContentAsString());
        assertEquals(0, client.getTotalPoints());
        mockMvc.perform(put("/users/getbyid/" + client.getCid() + "/addpoints/1000")).andExpect(
                status().isOk());
        client = Client.getClientFromJSON(mockMvc.perform(get("/users/getbyid/" + client.getCid())).andExpect(
                status().isOk()).andReturn().getResponse().getContentAsString());
        assertEquals(1000, client.getTotalPoints());
    }

    @Test
    void testDeleteClient() throws Exception {
        mockMvc.perform(post("/users/post/TestDeleteClient?password=password")).andExpectAll(
                status().isOk()
        );
    }

    @Test
    void testDeleteAll() throws Exception {
        mockMvc.perform(post("/users/post/TestDeleteAll1?password=password")).andExpectAll(
                status().isOk()
        );
        mockMvc.perform(post("/users/post/TestDeleteAll2?password=password")).andExpectAll(
                status().isOk()
        );
        mockMvc.perform(post("/users/post/TestDeleteAll3?password=password")).andExpectAll(
                status().isOk()
        );

        String reponse = mockMvc.perform(get("/users/getall")).andExpectAll(
                status().isOk()
        ).andReturn().getResponse().getContentAsString();

        String deleteAllResponse = mockMvc.perform(delete("/users/deleteall"))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        assertTrue(deleteAllResponse.contains("TestDeleteAll1"));
        assertTrue(deleteAllResponse.contains("TestDeleteAll2"));
        assertTrue(deleteAllResponse.contains("TestDeleteAll3"));
        mockMvc.perform(get("/users/getall")).andExpectAll(
                status().isOk(),
                content().string("[]")
        );
    }
}
