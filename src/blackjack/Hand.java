package blackjack;

import com.google.common.collect.Lists;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 一手牌
 */
@Data
public class Hand {
    private Person owner;//手牌的主人
    private List<Card> cards;//手牌中的牌
    private int result;

    public Hand(Person owner) {
        this.owner = owner;
        this.cards = Lists.newArrayList();
    }

    /**
     * 计算出所有牌可能的值
     *
     * @return
     */
    public List<Integer> calculateTotalValue() {
        List<Integer> valueList = Lists.newArrayList(0);
        for (Card card : cards) {
            List<Integer> temp = Lists.newArrayList();
            if (card.getFaceValue() != 1) {
                temp.addAll(valueList.stream().map(integer -> integer + card.getValue()).collect(Collectors.toList()));
            } else {
                temp.addAll(valueList.stream().map(integer -> integer + 1).collect(Collectors.toList()));
                temp.addAll(valueList.stream().map(integer -> integer + 11).collect(Collectors.toList()));
            }
            valueList = temp;
        }
        return valueList;
    }

    /**
     * 抽牌一张
     */
    public Card drawCard() {
        PlayRoom.playRoom.setDealingHand(this);
        Pile pile = owner.getBlackJackGame().getPile();
        Card card = pile.getTopCard().flop();
        addCard(card);
        return card;
    }

    /**
     * 添加一张手牌
     *
     * @param card
     */
    public void addCard(Card card) {
        cards.add(card);
    }

    /**
     * 弃牌
     *
     * @return
     */
    public List<Card> returnAllCards() {
        List<Card> cardList = Lists.newArrayList(cards).stream().map(card ->
                card.isSeen() ? card.flop() : card
        ).collect(Collectors.toList());
        cards.clear();
        return cardList;
    }

    /**
     * 是否能够分派
     *
     * @return
     */
    public boolean canSpilt() {
        return cards.size() == 2 && cards.get(0).getFaceValue().equals(cards.get(1).getFaceValue());
    }

    /**
     * 是否满手牌
     *
     * @return
     */
    public boolean isFull() {
        return cards.size() >= 5;
    }

    /**
     * 是否爆牌
     *
     * @return
     */
    public boolean isBust() {
        List<Integer> valueList = calculateTotalValue();
        for (Integer integer : valueList) {
            if (integer <= 21) {
                return false;
            }
        }
        return true;
    }

    /**
     * 是否是五小龙
     *
     * @return
     */
    public boolean isFiveDragon() {
        return isFull() && getMinValue() <= 21;
    }

    /**
     * 是否是BlackJack
     *
     * @return
     */
    public boolean isBlackJack() {
        List<Integer> valueList = calculateTotalValue();
        return cards.size() == 2 && valueList.contains(21);
    }

    /**
     * 获得最小的可能值
     *
     * @return
     */
    public int getMinValue() {
        List<Integer> valueList = calculateTotalValue();
        return valueList.stream().mapToInt(i -> i).min().orElse(0);
    }

    /**
     * 获得最大值
     *
     * @return
     */
    public int getMaxValue() {
        List<Integer> valueList = calculateTotalValue();
        return valueList.stream().mapToInt(i -> i).max().orElse(30);
    }

    /**
     * 计算小于等于21的最大值
     *
     * @return
     */
    public int getAvailableMaxValue() {
        List<Integer> valueList = calculateTotalValue();
        return valueList.stream().mapToInt(i -> i).filter(integer -> integer <= 21).max().orElse(0);
    }

    /**
     * 结算和庄家的差距
     *
     * @param dealerValue
     * @return
     */
    public int balance(int dealerValue) {
        result = getAvailableMaxValue() - dealerValue;
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Hand)) return false;
        if (!super.equals(o)) return false;

        Hand hand = (Hand) o;

        if (owner != null ? !owner.equals(hand.owner) : hand.owner != null) return false;
        return cards != null ? cards.equals(hand.cards) : hand.cards == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (owner != null ? owner.hashCode() : 0);
        result = 31 * result + (cards != null ? cards.hashCode() : 0);
        return result;
    }
}
