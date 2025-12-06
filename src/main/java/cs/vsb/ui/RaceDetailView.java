package cs.vsb.ui;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import cs.vsb.console.UserActionStrategy;
import cs.vsb.domain.Race;
import cs.vsb.domain.RaceEntry;
import cs.vsb.service.RaceEntryService;
import cs.vsb.service.RaceService;

import java.sql.SQLException;
import java.util.List;

@Route(value = "race", layout = MainLayout.class)
public class RaceDetailView extends VerticalLayout implements HasUrlParameter<Long> {

    private final RaceService raceService;
    private final RaceEntryService raceEntryService;

    public RaceDetailView() throws SQLException {
        this.raceService = new RaceService();
        this.raceEntryService = new RaceEntryService();
        setSizeFull();
    }

    @Override
    public void setParameter(BeforeEvent event, Long raceId) {
        UserActionStrategy strategy = VaadinSession.getCurrent().getAttribute(UserActionStrategy.class);
        if (strategy == null) {
            UI.getCurrent().navigate(LoginView.class);
            return;
        }

        try {
            // 1. Fetch Context Data
            Race race = raceService.getRace(raceId);
            List<RaceEntry> entries = raceEntryService.getAllRaceEntries().stream()
                    .filter(e -> e.getRace().getId().equals(raceId))
                    .toList();

            removeAll();

            // 2. Header
            add(new H1(race.getName()));
            add(new H3(race.getLocation().getCity()));

            // 3. Delegate to Strategy
            add(strategy.renderRaceDetail(race, entries));

        } catch (SQLException e) {
            Notification.show("Error: " + e.getMessage());
        }
    }
}