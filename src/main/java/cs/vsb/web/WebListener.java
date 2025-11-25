package cs.vsb.web;

import cs.vsb.db.DatabaseInitializer;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;

@jakarta.servlet.annotation.WebListener
public class WebListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("Web Server starting... Initializing Database.");
        DatabaseInitializer.init();
    }
}