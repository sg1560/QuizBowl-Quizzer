package com.QuizBowl.Session;

import com.QuizBowl.questions.Category;
import com.QuizBowl.users.Client;
import jakarta.websocket.Session;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Handles the interface for persistent back and forth messaging between clients and the database.
 * Each StudySession has 4 phases: Starting, Running, Ended and Closed.
 */
public abstract class StudySession {
    public static final JSONObject onEndSessionMessage = new JSONObject("{\"type\":\"sessionended\"}");
    public static final JSONObject onCloseSessionMessage = new JSONObject("{\"type\":\"sessionclosed\"}");
    public static final JSONObject onStartStudySessionMessage = new JSONObject("{\"type\":\"start\"}");
    private StudySessionState sessionState;
    private long sessionPoints;
    private StudySessionType studySessionType;

    public StudySession(StudySessionType studySessionType) {
        sessionState = StudySessionState.Starting;
        sessionPoints = 0;
        this.studySessionType = studySessionType;
    }

    /**
     * Begins the study session.
     */
    public void startStudySession() {
        sessionState = StudySessionState.Running;
    }

    /**
     * Ends the practice session.
     * Note that the connections are still open until the study session is closed.
     */
    public void endPracticeSession() {
        sessionState = StudySessionState.Ended;
    }

    /**
     * @return returns true if the client is allowed to join the session, false otherwise
     */
    public boolean isJoinable(Client client) {
        return sessionState == StudySessionState.Starting;
    }

    /**
     * Adds a member to this session
     * @return true if the member was added, false and closes the session otherwise
     */
    public abstract boolean addMember(Client client, Session session, Category category);

    /**
     * Sends a message to the Client
     */
    public abstract void sendMessage(Client client, JSONArray message);

    /**
     * Sends a message to all clients
     */

    public abstract void sendMessageToAll(JSONArray message);

    /**
     * Handles receiving a message from a specific client.
     */
    public abstract void onMessage(Client client, String message);

    /**
     * Handles recieving a message from a specific client.
     * Uses Session to find which client sent the message.
     */
    public void onMessage(Session session, String message) {
        onMessage(getClientFromSession(session), message);
    }

    /**
     * Closes the session of the client given.
     * In the case that this was the last client, closes the entire session.
     */
    public void onClose(Session session) {
        try {
            session.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Closes all client sessions and removes the session from StudySessionController.
     * No more messages may be sent after the session is closed.
     */
    public void closeSession() {
        sessionState = StudySessionState.closed;
        StudySessionController.getInstance().closeSession(this);
    }

    /**
     * Adds points to the StudySession but only if the session has started and hasn't ended.
     * The points are not associated with any client.
     * @param points the amount of points to add
     */
    public void addPoints(long points) {
        if (sessionState == StudySessionState.Running)
            sessionPoints += points;
    }

    /**
     * @return the total amount of points gotten by all clients in the session
     */
    public long getTotalSessionPoints() {
        return sessionPoints;
    }

    /**
     * @return the type of the study session.
     */
    public StudySessionType getStudySessionType() {
        return studySessionType;
    }

    /**
     *
     * @return the current session state. See StudySessionState.java.
     */
    public StudySessionState getSessionState() {
        return sessionState;
    }

    /**
     * @param session the session of the client to get
     * @return the client associated with the given session.
     */
    public abstract Client getClientFromSession(Session session);
}

