package dataaccess;

import model.*;

import java.util.HashMap;

public class MemoryUserDAO implements UserDAO {

    static final private HashMap<String, UserData> USERS = new HashMap<>();

    @Override
    public void createUser(UserData userInfo) throws AlreadyTakenException {
        if (USERS.get(userInfo.username()) != null) {
            throw new AlreadyTakenException();
        }

        var user = new UserData(userInfo.username(), userInfo.password(), userInfo.email());
        USERS.put(user.username(), user);
    }

    @Override
    public UserData getUser(String userName) {
        return USERS.get(userName);
    }

    public void clear() {
        USERS.clear();
    }
}
