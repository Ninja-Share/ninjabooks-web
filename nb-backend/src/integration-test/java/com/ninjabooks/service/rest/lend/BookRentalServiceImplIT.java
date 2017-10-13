package com.ninjabooks.service.rest.lend;

import com.ninjabooks.config.IntegrationTest;
import com.ninjabooks.domain.Book;
import com.ninjabooks.domain.BookStatus;
import com.ninjabooks.domain.Borrow;
import com.ninjabooks.service.dao.borrow.BorrowService;
import com.ninjabooks.service.dao.user.UserService;
import com.ninjabooks.util.constants.DomainTestConstants;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

/**
 * @author Piotr 'pitrecki' Nowak
 * @since 1.0
 */
@IntegrationTest
@RunWith(SpringJUnit4ClassRunner.class)
public class BookRentalServiceImplIT
{
    private static final int EXPECTED_SIZE = 1;
    private static final String[] IGNORED_FILEDS = {"queues", "histories", "comments", "description"};

    @Autowired
    private BookRentalService sut;

    @Autowired
    private BorrowService borrowService;

    @Autowired
    private UserService userService;

    @Test
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Sql(value = "classpath:it_rent_import.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    public void testRentBookShouldSucced() throws Exception {
        sut.rentBook(DomainTestConstants.ID, DomainTestConstants.DATA);
//        List<Borrow> borrows = userDao.getById(DomainTestConstants.ID).get().getBorrows();
        Stream<Borrow> actual = borrowService.getAll();

        assertSoftly(softly -> {
            assertThat(actual).hasSize(EXPECTED_SIZE);
            assertThat(actual).extracting("book", Book.class)
                .usingElementComparatorIgnoringFields(IGNORED_FILEDS)
                .containsExactly(DomainTestConstants.BOOK);
            assertThat(actual).extracting("book", Book.class)
                .extracting(Book::getStatus)
                .isEqualTo(BookStatus.BORROWED);
        });
    }
}
