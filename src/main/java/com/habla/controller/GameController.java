package com.habla.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.habla.domain.SessionHandler;
import com.habla.domain.gameplay.GameSession;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.annotation.Resource;
import javax.validation.Valid;

@RestController
public class GameController {
    @Resource
    SessionHandler sessionHandler;

    @GetMapping("/")
    public String index() {
        return "Welcome to Habla Conmigo!";
    }

    @GetMapping("/num-sessions")
    public int numSessions(@Qualifier SessionHandler sessionHandler) {
        return sessionHandler.numActiveSessions();
    }

    @PostMapping("/create-session")
    @ResponseBody
    public String createSession(@RequestBody User creator) {
        try {
            return sessionHandler.createSession(creator);
        } catch (InstantiationException exception) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "at capacity, sorry");
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/join-session/{id}")
    @ResponseBody
    public GameSession joinSession(@PathVariable String id, @Valid @RequestBody User user) {
        // TODO hndle this better, not found, not allowed, etc.
        return sessionHandler.tryJoinSession(user, id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "entity not found"));
    }

    @PostMapping("/session/{id}/start")
    @ResponseBody
    public GameSession startGame(@PathVariable String id) {
        // TODO hndle this better, not found, not allowed, etc.
        return sessionHandler.startGame(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "entity not found"));
    }

    @PostMapping("/session/{id}/mark-approved")
    @ResponseBody
    public GameSession approveWord(@PathVariable String id) {
        // TODO
        return null;
    }

    @PostMapping("/session/{id}/mark-failed")
    @ResponseBody
    public GameSession failWord(@PathVariable String id) {
        return null;
    }

    @PostMapping("/session/{id}/end-game")
    @ResponseBody
    public GameSession endGame(@PathVariable String id) {
        return null;
    }

    @GetMapping("/session/{id}")
    @ResponseBody
    public GameSession getSession(@PathVariable String id){
        System.out.println("Retreiving session with id " + id);
        // TODO break out into a controller advice later
        return sessionHandler.getSession(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "entity not found"));
    }

}
