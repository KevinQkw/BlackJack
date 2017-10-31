package blackjack;

import com.google.common.collect.Lists;
import lombok.Data;

import java.util.List;
import java.util.Random;
import java.util.Stack;

/**
 * 牌堆
 */
@Data
public class Pile {
    private Stack<Card> cards;
    private List<Deck> decks;

    public Pile(List<Deck> decks) {
        this.decks = decks;
        init();
    }

    public Pile() {
        decks = Lists.newArrayList();
        cards = new Stack<>();
    }

    public Pile(Integer deckNum) {
        decks = Lists.newArrayList();
        for (int i = 0; i < deckNum; i++) {
            decks.add(new Deck());
        }
        init();
    }

    private void init() {
        cards = new Stack<>();
        for (Deck deck : decks) {
            for (Card card : deck.getCards()) {
                cards.push(card);
            }
        }
        System.out.println("初始化牌堆完成");
    }

    /**
     * 取得牌堆顶部的第一张牌
     * @return
     */
    public Card getTopCard() {
        return cards.pop();
    }

    /**
     * 返回给牌堆牌
     * @param cards
     */
    public void returnCards(List<Card> cards) {
        this.cards.addAll(cards);
    }

    /**
     * 是否需要返还弃牌（小于一半）
     * @return
     */
    public boolean needReturnCards() {
        return cards.size() < decks.size() * 52 / 2;
    }

    /**
     * 洗牌
     */
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
