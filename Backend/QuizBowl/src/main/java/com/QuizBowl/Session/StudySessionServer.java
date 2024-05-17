package com.QuizBowl.Session;

import com.QuizBowl.questions.Category;
import com.QuizBowl.questions.CategoryController;
import com.QuizBowl.questions.QuestionController;
import com.QuizBowl.questions.UniversalCategory;
import com.QuizBowl.users.*;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;


@ServerEndpoint("/session/{studysessiontype}/{cid}/team/{tid}/size/{size}/category/{catid}")
@Component
public class StudySessionServer {
    static QuestionController questionController;

    static ClientController clientController;
    static TeamController teamController;
    static CategoryController categoryController;
    static ClientQuestionController clientQuestionController;
    /**
     * Keeps track of which session is associated with which Study Session
     */
    private static final Hashtable<Session, StudySession> clientSessionMap = new Hashtable<>();

    @OnOpen
    public void onOpen(Session session, @PathParam(value = "studysessiontype") String studySessionType, @PathParam(value="cid") long cid, @PathParam(value="tid") long tid, @PathParam(value="size") int size, @PathParam(value="catid") long catid) throws IOException {
        StudySessionType type;
        try {
            type = StudySessionType.valueOf(studySessionType);
        } catch (IllegalArgumentException e){
            session.getBasicRemote().sendText("Error, your path parameter studysessiontype either did not exist or was spelled wrong. Will default to collaborative session.");
            type = StudySessionType.Collaborative;
        }
        Client client = clientController.getUserById(cid);
        Category category;
        if (catid == 0) {
            category = UniversalCategory.getInstance();
        } else {
            category = categoryController.getCategoryById(catid);
        }

        Team team = null;
        if (StudySessionType.valueOf(studySessionType) != StudySessionType.Solo) {
            team = teamController.getTeamById(tid);
            if (team == null) {
                session.getBasicRemote().sendText("There was no team associated with the team id given");
                return;
            }
            if (!team.memberInTeam(client)) {
                session.getBasicRemote().sendText("Study Session request failed, client was not a member of the team");
            }
        }
        if (client == null) {
            session.getBasicRemote().sendText("There was no clint associated with the client id given");
            return;
        }

        Optional<StudySession> joinableSession = StudySessionController.getInstance().getJoinableStudySessions(client, team, type).stream().findFirst();
        StudySession studySession;
        StudySessionType finalType = type;
        Team finalTeam = team;
        studySession = joinableSession.orElseGet(() -> StudySessionController.getInstance().startNewSession(finalTeam, finalType, size, clientController, questionController, categoryController, clientQuestionController));
        if (studySession.addMember(client, session, category)) {
            clientSessionMap.put(session, studySession);
        }
    }

    @OnClose
    public void onClose(Session session) {
        if (clientSessionMap.containsKey(session)) {
            clientSessionMap.get(session).onClose(session);
            clientSessionMap.remove(session);
        }
    }

    @OnMessage
    public void onMessage(Session session, String message) {
        if (!clientSessionMap.containsKey(session)) return;
        clientSessionMap.get(session).onMessage(session, message);
    }

    @OnError
    public void onError(Session session, Throwable throwable) throws Throwable {
        onClose(session);
        throw new RuntimeException("ClientError", throwable);
    }

    @Autowired
    public void setControllers(QuestionController questionController, ClientController clientController, TeamController teamController, CategoryController categoryController, ClientQuestionController clientQuestionController) {
        StudySessionServer.questionController = questionController;
        StudySessionServer.clientController = clientController;
        StudySessionServer.teamController = teamController;
        StudySessionServer.categoryController = categoryController;
        StudySessionServer.clientQuestionController = clientQuestionController;
    }
}
