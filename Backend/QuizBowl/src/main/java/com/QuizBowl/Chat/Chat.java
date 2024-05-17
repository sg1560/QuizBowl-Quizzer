package com.QuizBowl.Chat;

import com.QuizBowl.users.*;
import com.fasterxml.jackson.databind.util.JSONPObject;
import com.mysql.cj.xdevapi.JsonArray;
import jakarta.websocket.Session;
import org.antlr.v4.runtime.misc.Pair;
import org.apache.catalina.filters.RemoteIpFilter;
import org.apache.tomcat.Jar;
import org.h2.util.json.JSONArray;
import org.h2.util.json.JSONObject;

import java.io.IOException;
import java.io.PipedInputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public class Chat {
    ClientController clientController;
    TeamController teamController;

    UserRepository clientRepository;

    private Team team;
    List<Pair<Client, String>> messageLog = new ArrayList<>();

    private Map<Session, Client> sessionClientMap = new Hashtable<>();
    private Map<Client, Session> clientSessionMap = new Hashtable<>();

    public Chat(ClientController clientController, TeamController teamController, Team team) {
        this.clientController = clientController;
        this.teamController = teamController;
        this.team = team;
    }

    public void onMessage(Session session, String message) throws IOException {
        if (message.equals("ondisconnect")) {
            onClose(session);
        } else {
            Client client = sessionClientMap.get(session);
            messageLog.add(new Pair<>(client, message));
            updateClients(client, message);
        }
    }

    public void updateClients(Client client, String message) throws IOException {
        for (Session session : sessionClientMap.keySet()) {

            session.getBasicRemote().sendText("[{\"type\":\"newmessage\", \"client\":" + client.getCid() + ", \"text\":\"" + message + "\"}]");
        }
    }

    public void addUser(Session session, Client client) throws IOException {
        sessionClientMap.put(session, client);
        clientSessionMap.put(client, session);
        String catchupMessage = "[";
        ArrayList<String> messages = new ArrayList<>();
        for (var chatMember : clientSessionMap.keySet()) {
            messages.add(chatMember.toJSON());
        }
        messages.add("{\"type\":\"newmessage\", \"client\":" + client.getCid() + ", \"text\":\"This is the start of the group chat.\"}");
        for (var message : messageLog) {
            messages.add("{\"type\":\"newmessage\", \"client\":" + message.a.getCid() + ", \"text\":\"" + message.b + "\"}");
        }
        catchupMessage += String.join(",", messages);
        catchupMessage += "]";
        session.getBasicRemote().sendText(catchupMessage);
        for (var chatMember : clientSessionMap.keySet()) {
            if (chatMember == client) continue;
            clientSessionMap.get(chatMember).getBasicRemote().sendText("[" + client.toJSON() + "]");
        }
    }

    public void onClose(Session session) {
        Client client = sessionClientMap.get(session);
        sessionClientMap.remove(session);
        clientSessionMap.remove(client);

        for (var chatMember : clientSessionMap.keySet()) {
            try {
                clientSessionMap.get(chatMember).getBasicRemote().sendText("[{\"type\":\"leftchat\", \"client\":" + client.toJSON() + "}]");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        if(sessionClientMap.isEmpty()){
            ChatServer.onChatEnd(this, team);
        }
    }
}
