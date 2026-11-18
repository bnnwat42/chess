package client;

import chess.ChessGame;
import client.ServerFacade;
import dataaccess.sql.DatabaseManager;
import exceptions.AlreadyTakenException;
import exceptions.DataAccessException;
import model.UserData;
import org.junit.jupiter.api.*;
import server.Server;

import java.sql.SQLException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;
    private ServerFacade facade;
    private static int port;
    private static String authToken;

    @BeforeAll
    public static void init() {
        server = new Server();
        port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @BeforeEach
    public void createFacade() {
        facade = new ServerFacade("localhost", port);
        facade.clearDatabase();
        UserData newUser = new UserData("Bob", "1234", "gocougs@byu.edu");
        authToken = facade.registerUser(newUser);
    }

    private boolean checkIfEmpty() throws SQLException, DataAccessException {
        var response = DatabaseManager.getFromDatabase(
                "SELECT NOT EXISTS (SELECT 1 FROM users) AS isEmpty;"
        );
        Map<String, String> userMap = response.getFirst();
        return userMap.get("isEmpty").equals("1");
    }

    private boolean existsAuth(String authToken) throws SQLException, DataAccessException {
        String retrivalString = "SELECT EXISTS (SELECT 1 FROM authTokens WHERE authToken = ?) AS doesExist;";
        var response = DatabaseManager.getFromDatabase(retrivalString, authToken);
        return response.getFirst().get("doesExist").equals("1");
    }

    @Test
    public void clearDatabase() {
        facade.clearDatabase();
        assertTrue(assertDoesNotThrow(this::checkIfEmpty));
    }

    @Test
    public void registerUser() {
        assertTrue(assertDoesNotThrow(() -> existsAuth(authToken)));
        UserData duplicateUser = new UserData("Bob", "1234", "gocougs@byu.edu");
        assertThrows(RuntimeException.class, () -> facade.registerUser(duplicateUser));
    }

    @Test
    public void loginUser() {
        UserData newUser = new UserData("Rob", "4321", "gocougs@byu.edu");
        facade.registerUser(newUser);
        var authToken = facade.loginUser(newUser);
        assertTrue(assertDoesNotThrow(() -> existsAuth(authToken)));
        assertThrows(RuntimeException.class, () -> facade.logoutUser("randomAuth"));
    }

    @Test
    public void logoutUser() {
        UserData newUser = new UserData("Rob", "4321", "gocougs@byu.edu");
        facade.registerUser(newUser);
        var authToken = facade.loginUser(newUser);
        facade.logoutUser(authToken);
        assertFalse(assertDoesNotThrow(() -> existsAuth(authToken)));
    }

    @Test
    public void listGames() {
        for (int i = 0; i < 5; i++) {
            facade.createGame(authToken, "Bob" + i);
        }
        var games = facade.listGames(authToken);
        assertEquals(5, games.size());
    }

    @Test
    public void createGame() {
        String gameName = "Bob's game";
        facade.createGame(authToken, gameName);
        String retrivalString = "SELECT EXISTS (SELECT 1 FROM games WHERE gameName = ?) AS doesExist;";
        var response = assertDoesNotThrow(() -> DatabaseManager.getFromDatabase(retrivalString, gameName));
        assertEquals("1", response.getFirst().get("doesExist"));
    }

    @Test
    public void joinGame() {
        String gameName = "Bob's game";
        int gameId = facade.createGame(authToken, gameName);
        facade.joinGame(authToken, gameId, ChessGame.TeamColor.WHITE);
        String retrivalString = "SELECT EXISTS (SELECT 1 FROM games WHERE whiteUsername = ?) AS doesExist;";
        var response = assertDoesNotThrow(() -> DatabaseManager.getFromDatabase(retrivalString, "Bob"));
        assertEquals("1", response.getFirst().get("doesExist"));
    }

    @Test
    void getGame() {
        String gameName = "Bob's game";
        int gameId = facade.createGame(authToken, gameName);
        facade.getGame(gameId);
        assertThrows(RuntimeException.class, () -> facade.getGame(-1));
    }
}
