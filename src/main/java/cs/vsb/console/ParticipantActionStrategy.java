package cs.vsb.console;

import cs.vsb.domain.*;
import cs.vsb.orm.UnitOfWork;
import cs.vsb.service.*;
import cs.vsb.value.Location;

import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;

public class ParticipantActionStrategy implements UserActionStrategy {
    private final RaceEntryService raceEntryService;
    private final UserService userService;
    private final RaceService raceService;
    private final RacerService racerService;
    private final CategoryService categoryService;

    public ParticipantActionStrategy(RaceEntryService raceEntryService, RacerService racerService,UserService userService,RaceService raceService,CategoryService categoryService) throws SQLException {
        this.racerService = racerService;
        this.userService = userService;
        this.raceService = raceService;
        this.categoryService = categoryService;
        this.raceEntryService = raceEntryService;
    }

    @Override
    public void displayMenu() {
        System.out.println("----------------------------------------");
        System.out.println("You are logged in as an Participant.");
        System.out.println("1. Show all Races");
        System.out.println("2. Show all Categories");
        System.out.println("3. Show all Racers");
        System.out.println("4. Show all Race Entries");
        System.out.print("Enter choice: ");
    }
    @Override
    public void handleChoice(UnitOfWork uow, User currentUser) throws SQLException {
        Participant participant = (Participant) currentUser;
        Scanner sc = new Scanner(System.in);
        switch(sc.nextInt()){
            case 1:
                List<Race> races = raceService.getAllRaces();
                races.forEach(System.out::println);
                break;
            case 2:
                List<Category> categories = categoryService.getAllCategories();
                categories.forEach(System.out::println);
                break;
            case 3:
                List<Racer> racers = racerService.getAllRacers();
                racers.forEach(System.out::println);
                break;
            case 4:
                List<RaceEntry> raceEntries = raceEntryService.getAllRaceEntries();
                raceEntries.forEach(System.out::println);
                break;
            default:
                System.out.println("Invalid Choice");
                break;
        }
    }
}
