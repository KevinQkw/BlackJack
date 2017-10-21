package blackjack;

import com.google.common.collect.Lists;

import java.util.List;
import java.util.Random;
import java.util.Stack;

public class Pile {
    private Stack<Card> cards;
    private List<Deck> decks;

    public Pile(List<Deck> decks) {
        this.decks = decks;
    }

    public Pile(Integer deckNum) {
        decks = Lists.newArrayList();
        for (int i = 0; i < deckNum; i++) {
            decks.add(new Deck());
        }
    }

    public Card getTopCard() {
        return cards.pop();
    }

    public void returnCard(Card card) {
        cards.add(card);
    }

    public void returnCards(List<Card> cards) {
        this.cards.addAll(cards);
    }

    public boolean isOutOfCards() {
        return cards.empty();
    }

    public boolean needReturnCards() {
        return cards.size() < decks.size() * 52 / 2;
    }

    public void shuffle() {
        List<Card> cardList = Lists.newArrayList(cards);
        cards = new Stack<>();
        Random random = new Random();
        int length = cardList.size();
        for (int i = 0; i < length; i++) {
            int num = random.nextInt(cardList.size());
            Card card = cardList.remove(num);
            cards.push(card);
        }
        System.out.println("洗牌完成");
    }
}
