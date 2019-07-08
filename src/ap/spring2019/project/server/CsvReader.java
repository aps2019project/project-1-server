package ap.spring2019.project.server;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public class CsvReader {

    public static void readStock(HashMap<String, Integer> stocks){
        readStock("Heroes", stocks);
        readStock("Minions", stocks);
        readStock("Spells", stocks);
        readStock("Items", stocks);
    }

    public static void readStock(String cardType, HashMap<String, Integer> stocks){
//        ArrayList<String[]> data = new ArrayList<String[]>();
        String fileAddress = "Files/" + cardType +".csv";
        try {
            FileReader fileReader = new FileReader(fileAddress);
            BufferedReader reader = new BufferedReader(fileReader);
            String line = reader.readLine();
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                stocks.put(data[2], Integer.parseInt(data[0]));
            }
            fileReader.close();
            reader.close();
        } catch (IOException io){
            io.printStackTrace();
        }
    }

    public static String readFile(String cardType) {
        try {
            InputStream is = new FileInputStream("Files/" + cardType +".csv");
            BufferedReader buf = new BufferedReader(new InputStreamReader(is));
            String line = buf.readLine();
            StringBuilder sb = new StringBuilder();
            while (line != null) {
                sb.append(line).append("\n");
                line = buf.readLine();
            }
            return sb.toString();
        } catch (IOException i){
            i.printStackTrace();
        }
        return null;
    }
}

