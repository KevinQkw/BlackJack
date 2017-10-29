package blackjack;

import lombok.Data;

import java.util.List;

/**
 * 庄家和玩家的父类抽象类
 */
@Data
public abstract class Person {

    protected BlackJackGame blackJackGame;//游戏类

    public Person(BlackJackGame blackJackGame) {
        this.blackJackGame = blackJackGame;
    }

    public abstract List<Card> discardAll();
}
