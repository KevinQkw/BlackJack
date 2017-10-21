package blackjack;

import com.google.common.collect.Lists;
import lombok.Data;

import java.util.List;

@Data
public class Player extends Person {
    private List<Hand> hands;
    private Integer money;
    private Integer bet;
    private boolean hasBuyInsurance;

    public Player(Integer money) {
        this.money = money;
    }

    public boolean buyInsurance() {
        return addBet(bet / 2);
    }

    private boolean addBet(Integer amount) {
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

    @Override
    public void discardAll(Pile pile) {
        for (Hand hand : hands) {
            pile.returnCards(hand.returnAllCards());
        }
        hands = Lists.newArrayList(new Hand());
    }

    public Card drawSeenCard(Pile pile) {
        Card card = pile.getTopCard();
        card.flop();
        return card;
    }
}
