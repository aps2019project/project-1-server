package ap.spring2019.project.server;

import ap.spring2019.project.logic.Account;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import server.CardType;

import java.io.*;
import java.net.Socket;
import java.rmi.ServerError;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Vector;

import static ap.spring2019.project.server.GameType.*;

class Listener implements Runnable {

    private Gson gson;
    private Socket socket;
    private DataInputStream inputStream;
    private DataOutputStream outputStream;
    private AccountDatas accountDatas;
    Listener(Socket socket) {
        try {
            this.socket = socket;
            this.gson = new GsonBuilder().create();
            this.inputStream = new DataInputStream(socket.getInputStream());
            this.outputStream = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            String command = getCommand();
            if (command.matches("login \\w+ \\w+")) {
                loginUser(command.split(" ")[1], command.split(" ")[2]);
            } else if (command.matches("create account \\w+ \\w+")) {
                createAccount(command.split(" ")[2], command.split(" ")[3]);
            } else if (command.matches("update account")) {
                Account.updateAccount(getData(Account.class));
            } else if (command.matches("get online accounts")) {
                sendOnlineUsers();
            } else if (command.matches("get all accounts")) {
                sendData(Account.getAccounts());
            } else if (command.matches("logout")) {
                logOutUser();
            } else if (command.matches("get accounts")) {
                sendData(Account.getAccounts());
            } else if (command.matches("get online users")) {
                sendData(new HashSet<>(Server.getOnlineUsers().keySet()));
            } else if (command.matches("Create Card \\w+")) {
                getCardString(CardType.valueOf(command.split(" ")[2]));
            } else if (command.matches("Send Card File \\w+")){
                sendData(CsvReader.readFile(command.split(" ")[3]));
            } else if (command.matches("Buy Card \\w+ \\w+")){
                buyCard(CardType.valueOf(command.split(" ")[2]), command.split(" ")[3]);
            } else if (command.matches("Sell Card \\w+ \\w+")){

            } else if (command.matches("play game orders")) {
                handlePlayGame();
            } else if (command.matches("get enemy account" )) {
                handleGetEnemyAccount();
            } else if (command.matches("apply play multiplayer game")) {
                handleApplyPlayMultiplayerGame();
            } else if (command.matches("cancel applying" )) {
                handleCancelApply();
            } else if (command.matches( "get applying condition" )) {
                handleGetApplyingCondition();
            } else if (command.matches("get my number in game")) {
                sendData((Integer)accountDatas.getNumberInGame());
            }
        }
    }

    private void logOutUser() {
        String username = Server.getUserName(socket);
        if (!username.equals("")) {
            Server.deleteUser(username);
        }
    }

    private void sendOnlineUsers() {
        ArrayList<Account> onlineAccounts = new ArrayList<>();
        for (Account account: Account.getAccounts()) {
            if (Server.isAccountOnline(account.getUsername())) {
                onlineAccounts.add(account);
            }
        }
        sendData(onlineAccounts);
    }

    private String getCommand() {
        try {
            return inputStream.readUTF();
        } catch (IOException e) {
            logOutUser();
            e.printStackTrace();
            Thread.currentThread().interrupt();
            return "";
        }
    }

    private <T> void sendData(T data) {
        try {
            outputStream.writeUTF(gson.toJson(data));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private <T> T getData(Class<T> cls) {
        try {
            return gson.fromJson(inputStream.readUTF(), cls);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void createAccount(String userName, String password) {
        if (!Account.isUserNameAvailable(userName)) {
            sendData("Error: Username is invalid");
            return;
        }
        Account account = new Account(userName, password);
        Server.addUser(account.getUsername(), socket);
        sendData("Done");
        sendData(account);

        Account.saveAccountDetails();
    }

    private void loginUser(String userName, String password) {
        if (!Account.checkIfPasswordIsCorrect(userName, password)) {
            sendData("Error: Account doesn't exists");
            return;
        }
        if (Server.isAccountOnline(userName)) {
            sendData("Error: Account is online");
            return;
        }
        Server.addUser(userName, socket);
        this.accountDatas = Server.getAccountDatas(userName);
        sendData("Done");
        sendData(Account.findAccount(userName));
        Account.saveAccountDetails();
    }

    public void getCardString(CardType type){
        try {
            File file;
            FileWriter fileWriter;
            switch (type) {
                case HERO:
                    file = Server.getHeroes();
                    break;
                case MINION:
                    file = Server.getMinions();
                    break;
                case SPELL:
                    file = Server.getSpells();
                    break;
                default:
                    file = Server.getItems();
                    break;
            }
            synchronized (file) {
                fileWriter = new FileWriter(file, true);
                String data = getCommand();
                fileWriter.write(data);
                fileWriter.flush();
                fileWriter.close();
            }

        }catch (IOException i){
            i.printStackTrace();
        }
    }

    public void buyCard(CardType cardType, String name){
        int stock = Server.getCardStocks().get(name);
        if(stock <= 0){
            sendData("Out of Stock");
        } else {
            Server.getCardStocks().put(name, stock - 1);
            CsvWriter.updateStock(cardType, name, stock - 1);
            sendData("Done");
        }
    }
    public void handlePlayGame() {
        String line = null;
        line = getCommand();

        switch (line) {
            case "get mousePos":
                AccountDatas enemyDatas = Server.getAccountDatas(accountDatas.getEnemyAccount().getUsername());
                sendData(enemyDatas.pickMousePos());
                break;
            case "send mousePos":
                accountDatas.addMousePos(getData(Mousepos.class));
                break;
        }
    }
    public void handleGetEnemyAccount(){
        sendData(accountDatas.getEnemyAccount());
    }
    public void handleApplyPlayMultiplayerGame() {
        GameType type = KILL_HERO;
        int numberOfFlags = 0;
        type = getGameTypeByString(getCommand());
        numberOfFlags = Integer.parseInt(getCommand());

        accountDatas.setGame(type, numberOfFlags);
        accountDatas.addToWaitList();
    }

    public static GameType getGameTypeByString(String type) {
        if(type.equals("kill hero")) return KILL_HERO;
        else if(type.equals("capture the flag")) return CAPTURE_FLAGES;
        else if(type.equals("rollup flags")) return ROLLUP_FLAGES;
        else return  KILL_HERO;
    }

    public void handleCancelApply() {
        accountDatas.removeFromWaitList();
    }
    public void handleGetApplyingCondition() {
        String outCommand = "waiting";
        if( accountDatas.getWaitArrayInServer().size() > 1) {
            outCommand = "accepted";
            Server.setNewGame(accountDatas.getWaitArrayInServer());
        } else if(accountDatas.getEnemyAccount() != null) {
            outCommand = "accepted";
        }
        sendData(outCommand);
    }


}