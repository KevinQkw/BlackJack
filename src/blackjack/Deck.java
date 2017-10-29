package blackjack;

import com.google.common.collect.Sets;
import lombok.Data;

import java.util.Set;

@Data
public class Deck {
    private Set<Card> cards;

    public Deck() {
        cards = Sets.newHashSet();
        for (Suit suit : Suit.values()) {
            for (int i = 1; i <= 13; i++) {
                Card card = new Card(suit, i);
                cards.add(card);
            }
        }
    }
}
