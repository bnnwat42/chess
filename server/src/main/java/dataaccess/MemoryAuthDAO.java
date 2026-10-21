package dataaccess;

import model.AuthData;

import java.util.HashMap;

public class MemoryAuthDAO implements AuthDAO {

    static final HashMap<String, AuthData> authStorage = new HashMap<>();

    @Override
    public void createAuth(AuthData authData) {
        authStorage.put(authData.authToken(), authData);
    }

    @Override
    public AuthData getAuth(String authToken) {
        return authStorage.get(authToken);
    }

    @Override
    public void deleteAuth(String authToken) {
        authStorage.remove(authToken);
    }

    public static void clear() {
        authStorage.clear();
    }


}
