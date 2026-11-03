package dataaccess.interfaces;

import exceptions.AlreadyTakenException;
import model.UserData;

public interface UserDAO {

    void createUser(UserData userInfo) throws AlreadyTakenException;

    UserData getUser(String userName);

    void clear();
}
