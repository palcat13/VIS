package cs.vsb.orm;

import cs.vsb.domain.Organizer;
import cs.vsb.domain.Race;
import cs.vsb.mappers.UserMapper;
import cs.vsb.value.Location;

import java.sql.SQLException;
import java.time.LocalDate;

public class RaceProxy extends Race {

    private final Long organizerId;
    private final UserMapper userMapper;
    private boolean isLoaded = false;

    public RaceProxy(Long id, String name, Location location, LocalDate date, Long organizerId,double entryFee,boolean published, UserMapper userMapper) {
        super(id, name, location, date, null,entryFee,published);
        this.organizerId = organizerId;
        this.userMapper = userMapper;
    }

    @Override
    public Organizer getOrganizer() {
        if (!isLoaded) {
            if (super.getOrganizer() == null) {
                try {
                    Organizer fetchedOrganizer = (Organizer) userMapper.findById(organizerId);
                    super.setOrganizer(fetchedOrganizer);

                } catch (SQLException e) {
                    throw new RuntimeException("Lazy loading failed for Organizer ID: " + organizerId, e);
                }
            }
            isLoaded = true;
        }
        return super.getOrganizer();
    }
}