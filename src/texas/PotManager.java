package src.texas;

import src.core.*;
import java.util.*;

public class PotManager {
    private int pot;
    private final Map<Player, Integer> bets;

    public PotManager() {
        this.pot = 0;
        this.bets = new HashMap<>();
    }

    public void placeBet(Player player, int amount) {
        if (amount > player.getChips()) {
            throw new IllegalArgumentException(player.getName() + " tried to bet more than available chips");
        }

        pot += amount;
        player.setChips(player.getChips() - amount);

        bets.put(player, bets.getOrDefault(player, 0) + amount);

        System.out.println(player.getName() + " bets " + amount + " chips. (Total in pot: " + pot + ")");
    }



    public int getPot() {
        return pot;
    }

    public void clear() {
        pot = 0;
        bets.clear();
    }

}
