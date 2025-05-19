package src.core;

public class Session {
    private static int playerChips = 100;

    public static int getPlayerChips() {
        return playerChips;
    }

    public static void setPlayerChips(int chips) {
        playerChips = chips;
    }
}
