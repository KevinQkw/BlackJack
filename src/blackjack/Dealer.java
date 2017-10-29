package blackjack;

import java.util.List;

/**
 * 庄家
 */
public class Dealer extends Person {
    private Hand hand;
    protected String name;

    public Dealer(BlackJackGame blackJackGame) {
        super(blackJackGame);
        hand = new Hand(this);
    }

    /**
     * 庄家抽牌，小于17直接抽，要是含有A使得最小小于17，大的大于17，庄家庄家决定是否抽牌
     * @param pile
     * @return
     */
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

    /**
     * 展示手牌
     */
    public void showHand() {
        for (Card card : hand.getCards()) {
            if (!card.isSeen()) {
                card.flop();
            }
        }
    }

    /**
     * 抽明牌
     * @param pile
     * @return
     */
    public Card drawSeenCard(Pile pile) {
        Card card = pile.getTopCard();
        card.flop();
        hand.addCard(card);
        return card;
    }

    /**
     * 抽暗牌
     * @param pile
     * @return
     */
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
