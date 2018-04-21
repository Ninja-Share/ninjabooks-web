package com.ninjabooks.controller;

import com.ninjabooks.config.AbstractBaseIT;

import static com.ninjabooks.util.constants.DomainTestConstants.AUTHOR;
import static com.ninjabooks.util.constants.DomainTestConstants.CAN_EXTEND;
import static com.ninjabooks.util.constants.DomainTestConstants.ID;
import static com.ninjabooks.util.constants.DomainTestConstants.ISBN;
import static com.ninjabooks.util.constants.DomainTestConstants.TITLE;
import static com.ninjabooks.utils.JSONDateConstans.BORROW_DATE;
import static com.ninjabooks.utils.JSONDateConstans.ORDER_DATE;
import static com.ninjabooks.utils.JSONDateConstans.RETURN_DATE;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Piotr 'pitrecki' Nowak
 * @since 1.0
 */
public class NotificationControllerIT extends AbstractBaseIT
{
    private static final String SQL_UPDATE_BORROW_QUERY = "UPDATE BORROW SET ACTIVE=false WHERE ID=1";
    private static final String SQL_UPDATE_QUEUE_QUERY = "UPDATE QUEUE SET ACTIVE=false WHERE ID=1";

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    @Before
    public void setUp() throws Exception {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    @Test
    @Sql(value = "classpath:sql_query/it_import.sql")
    public void testUserNotificationShouldFoundUserBorrowsAndQueues() throws Exception {
        mockMvc.perform(get("/api/notification/1"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
            .andExpect(jsonPath("$.['Queues list'][0].id").value(ID))
            .andExpect(jsonPath("$.['Queues list'][0].author").value(AUTHOR))
            .andExpect(jsonPath("$.['Queues list'][0].title").value(TITLE))
            .andExpect(jsonPath("$.['Queues list'][0].isbn").value(ISBN))
            .andExpect(jsonPath("$.['Queues list'][0].orderDate").value(ORDER_DATE.value()))
            .andExpect(jsonPath("$.['Books list'][0].id").value(ID))
            .andExpect(jsonPath("$.['Books list'][0].author").value(AUTHOR))
            .andExpect(jsonPath("$.['Books list'][0].title").value(TITLE))
            .andExpect(jsonPath("$.['Books list'][0].isbn").value(ISBN))
            .andExpect(jsonPath("$.['Books list'][0].borrowDate").value(BORROW_DATE.value()))
            .andExpect(jsonPath("$.['Books list'][0].expectedReturnDate").value(RETURN_DATE.value()))
            .andExpect(jsonPath("$.['Books list'][0].canExtendBorrow").value(CAN_EXTEND));
    }

    @Test
    @Sql(value = "classpath:sql_query/it_import.sql", statements = {SQL_UPDATE_QUEUE_QUERY})
    public void testUserNotificationShouldReutnOnlyBookList() throws Exception {
        mockMvc.perform(get("/api/notification/1"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
            .andExpect(jsonPath("$.['Queues list'][*]").isEmpty())
            .andExpect(jsonPath("$.['Books list'][0].id").value(ID))
            .andExpect(jsonPath("$.['Books list'][0].author").value(AUTHOR))
            .andExpect(jsonPath("$.['Books list'][0].title").value(TITLE))
            .andExpect(jsonPath("$.['Books list'][0].isbn").value(ISBN))
            .andExpect(jsonPath("$.['Books list'][0].borrowDate").value(BORROW_DATE.value()))
            .andExpect(jsonPath("$.['Books list'][0].expectedReturnDate").value(RETURN_DATE.value()))
            .andExpect(jsonPath("$.['Books list'][0].canExtendBorrow").value(CAN_EXTEND));
    }

    @Test
    @Sql(value = "classpath:sql_query/it_import.sql", statements = {SQL_UPDATE_BORROW_QUERY})
    public void testUserNotificationShouldReturnOnlyQueuesList() throws Exception {
        mockMvc.perform(get("/api/notification/1"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
            .andExpect(jsonPath("$.['Queues list'][0].id").value(ID))
            .andExpect(jsonPath("$.['Queues list'][0].author").value(AUTHOR))
            .andExpect(jsonPath("$.['Queues list'][0].title").value(TITLE))
            .andExpect(jsonPath("$.['Queues list'][0].isbn").value(ISBN))
            .andExpect(jsonPath("$.['Queues list'][0].orderDate").value(ORDER_DATE.value()))
            .andExpect(jsonPath("$.['Books list'][*]").isEmpty());
    }

    @Test
    @Sql(value = "classpath:sql_query/it_import.sql", statements = {SQL_UPDATE_BORROW_QUERY, SQL_UPDATE_QUEUE_QUERY})
    public void testUserNotificationShouldReturnStatusNotContent() throws Exception {
        mockMvc.perform(get("/api/notification/1"))
            .andDo(print())
            .andExpect(status().isOk());
    }

    @Test
    public void testUserNotificationShouldReturnStatusBadRequest() throws Exception {
        mockMvc.perform(get("/api/notification/1"))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.request").value("/api/notification/1"))
            .andExpect(jsonPath("$.message").value("Entity with id: 1 not found"));
    }
}
