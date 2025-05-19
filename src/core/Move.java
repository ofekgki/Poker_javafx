package src.core;

public class Move {
    private final String type;
    private final int amount;

    public Move(String type, int amount) {
        this.type = type;
        this.amount = amount;
    }

    public String getType() {
        return type;
    }

    public int getAmount() {
        return amount;
    }

    public String toString() {
        return type + (amount > 0 ? " " + amount :"");
    }
}
