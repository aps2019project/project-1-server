package ap.spring2019.project.server;

import java.net.Socket;
import java.util.ArrayList;

public class Game {

    private int ID;
    private String player1;
    private String player2;
    private ArrayList<Socket> audiences = new ArrayList<>();

    public Game(String player1, String player2) {
        this.ID = generateRandomID();
        this.player1 = player1;
        this.player2 = player2;
    }

    public int getID() {
        return ID;
    }

    public String getPlayer1Username() {
        return player1;
    }

    public String getPlayer2UserName() {
        return player2;
    }

    public Socket getPlayer1Socket() {
        return Server.getSocket(player1);
    }

    public Socket getPlayer2Socket() {
        return Server.getSocket(player2);
    }

    private int generateRandomID() {
        int result = 0;
        for (int i = 0; i < 7; ++i)
            result = 10 * result + (int) (Math.random() * 10 + 1);
        return result;
    }

}
