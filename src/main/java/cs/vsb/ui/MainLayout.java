package cs.vsb.ui;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.RouterLayout;
import com.vaadin.flow.server.VaadinSession;
import cs.vsb.console.OrganizerActionStrategy;
import cs.vsb.console.ParticipantActionStrategy;
import cs.vsb.console.TimekeeperActionStrategy;
import cs.vsb.console.UserActionStrategy;
import cs.vsb.domain.Organizer;
import cs.vsb.domain.Participant;
import cs.vsb.domain.Timekeeper;
import cs.vsb.domain.User;

public class MainLayout extends VerticalLayout implements RouterLayout, BeforeEnterObserver {

    private User loggedUser;

    public MainLayout() {
        setSizeFull();
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        // 1. Shared Authentication Check
        loggedUser = VaadinSession.getCurrent().getAttribute(User.class);

        if (loggedUser == null) {
            event.rerouteTo(LoginView.class);
            return;
        }



        // 2. Ensure Header is created only once or updated
        // (For simplicity, we recreate it to ensure correct user info)
        removeAll();
        createHeader();
    }

    private void createHeader() {
        HorizontalLayout header = new HorizontalLayout();
        header.setWidthFull();
        header.setJustifyContentMode(JustifyContentMode.BETWEEN);
        header.setAlignItems(Alignment.CENTER);

        H1 title = new H1("Race Management System");
        // Click title to go to Dashboard
        title.addClickListener(e -> UI.getCurrent().navigate(MainView.class));
        title.getStyle().set("cursor", "pointer");

        String role = loggedUser != null ? loggedUser.getRole() : "";
        H3 userInfo = new H3(loggedUser.getUsername() + " (" + role + ")");

        Button logoutBtn = new Button("Odhlásit", e -> logout());
        logoutBtn.addThemeVariants(ButtonVariant.LUMO_ERROR);

        HorizontalLayout rightSide = new HorizontalLayout(userInfo, logoutBtn);
        rightSide.setAlignItems(Alignment.CENTER);

        header.add(title, rightSide);
        add(header);
    }

    private void logout() {
        VaadinSession.getCurrent().setAttribute(User.class, null);
        UI.getCurrent().navigate(LoginView.class);
    }
}