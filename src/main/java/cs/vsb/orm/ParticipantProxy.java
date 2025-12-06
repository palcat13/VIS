package cs.vsb.orm;

import cs.vsb.domain.Participant;
import cs.vsb.domain.Racer;
import cs.vsb.mappers.RacerMapper;

import java.sql.SQLException;

public class ParticipantProxy extends Participant {

    private final Long racerId;
    private final RacerMapper racerMapper;
    private boolean isLoaded = false;

    public ParticipantProxy(Long id, String username, String password, String email, Long racerId, RacerMapper racerMapper) {
        super(id, username, password, email, null);
        this.racerId = racerId;
        this.racerMapper = racerMapper;
    }

    @Override
    public Racer getRacer() {
        if (!isLoaded) {
            if (super.getRacer() == null && racerId != null) {
                try {
                    Racer fetchedRacer = racerMapper.findById(racerId);
                    super.setRacer(fetchedRacer);
                } catch (SQLException e) {
                    throw new RuntimeException("Lazy loading failed for Racer ID: " + racerId, e);
                }
            }
            isLoaded = true;
        }
        return super.getRacer();
    }
}