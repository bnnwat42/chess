package dataaccess;

import model.*;

import java.util.HashMap;

public class MemoryUserDAO implements UserDAO {

    static final private HashMap<String, UserData> users = new HashMap<>();

    @Override
    public void createUser(UserData userInfo) throws AlreadyTakenException {
        if (users.get(userInfo.username()) != null) {
            throw new AlreadyTakenException();
        }

        var user = new UserData(userInfo.username(), userInfo.password(), userInfo.email());
        users.put(user.username(), user);
    }

    @Override
    public UserData getUser(String userName) {
        return users.get(userName);
    }

    public static void clear() {
        users.clear();
    }
}
