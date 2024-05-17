package com.QuizBowl.Chat;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;

import com.QuizBowl.Session.StudySessionServer;
import com.QuizBowl.questions.CategoryController;
import com.QuizBowl.questions.QuestionController;
import com.QuizBowl.users.Team;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.QuizBowl.users.ClientController;
import com.QuizBowl.users.TeamController;
import com.QuizBowl.users.Client;
import org.springframework.web.bind.annotation.RequestParam;

@ServerEndpoint("/chat/{cid}/{teamId}")
@Component
public class ChatServer {

    static ClientController clientController;
    static TeamController teamController;

    private static Map <Session, Chat> sessionChatMap = new Hashtable < > ();
    private static Map <Long, Chat> teamChat = new Hashtable<>();
    /**
     * This method is called when a new WebSocket connection is established.
     *
     * @param session represents the WebSocket session for the connected team.
     * @param teamId specified in path parameter.
     */
    @OnOpen
    public void onOpen(Session session, @PathParam(value = "teamId") long teamId, @PathParam(value = "cid") long cid) throws IOException {
        Team t = teamController.getTeamById(teamId);
        Client c = clientController.getUserById(cid);
        if(!teamChat.containsKey(teamId)){
            Chat chat = new Chat(clientController, teamController, t);
            teamChat.put(teamId, chat);
        }
        Chat chat = teamChat.get(teamId);
        chat.addUser(session, c);
        sessionChatMap.put(session, chat);
    }

    /**
     * Handles incoming WebSocket messages from a client.
     *
     * @param session The WebSocket session representing the client's connection.
     * @param message The message received from the client.
     */
    @OnMessage
    public void onMessage(Session session, String message) throws IOException {
        sessionChatMap.get(session).onMessage(session, message);
    }

    /**
     * Handles the closure of a WebSocket connection.
     *
     *
     */
    @OnClose
    public void onClose(Session session) {
        sessionChatMap.get(session).onClose(session);
        sessionChatMap.remove(session);
    }
    public static void onChatEnd(Chat chat, Team team){
        teamChat.remove(team.getTid());
    }

    @OnError
    public void onError(Session session, Throwable throwable) throws Throwable {
        throw new RuntimeException("ClientError", throwable);
    }

    @Autowired
    public void setControllers(ClientController clientController, TeamController teamController) {
        ChatServer.clientController = clientController;
        ChatServer.teamController = teamController;
    }
}

