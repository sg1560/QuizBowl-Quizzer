package com.QuizBowl.Session;

import com.QuizBowl.questions.CategoryController;
import com.QuizBowl.questions.QuestionController;
import com.QuizBowl.users.Client;
import com.QuizBowl.users.ClientController;
import com.QuizBowl.users.ClientQuestionController;
import com.QuizBowl.users.Team;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;

/**
 * StudySessionController is a singleton class that keeps track of all StudySessions currently in progress.
 * It manages searching for joinable study sessions.
 * Once a session is closed it can no longer be found in StudySessionController.
 */
public class StudySessionController {
    private static StudySessionController studySessionController;
    private final Hashtable<Team, HashSet<StudySession>> activeStudySession;
    private final Hashtable<StudySession, Team> studySessionTeams;
    private final HashSet<StudySession> soloStudySessions;

    private StudySessionController() {
        activeStudySession = new Hashtable<>();
        studySessionTeams = new Hashtable<>();
        soloStudySessions = new HashSet<>();
    }

    public static StudySessionController getInstance() {
        if (studySessionController == null) {
            studySessionController = new StudySessionController();
        }
        return studySessionController;
    }

    /**
     * Stores a new StudySession, so it can be accessed and joined.
     */
    public StudySession startNewSession(Team team, StudySessionType type, int numberOfQuestions, ClientController clientController, QuestionController questionController, CategoryController categoryController, ClientQuestionController clientQuestionController) {
        StudySession studySession;
        switch (type) {
            case Competitive -> studySession = new CompetitiveStudySession(type, clientController, questionController, categoryController, clientQuestionController, numberOfQuestions);
            case Solo -> studySession = new SoloStudySession(type, clientController, questionController, categoryController, clientQuestionController, numberOfQuestions);
            case Collaborative -> studySession = new CollaborativeStudySession(type, clientController, questionController, categoryController, clientQuestionController, numberOfQuestions);
            default -> studySession = new CollaborativeStudySession(type, clientController, questionController, categoryController, clientQuestionController, numberOfQuestions);
        }
        if (type == StudySessionType.Solo) {
            soloStudySessions.add(studySession);
        } else {
            if (!activeStudySession.containsKey(team)) {
                activeStudySession.put(team, new HashSet<StudySession>());
            }
            activeStudySession.get(team).add(studySession);
            studySessionTeams.put(studySession, team);
        }
        return studySession;
    }

    /**
     * Handles closing a study session
     */
    public void closeSession(StudySession studySession) {
        if (studySession.getStudySessionType() == StudySessionType.Solo) {
            soloStudySessions.remove(studySession);
        } else {
            Team team = studySessionTeams.remove(studySession);
            activeStudySession.get(team).remove(studySession);
            if (activeStudySession.get(team).isEmpty()) {
                activeStudySession.remove(team);
            }
        }
    }

    /**
     * @return a list of joinable sessions being hosted by the team.
     */
    public List<StudySession> getJoinableStudySessions(Client client, Team team, StudySessionType type) {
        if (team == null || !activeStudySession.containsKey(team)) return new ArrayList<>();
        return activeStudySession.get(team).stream().filter((s) -> s.isJoinable(client) && s.getStudySessionType() == type).toList();
    }
}
