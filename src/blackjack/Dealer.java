package blackjack;

import com.google.common.collect.Lists;

import java.util.List;


public class Dealer extends Person {
    private Hand hand;
    protected String name;

    public int drawCards(Pile pile) {
        while (true) {
            if (hand.isBust()) {
                return hand.getMinValue();
            } else if (hand.getMinValue() < 17 && hand.getMaxValue() > 16) {
                throw new RuntimeException();
            } else if (hand.getMinValue() < 17) {
                drawSeenCard(pile);
            } else {
                return hand.getMaxValue();
            }
        }
    }

    public void drawSeenCard(Pile pile) {
        Card card = pile.getTopCard();
        card.flop();
        hand.addCard(card);
    }

    public void drawBlindCard(Pile pile) {
        Card card = pile.getTopCard();
        hand.addCard(card);
    }

    @Override
    public void discardAll(Pile pile) {
        pile.returnCards(hand.returnAllCards());
    }
}
