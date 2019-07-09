package ap.spring2019.project.server;


import static ap.spring2019.project.server.ApplyCondition.*;

public class ContentsBetweenTwoPlayers {
    private String firstAccount;
    private String secondAccount;
    private ApplyCondition applyCondition = NOTHING;
    private GameType gameType = GameType.KILL_HERO;
    private int numberOfFlags = 0;

    public ContentsBetweenTwoPlayers(String firstAccount, String secondAccount) {
        this.firstAccount = firstAccount;
        this.secondAccount = secondAccount;
    }
    public String getAnotherAccount(String username)  {
        if(firstAccount.equals(username)) return secondAccount;
        return firstAccount;
    }

    public ApplyCondition getApplyCondition() {
        return applyCondition;
    }

    public void applyCondition(String username,GameType gameType, int numberOfFlags) {
        if(username.equals(firstAccount)) {
            applyCondition = ApplyCondition.WAITING_FOR_SECOND;
        }
        else {
            applyCondition = ApplyCondition.WAITING_FOR_FIRST;
        }
        this.gameType = gameType;
        this.numberOfFlags = numberOfFlags;
    }
    public void acceptCondition() {
        applyCondition = ACCEPTED;
    }
    public void cancelApplying() {
        applyCondition = NOTHING;
    }
    public boolean check(String firstAccount, String secondAccount) {
        return (this.firstAccount.equals(firstAccount) && this.secondAccount.equals(secondAccount)) ||
                (this.secondAccount.equals(firstAccount) && this.firstAccount.equals(secondAccount));
    }
    public String getApplyingCondition(String username) {
        if(applyCondition == NOTHING) return "nothing";
        else if(applyCondition == ACCEPTED) return "accepted";
        if( (applyCondition == WAITING_FOR_FIRST && secondAccount.equals(username)) ||
            (applyCondition == WAITING_FOR_SECOND && firstAccount.equals(username)) ) return "wait for another";
        return "wait for me";
    }
}
