package cs.vsb.mappers;

import cs.vsb.domain.Participant;
import cs.vsb.domain.Payment;
import cs.vsb.domain.Race;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PaymentMapper {

    private final Connection connection;
    private final RaceMapper raceMapper;
    private final UserMapper userMapper;

    public PaymentMapper(Connection connection) {
        this.connection = connection;
        this.raceMapper = new RaceMapper(connection);
        this.userMapper = new UserMapper(connection);
    }

    public void insert(Payment payment) throws SQLException {
        String sql = "INSERT INTO r_payment (race_id, participant_id, paid, payment_date) VALUES (?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setLong(1, payment.getRace().getId());
            stmt.setLong(2, payment.getParticipant().getId());
            stmt.setBoolean(3, payment.isPaid());
            stmt.setDate(4, Date.valueOf(payment.getDate()));

            stmt.executeUpdate();

            ResultSet keys = stmt.getGeneratedKeys();
            if (keys.next()) {
                payment.setId(keys.getLong(1));
            }
        }
    }

    public void update(Payment payment) throws SQLException {
        String sql = "UPDATE r_payment SET race_id=?, participant_id, paid=?, payment_date=? WHERE id=?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, payment.getRace().getId());
            stmt.setLong(2, payment.getParticipant().getId());
            stmt.setBoolean(3, payment.isPaid());
            stmt.setDate(4, Date.valueOf(payment.getDate()));
            stmt.setLong(5, payment.getId());
            stmt.executeUpdate();
        }
    }

    public void delete(Long id) throws SQLException {
        String sql = "DELETE FROM r_payment WHERE id=?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        }
    }

    public Payment findById(Long id) throws SQLException {
        String sql = "SELECT * FROM r_payment WHERE id=?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapRow(rs);
            }
        }
        return null;
    }

    public List<Payment> findAll() throws SQLException {
        String sql = "SELECT * FROM r_payment";
        List<Payment> payments = new ArrayList<>();
        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                payments.add(mapRow(rs));
            }
        }
        return payments;
    }

    private Payment mapRow(ResultSet rs) throws SQLException {
        Long id = rs.getLong("id");
        Long raceId = rs.getLong("race_id");
        Long participantId = rs.getLong("participant_id");
        boolean paid = rs.getBoolean("paid");
        Date date = rs.getDate("payment_date");

        Race race = raceMapper.findById(raceId);
        Participant participant = (Participant) userMapper.findById(participantId);

        Payment payment = new Payment(race, participant, paid, date.toLocalDate());
        payment.setId(id);
        return payment;
    }
}