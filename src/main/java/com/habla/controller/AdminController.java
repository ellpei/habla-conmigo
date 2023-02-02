package com.habla.controller;

import com.habla.service.SessionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("admin")
public class AdminController {
    @Autowired
    SessionHandler sessionHandler;

    @GetMapping(path = "/all-sessions", produces = "application/json")
    public List<String> getAllSessionIds() {
        return sessionHandler.getAllSessionIds();
    }

    @DeleteMapping(path = "/delete-all", produces = "application/json")
    public List<String> deleteAllSessionIds() {
        return sessionHandler.deleteAllSessions();
    }

    @DeleteMapping(path = "/delete/{id}", produces = "application/json")
    public void deleteSession(@PathVariable String id) {
        sessionHandler.deleteSession(id);
    }
}
