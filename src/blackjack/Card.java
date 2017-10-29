package blackjack;

import lombok.Data;

/**
 * 牌类
 */
@Data
public class Card {
    private Suit suit;//花色
    private Integer faceValue;//牌表面值
    private Integer value;//实际的值
    private boolean isSeen;//是否正面朝上

    public Card(Suit suit, Integer faceValue) {
        this.suit = suit;
        this.faceValue = faceValue;
        this.value = faceValue >= 10 ? 10 : faceValue;
        this.isSeen = false;
    }

    /**
     * 翻面
     * @return
     */
    public Card flop() {
        isSeen = !isSeen;
        return this;
    }
}
