package com.ninjabooks.controller;

import com.ninjabooks.config.IntegrationTest;
import com.ninjabooks.security.utils.TokenUtils;
import com.ninjabooks.util.constants.DomainTestConstants;
import com.ninjabooks.utils.TestDevice;

import java.text.MessageFormat;

import com.jayway.jsonpath.JsonPath;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Piotr 'pitrecki' Nowak
 * @since 1.0
 */
@IntegrationTest
@RunWith(SpringJUnit4ClassRunner.class)
public class AccountControllerIT
{
    private static final String JSON =
        "{" +
            "\"firstName\":\"" + DomainTestConstants.FIRSTNAME + "\"," +
            "\"lastName\":\"" + DomainTestConstants.LASTNAME + "\"," +
            "\"email\":\"" + DomainTestConstants.EMAIL + "\"," +
            "\"password\":\"" + DomainTestConstants.PASSWORD + "\"" +
        "}";
    private static final String USER_CREATE_MESSAGE = "User was successfully created";
    private static final String SHORT_PASSWORD = "aa";

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private TokenUtils tokenUtils;

    @Autowired
    private UserDetailsService userDetailsService;

    private MockMvc mockMvc;

    @Before
    public void setUp() throws Exception {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    @Test
    public void testCreateUserShouldSucceed() throws Exception {
        mockMvc.perform(post("/api/user")
            .content(JSON)
            .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isCreated());
    }

    @Test
    public void testCreateUserShouldCreateUserAndReturnExpectedMessage() throws Exception {
        mockMvc.perform(post("/api/user")
            .content(JSON)
            .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(jsonPath("$.message").value(USER_CREATE_MESSAGE));
    }

    @Test
    public void testCreateUserWithoutMailFieldShouldFailed() throws Exception {
        String json = JsonPath.parse(JSON).delete("$.email").jsonString();
        mockMvc.perform(post("/api/user")
            .content(json).contentType(MediaType.APPLICATION_JSON_UTF8))
            .andDo(print())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("email field must be not empty"));
    }

    @Test
    public void testCreateUserWithoutFirstNameFieldShouldFailed() throws Exception {
        String json = JsonPath.parse(JSON).delete("$.firstName").jsonString();
        mockMvc.perform(post("/api/user")
            .content(json).contentType(MediaType.APPLICATION_JSON_UTF8))
            .andDo(print())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("firstName field must be not empty"));
    }

    @Test
    public void testCreateUserWithoutLastNameFieldShouldFailed() throws Exception {
        String json = JsonPath.parse(JSON).delete("$.lastName").jsonString();
        mockMvc.perform(post("/api/user")
            .content(json).contentType(MediaType.APPLICATION_JSON_UTF8))
            .andDo(print())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("lastName field must be not empty"));
    }

    @Test
    public void testCreateUserWithoutPasswordFieldShouldFailed() throws Exception {
        String json = JsonPath.parse(JSON).delete("$.password").jsonString();
        mockMvc.perform(post("/api/user")
            .content(json).contentType(MediaType.APPLICATION_JSON_UTF8))
            .andDo(print())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("password field must be not empty"));
    }

    @Test
    public void testCreateUserWithShortPasswordShouldFailed() throws Exception {
        String json = JsonPath.parse(JSON).set("$.password", SHORT_PASSWORD).jsonString();
        mockMvc.perform(post("/api/user")
            .content(json).contentType(MediaType.APPLICATION_JSON_UTF8))
            .andDo(print())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("password is too short, minimum length must equals: 8"));
    }

    @Test
    public void testCreateUserWithMalformedEmailShouldFailed() throws Exception {
        String json = JsonPath.parse(JSON).set("$.email", DomainTestConstants.NAME).jsonString();
        mockMvc.perform(post("/api/user")
            .content(json).contentType(MediaType.APPLICATION_JSON_UTF8))
            .andDo(print())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("email is not a well-formated"));
    }

    @Test
    @Sql(value = "classpath:it_import.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    public void testCreateUserWhichAlreadyExistShouldThrowsException() throws Exception {
        String expectedResponse = MessageFormat.format("Username email: {0} already exist in database", DomainTestConstants.EMAIL);
        mockMvc.perform(post("/api/user")
            .content(JSON).contentType(MediaType.APPLICATION_JSON_UTF8))
            .andDo(print())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value(expectedResponse));
    }

    @Test
    @Sql(value = "classpath:it_import.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    public void testGetAuthentationShouldSucceed() throws Exception {
        mockMvc.perform(get("/api/user")
            .header("Authorization", generateToken())
            .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isFound());
    }

    @Test
    @Sql(value = "classpath:it_import.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    public void testGetAuthentationShouldReturnExpectedJSON() throws Exception {
        mockMvc.perform(get("/api/user")
            .header("Authorization", generateToken())
            .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(jsonPath("$.id").value(DomainTestConstants.ID))
            .andExpect(jsonPath("$.firstName").value(DomainTestConstants.FIRSTNAME))
            .andExpect(jsonPath("$.lastName").value(DomainTestConstants.LASTNAME))
            .andExpect(jsonPath("$.email").value(DomainTestConstants.EMAIL));
    }

    private String generateToken() {
        UserDetails userDetails = userDetailsService.loadUserByUsername(DomainTestConstants.EMAIL);
        String token = tokenUtils.generateToken(userDetails, TestDevice.createDevice());
        return "Bearer " + token;
    }
}
