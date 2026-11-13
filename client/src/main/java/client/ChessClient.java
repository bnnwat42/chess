package client;

import model.UserData;

import java.util.Arrays;
import java.util.Scanner;

public class ChessClient {
    private String userName;
    private STATE userState;
    private String authToken;
    ServerFacade facade;

    public ChessClient(String host, int port) {
        userName = null;
        userState = STATE.LOGGEDOUT;
        facade = new ServerFacade(host, port);
    }

    public ChessClient() {
        userName = null;
        userState = STATE.LOGGEDOUT;
        facade = new ServerFacade("localhost", 8080);
    }

    public void serviceLoop() {
        System.out.println("CS 240 Chess Client!!!");
        renderOptions();

        Scanner scanner = new Scanner(System.in);
        var response = new String[0];
        while (!userState.equals(STATE.QUIT)) {
            response = scanner.nextLine().split(" ");
            if (userState == STATE.LOGGEDOUT) {
                loggedOutOptions(response);
            } else if (userState == STATE.LOGGEDIN) {
                loggedInOptions(response);
            }
        }
    }

    private void loggedInOptions(String[] response) {
        if (response[0].equalsIgnoreCase("help")) {
            help(response);
        } else if (response[0].equalsIgnoreCase("quit")) {
            userState = STATE.QUIT;
        } else if (response[0].equalsIgnoreCase("create")) {
            create(response);
        } else if (response[0].equalsIgnoreCase("list")) {
            list(response);
        } else if (response[0].equalsIgnoreCase("join")) {
//            facade.clearDatabase();
        } else if (response[0].equalsIgnoreCase("observe")) {
//            facade.clearDatabase();
        } else if (response[0].equalsIgnoreCase("logout")) {
            logout(response);
        } else {
            System.out.println("Invalid command");
        }
    }

    private void list(String[] response) {
        if (response.length != 1) {
            System.out.println("Wrong number of arguments");
            return;
        }
        facade.listGames(authToken);
    }

    private void create(String[] response) {
        if (response.length != 2) {
            System.out.println("Wrong number of arguments");
            return;
        }
        facade.createGame(authToken, response[1]);
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
            facade.clearDatabase();
            System.out.println("Cleared database");
        } else {
            System.out.println("Invalid command");
        }
    }

    private void logout(String[] response) {
        if (response.length != 1) {
            System.out.println("Wrong number of arguments");
            return;
        }
        facade.logoutUser();
        authToken = "";
        userName = "";
        userState = STATE.LOGGEDOUT;
    }

    private void login(String[] response) {
        if (response.length != 3) {
            System.out.println("Wrong number of arguments");
            return;
        }
        String returnedToken = facade.loginUser(new UserData(response[1], response[2], null));
        userState = STATE.LOGGEDIN;
        userName = response[1];
        authToken = returnedToken;
        renderOptions();
    }

    private void register(String[] response) {
        if (response.length != 4) {
            System.out.println("Wrong number of arguments");
            return;
        }
        String returnedToken = facade.registerUser(new UserData(response[1], response[2], response[3]));
        userState = STATE.LOGGEDIN;
        userName = response[1];
        authToken = returnedToken;
        renderOptions();
    }

    private void help(String[] response) {
        if (response.length == 1) {
            renderOptions();
        } else if (userState.equals(STATE.LOGGEDOUT)) {
            switch (response[1]) {
                case "register" -> System.out.println("register <USERNAME> <PASSWORD> <EMAIL> - create an account");
                case "login" -> System.out.println("login <USERNAME> <PASSWORD> - login into an account");
                case "quit" -> System.out.println("quit - quit playing chess");
                case "help" ->
                        System.out.println("help - print all available actions\nhelp <ACTION> - learn about action");
                default -> System.out.printf("unknow command %s\n", response[1]);
            }
        } else if (userState.equals(STATE.LOGGEDIN)) {
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
        }
    }

    private void renderOptions() {
        System.out.println("Available commands: ");
        if (userState.equals(STATE.LOGGEDOUT)) {
            System.out.println("register <USERNAME> <PASSWORD> <EMAIL>");
            System.out.println("login <USERNAME> <PASSWORD>");
            System.out.println("quit");
            System.out.println("help");
        } else if (userState.equals(STATE.LOGGEDIN)) {
            System.out.println("create <NAME>");
            System.out.println("list");
            System.out.println("join <ID> [WHITE|BLACK]");
            System.out.println("observe <ID>");
            System.out.println("logout");
            System.out.println("quit");
            System.out.println("help");
        }
    }

}
