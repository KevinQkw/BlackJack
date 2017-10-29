package blackjack;

import com.google.common.collect.Lists;
import lombok.Data;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;

@Data
public class BlackJackGame {
    private Dealer dealer;
    private List<Player> players;
    private Pile pile;
    private Queue<Hand> hands;
    private List<Card> discards;

    public BlackJackGame(Integer deckNum) {
        this.dealer = new Dealer(this);
        this.pile = new Pile(deckNum);
        this.players = Lists.newArrayList();
        this.hands = new LinkedList<>();
        this.discards = Lists.newArrayList();
    }

    public boolean addPlayer(Player player) {
        return players.size() < 5 && players.add(player);
    }

    public boolean startTurn() {
        if (pile.needReturnCards()) {
            pile.returnCards(discards);
            discards.clear();
        }
        pile.shuffle();
        for (Player player : players) {
            player.init();
            for (Hand hand : player.getHands()) {
                for (int i = 0; i < 2; i++) {
                    Card card = player.drawSeenCard(pile);
                    hand.addCard(card);
                }
                hands.offer(hand);
            }
        }
        Card card = dealer.drawSeenCard(pile);
        dealer.drawBlindCard(pile);
        hands.offer(dealer.getHand());
        return card.getFaceValue().equals(1);
    }

    public void endTurn() {
        for (Player player : players) {
            discards.addAll(player.discardAll());
        }
        discards.addAll(dealer.discardAll());
    }

    public void dealerTurn() {
        dealer.showHand();
        dealer.drawCards(pile);
    }

    public void balance() {
        Hand dealerHand = dealer.getHand();
        int dealerValue = dealerHand.getAvailableMaxValue();
        List<Player> balancePlayers = players.stream().filter(player -> player.getBet() > 0).collect(Collectors.toList());
        if (dealerHand.isBlackJack()) {
            for (Player player : balancePlayers) {
                for (Hand hand : player.getHands()) {
                    if (hand.isBlackJack()) {
                        player.getWinnerMoney(0.0);
                    }
                }
                if (player.isHasBuyInsurance()) {
                    player.getWinnerMoney(0.0);
                }
            }
        }
        for (Player player : balancePlayers) {
            for (Hand hand : player.getHands()) {
                int temp = hand.balance(dealerValue);
                if (temp > 0) {
                    player.getWinnerMoney(1.0);
                }
            }
            player.setBet(0);
        }
    }

    public Hand continueGame() {
        return hands.size() == 0 ? null : hands.poll();
    }

    public boolean isThisTurnOver() {
        return hands.size() == 0;
    }
}
