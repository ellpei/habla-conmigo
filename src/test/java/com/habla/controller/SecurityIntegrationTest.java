package com.habla.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.habla.domain.gameplay.GameSession;
import com.habla.domain.gameplay.Player;
import com.habla.service.SessionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.stream.Stream;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SecurityIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mvc;

    @MockBean
    private SessionHandler sessionHandlerMock;

    private static final UserDTO userDto = new UserDTO("hubba", "Swedish");


    @BeforeEach
    public void setup() {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
        when(sessionHandlerMock.getSession("x")).thenReturn(new GameSession(new Player(userDto), 10));
    }

    private static Stream<Arguments> sessionEndpointsParameters() throws JsonProcessingException {
        return Stream.of(
                Arguments.of(null, HttpMethod.GET, "/session/total"),
                Arguments.of(userDto, HttpMethod.POST, "/session/create"),
                Arguments.of(userDto, HttpMethod.POST, "/session/x/join"),
                Arguments.of(null, HttpMethod.POST, "/session/x/start"),
                Arguments.of(null, HttpMethod.POST, "/session/x/approve"),
                Arguments.of(null, HttpMethod.POST, "/session/x/fail"),
                Arguments.of(null, HttpMethod.POST, "/session/x/end"),
                Arguments.of(null, HttpMethod.GET, "/session/x")
        );
    }

    @ParameterizedTest
    @MethodSource("sessionEndpointsParameters")
    public void testSessionsAccessControl(Object request, HttpMethod httpMethod, String url) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = objectMapper.writeValueAsString(request);

        mvc.perform(request(httpMethod, url)
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(user("uname").roles("USER")))
                .andExpect(status().is(HttpStatus.OK.value()));
    }

    private static Stream<Arguments> adminEndpointsParameters() {
        return Stream.of(
                Arguments.of("ADMIN", HttpMethod.GET, "/admin/all-sessions", HttpStatus.OK),
                Arguments.of("USER", HttpMethod.GET, "/admin/all-sessions", HttpStatus.FORBIDDEN),
                Arguments.of("ADMIN", HttpMethod.DELETE, "/admin/delete-all", HttpStatus.OK),
                Arguments.of("USER", HttpMethod.DELETE, "/admin/delete-all", HttpStatus.FORBIDDEN),
                Arguments.of("ADMIN", HttpMethod.DELETE, "/admin/delete/xxx", HttpStatus.OK),
                Arguments.of("USER", HttpMethod.DELETE, "/admin/delete/xxx", HttpStatus.FORBIDDEN)
        );
    }

    @ParameterizedTest
    @MethodSource("adminEndpointsParameters")
    public void testAdminAccessControl(String role, HttpMethod httpMethod, String url, HttpStatus expectedStatus) throws Exception {
        mvc.perform(request(httpMethod, url).with(user("uname").roles(role)))
                .andExpect(status().is(expectedStatus.value()));
    }

}