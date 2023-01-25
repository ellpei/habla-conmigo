package com.habla.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.habla.controller.User;
import com.habla.domain.gameplay.GameSession;
import com.habla.domain.gameplay.Player;
import com.habla.domain.util.InputHelper;
import org.apache.commons.text.RandomStringGenerator;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SessionHandler {
    private ConcurrentHashMap<String, GameSession> sessions;
    private int numActiveSessions;
    private int maxConcurrentSessions;

    public SessionHandler() {
        this.maxConcurrentSessions = 10;
        this.sessions = new ConcurrentHashMap<>();
        this.numActiveSessions = 0;
    }

    public String createSession(User creator) throws InstantiationException, JsonProcessingException {
        if (numActiveSessions >= maxConcurrentSessions) {
            throw new InstantiationException("Sorry, we are at capacity right now"); // at capacity
        }
        Player player = new Player(creator.getUsername(), InputHelper.parseLanguage(creator.getNativeLanguage()));
        GameSession newSession = new GameSession(player, creator.getNumDesiredWords());
        String sessionId = generateSessionId();
        sessions.put(sessionId, newSession);
        numActiveSessions++;

        ObjectMapper objectMapper = new ObjectMapper();
        System.out.println("Player " + objectMapper.writeValueAsString(player) + " created session " + sessionId + objectMapper.writeValueAsString(newSession));
        return sessionId;
    }

    public int numActiveSessions() {
        return this.numActiveSessions;
    }

    public Optional<GameSession> getSession(String id) {
        return Optional.ofNullable(sessions.get(id));
    }

    public Optional<GameSession> tryJoinSession(User user, String sessionId) {
        Player player = new Player(user.getUsername(),  InputHelper.parseLanguage(user.getNativeLanguage()));
        GameSession session = getSession(sessionId).orElseThrow(() -> new NoSuchElementException("Session not found"));
        session.setPlayer2(player);
        return Optional.ofNullable(session);
    }

    public Optional<GameSession> startSession(String sessionId) {
        GameSession session = getSession(sessionId).orElseThrow(() -> new NoSuchElementException("Session not found"));
        if (session.canStart()) {
            session.startGame();
        } else {
            throw new IllegalStateException("Not at ready state, cannot start game");
        }
        return Optional.empty(); // TODO
    }

    private String generateSessionId() {
        RandomStringGenerator generator = new RandomStringGenerator.Builder().withinRange('a', 'z').build();
        return generator.generate(5).toUpperCase();
    }

    public Optional<GameSession> startGame(String id) {
        // TODO
        return Optional.empty();
    }


}
