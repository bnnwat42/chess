package dataaccess.interfaces;

import exceptions.AlreadyTakenException;
import exceptions.DataAccessException;
import model.UserData;

public interface UserDAO {

    void createUser(UserData userInfo) throws AlreadyTakenException, DataAccessException;

    UserData getUser(String userName) throws DataAccessException;

    void clear() throws DataAccessException;
}
