package cs.vsb.console;

import com.vaadin.flow.component.Component;
import cs.vsb.domain.Race;
import cs.vsb.domain.RaceEntry;

import java.util.List;

public interface UserActionStrategy {
    // Renders the main dashboard (MainView)
    Component renderDashboard();

    // Renders the detail view for a specific race (RaceDetailView)
    Component renderRaceDetail(Race race, List<RaceEntry> entries);
}