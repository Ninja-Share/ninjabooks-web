package com.ninjabooks.security.utils;

import com.ninjabooks.config.AbstractBaseIT;
import com.ninjabooks.error.exception.TokenException;
import com.ninjabooks.utils.TestDevice;

import static com.ninjabooks.util.constants.DomainTestConstants.EMAIL;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

/**
 * @author Piotr 'pitrecki' Nowak
 * @since 1.0
 */
@Sql(value = "classpath:sql_query/it_import.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
public class TokenUtilsIT extends AbstractBaseIT
{
    private static final LocalDate TODAY = LocalDate.now();
    private static final LocalDate EXPECTED_EXP_DATE = TODAY.plusDays(7);
    private static final Audience EXPECTED_AUDIENCE = Audience.UNKNOWN;
    private static final String RANDOM_TOKEN =
        "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9" +
        ".eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOnRydWV9" +
        ".TJVA95OrM7E2cBab30RMHrHDcEfxjoYZgeFONFh7HgQ";

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private TokenUtils sut;

    @Test
    public void testGenerateTokenShouldSucceed() throws Exception {
        String actual = sut.generateToken(obtainUserDetails(), TestDevice.createDevice());

        assertThat(actual).isNotEmpty().containsPattern("\\.+{2}");
    }

    @Test
    public void testGetUsernameShouldReturnExpectedUsername() throws Exception {
        String actual = sut.getUsernameFromToken(generateToken());

        assertThat(actual).isEqualTo(EMAIL);
    }

    @Test
    public void testGetUsernameFromWrongTokenShouldReturnException() throws Exception {
        assertThatExceptionOfType(TokenException.class)
            .isThrownBy(() -> sut.getUsernameFromToken(RANDOM_TOKEN))
            .withNoCause();
    }

    @Test
    public void testGetCreatedDateShouldRetunExpectedDate() throws Exception {
        LocalDateTime actual = sut.getCreatedDateFromToken(generateToken());

        assertThat(actual.toLocalDate()).isEqualTo(TODAY);
    }

    @Test
    public void testGetCreatedDateFromWrongTokenShouldThrowsException() throws Exception {
        assertThatExceptionOfType(TokenException.class)
            .isThrownBy(() -> sut.getCreatedDateFromToken(RANDOM_TOKEN))
            .withNoCause();
    }

    @Test
    public void testGetExpirationDateShouldReturnExptectedDate() throws Exception {
        LocalDateTime actual = sut.getExpirationDateFromToken(generateToken());

        assertThat(actual.toLocalDate()).isEqualTo(EXPECTED_EXP_DATE);
    }

    @Test
    public void testGetExpirationDateFromWrongTokenShouldThrowsException() throws Exception {
        assertThatExceptionOfType(TokenException.class)
            .isThrownBy(() -> sut.getExpirationDateFromToken(RANDOM_TOKEN))
            .withNoCause();
    }

    @Test
    public void testGetAudienceShouldReturnExpectedAudience() throws Exception {
        Audience actual = sut.getAudienceFromToken(generateToken());

        assertThat(actual).isEqualTo(EXPECTED_AUDIENCE);
    }

    @Test
    public void testGetAudienceFromWrongTokenShouldThrowsException() throws Exception {
        assertThatExceptionOfType(TokenException.class)
            .isThrownBy(() -> sut.getAudienceFromToken(RANDOM_TOKEN))
            .withNoCause();
    }

    @Test
    public void testCanTokenRefreshShouldReturnTrue() throws Exception {
        Boolean actual = sut.canTokenBeRefreshed(generateToken(), TODAY.atStartOfDay());

        assertThat(actual).isTrue();
    }

    @Test
    public void testRefreshTokenShouldSucceedAndReturnNewToken() throws Exception {
        String tokenToRefresh = generateToken();
        String actual = sut.refreshToken(tokenToRefresh);

        assertThat(actual).isNotEqualTo(tokenToRefresh);
    }

    @Test
    public void testRefreshTokenShouldThrowsExceptionWhenTokenIsWrong() throws Exception {
        assertThatExceptionOfType(TokenException.class)
            .isThrownBy(() -> sut.refreshToken(RANDOM_TOKEN))
            .withNoCause();
    }

    @Test
    public void testIsValidateTokenShouldSuceedAndReturnTrue() throws Exception {
        Boolean actual = sut.isValid(generateToken(), obtainUserDetails());

        assertThat(actual).isTrue();
    }

    @Test
    public void testIsValidateTokenWithWrongTokenShouldFailedAndReturnFalse() throws Exception {
        assertThatExceptionOfType(TokenException.class)
            .isThrownBy(() -> sut.isValid(RANDOM_TOKEN, obtainUserDetails()))
            .withNoCause();
    }

    private UserDetails obtainUserDetails() {
        return userDetailsService.loadUserByUsername(EMAIL);
    }

    private String generateToken() {
        return sut.generateToken(obtainUserDetails(), TestDevice.createDevice());
    }
}
