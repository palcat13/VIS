package cs.vsb.ui;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import cs.vsb.console.UserActionStrategy;

@Route(value = "", layout = MainLayout.class)
public class MainView extends VerticalLayout implements BeforeEnterObserver {

    public MainView() {
        setSizeFull();
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        UserActionStrategy strategy = VaadinSession.getCurrent().getAttribute(UserActionStrategy.class);

        if (strategy == null) {
            // Logic handled in MainLayout, but double check
            return;
        }

        removeAll();
        // Delegate rendering to the cached strategy
        add(strategy.renderDashboard());
    }
}