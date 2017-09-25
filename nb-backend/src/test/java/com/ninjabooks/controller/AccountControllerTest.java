package com.ninjabooks.controller;

import com.ninjabooks.error.handler.AccountControllerHandler;
import com.ninjabooks.error.user.UserAlreadyExistException;
import com.ninjabooks.security.user.SpringSecurityUser;
import com.ninjabooks.security.utils.TokenUtils;
import com.ninjabooks.service.rest.account.AccountService;
import com.ninjabooks.util.SecurityHeaderUtils;
import com.ninjabooks.util.constants.DomainTestConstants;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Piotr 'pitrecki' Nowak
 * @since 1.0
 */
public class AccountControllerTest
{
    private static final String JSON =
        "{" +
            "\"firstName\":\"" + DomainTestConstants.FIRSTNAME + "\"," +
            "\"lastName\":\"" + DomainTestConstants.LASTNAME + "\"," +
            "\"email\":\"" + DomainTestConstants.EMAIL + "\"," +
            "\"password\":\"" + DomainTestConstants.PASSWORD + "\"}" +
            "}";

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    private AccountService accountServiceMock;

    @Mock
    private TokenUtils tokenUtilsMock;

    @Mock
    private UserDetailsService userDetailsServiceMock;

    @Mock
    private SecurityHeaderUtils securityHeaderFinder;

    private MockMvc mockMvc;
    private AccountController sut;

    @Before
    public void setUp() throws Exception {
        this.sut = new AccountController(accountServiceMock, tokenUtilsMock,
            userDetailsServiceMock, securityHeaderFinder);
        this.mockMvc = MockMvcBuilders.standaloneSetup(sut)
            .setControllerAdvice(new AccountControllerHandler())
            .build();
    }

    @Test
    public void testCreateUserShouldSucceed() throws Exception {
        mockMvc.perform(post("/api/users")
            .content(JSON)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated());

        verify(accountServiceMock, atLeastOnce()).createUser(any());
    }

    @Test
    public void testCreateUserWhichAlreadyExistShouldThrowsException() throws Exception {
        doThrow(UserAlreadyExistException.class).when(accountServiceMock).createUser(any());

        mockMvc.perform(post("/api/users")
            .content(JSON).contentType(MediaType.APPLICATION_JSON_UTF8))
            .andDo(print())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
            .andExpect(status().isBadRequest());

        verify(accountServiceMock, atLeastOnce()).createUser(any());
    }

    @Test
    public void testGetAuthentationShouldReturnUserInfo() throws Exception {
        SpringSecurityUser springUser = initSpringUser();
        when(userDetailsServiceMock.loadUserByUsername(any())).thenReturn(springUser);

        mockMvc.perform(get("/api/users")
            .header("Authorization", anyString())
            .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isFound());

        verify(userDetailsServiceMock, atLeastOnce()).loadUserByUsername(any());
        verify(tokenUtilsMock, atLeastOnce()).getUsernameFromToken(any());
    }

    private SpringSecurityUser initSpringUser() {
        SpringSecurityUser springSecurityUser = new SpringSecurityUser();
        springSecurityUser.setName(DomainTestConstants.NAME);
        springSecurityUser.setEmail(DomainTestConstants.EMAIL);
        return springSecurityUser;
    }
}
