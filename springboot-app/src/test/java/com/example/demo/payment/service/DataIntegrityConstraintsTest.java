package com.example.demo.payment.service;

import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;

import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;

class DataIntegrityConstraintsTest {

    @Test
    void readsMariaDbIdempotencyConstraintWithoutMatchingRequestNo() {
        SQLException sqlException = new SQLException(
                "Duplicate entry 'PAY-ORDER-1' for key 'UK_PAYMENT_REQUESTS_IDEMPOTENCY_KEY'");
        DataIntegrityViolationException exception = new DataIntegrityViolationException("could not execute", sqlException);

        assertThat(DataIntegrityConstraints.violates(exception, DataIntegrityConstraints.IDEMPOTENCY_KEY)).isTrue();
        assertThat(DataIntegrityConstraints.violates(exception, DataIntegrityConstraints.REQUEST_NO)).isFalse();
    }

    @Test
    void readsMariaDbRequestNoConstraint() {
        SQLException sqlException = new SQLException(
                "Duplicate entry 'PAY-20260101-ABCDEF' for key 'UK_PAYMENT_REQUESTS_REQUEST_NO'");
        DataIntegrityViolationException exception = new DataIntegrityViolationException("could not execute", sqlException);

        assertThat(DataIntegrityConstraints.violates(exception, DataIntegrityConstraints.REQUEST_NO)).isTrue();
        assertThat(DataIntegrityConstraints.violates(exception, DataIntegrityConstraints.IDEMPOTENCY_KEY)).isFalse();
    }

    @Test
    void ignoresSqlTextStoredAsConstraintName() {
        String dump = "UK_PAYMENT_REQUESTS_IDEMPOTENCY_KEY ON PAYMENT_REQUESTS(IDEMPOTENCY_KEY); "
                + "SQL statement: insert into PAYMENT_REQUESTS (IDEMPOTENCY_KEY, REQUEST_NO) values (?, ?)";
        DataIntegrityViolationException exception = new DataIntegrityViolationException(
                "could not execute",
                new ConstraintViolationException("duplicate", new SQLException("duplicate"), dump)
        );

        assertThat(DataIntegrityConstraints.violates(exception, DataIntegrityConstraints.IDEMPOTENCY_KEY)).isFalse();
        assertThat(DataIntegrityConstraints.violates(exception, DataIntegrityConstraints.REQUEST_NO)).isFalse();
    }

    @Test
    void ignoresColumnNamesInsideInsertSql() {
        SQLException sqlException = new SQLException(
                "could not execute statement [insert into PAYMENT_REQUESTS (IDEMPOTENCY_KEY, REQUEST_NO, AMOUNT) values (?, ?, ?)]");
        DataIntegrityViolationException exception = new DataIntegrityViolationException("could not execute", sqlException);

        assertThat(DataIntegrityConstraints.violates(exception, DataIntegrityConstraints.IDEMPOTENCY_KEY)).isFalse();
        assertThat(DataIntegrityConstraints.violates(exception, DataIntegrityConstraints.REQUEST_NO)).isFalse();
    }

    @Test
    void readsHibernateConstraintName() {
        DataIntegrityViolationException exception = new DataIntegrityViolationException(
                "could not execute",
                new ConstraintViolationException("duplicate", new SQLException("duplicate"), "uk_payment_requests_idempotency_key")
        );

        assertThat(DataIntegrityConstraints.violates(exception, DataIntegrityConstraints.IDEMPOTENCY_KEY)).isTrue();
    }
}
