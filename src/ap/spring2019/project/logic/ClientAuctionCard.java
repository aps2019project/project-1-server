package ap.spring2019.project.logic;

import java.util.ArrayList;

public class ClientAuctionCard {

    private int id;
    private String seller;
    private String cardName;
    private String username;
    private int lastOffer;
    private float remainingTime = 1f;

    private ClientAuctionCard(String cardName, String username, int lastOffer, int id, String seller) {
        this.seller = seller;
        this.id = id;
        this.cardName = cardName;
        this.username = username;
        this.lastOffer = lastOffer;
    }

    public static ArrayList<ClientAuctionCard> getClientKnownArrayList(ArrayList<AuctionCard> auctionCards) {
        ArrayList<ClientAuctionCard> result = new ArrayList<>();
        for (AuctionCard card: auctionCards) {
            ClientAuctionCard temp = new ClientAuctionCard(card.getCardName(), card.getLastUsername(), card.getHighestOffer(), card.getID(), card.getSeller());
            temp.remainingTime = 1 - (System.currentTimeMillis() - card.getStartedTime()) / 300000f;
            result.add(temp);
        }
        return result;
    }

    public static ClientAuctionCard getClientKnownCard(AuctionCard card) {
        if (card == null)
            return null;
        ClientAuctionCard temp = new ClientAuctionCard(card.getCardName(), card.getLastUsername(), card.getHighestOffer(), card.getID(), card.getSeller());
        temp.remainingTime = (System.currentTimeMillis() - card.getStartedTime()) / 300000f;
        return temp;
    }

    public float getRemainingTime() {
        return this.remainingTime;
    }
}
