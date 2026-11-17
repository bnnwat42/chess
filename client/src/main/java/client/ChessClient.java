package client;

import chess.ChessGame;
import model.GameData;
import model.UserData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ui.BoardRenderer;
import ui.EscapeSequences;

import java.sql.Statement;
import java.util.ArrayList;
import java.util.Scanner;

public class ChessClient {
    private static final Logger log = LoggerFactory.getLogger(ChessClient.class);
    private String userName;
    private STATE userState;
    private String authToken;
    private final ServerFacade facade;
    private Scanner scanner;
    int activeGameId = -1;
    ChessGame.TeamColor teamColor = null;

    public ChessClient(String host, int port) {
        userName = null;
        userState = STATE.LOGGED_OUT;
        facade = new ServerFacade(host, port);
    }

    public ChessClient() {
        userName = null;
        userState = STATE.LOGGED_OUT;
        facade = new ServerFacade("localhost", 8080);
        scanner = new Scanner(System.in);
    }

    public void serviceLoop() {
        System.out.print(EscapeSequences.SET_TEXT_COLOR_WHITE + EscapeSequences.RESET_BG_COLOR + EscapeSequences.ERASE_SCREEN);
        System.out.println("CS 240 Chess Client!!!");
        renderOptions();

        var response = new String[0];
        while (!userState.equals(STATE.QUIT)) {
            System.out.print("[" + userState + "] >>> ");
            response = scanner.nextLine().split(" ");
            if (userState == STATE.LOGGED_OUT) {
                loggedOutOptions(response);
            } else if (userState == STATE.LOGGED_IN) {
                loggedInOptions(response);
            } else if (userState == STATE.IN_GAME) {
                gameOptions(response);
            } else if (userState == STATE.OBSERVING) {
                observerOptions(response);
            }
        }
        facade.logoutUser(authToken);
    }

    private void observerOptions(String[] response) {
        if (response[0].equalsIgnoreCase("help")) {
            help(response);
        } else if (response[0].equalsIgnoreCase("quit")) {
            activeGameId = -1;
            teamColor = null;
            changeState(STATE.LOGGED_IN);
        } else {
            GameData gameData = null;
            try {
                gameData = facade.getGame(activeGameId);
            } catch (Exception e) {
                System.out.println("Could not find chess game");
            }
            BoardRenderer.renderBoard(gameData.game(), ChessGame.TeamColor.WHITE);
        }
    }

    private void gameOptions(String[] response) {
        if (response[0].equalsIgnoreCase("help")) {
            help(response);
        } else if (response[0].equalsIgnoreCase("quit")) {
            activeGameId = -1;
            teamColor = null;
            changeState(STATE.LOGGED_IN);
        } else {
            GameData gameData = null;
            try {
                gameData = facade.getGame(activeGameId);
            } catch (Exception e) {
                System.out.println("Could not find chess game");
            }
            BoardRenderer.renderBoard(gameData.game(), teamColor);
        }
    }

    private void loggedInOptions(String[] response) {
        if (response[0].equalsIgnoreCase("help")) {
            help(response);
        } else if (response[0].equalsIgnoreCase("quit")) {
            logout(response);
            userState = STATE.QUIT;
        } else if (response[0].equalsIgnoreCase("create")) {
            create(response);
        } else if (response[0].equalsIgnoreCase("list")) {
            list(response);
        } else if (response[0].equalsIgnoreCase("join")) {
            joinOrObserve(response);
        } else if (response[0].equalsIgnoreCase("observe")) {
            joinOrObserve(response);
        } else if (response[0].equalsIgnoreCase("logout")) {
            logout(response);
        } else {
            System.out.println("Invalid command");
        }
    }

    private void joinOrObserve(String[] response) {
        if (response[0].equalsIgnoreCase("join") && response.length != 3) {
            System.out.println("Wrong number of arguments");
            return;
        } else if (response[0].equalsIgnoreCase("observe") && response.length != 2) {
            System.out.println("Wrong number of arguments");
            return;
        }
        ArrayList<GameData> gameList;
        try {
            gameList = facade.listGames(authToken);
        } catch (RuntimeException e) {
            return;
        }

        String numResponse = response[1];
        try {
            int gameNum = Integer.parseInt(numResponse);
            if (gameList.size() < gameNum || gameNum <= 0) {
                System.out.println("Please enter valid game number");
                return;
            }
            GameData game = gameList.get(gameNum - 1);
            if (response[0].equalsIgnoreCase("observe")) {
                activeGameId = gameList.get(gameNum - 1).gameID();
                System.out.println("observing");
                changeState(STATE.OBSERVING);
                GameData gameData = null;
                observerOptions(new String[]{""});
                renderOptions();
                return;
            }
            joinGameNoNum(gameNum, game, gameList, response[2]);
        } catch (NumberFormatException e) {
            System.out.println("Please enter valid game number");
        }
    }

    private void list(String[] response) {
        if (response.length != 1) {
            System.out.println("Wrong number of arguments");
            return;
        }
        ArrayList<GameData> gameList;
        try {
            gameList = facade.listGames(authToken);
        } catch (RuntimeException e) {
            return;
        }
        if (gameList.isEmpty()) {
            System.out.println("No games exist. Please create one.");
        }
        int counter = 1;
        for (GameData game : gameList) {
            System.out.print(counter++ + ". ");
            System.out.print(game.gameName());
            System.out.print(" white: " + game.whiteUsername());
            System.out.println(" black: " + game.blackUsername());
        }
    }

    private void joinGameNoNum(int gameNum, GameData game, ArrayList<GameData> gameList, String color) {

        if (game.blackUsername() != null && game.whiteUsername() != null) {
            System.out.println("Sorry, all spots filled");
            return;
        }
        if (color.equalsIgnoreCase("W") ||
                color.equalsIgnoreCase("white")) {
            if (game.whiteUsername() != null) {
                System.out.println("White already taken. Please choose new color.");
                return;
            }
            try {
                facade.joinGame(authToken, gameList.get(gameNum - 1).gameID(), ChessGame.TeamColor.WHITE);
                teamColor = ChessGame.TeamColor.WHITE;
            } catch (RuntimeException e) {
                return;
            }
        } else if (color.equalsIgnoreCase("B") ||
                color.equalsIgnoreCase("black")) {
            if (game.blackUsername() != null) {
                System.out.println("Black already taken. Please choose new color.");
                return;
            }
            try {
                facade.joinGame(authToken, gameList.get(gameNum - 1).gameID(), ChessGame.TeamColor.BLACK);
                teamColor = ChessGame.TeamColor.BLACK;
            } catch (RuntimeException e) {
                return;
            }
        } else {
            System.out.println("Please enter a valid color");
            return;
        }
        activeGameId = gameList.get(gameNum - 1).gameID();
        System.out.println("joined");
        changeState(STATE.IN_GAME);
        GameData gameData = null;
        gameOptions(new String[]{""});
    }

    private void create(String[] response) {
        if (response.length != 2) {
            System.out.println("Wrong number of arguments");
            return;
        }
        try {
            facade.createGame(authToken, response[1]);
            System.out.println("Created");
        } catch (RuntimeException e) {
            return;
        }
    }

    private void loggedOutOptions(String[] response) {
        if (response[0].equalsIgnoreCase("help")) {
            help(response);
        } else if (response[0].equalsIgnoreCase("quit")) {
            userState = STATE.QUIT;
        } else if (response[0].equalsIgnoreCase("login")) {
            login(response);
        } else if (response[0].equalsIgnoreCase("register")) {
            register(response);
        } else if (response[0].equalsIgnoreCase("clear")) {
            try {
                facade.clearDatabase();
                System.out.println("Cleared database");
            } catch (RuntimeException e) {
                return;
            }
        } else {
            System.out.println("Invalid command");
        }
    }

    private void logout(String[] response) {
        if (response.length != 1) {
            System.out.println("Wrong number of arguments");
            return;
        }
        try {
            facade.logoutUser(authToken);
            authToken = "";
            userName = "";
            changeState(STATE.LOGGED_OUT);
        } catch (RuntimeException e) {
            return;
        }
    }

    private void login(String[] response) {
        if (response.length != 3) {
            System.out.println("Wrong number of arguments");
            return;
        }
        try {
            String returnedToken = facade.loginUser(new UserData(response[1], response[2], null));
            changeState(STATE.LOGGED_IN);
            userName = response[1];
            authToken = returnedToken;
            renderOptions();
        } catch (RuntimeException e) {
            return;
        }
    }

    private void register(String[] response) {
        if (response.length != 4) {
            System.out.println("Wrong number of arguments");
            return;
        }
        try {
            String returnedToken = facade.registerUser(new UserData(response[1], response[2], response[3]));
            changeState(STATE.LOGGED_IN);
            userName = response[1];
            authToken = returnedToken;
            renderOptions();
        } catch (RuntimeException e) {
            return;
        }
    }

    private void help(String[] response) {
        if (response.length == 1) {
            renderOptions();
        } else if (userState.equals(STATE.LOGGED_OUT)) {
            switch (response[1]) {
                case "register" -> System.out.println("register <USERNAME> <PASSWORD> <EMAIL> - create an account");
                case "login" -> System.out.println("login <USERNAME> <PASSWORD> - login into an account");
                case "quit" -> System.out.println("quit - quit playing chess");
                case "help" ->
                        System.out.println("help - print all available actions\nhelp <ACTION> - learn about action");
                default -> System.out.printf("unknow command %s\n", response[1]);
            }
        } else if (userState.equals(STATE.LOGGED_IN)) {
            switch (response[0]) {
                case "create" -> System.out.println("create <NAME> - create a new game with the given name");
                case "list" -> System.out.println("list - list all games and their IDs");
                case "join" ->
                        System.out.println("join <ID> [WHITE|BLACK] - join a game as white or black, or let the server assign a color");
                case "observe" -> System.out.println("observe <ID> - watch a game in progress");
                case "logout" -> System.out.println("logout - log out");
                case "quit" -> System.out.println("quit - quit playing chess");
                case "help" ->
                        System.out.println("help - print all available actions\nhelp <ACTION> - learn about action");
                default -> System.out.printf("unknown command %s\n", response[1]);
            }
        } else if (userState.equals(STATE.IN_GAME)) {
            switch (response[0]) {
                case "quit" -> System.out.println("quit - leave game");
                case "help" ->
                        System.out.println("help - print all available actions\nhelp <ACTION> - learn about action");
                default -> System.out.printf("unknown command %s\n", response[1]);
            }
        }
    }

    private void renderOptions() {
        System.out.println("Available commands: ");
        if (userState.equals(STATE.LOGGED_OUT)) {
            System.out.println("register <USERNAME> <PASSWORD> <EMAIL>");
            System.out.println("login <USERNAME> <PASSWORD>");
            System.out.println("quit");
            System.out.println("help");
        } else if (userState.equals(STATE.LOGGED_IN)) {
            System.out.println("create <NAME>");
            System.out.println("list");
            System.out.println("join <ID> [(W)HITE|(B)LACK]");
            System.out.println("observe <ID>");
            System.out.println("logout");
            System.out.println("quit");
            System.out.println("help");
        } else if (userState.equals(STATE.IN_GAME)) {
            System.out.println("quit");
            System.out.println("help");
            System.out.println("Coming soon: make a move");
        } else if (userState.equals(STATE.OBSERVING)) {
            System.out.println("quit");
            System.out.println("help");
        }
    }

    private void changeState(STATE newState) {
        userState = newState;
        renderOptions();
    }

}
