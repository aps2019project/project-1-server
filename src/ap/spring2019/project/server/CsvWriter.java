package ap.spring2019.project.server;

import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import server.CardType;

public class CsvWriter {

    public static void write(String cardType, ArrayList<String> data) {
        String fileAddress = "Files/" + cardType +".csv";
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
        StringBuilder stringBuilder = new StringBuilder();
        for(String string : data){
            stringBuilder.append(string);
            stringBuilder.append(c);
        }
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        return stringBuilder.toString();
    }

    public static void writeCardFiles(String cardType, String data){
        try {
            FileWriter fileWriter = new FileWriter(new File("Files/" + cardType +".csv"));
            fileWriter.write(data);
            fileWriter.flush();
            fileWriter.close();
        }catch (IOException i){
            i.printStackTrace();
        }
    }

    public static void updateStock(CardType cardType, String name, int stock){
        ArrayList<String[]> datas;
        switch (cardType){
            case HERO:
                datas = CsvReader.readCards("Heroes");
                break;
            case MINION:
                datas = CsvReader.readCards("Minions");
                break;
            case SPELL:
                datas = CsvReader.readCards("Spells");
                break;
            default:
                datas = CsvReader.readCards("Items");
                break;
        }
    }
}