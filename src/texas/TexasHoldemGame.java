package src.texas;

import src.core.*;
import java.util.*;

public class TexasHoldemGame {


    private final Deck deck;
    private final List<Player> players;
    private final List<Card> communityCards;
    private final PotManager potManager = new PotManager();



    public TexasHoldemGame(List<Player> players) {
        this.deck = new Deck();
        this.players = players;
        this.communityCards = new ArrayList<>();
    }

    public void startGame(){
        System.out.println(" === Starting Texas Holdem Game ===");
        dealHoleCards();
        dealFlop();
        dealTurn();
        dealRiver();

        System.out.println("\n Community Cards: ");
        for(Card c : communityCards){
            System.out.println(c);
        }

        System.out.println("\n Player Hands: ");
        for(Player p : players){
            System.out.println(p.getName() + ": " + p.getHand());
        }

        System.out.println("\n--- Betting Round ---");
        for (Player player : players) {
            Move move = player.makeMove();
            if (move.getType().equalsIgnoreCase("bet")) {
                potManager.placeBet(player, move.getAmount());
            } else {
                System.out.println(player.getName() + " folds.");
            }
        }
        System.out.println("Total pot: " + potManager.getPot() + " chips.");


        System.out.println("\n Evaluating Hands....");
        List<PokerHandEvaluator.EvaluatedHand> results = new ArrayList<>();
        for (Player p : players) {
            results.add(PokerHandEvaluator.evaluate(p,communityCards));

        }

        results.sort(Collections.reverseOrder());

        System.out.println("\n Ranking:  ");
        for (PokerHandEvaluator.EvaluatedHand hand : results) {
            System.out.println("    " + hand);
        }

        PokerHandEvaluator.EvaluatedHand winner = results.getFirst();
        System.out.println("\n Winner: " + winner.player.getName() + " with " + winner.rank + " a total of : " + potManager.getPot());
        winner.player.setChips(potManager.getPot() + winner.player.getChips());
        System.out.println("Total balance -> " + winner.player.getChips());

    }

    private void dealHoleCards(){
        for (int i = 0 ; i < 2 ; i++){
            for (Player p : players){
                p.receiveCard(deck.deal());
            }
        }

    }

    private void dealFlop(){
        deck.deal(); // Burning Card
        for (int i = 0; i < 3; i++) {
            communityCards.add(deck.deal());
        }
    }

    private void dealTurn(){
        deck.deal(); // Burning Card
        communityCards.add(deck.deal());
    }

    private void dealRiver(){
        deck.deal(); // Burning Card
        communityCards.add(deck.deal());
    }

}
