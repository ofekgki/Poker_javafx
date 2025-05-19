package src.core;

import java.util.Scanner;

public class HumanPlayer extends Player {
    private static final Scanner scanner = new Scanner(System.in);

    public HumanPlayer(String name, int chips) {
        super(name, chips);
    }

    @Override
    public Move makeMove() {
        System.out.println(name + ", your hand: " + hand);
        String type;
        int amount = 0;

        while (true) {
            System.out.print("Enter move (BET <amount> / FOLD): ");
            String input = scanner.nextLine().trim().toUpperCase();

            if (input.startsWith("BET")) {
                String[] parts = input.split(" ");
                if (parts.length == 2) {
                    try {
                        amount = Integer.parseInt(parts[1]);
                        type = "BET";
                        break;
                    } catch (NumberFormatException e) {
                        System.out.println("❌ Invalid amount.");
                    }
                } else {
                    System.out.println("❌ Format must be: BET <amount>");
                }
            } else if (input.equals("FOLD")) {
                type = "FOLD";
                break;
            } else {
                System.out.println("❌ Invalid move. Type BET <amount> or FOLD.");
            }
        }

        return new Move(type, amount);
    }
}