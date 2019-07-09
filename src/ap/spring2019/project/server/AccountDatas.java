package ap.spring2019.project.server;

import ap.spring2019.project.logic.Account;

import java.util.ArrayList;

import static ap.spring2019.project.server.GameType.*;

public class AccountDatas {
    private Account enemyAccount = null;
    private GameType gameType = KILL_HERO;
    private int numberOfFlags = 0;
    private Account account;
    private int numberInGame = 1;
    private ArrayList<MousePos> mouseposses = new ArrayList<>();

    public AccountDatas(Account account){
        this.account =account;
    }

    public void setEnemyAccount(Account enemyAccount) {
        this.enemyAccount = enemyAccount;
    }

    public void setGame(GameType gameType, int numberOfFlags) {
        this.gameType = gameType;
        this.numberOfFlags = numberOfFlags;
    }
    public ArrayList<Account> getWaitArrayInServer() {
        if(gameType == KILL_HERO) {
            return Server.getKillHeroGames();
        } else if(gameType == CAPTURE_FLAGES) {
            return Server.getCaptureTheFlag();
        } else if(gameType == ROLLUP_FLAGES){
            return Server.getRollUpFlagsGames()[numberOfFlags];
        }
        return Server.getKillHeroGames();
    }
    public void removeFromWaitList() {
        getWaitArrayInServer().remove(account);
    }
    public void addToWaitList() {
        getWaitArrayInServer().add(account);
    }

    public Account getEnemyAccount() {
        return enemyAccount;
    }

    public Account getAccount() {
        return account;
    }

    public GameType getGameType() {
        return gameType;
    }

    public int getNumberOfFlags() {
        return numberOfFlags;
    }

    public void setNumberInGame(int numberInGame) {
        this.numberInGame = numberInGame;
    }

    public int getNumberInGame() {
        return numberInGame;
    }

    public MousePos pickMousePos() {
        if(mouseposses.size() == 0) return null;
        MousePos mousepos = mouseposses.get(0);
        mouseposses.remove(0);
        return mousepos;
    }
    public void addMousePos(MousePos mousepos) {
        mouseposses.add(mousepos);
    }
}
