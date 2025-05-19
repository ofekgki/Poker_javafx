package src.texas;

import src.core.*;

import java.util.*;

public class PokerHandEvaluator {
    public enum HandRank {
        HIGH_CARD, PAIR, TWO_PAIR, THREE_OF_A_KIND, STRAIGHT,
        FLUSH, FULL_HOUSE, FOUR_OF_A_KIND, STRAIGHT_FLUSH
    }


    public static class EvaluatedHand implements Comparable<EvaluatedHand> {
        public final Player player;
        public final HandRank rank;
        public final List<Card> bestHand;
        public final List<Integer> rankValues;

        public EvaluatedHand(Player player, HandRank rank, List<Card> bestHand, List<Integer> rankValues) {
            this.player = player;
            this.rank = rank;
            this.bestHand = bestHand;
            this.rankValues = rankValues;
        }

        @Override
        public int compareTo(EvaluatedHand o) {
            int cmp = Integer.compare(this.rank.ordinal(), o.rank.ordinal());
            if (cmp != 0) return cmp;

            for (int i = 0; i < Math.min(this.rankValues.size(), o.rankValues.size()); i++) {
                cmp = Integer.compare(this.rankValues.get(i), o.rankValues.get(i));
                if (cmp != 0) return cmp;
            }
            return 0;
        }


        public String toString() {
            return player.getName() + ": " + rank + "-> " + bestHand;
        }
    }

    public static EvaluatedHand evaluate(Player player, List<Card> communityCards) {
        List<Card> allCards = new ArrayList<>();
        allCards.addAll(player.getHand());
        allCards.addAll(communityCards);

        return determineBestHand(player, allCards);

    }

    public static EvaluatedHand determineBestHand(Player player, List<Card> cards) {
        HandRank bestRank = HandRank.HIGH_CARD;
        List<Card> bestCombo = null;
        List<Integer> bestValues = null;

        for (List<Card> combo : combinations(cards, 5)) {
            HandRank currentRank = evaluateComboRank(combo);
            List<Integer> values = extractSortedValues(combo);
            if (currentRank.ordinal() > bestRank.ordinal() || (currentRank == bestRank && compareValues(values, bestValues) > 0))
            {
                bestRank = currentRank;
                bestCombo = new ArrayList<>(combo);
                bestValues = values;
            }

        }

        return new EvaluatedHand(player, bestRank, bestCombo, bestValues != null ? bestValues : new ArrayList<>());

    }

    public static HandRank evaluateComboRank(List<Card> combo) {

        Map<String, Integer> rankCounts = new HashMap<>();
        Map<Card.Suit, Integer> suitCounts = new HashMap<>();

        for (Card c : combo) {
            rankCounts.merge(c.getRank().name(), 1, Integer::sum);
            suitCounts.merge(c.getSuit(), 1, Integer::sum);
        }


        boolean hasThree = rankCounts.containsValue(3);
        boolean hasFour = rankCounts.containsValue(4);
        boolean hasFlush = suitCounts.containsValue(5);
        boolean hasStraight = isStraight(combo);
        long pairs = rankCounts.values().stream().filter(v -> v == 2).count();

        if (hasFlush && hasStraight) return HandRank.STRAIGHT_FLUSH;
        if (hasFour) return HandRank.FOUR_OF_A_KIND;
        if (hasThree && pairs >= 1) return HandRank.FULL_HOUSE;
        if (hasFlush) return HandRank.FLUSH;
        if (hasStraight) return HandRank.STRAIGHT;
        if (hasThree) return HandRank.THREE_OF_A_KIND;
        if (pairs >= 2) return HandRank.TWO_PAIR;
        if (pairs == 1) return HandRank.PAIR;

        return HandRank.HIGH_CARD;
    }

    private static boolean isStraight(List<Card> cards) {
        Set<Integer> ranks = new HashSet<>();
        for (Card card : cards) {
            ranks.add(card.getRank().ordinal());
        }

        List<Integer> sorted = new ArrayList<>(ranks);
        Collections.sort(sorted);

        int consecutive = 1;

        for (int i = 1; i < sorted.size(); i++) {
            if (sorted.get(i) == sorted.get(i - 1) + 1) {
                consecutive++;
                if (consecutive >= 5) return true;
            } else {
                consecutive = 1;
            }

        }

        return false;
    }

    private static List<Integer> extractSortedValues(List<Card> cards) {
        List<Integer> values = new ArrayList<>();
        for (Card c : cards) {
            values.add(c.getRank().ordinal());
        }
        values.sort(Comparator.reverseOrder());
        return values;
    }




    private static int compareValues(List<Integer> a, List<Integer> b) {
        if (a == null || b == null) return 0;
        for (int i = 0; i < Math.min(a.size(), b.size()); i++) {
            int cmp = Integer.compare(a.get(i), b.get(i));
            if (cmp != 0) return cmp;
        }
        return 0;
    }


    private static List<List<Card>> combinations(List<Card> cards, int k) {
        List<List<Card>> result = new ArrayList<>();
        combinationHelper(cards, k, 0, new ArrayList<>(), result);
        return result;
    }

    private static void combinationHelper(List<Card> list, int k, int index, List<Card> current, List<List<Card>> result) {
        if (current.size() == k) {
            result.add(new ArrayList<>(current));
            return;
        }
        for (int i = index; i < list.size(); i++) {
            current.add(list.get(i));
            combinationHelper(list, k, i + 1, current, result);
            current.removeLast();
        }
    }


}
