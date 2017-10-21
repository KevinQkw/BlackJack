package blackjack;

import lombok.Data;

@Data
public class Card {
    private CardColor cardColor;
    private Integer faceValue;
    private Integer value;
    private boolean isSeen;

    public Card(CardColor cardColor, Integer faceValue) {
        this.cardColor = cardColor;
        this.faceValue = faceValue;
        this.value = faceValue >= 10 ? 10 : faceValue;
        this.isSeen = false;
    }

    public void flop() {
        isSeen = !isSeen;
    }
}
