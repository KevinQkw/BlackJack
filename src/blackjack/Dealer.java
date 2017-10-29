package blackjack;

import com.google.common.collect.Lists;

import java.util.List;


public class Dealer extends Person {
    private Hand hand;
    protected String name;

    public Dealer(BlackJackGame blackJackGame) {
        super(blackJackGame);
        hand = new Hand(this);
    }

    public int drawCards(Pile pile) {
        while (true) {
            if (hand.isBust()) {
                return hand.getMinValue();
            } else if(hand.isBlackJack()){
                return 21;
            }else if (hand.getMinValue() < 17 && hand.getMaxValue() > 16) {
                throw new RuntimeException();
            } else if (hand.getMinValue() < 17) {
                drawSeenCard(pile);
            } else {
                return hand.getMaxValue();
            }
        }
    }

    public void showHand() {
        for (Card card : hand.getCards()) {
            if (!card.isSeen()) {
                card.flop();
            }
        }
    }

    public Card drawSeenCard(Pile pile) {
        Card card = pile.getTopCard();
        card.flop();
        hand.addCard(card);
        return card;
    }

    public Card drawBlindCard(Pile pile) {
        Card card = pile.getTopCard();
        hand.addCard(card);
        return card;
    }

    public Hand getHand() {
        return hand;
    }

    @Override
    public List<Card> discardAll() {
        return hand.returnAllCards();
    }
}
