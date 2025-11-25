package cs.vsb.orm;

import java.sql.SQLException;

public interface IUnitOfWork {
    public void begin();
    public void commit() throws SQLException;
    public void rollback();
    public void registerNew(Object object);
}
