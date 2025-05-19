package src.core;

import java.util.ArrayList;
import java.util.List;

public abstract class Player {
    protected String name;
    protected int chips;
    protected List<Card> hand;
    private int wins = 0;

    public Player(String name, int chips) {
        this.name = name;
        this.chips = chips;
        this.hand = new ArrayList<>();
    }


    public void addWin() {
        wins++;
    }

    public int getWins() {
        return wins;
    }

    public void receiveCard(Card card) {
        hand.add(card);
    }

    public void clearHand() {
        hand.clear();
    }

    public abstract Move makeMove();

    public String getName() {
        return name;
    }

    public List<Card> getHand() {
        return hand;
    }

    public int getChips() {
        return chips;
    }

    public void setChips(int chips) {
        this.chips = chips;
    }

    public void addChips(int amount) {
        this.chips += amount;
    }
}

