package cs.vsb.console;

import cs.vsb.domain.User;
import cs.vsb.orm.UnitOfWork;

import java.sql.SQLException;

public interface UserActionStrategy  {

    void displayMenu();

    void handleChoice(UnitOfWork uow, User currentUser) throws SQLException;
}
