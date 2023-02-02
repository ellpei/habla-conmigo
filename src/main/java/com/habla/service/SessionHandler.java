package com.habla.service;

import com.habla.controller.UserDTO;
import com.habla.domain.gameplay.GameSession;
import com.habla.domain.gameplay.Player;
import com.habla.exception.SessionNotFoundException;
import com.habla.response.GameSessionDTO;
import org.apache.commons.text.RandomStringGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SessionHandler {
    private final ConcurrentHashMap<String, GameSession> sessions;
    private int maxConcurrentSessions;

    private final DictionaryLoaderService dictionaryLoaderService;

    @Autowired
    public SessionHandler(DictionaryLoaderService dictionaryLoaderService) {
        this.maxConcurrentSessions = 10;
        this.sessions = new ConcurrentHashMap<>();
        this.dictionaryLoaderService = dictionaryLoaderService;
        System.out.println("init session handler");
    }
    public SessionHandler(DictionaryLoaderService dictionaryLoaderService, int maxConcurrentSessions) {
        this(dictionaryLoaderService);
        this.maxConcurrentSessions = maxConcurrentSessions;
    }

    public String createSession(UserDTO creator) throws InstantiationException {
        if (sessions.size() >= maxConcurrentSessions) {
            throw new InstantiationException("Sorry, we are at capacity right now"); // at capacity
        }
        return createAndStoreSession(creator);
    }

    private String createAndStoreSession(UserDTO creator) {
        GameSession newSession = new GameSession(new Player(creator), creator.getNumDesiredWords());
        String sessionId = generateSessionId();
        sessions.put(sessionId, newSession);
        return sessionId;
    }

    public int numActiveSessions() {
        return sessions.size();
    }

    private GameSession getSession(String id) throws SessionNotFoundException {
        return Optional.ofNullable(sessions.get(id))
                .orElseThrow(() -> new SessionNotFoundException("Session with id " + id + " could not be found"));
    }

    public GameSessionDTO retrieveSession(String id) throws SessionNotFoundException {
        return getSession(id).toDto();
    }

    public GameSessionDTO tryJoinSession(UserDTO user, String sessionId) throws SessionNotFoundException {
        Player player = new Player(user);
        return getSession(sessionId).tryJoinSession(player).toDto();
    }

    public GameSessionDTO startGame(String sessionId) throws SessionNotFoundException {
        return getSession(sessionId).startGame(dictionaryLoaderService).toDto();
    }

    public GameSessionDTO endGame(String sessionId) throws SessionNotFoundException {
        GameSession endedSession = getSession(sessionId).endGame();
        sessions.remove(sessionId);
        return endedSession.toDto();
    }

    public GameSessionDTO approveWord(String sessionId, String approverUsername) {
        return getSession(sessionId).approveWord(approverUsername).toDto();
    }

    public GameSessionDTO failWord(String sessionId, String approverUsername) {
        return getSession(sessionId).failWord(approverUsername).toDto();
    }

    private String generateSessionId() {
        RandomStringGenerator generator = new RandomStringGenerator.Builder().withinRange('a', 'z').build();
        return generator.generate(5).toUpperCase();
    }

    public List<String> getAllSessionIds() {
        return sessions.keySet().stream().toList();
    }

    public List<String> deleteAllSessions() {
        List<String> sessionIds = getAllSessionIds();
        sessions.clear();
        return sessionIds;
    }

    public void deleteSession(String id) {
        if (sessions.remove(id) == null ) {
            throw new SessionNotFoundException("Session with id " + id + " not found, cannot delete");
        }
    }

}
