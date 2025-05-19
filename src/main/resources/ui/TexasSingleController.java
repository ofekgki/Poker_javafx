package ui;


import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

import java.io.IOException;

import src.core.*;
import src.texas.*;

import java.util.*;


public class TexasSingleController {

    @FXML
    private HBox playerCardsBox;
    @FXML
    private HBox communityCardsBox;
    @FXML
    private Label potLabel;
    @FXML
    private Label playerNameLabel;
    @FXML
    private Label playerChipsLabel;
    @FXML
    private TextField betAmountField;
    @FXML
    private Button betButton;
    @FXML
    private Button foldButton;
    @FXML
    private Button nextStageButton;
    @FXML
    private Button resetButton;
    @FXML private Label bot1Label;
    @FXML private Label bot2Label;


    private Deck deck;
    private PotManager potManager;
    private List<Card> community;
    private Player human;
    private List<Player> bots;
    private int stage = 0; // 0 = flop, 1 = turn, 2 = river

    @FXML
    private void initialize() {
        human = new HumanPlayer("You", Session.getPlayerChips());
        bots = List.of(new BotPlayer("Bot1", 100), new BotPlayer("Bot2", 100));
        startNewGame();
    }

    private void startNewGame() {
        deck = new Deck();
        potManager = new PotManager();
        community = new ArrayList<>();
        stage = 0;

        // Only clear hands, don't reset chips
        human.clearHand();
        for (Player bot : bots) bot.clearHand();

        dealHoleCards();
        updatePlayerCards(human.getHand());
        updateCommunityCards();
        updateChipsDisplay();
        updatePot();
    }


    private void dealHoleCards() {
        for (int i = 0; i < 2; i++) {
            human.receiveCard(deck.deal());
            for (Player bot : bots) {
                bot.receiveCard(deck.deal());
            }
        }
    }

    private void updateChipsDisplay() {
        playerNameLabel.setText(human.getName());
        playerChipsLabel.setText("Chips: " + human.getChips());

        Player bot1 = bots.get(0);
        Player bot2 = bots.get(1);

        if (bot1.getChips() <= 0) {
            bot1Label.setText(bot1.getName() + ": OUT");
        } else {
            bot1Label.setText(bot1.getName() + ": " + bot1.getChips() + " chips");
        }

        if (bot2.getChips() <= 0) {
            bot2Label.setText(bot2.getName() + ": OUT");
        } else {
            bot2Label.setText(bot2.getName() + ": " + bot2.getChips() + " chips");
        }
    }
    private void updatePot() {
        potLabel.setText("Pot: " + potManager.getPot());
    }

    private void updatePlayerCards(List<Card> cards) {
        playerCardsBox.getChildren().clear();
        for (Card card : cards) {
            playerCardsBox.getChildren().add(createCardImageView(getCardImageName(card)));
        }
    }

    private void updateCommunityCards() {
        communityCardsBox.getChildren().clear();
        for (Card card : community) {
            communityCardsBox.getChildren().add(createCardImageView(getCardImageName(card)));
        }
    }

    private ImageView createCardImageView(String cardCode) {
        Image img = new Image(getClass().getResourceAsStream("/images/cards/" + cardCode));
        ImageView iv = new ImageView(img);
        iv.setFitHeight(100);
        iv.setPreserveRatio(true);
        return iv;
    }

    private String getCardImageName(Card card) {
        String rank = switch (card.getRank()) {
            case TWO -> "2";
            case THREE -> "3";
            case FOUR -> "4";
            case FIVE -> "5";
            case SIX -> "6";
            case SEVEN -> "7";
            case EIGHT -> "8";
            case NINE -> "9";
            case TEN -> "10";
            case JACK -> "J";
            case QUEEN -> "Q";
            case KING -> "K";
            case ACE -> "A";
        };

        String suit = switch (card.getSuit()) {
            case HEARTS -> "H";
            case DIAMONDS -> "D";
            case CLUBS -> "C";
            case SPADES -> "S";
        };

        return rank + suit + ".png";
    }

    @FXML
    private void onBetClicked() {
        try {
            int amount = Integer.parseInt(betAmountField.getText());
            if (amount > 0 && amount <= human.getChips()) {
                potManager.placeBet(human, amount);
                updateChipsDisplay();
                updatePot();
                simulateBots();
            } else {
                showAlert("Invalid bet amount");
            }
        } catch (NumberFormatException e) {
            showAlert("Enter a valid number.");
        }
    }

    @FXML
    private void onFoldClicked() {
        showAlert("You folded. Round ends.");
        disableActions();
    }

    @FXML
    private void onNextStageClicked() {
        switch (stage) {
            case 0 -> {
                dealFlop();
                updateCommunityCards();
            }
            case 1 -> {
                dealTurn();
                updateCommunityCards();
            }
            case 2 -> {
                dealRiver();
                updateCommunityCards();
                showDown();
                disableActions();
            }
        }
        stage++;
    }

    private void dealFlop() {
        deck.deal(); // burn
        for (int i = 0; i < 3; i++) {
            community.add(deck.deal());
        }
    }

    private void dealTurn() {
        deck.deal();
        community.add(deck.deal());
    }

    private void dealRiver() {
        deck.deal();
        community.add(deck.deal());
    }

    private void simulateBots() {
        for (Player bot : bots) {
            if (bot.getChips() <= 0) continue; // skip bankrupt bot

            Move move = bot.makeMove();
            if (move.getType().equalsIgnoreCase("BET") && move.getAmount() <= bot.getChips()) {
                potManager.placeBet(bot, move.getAmount());
                System.out.println(bot.getName() + " bets " + move.getAmount());
            } else {
                System.out.println(bot.getName() + " folds.");
            }
        }
    }

    private void showDown() {
        List<Player> activePlayers = new ArrayList<>();
        activePlayers.add(human);
        activePlayers.addAll(bots);

        List<PokerHandEvaluator.EvaluatedHand> hands = new ArrayList<>();
        for (Player player : activePlayers) {
            hands.add(PokerHandEvaluator.evaluate(player, community));
        }

        hands.sort(Collections.reverseOrder());
        PokerHandEvaluator.EvaluatedHand winner = hands.get(0);

        int pot = potManager.getPot();

        winner.player.addChips(pot); // ✅ award pot
        winner.player.addWin();
        potManager.clear();
        Session.setPlayerChips(human.getChips());  // always sync after chips change
        StringBuilder result = new StringBuilder("🏆 Winner: " + winner.player.getName() + "\n\n");
        for (PokerHandEvaluator.EvaluatedHand hand : hands) {
            result.append(hand.player.getName())
                    .append(": ").append(hand.rank)
                    .append(" → ").append(hand.bestHand).append("\n");
        }
        result.append("\n").append("💥 Total Wins:\n")
                .append(human.getName()).append(": ").append(human.getWins()).append("\n");
        for (Player bot : bots) {
            result.append(bot.getName()).append(": ").append(bot.getWins()).append("\n");
        }

        updateChipsDisplay(); // ✅ reflect new chips
        showAlert(result.toString());
    }

    @FXML
    private void onResetClicked() {
        if (Session.getPlayerChips() <= 0) {
            showAlert("You're out of chips! Returning to main menu...");
            goToMainMenu();
            return;
        }

        playerCardsBox.getChildren().clear();
        communityCardsBox.getChildren().clear();
        betAmountField.setText("");
        betButton.setDisable(false);
        foldButton.setDisable(false);
        nextStageButton.setDisable(false);
        startNewGame();
    }

    private void goToMainMenu() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/MainMenu.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) resetButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Poker & Blackjack Game");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void disableActions() {
        betButton.setDisable(true);
        foldButton.setDisable(true);
        nextStageButton.setDisable(true);
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
    }


}
