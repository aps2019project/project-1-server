package ap.spring2019.project.logic;

import ap.spring2019.project.server.GameType;

import java.util.Date;

public class MatchResult {

    private String firstPlayer;
    private String secondPlayer;
    private String winner;
    private Date date;
    private int reward;

    public Account getFirstPlayer() {
        return Account.findAccount(firstPlayer);
    }

    public Account getSecondPlayer() {
        return Account.findAccount(secondPlayer);
    }

    public Account getWinner() {
        return Account.findAccount(winner);
    }
}
