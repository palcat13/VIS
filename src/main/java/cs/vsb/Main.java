package cs.vsb;

import cs.vsb.console.OrganizerActionStrategy;
import cs.vsb.console.ParticipantActionStrategy;
import cs.vsb.console.UserActionStrategy;
import cs.vsb.db.DatabaseInitializer;
import cs.vsb.domain.*;
import cs.vsb.orm.UnitOfWork;
import cs.vsb.service.*;
import cs.vsb.value.Location;

import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

public class Main {

    private static UserActionStrategy currentStrategy;

    private static UserActionStrategy determineStrategy(User user, RacerService racerService, RaceService raceService, RaceEntryService raceEntryService, CategoryService categoryService,UserService userService) throws SQLException {
        if (user instanceof Organizer organizer) {
            return new OrganizerActionStrategy(raceEntryService,racerService,userService,raceService,categoryService);
        } else if (user instanceof Participant participant) {
            return new ParticipantActionStrategy(raceEntryService,racerService,userService,raceService,categoryService);
        }
        return null;
    }

    public static void main(String[] args) throws SQLException {
        DatabaseInitializer.init();
        Scanner sc = new Scanner(System.in);
        RacerService racerService = new RacerService();
        RaceService raceService = new RaceService();
        RaceEntryService raceEntryService = new RaceEntryService();
        CategoryService categoryService = new CategoryService();
        UserService userService = new UserService();


        UnitOfWork uow = new UnitOfWork(categoryService,raceEntryService,racerService,raceService,userService);
        uow.begin();
        User currentUser = null;

        while(true) {
            System.out.println("Choose action :");
            System.out.println("1.Log in");
            System.out.println("2.Register");
            String action = sc.nextLine();
            if(action.equals("1")) {
                System.out.println("Please enter your username:");
                String username = sc.nextLine();
                System.out.println("Please enter your password:");
                String password = sc.nextLine();
                currentUser = userService.logIn(username, password);
                break;
            }
            else if (action.equals("2")) {
                System.out.println("Please enter your username:");
                String username = sc.nextLine();
                System.out.println("Please enter your password:");
                String password = sc.nextLine();
                System.out.println("Please enter your email address:");
                String email = sc.nextLine();
                System.out.println("Are you participant/organizer:");
                String userType = sc.nextLine();
                if (Objects.equals(userType, "organizer")){
                    currentUser = new Organizer(username,password,email);
                }
                else if (Objects.equals(userType, "participant")){
                    currentUser = new Participant(username,password,email,null);
                }
                uow.registerNew(currentUser);
                uow.commit();
                System.out.println("Registered!");
            }
        }


        currentStrategy = determineStrategy(currentUser, racerService, raceService, raceEntryService, categoryService,userService);


        System.out.println("Logged in with id " + currentUser.getId());
        while(true){
            currentStrategy.displayMenu();
            currentStrategy.handleChoice(uow, currentUser);
        }
    }
}