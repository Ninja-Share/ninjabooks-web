package com.ninjabooks.controller;

import com.ninjabooks.config.AbstractBaseIT;

import static com.ninjabooks.util.constants.DomainTestConstants.DATA;
import static com.ninjabooks.util.constants.DomainTestConstants.ID;
import static com.ninjabooks.util.constants.DomainTestConstants.TITLE;

import java.text.MessageFormat;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Piotr 'pitrecki' Nowak
 * @since 1.0
 */
public class BorrowControllerIT extends AbstractBaseIT
{
    private static final String UPDATE_BOOK_STATUS = "UPDATE BOOK SET STATUS = 'FREE' WHERE ID = 1;";
    private static final String RANDOM_QR_CODE = "dasdajsda";
    private static final String TRUNCATE_BOOK_TABLE = "TRUNCATE TABLE BOOK ;";
    private static final String TRUNCATE_BORROW_TABLE = "TRUNCATE TABLE BORROW ;";
    private static final String UPDATE_EXTEND_STATUS = "UPDATE BORROW SET CAN_EXTEND_RETURN_DATE=FALSE WHERE ID=1 ;";

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    @Before
    public void setUp() throws Exception {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    @Test
    @Sql(value = "classpath:sql_query/rent-scripts/return-script/it_return_import.sql",
         statements = UPDATE_BOOK_STATUS,
         executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    public void testBorrowBookShouldSucceed() throws Exception {
        mockMvc.perform(post("/api/borrow/{userID}/", ID)
            .param("qrCode", DATA))
            .andDo(print())
            .andExpect(status().isOk());
    }

    @Test
    public void testBorrowBookShouldReturnBadRequestWhenUserNotFound() throws Exception {
        mockMvc.perform(post("/api/borrow/{userID}/", ID)
            .param("qrCode", DATA))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message")
                .value(MessageFormat.format("Entity with id: {0} not found", ID)));
    }

    @Test
    @Sql(value = "classpath:sql_query/rent-scripts/return-script/it_return_import.sql",
         executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    public void testBorrowBookShouldReturnBadRequestWhenQRCodeNotFound() throws Exception {
        mockMvc.perform(post("/api/borrow/{userID}/", ID)
            .param("qrCode", RANDOM_QR_CODE))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message")
                .value(MessageFormat.format("Book not found by given qr code: {0}", RANDOM_QR_CODE)));
    }

    @Test
    @Sql(value = "classpath:sql_query/rent-scripts/return-script/it_return_import.sql",
         executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    public void testBorrowBookShouldReturnBadRequestWhenBookIsNotBorrowed() throws Exception {
        mockMvc.perform(post("/api/borrow/{userID}/", ID)
            .param("qrCode", DATA))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message")
                .value(MessageFormat.format("Book: {0} is already borrowed", TITLE)));
    }

    @Test
    @Sql(value = "classpath:sql_query/rent-scripts/return-script/it_return_import.sql",
         executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    public void testReturnBookShouldSucceed() throws Exception {
        mockMvc.perform(post("/api/borrow/return/")
            .param("qrCode", DATA))
            .andDo(print())
            .andExpect(status().isOk());
    }

    @Test
    @Sql(value = "classpath:sql_query/rent-scripts/return-script/it_return_import.sql",
         statements = UPDATE_BOOK_STATUS,
         executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    public void testReturnBookShouldFailedWhenBookIsNotBorrowed() throws Exception {
        mockMvc.perform(post("/api/borrow/return/")
            .param("qrCode", DATA))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message")
                .value(MessageFormat.format("Book: {0} is not borrowed, unable to return",
                    TITLE)));
    }

    @Test
    @Sql(value = "classpath:sql_query/rent-scripts/return-script/it_return_import.sql",
         executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    public void testExtendReturnDateShouldSucced() throws Exception {
        mockMvc.perform(post("/api/borrow/{userID}/extend/", ID)
            .param("bookID", String.valueOf(ID)))
            .andDo(print())
            .andExpect(status().isOk());
    }

    @Test
    @Sql(
        value = "classpath:sql_query/rent-scripts/return-script/it_return_import.sql",
        statements = {TRUNCATE_BORROW_TABLE, TRUNCATE_BOOK_TABLE},
        executionPhase = BEFORE_TEST_METHOD)
    public void testExtendBookShouldFailedWhenBookNotExist() throws Exception {
        mockMvc.perform(post("/api/borrow/{userID}/extend/", ID)
            .param("bookID", String.valueOf(ID)))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message")
                .value(MessageFormat.format("Entity with id: {0} not found", ID)));
    }

    @Test
    @Sql(
        statements = TRUNCATE_BORROW_TABLE,
        value = "classpath:sql_query/rent-scripts/return-script/it_return_import.sql",
        executionPhase = BEFORE_TEST_METHOD)
    public void testExtendBookShouldFailedWhenBookIsNotBorrowed() throws Exception {
        mockMvc.perform(post("/api/borrow/{userID}/extend/", ID)
            .param("bookID", String.valueOf(ID)))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message")
                .value(MessageFormat.format("Book: {0} is not borrowed", TITLE)));
    }

    @Test
    @Sql(
        value = "classpath:sql_query/rent-scripts/return-script/it_return_import.sql",
        statements = UPDATE_EXTEND_STATUS,
        executionPhase = BEFORE_TEST_METHOD)
    public void testExtendBookShouldFailedWhenExtendStatusIsFalse() throws Exception {
        mockMvc.perform(post("/api/borrow/{userID}/extend/", ID)
            .param("bookID", String.valueOf(ID)))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message")
                .value(MessageFormat.format("Unable extend book with id: {0}", ID)));
    }
}
