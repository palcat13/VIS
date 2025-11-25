package cs.vsb.ui;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import cs.vsb.domain.Race;
import cs.vsb.service.RaceService;

import java.sql.SQLException;

/**
 * The main view contains a text field for searching and a grid for displaying races.
 * Accessible at http://localhost:8080/
 */
@Route("")
public class MainView extends VerticalLayout {

    private RaceService raceService;
    private Grid<Race> grid = new Grid<>(Race.class, false);

    public MainView() {
        setSizeFull();

        // 1. Initialize Service (Manual injection since no Frameworks allowed)
        try {
            this.raceService = new RaceService();
        } catch (SQLException e) {
            add(new com.vaadin.flow.component.html.H1("Database Error: " + e.getMessage()));
            return;
        }

        // 2. Configure Grid Columns
        grid.addColumn(Race::getId).setHeader("ID").setAutoWidth(true);
        grid.addColumn(Race::getName).setHeader("Name");
        grid.addColumn(r -> r.getLocation().getCity()).setHeader("City");
        grid.addColumn(Race::getDate).setHeader("Date");

        // 3. Add a Refresh Button
        Button refreshBtn = new Button("Refresh Data", e -> updateList());

        add(refreshBtn, grid);

        // 4. Load data
        updateList();
    }

    private void updateList() {
        try {
            grid.setItems(raceService.getAllRaces());
        } catch (SQLException e) {
            Notification.show("Error fetching races: " + e.getMessage());
        }
    }
}