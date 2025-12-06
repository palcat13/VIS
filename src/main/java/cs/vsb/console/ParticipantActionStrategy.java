package cs.vsb.console;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.server.StreamResource;
import cs.vsb.domain.Participant;
import cs.vsb.domain.Race;
import cs.vsb.domain.RaceEntry;
import cs.vsb.service.RaceService;
import cs.vsb.ui.RaceDetailView;
import cs.vsb.ui.RaceRegistrationView;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.List;

public class ParticipantActionStrategy implements UserActionStrategy {

    private final Participant participant;
    private final RaceService raceService;

    public ParticipantActionStrategy(Participant participant, RaceService raceService) {
        this.participant = participant;
        this.raceService = raceService;
    }

    // --- DASHBOARD LOGIC ---
    @Override
    public Component renderDashboard() {
        VerticalLayout root = new VerticalLayout();
        Grid<Race> grid = new Grid<>(Race.class, false);
        grid.addColumn(Race::getName).setHeader("Závod");
        grid.addColumn(Race::getDate).setHeader("Datum");

        // Action Column: Details or Register
        grid.addComponentColumn(race -> {
            HorizontalLayout actions = new HorizontalLayout();

            Button registerBtn = new Button("Registrace");
            registerBtn.addClickListener(e -> UI.getCurrent().navigate(RaceRegistrationView.class, race.getId()));

            Button detailsBtn = new Button("Výsledky");
            detailsBtn.addClickListener(e -> UI.getCurrent().navigate(RaceDetailView.class, race.getId()));

            actions.add(registerBtn, detailsBtn);
            return actions;
        });

        try {
            grid.setItems(raceService.getAllRaces());
        } catch (SQLException e) {
            Notification.show("Error: " + e.getMessage());
        }

        root.add(new H3("Dostupné závody"), grid);
        return root;
    }

    // --- RACE DETAIL LOGIC ---
    @Override
    public Component renderRaceDetail(Race race, List<RaceEntry> entries) {
        VerticalLayout layout = new VerticalLayout();
        layout.setPadding(false);

        if (!race.isPublished()) {
            layout.add(new H3("Výsledky zatím nejsou zveřejněny."));
            return layout;
        }

        Grid<RaceEntry> grid = new Grid<>(RaceEntry.class, false);
        grid.addColumn(RaceEntry::getPlace).setHeader("Umístění");
        grid.addColumn(e -> e.getRacer().getFname() + " " + e.getRacer().getLname()).setHeader("Závodník");
        grid.addColumn(RaceEntry::getRaceTime).setHeader("Čas (ms)");

        grid.addComponentColumn(entry -> {
            Button download = new Button(VaadinIcon.DOWNLOAD.create());
            Anchor anchor = new Anchor(new StreamResource("diplom.txt", () ->
                    new ByteArrayInputStream(("Diplom pro " + entry.getRacer().getLname()).getBytes(StandardCharsets.UTF_8))), "");
            anchor.add(download);
            anchor.getElement().setAttribute("download", true);
            return anchor;
        });

        grid.setItems(entries);
        layout.add(grid);
        return layout;
    }
}