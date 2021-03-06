package com.ninjabooks.security.service.auth;

import com.ninjabooks.json.authentication.AuthenticationRequest;
import com.ninjabooks.security.user.SpringSecurityUser;
import com.ninjabooks.security.user.SpringSecurityUserFactory;
import com.ninjabooks.security.utils.TokenUtils;
import com.ninjabooks.utils.TestDevice;

import static com.ninjabooks.util.constants.DomainTestConstants.EMAIL;
import static com.ninjabooks.util.constants.DomainTestConstants.PLAIN_PASSWORD;
import static com.ninjabooks.util.constants.DomainTestConstants.USER_FULL;

import java.util.Optional;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Piotr 'pitrecki' Nowak
 * @since 1.0
 */
public class AuthenticationServiceImplTest
{
    private static final String SECURITY_PATTERN = "Bearer ";
    private static final String SECRET = "aaaaazzzzzxxxxccccvv";
    private static final String OLD_TOKEN =
        "Bearer " +
            "eyJhbGciOiJIUzUxMiJ9." +
            "eyJzdWIiOiJqb2huLmRlZUBleG1hcGxlLmNvbSIsImF1ZGllbmNlIjoidW5rbm93biIsImNyZWF0ZWQiOjE1MTQ4MDk2OTY1ODIsImV" +
            "4cCI6MTUxNTQxNDQ5Nn0." +
            "NWgUa9mG64GgCTj9qZXkgj7HF0ZbjtcaSTZ8isNVA49kVzW9xhFpFvVg778ZC8y0Fo_GatMFaA3TYv3nj0PWug";

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule().silent();

    @Mock
    private AuthenticationManager authenticationManagerMock;

    @Mock
    private UserDetailsService userDetailsServiceMock;

    private TokenUtils tokenUtils = new TokenUtils(SECRET);
    private AuthenticationService sut;

    @Before
    public void setUp() throws Exception {
        this.sut = new AuthenticationServiceImpl(authenticationManagerMock, userDetailsServiceMock, tokenUtils);
    }

    @Test
    public void testAuthUserShouldReturnUser() throws Exception {
        when(userDetailsServiceMock.loadUserByUsername(EMAIL)).thenReturn(initSpringUser());
        UserDetails actual =
            sut.authUser(createAuthRequest());

        assertThat(actual).extracting("username").containsExactly(EMAIL);
        verify(userDetailsServiceMock, atLeastOnce()).loadUserByUsername(anyString());
    }

    @Test
    public void testAuthUserShouldThrowsExceptionWhenUserDataNotValid() throws Exception {
        doThrow(BadCredentialsException.class)
            .when(userDetailsServiceMock).loadUserByUsername(EMAIL);

        assertThatExceptionOfType(BadCredentialsException.class)
            .isThrownBy(() -> sut.authUser(createAuthRequest()))
            .withNoCause();

        verify(userDetailsServiceMock, atLeastOnce()).loadUserByUsername(anyString());
    }

    @Test
    public void testRefreshTokenShouldReturnNewToken() throws Exception {
        when(userDetailsServiceMock.loadUserByUsername(EMAIL)).thenReturn(initSpringUser());
        String token = generateToken();
        Optional<String> actual = sut.refreshToken(token);

        assertSoftly(softly -> {
            assertThat(actual).isPresent();
            assertThat(actual).hasValueSatisfying(s -> {
                String t = token.replaceAll(SECURITY_PATTERN, "");
                assertThat(s).isNotEqualTo(t);
            });
        });
        verify(userDetailsServiceMock, atLeastOnce()).loadUserByUsername(anyString());
    }

    @Test
    @Ignore("Old token cause NPE")
    public void testRefreshTokenShoulReturnEmptyToken() throws Exception {
        when(userDetailsServiceMock.loadUserByUsername(EMAIL)).thenReturn(initSpringUser());
        Optional<String> actual = sut.refreshToken(OLD_TOKEN);

        assertThat(actual).isEmpty();
        verify(userDetailsServiceMock, atLeastOnce()).loadUserByUsername(anyString());
    }

    @Test
    public void testGetAuthUserShouldReturnExpectedSpringUser() throws Exception {
        when(userDetailsServiceMock.loadUserByUsername(EMAIL)).thenReturn(initSpringUser());
        SpringSecurityUser actual = sut.getAuthUser(generateToken());

        assertThat(actual).extracting("username").containsExactly(EMAIL);
    }

    private String generateToken() {
        String token = tokenUtils.generateToken(initSpringUser(), TestDevice.createDevice());
        return SECURITY_PATTERN + token;
    }

    private AuthenticationRequest createAuthRequest() {
        return new AuthenticationRequest(EMAIL, PLAIN_PASSWORD);
    }

    private SpringSecurityUser initSpringUser() {
        return SpringSecurityUserFactory.makeSecurityUser(USER_FULL);
    }
}
