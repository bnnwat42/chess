package dataaccess;

import model.*;

import java.util.HashMap;

public class MemoryUserDAO implements UserDAO {

    final private HashMap<String, UserData> users = new HashMap<>();

    @Override
    public void createUser(UserData userInfo) {
        var user = new UserData(userInfo.username(), userInfo.password(), userInfo.email());

        users.put(user.username(), user);
    }

    @Override
    public UserData getUser(String userName) {
        return users.get(userName);
    }
}
