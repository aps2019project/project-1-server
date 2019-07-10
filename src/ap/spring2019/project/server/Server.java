package ap.spring2019.project.server;


import ap.spring2019.project.logic.Account;
import ap.spring2019.project.logic.AuctionCard;
import ap.spring2019.project.logic.ClientAuctionCard;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    private static final int PORT = 8765;
    private static ServerSocket server;
    private static final HashMap<String, Socket> onlineUsers = new HashMap<>();
    private static final HashMap<String, AccountDatas> allAccountDatas = new HashMap<>();
    private static final ArrayList<AuctionCard> auctionMarket = new ArrayList<>();
    private static final ArrayList<Game> games = new ArrayList<>();
    private static final HashMap<String, Integer> cardStocks = new HashMap<>();
    private static final ArrayList<Account>[] rollUpFlagsGames = new ArrayList[8];
    private static final ArrayList<Account> killHeroGames = new ArrayList<>();
    private static final ArrayList<Account> captureTheFlag = new ArrayList<>();
    private static final File heroes = new File("Heroes.csv");
    private static final File minions = new File("Minions.csv");
    private static final File spells = new File("Spells.csv");
    private static final File Items = new File("Items.csv");
    private static String ip;


    static {
        try {
            server = new ServerSocket(PORT);
            foundInternetIP();
            auctionMarket.add(new AuctionCard("sasd", "iraj"));
            auctionMarket.add(new AuctionCard("sasd", "bahman"));
            auctionMarket.add(new AuctionCard("jggjh", "DamoolArc"));

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
        CsvReader.readStock(cardStocks);

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
            allAccountDatas.put(userName, new AccountDatas(Account.findAccount(userName)));
        }
    }

    static synchronized Socket deleteUser(String userName) {
        synchronized (onlineUsers) {
            try {
                allAccountDatas.remove(userName);
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

    static synchronized ArrayList<AuctionCard> getAuctionMarket() {
        synchronized (auctionMarket) {
            return auctionMarket;
        }
    }

    static synchronized void addAuction(AuctionCard card) {
        synchronized (auctionMarket) {
            auctionMarket.add(card);
        }
    }

    static synchronized void addNewOffer(String username, int auctionID, int offer) {
        synchronized (auctionMarket) {
            for (AuctionCard card: auctionMarket) {
                if (auctionID != card.getID())
                    continue;
                card.setNewOffer(username, offer);
            }
        }
    }

    static synchronized AuctionCard getAuctionByID(int id) {
        synchronized (auctionMarket) {
            for(AuctionCard auctionCard: auctionMarket) {
                if (auctionCard.getID() == id)
                    return auctionCard;
            }
            return null;
        }
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

    private static void foundInternetIP() {
        InetAddress localhost = null;
        try {
            localhost = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        assert localhost != null;
        System.out.println("System IP Address : " +
                (localhost.getHostAddress()).trim());

        String systemipaddress = "";
        try
        {
            URL url_name = new URL("http://bot.whatismyipaddress.com");

            BufferedReader sc =
                    new BufferedReader(new InputStreamReader(url_name.openStream()));

            systemipaddress = sc.readLine().trim();
        }
        catch (Exception e)
        {
            systemipaddress = "Cannot Execute Properly";
        }
        System.out.println("Public IP Address: " + systemipaddress +"\n");
        ip =  systemipaddress;
    }

    public static File getFile(CardType cardType) {
        switch (cardType){
            case HERO:
                return Server.getHeroes();
            case MINION:
                return Server.getMinions();
            case SPELL:
                return Server.getSpells();
            default:
                return Server.getItems();
        }
    }

    public static File getFile(String cardType) {
        if(cardType.equals("Heroes")){
            return getHeroes();
        } else if(cardType.equals("Minions")){
            return getMinions();
        } else if(cardType.equals("Spells")){
            return getSpells();
        } else
            return getItems();
    }

    public static File getHeroes() {
        return heroes;
    }

    public static File getMinions() {
        return minions;
    }

    public static File getSpells() {
        return spells;
    }

    public static File getItems() {
        return Items;
    }

    public static HashMap<String, Integer> getCardStocks() {
        return cardStocks;
    }

    public static ArrayList<Account> getCaptureTheFlag() {
        return captureTheFlag;
    }

    public static ArrayList<Account>[] getRollUpFlagsGames() {
        return rollUpFlagsGames;
    }

    public static ArrayList<Account> getKillHeroGames() {
        return killHeroGames;
    }
    public static void setNewGame(ArrayList<Account> accounts) {
        int randomNumber = (int)Math.floor(Math.random() * accounts.size());
        Account firstAccount = accounts.get(randomNumber);
        accounts.remove(firstAccount);
        randomNumber = (int)Math.floor(Math.random() * accounts.size());
        Account secondAccount = accounts.get(randomNumber);
        accounts.remove(secondAccount);
        Server.allAccountDatas.get(firstAccount.getUsername()).setEnemyAccount(secondAccount);
        Server.allAccountDatas.get(firstAccount.getUsername()).setNumberInGame(1);
        Server.allAccountDatas.get(secondAccount.getUsername()).setEnemyAccount(firstAccount);
        Server.allAccountDatas.get(secondAccount.getUsername()).setNumberInGame(2);
    }

    public static AccountDatas getAccountDatas(String username) {
        return allAccountDatas.get(username);
    }

    public static String getIp() {
        return ip;
    }
}
