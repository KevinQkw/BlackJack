package blackjack;

import lombok.Data;

import java.util.List;

@Data
public abstract class Person {

    protected BlackJackGame blackJackGame;

    public Person(BlackJackGame blackJackGame) {
        this.blackJackGame = blackJackGame;
    }

    public abstract List<Card> discardAll();
}
