package ap.spring2019.project.server;

import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class CsvWriter {

    public static void write(String cardType, ArrayList<String> data) {
        String fileAddress = cardType +".csv";
        try {
            FileWriter fileWriter = new FileWriter(fileAddress, true);
            fileWriter.append(join(',', data));
            fileWriter.append("\n");
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException io){
            io.printStackTrace();
        }
    }

    public static String join(char c, ArrayList<String> data) {
        return join(c, data.toArray(new String[0]));
    }

    public static String join(char c, String[] data) {
        StringBuilder stringBuilder = new StringBuilder();
        for(String string : data){
            stringBuilder.append(string);
            stringBuilder.append(c);
        }
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        return stringBuilder.toString();
    }

    public static void writeCardFiles(CardType cardType, String data){
        try {
            File file = Server.getFile(cardType);
            synchronized (file) {
                FileWriter fileWriter = new FileWriter(file);
                fileWriter.write(data);
                fileWriter.flush();
                fileWriter.close();
            }
        }catch (IOException i){
            i.printStackTrace();
        }
    }

    public static void updateStock(CardType cardType, String name, int stock){
        ArrayList<String[]> datas;
        File file;
        switch (cardType){
            case HERO:
                datas = CsvReader.readCards("Heroes");
                file = Server.getHeroes();
                break;
            case MINION:
                datas = CsvReader.readCards("Minions");
                file = Server.getMinions();
                break;
            case SPELL:
                datas = CsvReader.readCards("Spells");
                file = Server.getSpells();
                break;
            default:
                datas = CsvReader.readCards("Items");
                file = Server.getItems();
                break;
        }
        synchronized (file) {
            try {
                FileWriter fileWriter = new FileWriter(file);
                for (String[] row : datas) {
                    if (name.equals(row[3])) {
                        row[0] = Integer.toString(stock);
                    }
                    fileWriter.append(join(',', row));
                    fileWriter.append("\n");
                }
                fileWriter.flush();
                fileWriter.close();
            } catch (IOException i) {
                i.printStackTrace();
            }
        }
    }

    public static File copy(String type) {
        try{
            File file = new File(type + "Copy.csv");
            file.createNewFile();
            FileWriter fileWriter = new FileWriter(file);
            String data = CsvReader.readFile(type);
            fileWriter.append(data);
            fileWriter.flush();
            fileWriter.close();
            return file;
        } catch (IOException i){
            i.printStackTrace();
        }
        return null;
    }
}
