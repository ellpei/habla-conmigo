package com.habla.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.habla.response.GameSessionDTO;
import com.habla.controller.UserDTO;
import com.habla.exception.SessionNotFoundException;
import com.habla.domain.gameplay.GameSession;
import com.habla.domain.gameplay.Player;
import com.habla.domain.util.InputHelper;
import org.apache.commons.text.RandomStringGenerator;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SessionHandler {
    private final ConcurrentHashMap<String, GameSession> sessions;
    private final int maxConcurrentSessions;

    public SessionHandler() {
        this.maxConcurrentSessions = 10;
        this.sessions = new ConcurrentHashMap<>();
        System.out.println("init session handler");
    }

    public String createSession(UserDTO creator) throws InstantiationException, JsonProcessingException {
        if (sessions.size() >= maxConcurrentSessions) {
            throw new InstantiationException("Sorry, we are at capacity right now"); // at capacity
        }
        return createAndStoreSession(creator);
    }

    private String createAndStoreSession(UserDTO creator) {
        GameSession newSession = new GameSession(createPlayer(creator), creator.getNumDesiredWords());
        String sessionId = generateSessionId();
        sessions.put(sessionId, newSession);
        return sessionId;
    }

    private Player createPlayer(UserDTO user) {
        assert(!user.getUsername().isBlank());
        assert(!user.getNativeLanguage().isBlank());
        return new Player(user.getUsername(), InputHelper.parseLanguage(user.getNativeLanguage()));
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
        Player player = new Player(user.getUsername(), InputHelper.parseLanguage(user.getNativeLanguage()));
        return getSession(sessionId).tryJoinSession(player).toDto();
    }

    public GameSessionDTO startGame(String sessionId) throws SessionNotFoundException {
        return getSession(sessionId).startGame().toDto();
    }

    private String generateSessionId() {
        RandomStringGenerator generator = new RandomStringGenerator.Builder().withinRange('a', 'z').build();
        return generator.generate(5).toUpperCase();
    }

}