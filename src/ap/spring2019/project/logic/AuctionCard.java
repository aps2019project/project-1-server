package ap.spring2019.project.logic;

public class AuctionCard {
    private int ID;
    private String seller;
    private String lastUsername;
    private String cardName;
    private int highestOffer;
    private long startedTime;

    public String getLastUsername() {
        return lastUsername;
    }

    public String getCardName() {
        return cardName;
    }

    public int getHighestOffer() {
        return highestOffer;
    }

    public long getStartedTime() {
        return startedTime;
    }

    public AuctionCard(String username, String cardName) {
        this.ID = generateRandomID();
        this.seller = username;
        this.lastUsername = username;
        this.cardName = cardName;
        this.startedTime = System.currentTimeMillis();
        this.highestOffer = 100;
    }

    public void setNewOffer(String username, int daric) {
        if (daric <= highestOffer)
            return;
        this.lastUsername = username;
        this.highestOffer = daric;
    }

    public int getID() {
        return ID;
    }

    public String getSeller() {
        return seller;
    }

    private int generateRandomID() {
        int result = 0;
        for (int i = 0; i < 7; ++i)
            result = 10 * result + (int) (Math.random() * 10 + 1);
        return result;
    }
}
