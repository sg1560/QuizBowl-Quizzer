package com.QuizBowl.Session;

import com.QuizBowl.questions.*;
import com.QuizBowl.users.Client;
import com.QuizBowl.users.ClientController;
import com.QuizBowl.users.ClientQuestionController;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.websocket.Session;
import org.antlr.v4.runtime.misc.Pair;
import org.json.JSONArray;

import java.io.IOException;
import java.util.*;
import java.util.function.Function;

public class CompetitiveStudySession extends StudySession {
    private final ClientController clientController;
    private final QuestionController questionController;
    private final CategoryController categoryController;
    private final ClientQuestionController clientQuestionController;

    private HashMap<Client, Session> clientSessionHashMap;
    private HashMap<Session, Client> sessionClientHashMap;
    private HashMap<Client, Long> clientPoints;
    private HashMap<Client, Pair<Integer, Boolean>> clientState;
    private HashMap<Client, List<Question>> clientQuestions;
    private HashMap<Client, Category> clientQuestionCategories;

    private long numberOfQuestions;

    public CompetitiveStudySession(StudySessionType studySessionType, ClientController clientController, QuestionController questionController, CategoryController categoryController, ClientQuestionController clientQuestionController, long numberOfQuestions) {
        super(studySessionType);
        this.clientController = clientController;
        this.questionController = questionController;
        this.categoryController = categoryController;
        this.clientQuestionController = clientQuestionController;
        clientSessionHashMap = new HashMap<>();
        sessionClientHashMap = new HashMap<>();
        clientQuestionCategories = new HashMap<>();
        clientPoints = new HashMap<>();
        this.numberOfQuestions = numberOfQuestions;
    }

    private void SetupQuestions() {
        clientState = new HashMap<>();
        clientQuestions = new HashMap<>();
        for (Client client : clientSessionHashMap.keySet()) {
            clientState.put(client, new Pair<>(0,false));
            List<Question> questions = new ArrayList<>(clientQuestionCategories.get(client).getQuestionSet());

            // Returns the priority of the question
            // This is based off of how many people have gotten it right
            // A lower number means a higher priority
            Function<Question, Integer> questionPriority = (Question q) -> {
                ClientQuestionHistory questionHistory = clientQuestionController.getUserQuestionHistory(client.getCid(), q.getId());
                if (questionHistory == null) return 0;
                return questionHistory.getIncorrectAnswers() - questionHistory.getCorrectAnswers();
            };

            // Need an arraylist so that we can shuffle it later.
            // Sorts questions based on their priority, which is how much they have been solved previously.
            questions = new ArrayList<Question>(questions.stream()
                    .map((q) -> new Pair<Question, Integer>(q, questionPriority.apply(q)))
                    .sorted(Comparator.comparing(Q -> Q.b))
                    .map(E -> E.a).toList());

            // Remove questions until we have the desired amount
            questions = questions.subList(0, Math.min(questions.size(), (int) numberOfQuestions));

            // Randomise the order of the questions
            Collections.shuffle(questions);
            clientQuestions.put(client, questions);
        }
    }

    @Override
    public boolean addMember(Client client, Session session, Category category) {
        if (getSessionState() == StudySessionState.Starting) {
            try {
                sendMessageToAll(new JSONArray("[{\"type\":\"newmember\", \"user\":" + client.toJSON() + "}]"));
                clientSessionHashMap.put(client, session);
                sessionClientHashMap.put(session, client);
                clientQuestionCategories.put(client, category);
                clientPoints.put(client, 0L);
                clientSessionHashMap.keySet().forEach((c) -> {
                    try {
                        sendMessage(client, new JSONArray("[{\"type\":\"newmember\", \"user\":" + c.toJSON() + "}]") );
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                });
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            return true;
        } else {
            try {
                session.getBasicRemote().sendText("Failed to join because the session has already starting.");
                session.close();
            } catch (IOException e) {
                // Swallow because it was bad input anyway
            }
            return false;
        }
    }

    @Override
    public void startStudySession() {
        super.startStudySession();
        SetupQuestions();
        // Send a JSON array to each client
        clientQuestions.forEach((c, ql) -> {
            // Each JSON array needs to be populated with the list of questions
            sendMessage(c, new JSONArray(ql.stream().map((q) -> {
                try {
                    // Each question JSON object also needs a type:question for the frontend to check
                    return q.toJSON().put("type", "question");
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }).toList()).put(StudySession.onStartStudySessionMessage));
        });
    }

    private String getQuestionsAsJSONString(List<Question> questions) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            return objectMapper.writeValueAsString(questions);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void endPracticeSession() {
        super.endPracticeSession();
        sendMessageToAll(new JSONArray("[" + StudySession.onEndSessionMessage + "]"));
    }

    @Override
    public void sendMessage(Client client, JSONArray message) {
        try {
            clientSessionHashMap.get(client).getBasicRemote().sendText(message.toString());
        } catch (IOException e) {
            System.out.println("Sending a message to: " + client.getUserName() + " failed with message: " + message);
        }
    }

    private void updateCardState(Client client) {
        String side = "question";
        if (clientState.get(client).b) side = "answer";
        sendMessageToAll(new JSONArray("[{\"type\":\"cardstate\",\"question\":\"" + clientState.get(client).a + "\", \"side\":\"" + side + "\", \"user\":" + client.getCid() + ", \"progress\":" + clientState.get(client).a + ", \"max\":" + clientQuestions.get(client).size() + "}]"));
    }

    @Override
    public void sendMessageToAll(JSONArray message) {
        clientSessionHashMap.entrySet().stream()
                .filter((c) -> c.getValue().isOpen())
                .map(Map.Entry::getKey)
                .forEach((c) -> sendMessage(c, message));
    }

    @Override
    public void onMessage(Client client, String message) {
        String[] messageComponents = message.split("/");
        switch (messageComponents[0]) {
            case "ondisconnect":
                Session session = clientSessionHashMap.get(client);
                sessionClientHashMap.remove(session);
                clientSessionHashMap.remove(client);
                if (clientSessionHashMap.isEmpty()) {
                    new StudySessionServer().onClose(session);
                }
            case "close":
                if (getSessionState() != StudySessionState.closed)
                    closeSession();
                break;
            case "givePoints", "addPoints": // addPoints/{points}
                addPoints(Long.parseLong(messageComponents[1]));
                break;
            case "points", "getPoints": // getPoints
                sendMessage(client, new JSONArray("[{\"points\":" + getTotalSessionPoints() + "}]"));
                break;
            case "getNumberOfUsers", "users": // getNumberOfUsers
                sendMessage(client, new JSONArray("[{\"numberOfUsers\":" + clientSessionHashMap.size() + "}]"));
                break;

            case "startStudySession", "start": // startStudySession
                if (getSessionState() == StudySessionState.Starting) {
                    startStudySession();
                } else {
                    sendMessage(client, new JSONArray("[{\"warning\":The session has already started}]"));
                }
                break;
            case "question": // question/{qid}/{correct||incorrect}
                if (!canInteractWithQuestion(client, Long.parseLong(messageComponents[1]))) {
                    sendMessage(client, new JSONArray("[{\"warning\":could not processes action}]"));
                    break;
                }
                Question question = questionController.getQuestionById(Long.parseLong(messageComponents[1]));
                boolean isCorrect = messageComponents[2].equals("correct");
                handleQuestionRightOrWrong(client, question, isCorrect);
                break;
            case "flipCard", "flip" : // flipCard/{qid}/{question||answer}
                Pair<Integer, Boolean> state = clientState.get(client);
                if (!canInteractWithQuestion(client, Long.parseLong(messageComponents[1]))) {
                    sendMessage(client, new JSONArray("[{\"warning\":could not processes action}]"));
                    break;
                } else if ((messageComponents[2].equals("question") && !state.b)
                        || (!messageComponents[2].equals("question") && state.b)) {
                    sendMessage(client, new JSONArray("[{\"warning\":could not processes action}]"));
                    break;
                }
                clientState.put(client, new Pair<>(state.a, !state.b));
                updateCardState(client);
                break;

            default:
                sendMessage(client, new JSONArray("[{\"warning\":could not understand the message \"" + message + "\"}]"));
                break;
        }
    }

    private boolean canInteractWithQuestion(Client client, long qid) {
        return getSessionState() == StudySessionState.Running && getCurrentQuestion(client).getId() == qid;
    }

    public Question getCurrentQuestion(Client client) {
        return clientQuestions.get(client).get(clientState.get(client).a);
    }

    private void handleQuestionRightOrWrong(Client client, Question question, boolean isCorrect) {
        if (isCorrect) {
            clientQuestionController.addUserQuestionCorrect(client.getCid(), question.getId());
            addPoints(question.getPoints());
        } else {
            clientQuestionController.addUserQuestionIncorrect(client.getCid(), question.getId());
        }
        int clientQuestion = clientState.get(client).a;
        clientState.put(client, new Pair<>(clientQuestion + 1, false));
        sendMessageToAll(new JSONArray("[{\"type\":\"userprogress\", \"user\":" + client.getCid() + " , \"progress\":" + clientState.get(client).a + ", \"max\":" + clientQuestions.get(client).size() + "}]"));
        if (clientQuestion + 1 >= clientQuestions.get(client).size()) {
            if (clientSessionHashMap.keySet().stream().allMatch((c) -> clientState.get(c).a >= clientQuestions.get(c).size())) {
                endPracticeSession();
            } else {
                sendMessage(client, new JSONArray("[{\"type\":\"finished\"}]"));
            }
        } else {
            updateCardState(client);
        }
    }

    private int getClientProgress(Client client) {
        return clientState.get(client).a / clientQuestions.get(client).size();
    }

    @Override
    public void onClose(Session session) {
        if (!session.isOpen()) return;
        super.onClose(session);
        Client client = sessionClientHashMap.get(session);
        clientSessionHashMap.remove(client);
        sessionClientHashMap.remove(session);
        if (clientSessionHashMap.isEmpty()) {
            closeSession();
        }
    }

    @Override
    public void closeSession() {
        if (getSessionState() == StudySessionState.closed) return;
        super.closeSession();
        clientSessionHashMap.forEach((c, s) -> { if (s.isOpen()) sendMessage(c, new JSONArray("[" + StudySession.onCloseSessionMessage + "]")); });
        clientSessionHashMap.values().stream().toList().forEach((s) -> { // Needs to be a list to prevent concurrent modification exception apparently
            try {
                s.close();
            } catch (IOException e) {
                // Swallow silently, we want to close all sessions
            }
        });
    }

    @Override
    public Client getClientFromSession(Session session) {
        return sessionClientHashMap.get(session);
    }
}
