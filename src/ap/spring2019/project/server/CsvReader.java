package ap.spring2019.project.server;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public class CsvReader {

    public static ArrayList<String[]> readCards(String cardType){
        ArrayList<String[]> data = new ArrayList<String[]>();
        File file = Server.getFile(cardType);
        synchronized (file) {
            try {
                FileReader fileReader = new FileReader(file);
                BufferedReader reader = new BufferedReader(fileReader);
                String line = reader.readLine();
                while ((line = reader.readLine()) != null) {
                    data.add(line.split(","));
                }
                fileReader.close();
                reader.close();
            } catch (IOException io) {
                io.printStackTrace();
            }
        }
        return data;
    }

    public static void readStock(HashMap<String, Integer> stocks){
        readStock("Heroes", stocks);
        readStock("Minions", stocks);
        readStock("Spells", stocks);
        readStock("Items", stocks);
    }

    public static void readStock(String cardType, HashMap<String, Integer> stocks){
//        ArrayList<String[]> data = new ArrayList<String[]>();
        File file = Server.getFile(cardType);
        synchronized (file) {
            try {
                FileReader fileReader = new FileReader(file);
                BufferedReader reader = new BufferedReader(fileReader);
                String line = reader.readLine();
                while ((line = reader.readLine()) != null) {
                    String[] data = line.split(",");
                    stocks.put(data[2], Integer.parseInt(data[0]));
                }
                fileReader.close();
                reader.close();
            } catch (IOException io) {
                io.printStackTrace();
            }
        }
    }

    public static String readFile(String cardType) {
        File file = Server.getFile(cardType);
        synchronized (file) {
            try {
                InputStream is = new FileInputStream(file);
                BufferedReader buf = new BufferedReader(new InputStreamReader(is));
                String line = buf.readLine();
                StringBuilder sb = new StringBuilder();
                while (line != null) {
                    sb.append(line).append("\n");
                    line = buf.readLine();
                }
                return sb.toString();
            } catch (IOException i) {
                i.printStackTrace();
            }
        }
        return null;
    }
}

