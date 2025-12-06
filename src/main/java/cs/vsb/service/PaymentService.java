package cs.vsb.service;

import cs.vsb.db.ConnectionManager;
import cs.vsb.domain.Payment;
import cs.vsb.domain.Racer;
import cs.vsb.mappers.PaymentMapper;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class PaymentService {
    private final PaymentMapper mapper;

    public PaymentService() throws SQLException {
        Connection connection = ConnectionManager.getConnection();
        this.mapper = new PaymentMapper(connection);
    }

    public void createPayment(Payment payment) throws SQLException {
        if (!payment.isValid()) {
            throw new IllegalArgumentException("Invalid payment: " + payment.getValidationErrors());
        }
        mapper.insert(payment);
    }

    public void updatePayment(Payment payment) throws SQLException {
        if (payment.getId() == null) {
            throw new IllegalArgumentException("Payment ID cannot be null for update.");
        }
        mapper.update(payment);
    }

    public void deletePayment(Long id) throws SQLException {
        mapper.delete(id);
    }

    public List<Payment> getAllPayments() throws SQLException {
        return mapper.findAll();
    }
}