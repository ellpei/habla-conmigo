package com.habla.controller;

import com.habla.response.GameSessionDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;


@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GameControllerLifecycleTest {
    @Value(value="${local.server.port}")
    private int port;

    private final String basePath = "http://localhost:";

    private static final String CREATE_SESSION = "/create-session";
    private static final String NUM_SESSIONS = "/num-sessions";
    private static final String SESSION = "/session";
    private static final String JOIN = "/join";
    private static final String START = "/start";
    private static final String APPROVE = "/approve";
    private static final String FAIL = "/fail";
    private static final String END = "/end";

    private static final String TEST_USERNAME = "hubbabubba";
    private static final String TEST_USERNAME2 = "angel";
    private static final String NATIVE_LANGUAGE = "Swedish";
    private static final int NUM_DESIRED_WORDS = 15;

    @Autowired
    private TestRestTemplate restTemplate;


    @BeforeEach
    void setUp() {
    }

    @Test
    void index() {
        final ResponseEntity<String> res = restTemplate.getForEntity(basePath + port + "/", String.class);

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(res.getBody()).contains("Welcome to Habla Conmigo!");
    }

    @Test
    void numSessionsisZero() {
        final ResponseEntity<Integer> res = restTemplate.getForEntity(basePath + port + NUM_SESSIONS, Integer.class);

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(res.getBody()).isEqualTo(0);
    }

    @Test
    void numSessionsisNotZero() {
        final UserDTO requestBody = new UserDTO(TEST_USERNAME, NATIVE_LANGUAGE, NUM_DESIRED_WORDS);
        restTemplate.postForEntity(basePath + port + CREATE_SESSION, requestBody, String.class);

        final ResponseEntity<Integer> res = restTemplate.getForEntity(basePath + port + NUM_SESSIONS, Integer.class);

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(res.getBody()).isEqualTo(1);
    }

    @Test
    void createSessionHappyPath() {
        final UserDTO requestBody = new UserDTO(TEST_USERNAME, NATIVE_LANGUAGE, NUM_DESIRED_WORDS);

        final ResponseEntity<String> res = restTemplate.postForEntity(basePath + port + CREATE_SESSION, requestBody, String.class);

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(res.getBody().length()).isPositive();
    }

    @Test
    void createSessionMissingNumDesiredWords() {
        final UserDTO requestBody = new UserDTO(TEST_USERNAME, NATIVE_LANGUAGE);

        final ResponseEntity<String> res = restTemplate.postForEntity(basePath + port + CREATE_SESSION, requestBody, String.class);

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(res.getBody().length()).isPositive();
    }

    @Test
    void createSessionEmptyUserName() {
        final UserDTO requestBody = new UserDTO("", NATIVE_LANGUAGE, NUM_DESIRED_WORDS);

        final ResponseEntity<String> res = restTemplate.postForEntity(basePath + port + CREATE_SESSION, requestBody, String.class);

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void createSessionMissingNativeLanguage() {
        final UserDTO requestBody = new UserDTO(TEST_USERNAME, "");

        final ResponseEntity<String> res = restTemplate.postForEntity(basePath + port + CREATE_SESSION, requestBody, String.class);

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void getNonExistentSessionNotFound() {
        final String sessionId = "XYZ";

        final ResponseEntity<String> res = restTemplate.getForEntity(
                basePath + port + SESSION + "/" + sessionId,
                String.class);

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(res.getBody()).isEqualTo("Session with id XYZ could not be found");
    }

    @Test
    void getSessionCreatedHappyPath() {
        String sessionId = createSession(TEST_USERNAME);

        final ResponseEntity<GameSessionDTO> res = restTemplate.getForEntity(
                basePath + port + SESSION + "/" + sessionId,
                GameSessionDTO.class);

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(res.getBody()).isNotNull();
        assertThat(res.getBody().getStatus()).isEqualTo("CREATED");
        assertThat(res.getBody().getPlayer1().getUsername()).isEqualTo(TEST_USERNAME);
        assertThat(res.getBody().getPlayer2()).isNull();
        assertThat(res.getBody().getNumDesiredWords()).isEqualTo(NUM_DESIRED_WORDS);
        assertThat(res.getBody().getNumRemainingWords()).isEqualTo(0);
    }

    private String createSession(String creatorUsername) {
        final UserDTO createSessionRequestBody = new UserDTO(creatorUsername, NATIVE_LANGUAGE, NUM_DESIRED_WORDS);
        final ResponseEntity<String> createResponse = restTemplate.postForEntity(basePath + port + CREATE_SESSION, createSessionRequestBody, String.class);
        return createResponse.getBody();
    }

    @Test
    void joinNonExistentSessionNotFound() {
        final String sessionId = "XYZ";
        final UserDTO requestBody = new UserDTO(TEST_USERNAME2, "Spanish");

        final ResponseEntity<String> res = restTemplate.postForEntity(
                basePath + port + SESSION + "/" + sessionId + JOIN, requestBody,
                String.class);

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(res.getBody()).isEqualTo("Session with id XYZ could not be found");
    }

    @Test
    void joinSessionHappyPath() {
        String sessionId = createSession(TEST_USERNAME);
        final UserDTO joinSessionRequestBody = new UserDTO(TEST_USERNAME2, "Spanish");

        final ResponseEntity<GameSessionDTO> res = restTemplate.postForEntity(
                basePath + port + SESSION + "/" + sessionId + JOIN, joinSessionRequestBody,
                GameSessionDTO.class);

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(res.getBody()).isInstanceOf(GameSessionDTO.class);
        assertThat(res.getBody().getPlayer1().getUsername()).isEqualTo(TEST_USERNAME);
        assertThat(res.getBody().getPlayer2().getUsername()).isEqualTo(TEST_USERNAME2);
        assertThat(res.getBody().getStatus()).isEqualTo("READY");
    }

    @Test
    void joinSessionMissingNativeLanguage() {
        String sessionId = createSession(TEST_USERNAME);
        final UserDTO joinSessionRequestBody = new UserDTO(TEST_USERNAME2, null);

        final ResponseEntity<Object> res = restTemplate.postForEntity(
                basePath + port + SESSION + "/" + sessionId + JOIN, joinSessionRequestBody,
                Object.class);

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    private void joinSession(String sessionId, String joinerUsername) {
        final UserDTO joinSessionRequestBody = new UserDTO(joinerUsername, "Spanish");
        restTemplate.postForEntity(basePath + port + SESSION + "/" + sessionId + JOIN, joinSessionRequestBody, Object.class);
    }

    @Test
    void joinSessionAlreadyFull() {
        String sessionId = createSession(TEST_USERNAME);
        joinSession(sessionId, TEST_USERNAME2);
        final UserDTO joinSessionRequestBody2 = new UserDTO("another person", "Spanish");

        final ResponseEntity<String> res = restTemplate.postForEntity(
                basePath + port + SESSION + "/" + sessionId + JOIN, joinSessionRequestBody2, String.class);

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(res.getBody()).isEqualTo("Session already full, cannot join");
    }

    @Test
    void startGameHappyPath() {
        String sessionId = createSession(TEST_USERNAME);
        joinSession(sessionId, TEST_USERNAME2);

        final ResponseEntity<GameSessionDTO> res = restTemplate.postForEntity(
                basePath + port + SESSION + "/" + sessionId + START, null, GameSessionDTO.class);

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(res.getBody().getStatus()).isEqualTo("PLAYING");
        assertThat(res.getBody().getCurrentFlashCard()).isNotNull();
    }

    @Test
    void startGameInvalidStateException() {
        String sessionId = createSession(TEST_USERNAME);

        final ResponseEntity<String> res = restTemplate.postForEntity(
                basePath + port + SESSION + "/" + sessionId + START, null, String.class);

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(res.getBody()).isEqualTo("Game not in READY state, cannot start");
    }

    private void startGame(String sessionId) {
        restTemplate.postForEntity(basePath + port + SESSION + "/" + sessionId + START, null, GameSessionDTO.class);
    }

    private String prepareSessionAndStart() {
        String sessionId = createSession(TEST_USERNAME);
        joinSession(sessionId, TEST_USERNAME2);
        startGame(sessionId);
        return sessionId;
    }

    @Test
    void approveWordHappyPath() {
        String sessionId = prepareSessionAndStart();
        String approverUsername = TEST_USERNAME;

        final ResponseEntity<GameSessionDTO> res = restTemplate.postForEntity(
                basePath + port + SESSION + "/" + sessionId + APPROVE, approverUsername, GameSessionDTO.class);

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(res.getBody().getPlayer1().getPoints()).isEqualTo(0);
        assertThat(res.getBody().getPlayer2().getPoints()).isEqualTo(1);
        assertThat(res.getBody().getCurrentFlashCard().getPlayer2Passed()).isTrue();
        assertThat(res.getBody().getCurrentFlashCard().getPlayer1Passed()).isNull();
    }

    @ParameterizedTest
    @ValueSource(strings = {APPROVE, FAIL})
    void approveOrFailWordNotStartedInvalidStateException(String action) {
        String sessionId = createSession(TEST_USERNAME);
        joinSession(sessionId, TEST_USERNAME2);
        String approverUsername = TEST_USERNAME;

        final ResponseEntity<String> res = restTemplate.postForEntity(
                basePath + port + SESSION + "/" + sessionId + action, approverUsername, String.class);

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(res.getBody()).isEqualTo("Game not in PLAYING state, cannot perform operation");
    }

    @Test
    void failWordHappyPath() {
        String sessionId = prepareSessionAndStart();
        String approverUsername = TEST_USERNAME;

        final ResponseEntity<GameSessionDTO> res = restTemplate.postForEntity(
                basePath + port + SESSION + "/" + sessionId + FAIL, approverUsername, GameSessionDTO.class);

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(res.getBody().getPlayer1().getPoints()).isEqualTo(0);
        assertThat(res.getBody().getPlayer2().getPoints()).isEqualTo(0);
        assertThat(res.getBody().getCurrentFlashCard().getPlayer1Passed()).isNull();
        assertThat(res.getBody().getCurrentFlashCard().getPlayer2Passed()).isFalse();
    }

    @Test
    void endGame() {
        String sessionId = prepareSessionAndStart();

        final ResponseEntity<GameSessionDTO> res = restTemplate.postForEntity(
                basePath + port + SESSION + "/" + sessionId + END, null, GameSessionDTO.class);

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(res.getBody().getStatus()).isEqualTo("FINISHED");
    }
}