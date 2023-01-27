package com.habla.controller;

import com.habla.response.GameSessionDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
        final UserDTO requestBody = new UserDTO(TEST_USERNAME, NATIVE_LANGUAGE, NUM_DESIRED_WORDS);

        final ResponseEntity<String> created = restTemplate.postForEntity(basePath + port + CREATE_SESSION, requestBody, String.class);
        final String sessionId = created.getBody();

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
        final UserDTO createSessionRequestBody = new UserDTO(TEST_USERNAME, NATIVE_LANGUAGE, NUM_DESIRED_WORDS);
        final ResponseEntity<String> createResponse = restTemplate.postForEntity(basePath + port + CREATE_SESSION, createSessionRequestBody, String.class);
        final String sessionId = createResponse.getBody();
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
    void startGame() {
    }

    @Test
    void approveWord() {
    }

    @Test
    void failWord() {
    }

    @Test
    void endGame() {
    }
}