package ap.spring2019.project.logic;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class Account {

    private static final ArrayList<Account> allAccounts = new ArrayList<>();

    private String username;
    private String password;
    private ArrayList<String> collection = new ArrayList<>();
    private HashMap<String, ArrayList<String>> allDecks;
    private ArrayList<MatchResult> matchHistory = new ArrayList<>();
    private String mainDeck = "";
    private int daric = 15000;

    public Account(String username, String password) {
        this.username = username;
        this.password = password;
        synchronized (allAccounts) {
            allAccounts.add(this);
        }
    }

    public static ArrayList<Account> getAccounts() {
        return allAccounts;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public static void readAccountDetails() {
        Type type = new TypeToken<ArrayList<Account>>() {}.getType();
        Gson gson = new GsonBuilder().create();
        Scanner reader;
        String str = "";
        ArrayList<Account> data = new ArrayList<>();
        try {
            reader = new Scanner(new File("Accounts.json"));
        }
        catch (IOException e) {
            return;
        }

        while (reader.hasNext()){
            str = reader.nextLine();
        }
        try {
            data = gson.fromJson(str, type);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        reader.close();

        allAccounts.clear();
        allAccounts.addAll(data);
    }

    public static void saveAccountDetails() {
        synchronized (allAccounts) {
            Gson gson = new GsonBuilder().create();
            try {
                Writer writer = new FileWriter("Accounts.json");
                writer.write(gson.toJson(allAccounts));
                writer.close();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public static boolean isUserNameAvailable(String username) {
        if (doesAccountExist(username))
            return false;
        if (username.length() < 5)
            return false;
        return username.matches("[a-zA-Z].*");
    }

    public static boolean doesAccountExist(String username) {
        return !(findAccount(username) == null);
    }

    public static Account findAccount(String username) {
        synchronized (allAccounts) {
            for (Account account : allAccounts) {
                if (account.username.equals(username)) {
                    return account;
                }
            }
            return null;
        }
    }

    public static boolean checkIfPasswordIsCorrect(String username, String password) {
        Account account = findAccount(username);
        if (account == null)
            return false;
        return account.getPassword().equals(password);
    }

    public static void updateAccount(Account account) {
        if (account == null)
            return;
        synchronized (allAccounts) {
            if (!doesAccountExist(account.getUsername()))
                return;
            allAccounts.remove(findAccount(account.getUsername()));
            allAccounts.add(account);
        }
        saveAccountDetails();

    }
}
