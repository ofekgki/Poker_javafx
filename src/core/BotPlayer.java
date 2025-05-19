package src.core;

import java.util.Random;

public class BotPlayer extends Player {
    private final Random random = new Random();

    public BotPlayer(String name, int chips) {
        super(name, chips);
    }

    @Override
    public Move makeMove() {
        boolean willBet = random.nextBoolean();
        if (willBet) {
            int betAmount = 10 + random.nextInt(41);
            return new Move("BET", betAmount);
        }
        else{
            return new Move("FOLD",0);
        }
    }
}
