package dataaccess;

import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MemoryAuthDAOTest {

    @Test
    void createAuthAndGetAuth() {
        String authToken = "abc123";
        String username = "Bob";

        MemoryAuthDAO memoryStorage = new MemoryAuthDAO();
        AuthData newAuth = new AuthData(authToken, username);
        memoryStorage.createAuth(newAuth);

        AuthData returnedAuth = memoryStorage.getAuth(authToken);

        assertEquals(authToken, returnedAuth.authToken());
        assertEquals(username, returnedAuth.username());
    }

    @Test
    void deleteAuth() {
        String authToken = "abc123";
        String username = "Bob";

        MemoryAuthDAO memoryStorage = new MemoryAuthDAO();
        AuthData newAuth = new AuthData(authToken, username);
        memoryStorage.createAuth(newAuth);
        
        memoryStorage.deleteAuth(authToken);

        assertNull(memoryStorage.getAuth(authToken));
    }
}