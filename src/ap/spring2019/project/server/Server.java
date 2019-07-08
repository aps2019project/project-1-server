package ap.spring2019.project.server;


import ap.spring2019.project.logic.Account;
import ap.spring2019.project.server.Listener;
import ap.spring2019.project.server.Game;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    private static final int PORT = 8000;
    private static ServerSocket server;
    private static final HashMap<String, Socket> onlineUsers = new HashMap<>();
    private static final ArrayList<Game> games = new ArrayList<>();

    static {
        try {
            server = new ServerSocket(PORT);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println(e.getMessage());
            System.exit(0);
        }
    }

    public static void main(String[] args) {
        Account.readAccountDetails();
        ExecutorService serverSocketAdder = Executors.newSingleThreadExecutor();
        ExecutorService offlineUserGrabber = Executors.newSingleThreadExecutor();
        ExecutorService informationListener = Executors.newSingleThreadExecutor();

        serverSocketAdder.submit(() -> {
            while (Thread.currentThread().isAlive()) {
                try {
                    Socket socket = server.accept();
                    new Thread(new Listener(socket)).start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        offlineUserGrabber.submit(() -> {
            while (Thread.currentThread().isAlive())
                deleteOfflineUsers();
        });

        informationListener.submit(() -> {
            Scanner scanner = new Scanner(System.in);
            while (scanner.hasNext()) {
                String command = scanner.nextLine();
                if (command.matches("online users"))
                    System.out.println(onlineUsers);
                else if (command.matches("all users"))
                    System.out.println(Account.getAccounts());
            }
        });
    }

    static synchronized HashMap<String, Socket> getOnlineUsers() {
        synchronized (onlineUsers) {
            return onlineUsers;
        }
    }

    static synchronized ArrayList<Game> getGames() {
        synchronized (games) {
            return games;
        }
    }

    static synchronized void addUser(String userName, Socket socket) {
        synchronized (onlineUsers) {
            onlineUsers.put(userName, socket);
        }
    }

    static synchronized Socket deleteUser(String userName) {
        synchronized (onlineUsers) {
            try {
                return onlineUsers.remove(userName);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    static synchronized void addGame(ap.spring2019.project.server.Game game) {
        synchronized (games) {
            games.add(game);
        }
    }

    static synchronized void deleteGame(Game game) {
        synchronized (games) {
            try {
                games.remove(game);
            } catch (Exception ignored) {
            }
        }
    }

    static synchronized boolean isAccountOnline(String userName) {
        synchronized (onlineUsers) {
            return onlineUsers.containsKey(userName);
        }
    }

    static synchronized Socket getSocket(String userName) {
        synchronized (onlineUsers) {
            if (!onlineUsers.containsKey(userName))
                return null;
            return onlineUsers.get(userName);
        }
    }

    static synchronized String getUserName(Socket socket) {
        synchronized (onlineUsers) {
            if (!onlineUsers.containsValue(socket))
                return "";
            for (Map.Entry<String, Socket> user: onlineUsers.entrySet()) {
                if (user.getValue().equals(socket))
                    return user.getKey();
            }
        }
        return "";
    }

    static synchronized ArrayList<Account> getOnlineUsersArrayList() {
        synchronized (onlineUsers) {
            ArrayList<Account> result = new ArrayList<>();
            for (String username: onlineUsers.keySet()) {
                try {
                    result.add(Account.findAccount(username));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return result;
        }
    }

    private static void deleteOfflineUsers() {
        synchronized (onlineUsers) {
            for (Map.Entry<String, Socket> user : onlineUsers.entrySet()) {
                if (user.getValue().isClosed())
                    onlineUsers.remove(user.getKey());
            }
        }
    }



}
