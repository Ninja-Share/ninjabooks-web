package com.ninjabooks.dao.db;

import com.ninjabooks.config.AbstractBaseIT;
import com.ninjabooks.dao.QueueDao;
import com.ninjabooks.domain.Queue;

import static com.ninjabooks.util.constants.DomainTestConstants.ID;
import static com.ninjabooks.util.constants.DomainTestConstants.ORDER_DATE;
import static com.ninjabooks.util.constants.DomainTestConstants.QUEUE;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.stream.Stream;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Piotr 'pitrecki' Nowak
 * @since 1.0
 */
@Transactional
public class DBQueueDaoIT extends AbstractBaseIT
{
    private static final LocalDateTime NEW_ORDER_DATE = LocalDateTime.now();

    @Autowired
    private QueueDao sut;

    @Test
    public void testAddQueue() throws Exception {
        sut.add(QUEUE);
        Stream<Queue> actual = sut.getAll();

        assertThat(actual).containsExactly(QUEUE);
    }

    @Test
    @Sql(value = "classpath:sql_query/dao_import.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    public void testDeleteQueue() throws Exception {
        sut.delete(QUEUE);

        assertThat(sut.getAll()).isEmpty();
    }

    @Test
    @Sql(value = "classpath:sql_query/dao_import.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    public void testGetAllShouldReturnsAllRecord() throws Exception {
        Stream<Queue> actual = sut.getAll();

        assertThat(actual).containsExactly(QUEUE);
    }

    @Test
    public void testGetAllOnEmptyDBShouldReturnEmptyStream() throws Exception {
        Stream<Queue> actual = sut.getAll();

        assertThat(actual).isEmpty();
    }

    @Test
    @Sql(value = "classpath:sql_query/dao_import.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    public void testGetById() throws Exception {
        Optional<Queue> actual = sut.getById(ID);

        assertThat(actual).hasValueSatisfying(queue -> {
            assertThat(queue.getId()).isEqualTo(ID);
            assertThat(queue.getOrderDate()).isEqualTo(ORDER_DATE);
        });
    }

    @Test
    public void testGetByIdWhichNotExistShouldReturnEmptyOptional() throws Exception {
        Optional<Queue> actual = sut.getById(ID);

        assertThat(actual).isEmpty();
    }

    @Test
    @Sql(value = "classpath:sql_query/dao_import.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    public void testGetOrderByDate() throws Exception {
        Stream<Queue> actual = sut.getByOrderDate(ORDER_DATE);

        assertThat(actual).containsExactly(QUEUE);
    }

    @Test
    public void testGetOrderByDateWhichNotExistShouldReturnEmptyStream() throws Exception {
        Stream<Queue> actual = sut.getByOrderDate(ORDER_DATE);

        assertThat(actual).isEmpty();
    }

    @Test
    @Sql(value = "classpath:sql_query/dao_import.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    public void testUpdateQueue() throws Exception {
        Queue entityToUpdate = creafreFreshEntity();
        entityToUpdate.setOrderDate(NEW_ORDER_DATE);

        sut.update(entityToUpdate);
        Stream<Queue> actual = sut.getAll();

        assertThat(actual).containsExactly(entityToUpdate);
    }

    private Queue creafreFreshEntity() {
        Queue entityToUpdate = new Queue(ORDER_DATE);
        entityToUpdate.setId(ID);

        return entityToUpdate;
    }

}
