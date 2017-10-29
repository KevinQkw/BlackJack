package blackjack;

import com.google.common.collect.Lists;
import lombok.Data;

import java.util.LinkedList;
import java.util.List;

@Data
public class Player extends Person {
    private String name;
    private List<Hand> hands;
    private Integer money;
    private Integer bet;
    private boolean hasBuyInsurance = false;

    public Player(String name, Integer money, BlackJackGame blackJackGame) {
        super(blackJackGame);
        this.blackJackGame = blackJackGame;
        this.name = name;
        this.money = money;
        this.hands = Lists.newArrayList();
        this.bet = 0;
    }

    public void init() {
        hands.add(new Hand(this));
        hasBuyInsurance = false;
    }

    public boolean buyInsurance() {
        if (bet / 2 > money) {
            return false;
        }
        money -= bet / 2;
        return true;
    }

    public boolean addBet(Integer amount) {
        if (amount > money) {
            return false;
        } else {
            money -= amount;
            bet += amount;
            return true;
        }
    }

    public boolean doubleOperation() {
        return addBet(bet);
    }

    public void getWinnerMoney(Double multiple) {
        money += (int) (multiple * bet);
        money += bet;
    }

    public void spilt() {
        Hand hand = hands.get(0);
        if (hands.size() != 1 || !hand.canSpilt()) {
            throw new RuntimeException();
        }
        hands.clear();
        List<Card> cardList = hand.getCards();
        Hand newHand1 = new Hand(this);
        newHand1.addCard(cardList.get(0));
        newHand1.drawCard();
        Hand newHand2 = new Hand(this);
        newHand2.addCard(cardList.get(1));
        newHand2.drawCard();
        hands.add(newHand1);
        hands.add(newHand2);
        ((LinkedList<Hand>) blackJackGame.getHands()).addFirst(newHand2);
        ((LinkedList<Hand>) blackJackGame.getHands()).addFirst(newHand1);
    }

    @Override
    public List<Card> discardAll() {
        List<Card> cardList = Lists.newArrayList();
        for (Hand hand : hands) {
            cardList.addAll(hand.returnAllCards());
        }
        hands.clear();
        return cardList;
    }

    public Card drawSeenCard(Pile pile) {
        Card card = pile.getTopCard();
        card.flop();
        return card;
    }
}
