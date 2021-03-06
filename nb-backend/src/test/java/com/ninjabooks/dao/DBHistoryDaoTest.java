package com.ninjabooks.dao;

import com.ninjabooks.dao.db.DBHistoryDao;
import com.ninjabooks.domain.History;
import com.ninjabooks.util.CommonUtils;

import static com.ninjabooks.util.constants.DomainTestConstants.EXPECTED_RETURN_DATE;
import static com.ninjabooks.util.constants.DomainTestConstants.HISTORY;
import static com.ninjabooks.util.constants.DomainTestConstants.ID;

import java.time.LocalDate;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Piotr 'pitrecki' Nowak
 * @since 1.0
 */
public class DBHistoryDaoTest
{
    private static final LocalDate UPDATED_RETURN_DATE = LocalDate.now();
    private static final Supplier<Stream<History>> HISTORY_STREAM_SUPPLIER =
        CommonUtils.asSupplier(HISTORY);

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule().silent();

    @Mock
    private SessionFactory sessionFactoryMock;

    @Mock
    private Session sessionMock;

    @Mock
    private Query queryMock;

    private HistoryDao sut;

    @Before
    public void setUp() throws Exception {
        this.sut = new DBHistoryDao(sessionFactoryMock);
        when(sessionFactoryMock.getCurrentSession()).thenReturn(sessionMock);
        when(sessionMock.createQuery(any(), any())).thenReturn(queryMock);
    }

    @Test
    public void testAddHistory() throws Exception {
        when(sessionMock.save(any())).thenReturn(ID);
        sut.add(HISTORY);

        verify(sessionMock, atLeastOnce()).save(any());
    }

    @Test
    public void testDeleteHistoryByEnity() throws Exception {
        doNothing().when(sessionMock).delete(HISTORY);
        sut.delete(HISTORY);

        verify(sessionMock, atLeastOnce()).delete(any());
    }

    @Test
    public void testGetById() throws Exception {
        when(sessionMock.get((Class<Object>) any(), any())).thenReturn(HISTORY);
        Optional<History> actual = sut.getById(ID);

        assertThat(actual).contains(HISTORY);
        verify(sessionMock, atLeastOnce()).get((Class<Object>) any(), any());
    }

    @Test
    public void testGetByIdEnityWhichNotExistShouldReturnEmptyOptional() throws Exception {
        Optional<History> actual = sut.getById(ID);

        assertThat(actual).isEmpty();
    }

    @Test
    public void testFindAllHistoriesShouldReturnsAllRecords() throws Exception {
        when(queryMock.stream()).thenReturn(HISTORY_STREAM_SUPPLIER.get());
        Stream<History> actual = sut.getAll();

        assertThat(actual).containsExactly(HISTORY);
    }

    @Test
    public void testFindAllOnEmptyDBShouldReturnEmptyStream() throws Exception {
        assertThat(sut.getAll()).isEmpty();
    }

    @Test
    public void testUpdateHistoryByEntity() throws Exception {
        History historyBeforeUpdate = createFreshEntity();
        historyBeforeUpdate.setReturnDate(UPDATED_RETURN_DATE);

        doNothing().when(sessionMock).update(historyBeforeUpdate);
        sut.update(historyBeforeUpdate);

        verify(sessionMock, atLeastOnce()).update(any());
    }

    private History createFreshEntity() {
        return new History(EXPECTED_RETURN_DATE);
    }

}
