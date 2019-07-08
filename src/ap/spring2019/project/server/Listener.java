package ap.spring2019.project.server;

import ap.spring2019.project.logic.Account;
import ap.spring2019.project.server.Server;
import ap.spring2019.project.server.Game;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import server.CardType;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;

class Listener implements Runnable {

    private Gson gson;
    private Socket socket;
    private DataInputStream inputStream;
    private DataOutputStream outputStream;

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
}