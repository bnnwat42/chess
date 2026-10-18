package dataaccess;

import model.UserData;

public interface UserDAO {

    void createUser(UserData userInfo);

    Object getUser(String userName);
}
