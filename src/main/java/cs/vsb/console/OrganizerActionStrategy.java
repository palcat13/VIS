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

public class OrganizerActionStrategy implements UserActionStrategy {
    private final RaceEntryService raceEntryService;
    private final UserService userService;
    private final RaceService raceService;
    private final RacerService racerService;
    private final CategoryService categoryService;

    public OrganizerActionStrategy(RaceEntryService raceEntryService, RacerService racerService,UserService userService,RaceService raceService,CategoryService categoryService) throws SQLException {
        this.racerService = racerService;
        this.userService = userService;
        this.raceService = raceService;
        this.categoryService = categoryService;
        this.raceEntryService = raceEntryService;
    }

    @Override
    public void displayMenu() {
        System.out.println("----------------------------------------");
        System.out.println("You are logged in as an Organizer.");
        System.out.println("1. Create Race");
        System.out.println("2. Create Category");
        System.out.println("3. Create Racer");
        System.out.println("4. Create Race Entry");
        System.out.println("5. Show all Races");
        System.out.println("6. Show all Categories");
        System.out.println("7. Show all Racers");
        System.out.println("8. Show all Race Entries");
        System.out.println("9. Save Changes (Commit)");
        System.out.println("10. Discard Changes (Rollback)");
        System.out.print("Enter choice: ");
    }
    @Override
    public void handleChoice(UnitOfWork uow, User currentUser) throws SQLException {
        Organizer organizer = (Organizer) currentUser;
        Scanner sc = new Scanner(System.in);
        switch(sc.nextInt()){
            case 1:
                System.out.println("Enter Race Name: ");
                String raceName = sc.next();
                System.out.println("Enter Race Location: [city,country]");
                String raceLocation = sc.next();
                Location location = new Location(raceLocation.split(",")[0],raceLocation.split(",")[1]);
                System.out.println("Enter Race Date: [\"yyyy-mm-dd\"]");
                String raceDate = sc.next();
                Race race = new Race(raceName,location, LocalDate.parse(raceDate),organizer);

                if (!race.isValid()) {
                    System.out.println("Chyba: " + race.getValidationErrors());
                    break;
                }

                uow.registerNew(race);
                System.out.println("Race Created Successfully");
                break;
            case 2:
                System.out.println("Enter Category Name: ");
                String categoryName = sc.next();
                System.out.println("Enter Category Year From: ");
                int categoryYearFrom = sc.nextInt();
                System.out.println("Enter Category Year To: ");
                int categoryYearTo = sc.nextInt();
                System.out.println("Enter Category Gender: ");
                String categoryGender = sc.next();
                Category category = new Category(categoryName,categoryYearFrom,categoryYearTo,categoryGender);

                if (!category.isValid()) {
                    System.out.println("Chyba: " + category.getValidationErrors());
                    break;
                }

                uow.registerNew(category);
                System.out.println("Category Created Successfully");
                break;
            case 3:
                System.out.println("Enter Racer First Name: ");
                String racerFirstName = sc.next();
                System.out.println("Enter Racer Last Name: ");
                String racerLastName = sc.next();
                System.out.println("Enter Racer Birth Year: ");
                int racerBirthYear = sc.nextInt();
                System.out.println("Enter Racer Gender: ");
                String racerGender = sc.next();
                Racer racer = new Racer(racerFirstName,racerLastName,racerBirthYear,racerGender);

                if (!racer.isValid()) {
                    System.out.println("Chyba: " + racer.getValidationErrors());
                    break;
                }

                uow.registerNew(racer);
                System.out.println("Racer Created Successfully");
                break;
            case 4:
                System.out.println("Enter Racer id: ");
                Long racerId = sc.nextLong();
                Racer racer_selected = racerService.getRacer(racerId);
                System.out.println("Enter Race id: ");
                Long raceId = sc.nextLong();
                Race race_selected = raceService.getRace(raceId);
                System.out.println("Enter Start time: ");
                String startTime = sc.next();
                System.out.println("Enter End time: ");
                String endTime = sc.next();
                RaceEntry raceEntry = new RaceEntry(race_selected,racer_selected, Duration.between(LocalDateTime.parse(startTime),LocalDateTime.parse(endTime)).toMillis());

                if (!raceEntry.isValid()) {
                    System.out.println("Chyba: " + raceEntry.getValidationErrors());
                    break;
                }

                uow.registerNew(raceEntry);
                System.out.println("Race Entry Created Successfully");
                break;
            case 5:
                List<Race> races = raceService.getAllRaces();
                races.forEach(System.out::println);
                break;
            case 6:
                List<Category> categories = categoryService.getAllCategories();
                categories.forEach(System.out::println);
                break;
            case 7:
                List<Racer> racers = racerService.getAllRacers();
                racers.forEach(System.out::println);
                break;
            case 8:
                List<RaceEntry> raceEntries = raceEntryService.getAllRaceEntries();
                raceEntries.forEach(System.out::println);
                break;
            case 9:
                uow.commit();
                break;
            case 10:
                uow.rollback();
            default:
                System.out.println("Invalid Choice");
                break;
        }
    }
}
