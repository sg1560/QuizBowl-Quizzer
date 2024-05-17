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

public class SoloStudySession extends StudySession {
    private final ClientController clientController;
    private final QuestionController questionController;
    private final CategoryController categoryController;
    private final ClientQuestionController clientQuestionController;

    private Client client;
    private Session session;
    private long points;
    private List<Question> questions;
    private int currentQuestionIndex;
    private boolean cardOrientation;
    private Category category;
    private long numberOfQuestions;

    public SoloStudySession(StudySessionType studySessionType, ClientController clientController, QuestionController questionController, CategoryController categoryController, ClientQuestionController clientQuestionController, long numberOfQuestions) {
        super(studySessionType);
        this.clientController = clientController;
        this.questionController = questionController;
        this.categoryController = categoryController;
        this.clientQuestionController = clientQuestionController;
        this.numberOfQuestions = numberOfQuestions;
        points = 0;
        this.client = null;
        this.session = null;
    }

    private void SetupQuestions() {
        currentQuestionIndex = 0;
        cardOrientation = false;
        questions = new ArrayList<>(categoryController.getQuestionsInCategory(category.getId(),Optional.empty()));

        // Returns the priority of the question
        // This is based off of how many people have gotten it right
        // A lower number means a higher priority
        Function<Question, Long> questionPriority = (Question q) -> {
            long priority = 0;
            ClientQuestionHistory questionHistory = clientQuestionController.getUserQuestionHistory(client.getCid(), q.getId());
            if (questionHistory == null || questionHistory.getIncorrectAnswers() >= questionHistory.getCorrectAnswers()) {
                priority++;
            }
            return priority;
        };

        // Need an arraylist so that we can shuffle it later.
        // Sorts questions based on their priority, which is how much they have been solved previously.
        questions = new ArrayList<Question>(questions.stream()
                .map((q) -> new Pair<Question, Long>(q, questionPriority.apply(q)))
                .sorted(Comparator.comparing(Q -> Q.b))
                .map(E -> E.a).toList());

        // Remove questions until we have the desired amount
        questions = questions.subList(0, Math.min(questions.size(), (int) numberOfQuestions));

        // Randomise the order of the questions
        Collections.shuffle(questions);
    }

    @Override
    public boolean addMember(Client client, Session session, Category category) {
        // Only one player allowed
        if (getSessionState() == StudySessionState.Starting && this.client == null) {
            this.client = client;
            this.session = session;
            this.category = category;
            return true;
        }
        return false;
    }

    @Override
    public boolean isJoinable(Client client) {
        return super.isJoinable(client) && this.client == null;
    }

    @Override
    public void startStudySession() {
        super.startStudySession();
        SetupQuestions();

        sendMessageToAll(
                new JSONArray(questions.stream().map((q) -> {
                    try {
                        // Each question JSON object also needs a type:question for the frontend to check
                        return q.toJSON().put("type", "question");
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                }).toList()).put(StudySession.onStartStudySessionMessage)
        );
    }

    private String getQuestionsAsJSONString() {
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
            session.getBasicRemote().sendText(message.toString());
        } catch (IOException e) {
            System.out.println("Sending a message to: " + client.getUserName() + " failed with message: " + message);
        }
    }

    private void updateCardState(Client reason) {
        String side = "question";
        if (cardOrientation) side = "Answer";
        sendMessageToAll(new JSONArray("[{\"type\":\"updatecardstate\",\"questionindex\":\"" + currentQuestionIndex + "\",\"side\":\"" + side + "\", \"user\":" + reason.getCid() + "}]"));
    }

    @Override
    public void sendMessageToAll(JSONArray message) {
        sendMessage(client, message);
    }

    @Override
    public void onMessage(Client client, String message) {
        String[] messageComponents = message.split("/");
        switch (messageComponents[0]) {
            case "ondisconnect":
                new StudySessionServer().onClose(session);
            case "close":
                if (getSessionState() != StudySessionState.closed)
                    closeSession();
                break;
            case "givePoints", "addPoints": // addPoints/{points}
                addPoints(Long.parseLong(messageComponents[1]));
                break;
            case "points", "getPoints": // getPoints
                sendMessage(client, new JSONArray("[{\"type\":\"getpoints\",\"points\":" + getTotalSessionPoints() + "}]"));
                break;
            case "getNumberOfUsers", "users": // getNumberOfUsers
                sendMessage(client, new JSONArray("[{\"type\":\"numberofusers\",\"numberOfUsers\":" + 1 + "}]"));
                break;

            case "startStudySession", "start": // startStudySession
                if (getSessionState() == StudySessionState.Starting) {
                    startStudySession();
                } else {
                    sendMessage(client, new JSONArray("[{\"type\":\"warning\",\"warning\":The session has already started}]"));
                }
                break;
            case "question": // question/{qid}/{correct||incorrect}
                if (!canInteractWithQuestion(Long.parseLong(messageComponents[1]))) {
                    sendMessage(client, new JSONArray("[{\"type\":\"warning\",\"warning\":could not processes action}]"));
                    break;
                }
                Question question = questionController.getQuestionById(Long.parseLong(messageComponents[1]));
                boolean isCorrect = messageComponents[2].equals("correct");
                handleQuestionRightOrWrong(client, question, isCorrect);
                break;
            case "flipCard", "flip": // flipCard/{qid}/{question||answer}
                if (!canInteractWithQuestion(Long.parseLong(messageComponents[1]))) {
                    sendMessage(client, new JSONArray("[{\"type\":\"warning\",\"warning\":could not processes action}]"));
                    break;
                } else if ((messageComponents[2].equals("question") && !cardOrientation)
                        || (!messageComponents[2].equals("question") && cardOrientation)) {
                    sendMessage(client, new JSONArray("[{\"type\":\"warning\",\"warning\":could not processes action}]"));
                    break;
                }
                cardOrientation = !cardOrientation;
                updateCardState(client);
                break;

            default:
                sendMessage(client, new JSONArray("[{\"type\":\"warning\",\"warning\":could not understand the message \"" + message + "\"}]"));
                break;
        }
    }

    private boolean canInteractWithQuestion(long qid) {
        return getSessionState() == StudySessionState.Running && getCurrentQuestion().getId() == qid;
    }

    public Question getCurrentQuestion() {
        return questions.get(currentQuestionIndex);
    }

    private void handleQuestionRightOrWrong(Client client, Question question, boolean isCorrect) {
        if (isCorrect) {
            clientQuestionController.addUserQuestionCorrect(client.getCid(), question.getId());
            addPoints(question.getPoints());
        } else {
            clientQuestionController.addUserQuestionIncorrect(client.getCid(), question.getId());
        }
        currentQuestionIndex++;
        cardOrientation = false;
        if (currentQuestionIndex >= questions.size()) {
            endPracticeSession();
        } else {
            updateCardState(client);
        }
    }

    @Override
    public void onClose(Session session) {
        if (!session.isOpen()) return;
        super.onClose(session);
        closeSession();
    }

    @Override
    public void closeSession() {
        if (getSessionState() == StudySessionState.closed) return;
        super.closeSession();
    }

    @Override
    public Client getClientFromSession(Session session) {
        return client;
    }
}
