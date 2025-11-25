package cs.vsb.orm;

import cs.vsb.domain.*;
import cs.vsb.service.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Persistence;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UnitOfWork implements IUnitOfWork {
    List<Object> objects;
    CategoryService categoryService;
    RaceEntryService raceEntryService;
    RacerService racerService;
    RaceService raceService;
    UserService userService;

    public UnitOfWork(CategoryService categoryService, RaceEntryService raceEntryService, RacerService racerService, RaceService raceService,UserService userService) {
        this.categoryService = categoryService;
        this.raceEntryService = raceEntryService;
        this.racerService = racerService;
        this.raceService = raceService;
        this.userService = userService;
    }

    public void begin(){
        objects = new ArrayList<>();
    }

    public void registerNew(Object object){
        objects.add(object);
    }

    public void commit() throws SQLException {
        for(Object object : objects){
            if (object instanceof Category){
                categoryService.createCategory((Category) object);
            }
            if (object instanceof Racer){
                racerService.createRacer((Racer) object);
            }
            if (object instanceof Race){
                raceService.createRace((Race) object);
            }
            if (object instanceof RaceEntry){
                raceEntryService.createRaceEntry((RaceEntry) object);
            }
            if (object instanceof User){
                userService.createUser((User) object);
            }
        }
        objects.clear();
    }

    public void rollback(){
        objects.clear();
    }
}
