package dataaccess;

import model.AuthData;

import java.util.HashMap;

public class MemoryAuthDAO implements AuthDAO {

    static final HashMap<String, AuthData> AUTH_STORAGE = new HashMap<>();

    @Override
    public void createAuth(AuthData authData) {
        AUTH_STORAGE.put(authData.authToken(), authData);
    }

    @Override
    public AuthData getAuth(String authToken) {
        return AUTH_STORAGE.get(authToken);
    }

    @Override
    public void deleteAuth(String authToken) {
        AUTH_STORAGE.remove(authToken);
    }

    public void clear() {
        AUTH_STORAGE.clear();
    }


}
