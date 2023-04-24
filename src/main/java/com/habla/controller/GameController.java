package com.habla.controller;

import com.habla.response.GameSessionDTO;
import com.habla.service.SessionHandler;
import com.habla.exception.SessionNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;

@RestController
@RequestMapping("session")
@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST})
public class GameController {
    private Logger LOG = LoggerFactory.getLogger(GameController.class);

    @Autowired
    SessionHandler sessionHandler;

    @GetMapping(path = "/")
    public String index() {
        return "Welcome to Habla Conmigo!";
    }

    @GetMapping(path = "/total", produces = "application/json")
    public int numSessions() {
        return sessionHandler.numActiveSessions();
    }

    @PostMapping(path = "/create", consumes = "application/json", produces = "application/json")
    @ResponseBody
    public String createSession(@Valid @RequestBody UserDTO creator) {
        LOG.info("Create session {}", creator);
        try {
            return sessionHandler.createSession(creator);
        } catch (InstantiationException exception) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "at capacity, sorry");
        }
    }

    @PostMapping(path = "/{id}/join", consumes = "application/json", produces = "application/json")
    @ResponseBody
    public GameSessionDTO joinSession(@PathVariable String id, @Valid @RequestBody UserDTO user) throws SessionNotFoundException {
        return sessionHandler.tryJoinSession(user, id);
    }

    @PostMapping(path = "/{id}/start", produces = "application/json")
    @ResponseBody
    public GameSessionDTO startGame(@PathVariable String id) throws SessionNotFoundException {
        return sessionHandler.startGame(id);
    }

    @PostMapping(path = "/{id}/approve", produces = "application/json")
    @ResponseBody
    public GameSessionDTO approveWord(@PathVariable String id, @Valid @RequestBody String approverUsername) {
        return sessionHandler.approveWord(id, approverUsername);
    }

    @PostMapping(path = "/{id}/fail", produces = "application/json")
    @ResponseBody
    public GameSessionDTO failWord(@PathVariable String id, @Valid @RequestBody String approverUsername) {
        return sessionHandler.failWord(id, approverUsername);
    }

    @PostMapping(path = "/{id}/end", produces = "application/json")
    @ResponseBody
    public GameSessionDTO endGame(@PathVariable String id) {
        return sessionHandler.endGame(id);
    }

    @GetMapping(path = "/{id}", produces = "application/json")
    @ResponseBody
    public GameSessionDTO getSession(@PathVariable String id) throws SessionNotFoundException {
        System.out.println("Retreiving session with id " + id);
        return sessionHandler.retrieveSession(id);
    }

}
