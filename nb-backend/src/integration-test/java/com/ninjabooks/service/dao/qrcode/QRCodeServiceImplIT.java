package com.ninjabooks.service.dao.qrcode;

import com.ninjabooks.config.AbstractBaseIT;
import com.ninjabooks.dao.QRCodeDao;
import com.ninjabooks.domain.QRCode;

import static com.ninjabooks.util.constants.DomainTestConstants.DATA;
import static com.ninjabooks.util.constants.DomainTestConstants.QR_CODE_FULL;

import javax.transaction.Transactional;
import java.util.Optional;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Piotr 'pitrecki' Nowak
 * @since 1.0
 */
@Transactional
@Sql(value = "classpath:sql_query/it_import.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
public class QRCodeServiceImplIT extends AbstractBaseIT
{
    private static final String CUSTMOM_DATA = "123456ngkfcz";

    @Autowired
    private QRCodeDao sut;

    @Test
    public void testGetByDataShouldReturnOptionalWithExpectedQRCode() throws Exception {
        Optional<QRCode> actual = sut.getByData(DATA);

        assertThat(actual).contains(QR_CODE_FULL);
    }

    @Test
    public void testGetByDataShouldReturnEmptyOptionalWhenQRCodeNotFound() throws Exception {
        Optional<QRCode> actual = sut.getByData(CUSTMOM_DATA);

        assertThat(actual).isEmpty();
    }
}
