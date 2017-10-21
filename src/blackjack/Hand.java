package blackjack;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;
import lombok.Data;

@Data
public class Hand {
    private List<Card> cards;

    public Hand() {
        cards = Lists.newArrayList();
    }

    private List<Integer> calculateTotalValue() {
        List<Integer> valueList = Lists.newArrayList(0);
        for (Card card : cards) {
            List<Integer> temp = Lists.newArrayList();
            if (card.getFaceValue() != 1) {
                temp.addAll(valueList.stream().map(integer -> integer + card.getValue()).collect(Collectors.toList()));
            } else {
                temp.addAll(valueList.stream().map(integer -> integer + 1).collect(Collectors.toList()));
                temp.addAll(valueList.stream().map(integer -> integer + 10).collect(Collectors.toList()));
            }
            valueList = temp;
        }
        return valueList;
    }

    public void addCard(Card card) {
        cards.add(card);
    }

    public List<Card> returnAllCards() {
        List<Card> cardList = Lists.newArrayList(cards);
        cards.clear();
        return cardList;
    }

    public boolean isFull() {
        return cards.size() >= 5;
    }

    public boolean isBust() {
        List<Integer> valueList = calculateTotalValue();
        for (Integer integer : valueList) {
            if (integer <= 21) {
                return false;
            }
        }
        return true;
    }

    public boolean isBlackJack() {
        List<Integer> valueList = calculateTotalValue();
        return cards.size() == 2 && valueList.contains(21);
    }

    public int getMinValue() {
        List<Integer> valueList = calculateTotalValue();
        return valueList.stream().mapToInt(i -> i).min().orElse(0);
    }

    public int getMaxValue() {
        List<Integer> valueList = calculateTotalValue();
        return valueList.stream().mapToInt(i -> i).max().orElse(30);
    }
}
