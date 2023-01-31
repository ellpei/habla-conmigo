package com.habla.controller;

import com.habla.response.GameSessionDTO;
import com.habla.service.SessionHandler;
import com.habla.exception.SessionNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;

@RestController
public class GameController {
    @Autowired
    SessionHandler sessionHandler;

    @GetMapping(path = "/", produces = "application/json")
    public String index() {
        return "Welcome to Habla Conmigo!";
    }

    @GetMapping(path = "/num-sessions", produces = "application/json")
    public int numSessions() {
        return sessionHandler.numActiveSessions();
    }

    @PostMapping(path = "/create-session", produces = "application/json")
    @ResponseBody
    public String createSession(@Valid @RequestBody UserDTO creator) {
        try {
            return sessionHandler.createSession(creator);
        } catch (InstantiationException exception) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "at capacity, sorry");
        }
    }

    @PostMapping(path = "/session/{id}/join", consumes = "application/json", produces = "application/json")
    @ResponseBody
    public GameSessionDTO joinSession(@PathVariable String id, @Valid @RequestBody UserDTO user) throws SessionNotFoundException {
        return sessionHandler.tryJoinSession(user, id);
    }

    @PostMapping(path = "/session/{id}/start", produces = "application/json")
    @ResponseBody
    public GameSessionDTO startGame(@PathVariable String id) throws SessionNotFoundException {
        return sessionHandler.startGame(id);
    }

    @PostMapping(path = "/session/{id}/mark-approved", produces = "application/json")
    @ResponseBody
    public GameSessionDTO approveWord(@PathVariable String id) {
        // TODO
        return null;
    }

    @PostMapping(path = "/session/{id}/mark-failed", produces = "application/json")
    @ResponseBody
    public GameSessionDTO failWord(@PathVariable String id) {
        return null;
    }

    @PostMapping(path = "/session/{id}/end", produces = "application/json")
    @ResponseBody
    public GameSessionDTO endGame(@PathVariable String id) {
        return sessionHandler.endGame(id);
    }

    @GetMapping(path = "/session/{id}", produces = "application/json")
    @ResponseBody
    public GameSessionDTO getSession(@PathVariable String id) throws SessionNotFoundException {
        System.out.println("Retreiving session with id " + id);
        return sessionHandler.retrieveSession(id);
    }

}
