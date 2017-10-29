package blackjack;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 庄家和玩家的父类抽象类
 */
@Data
public abstract class Person {

    protected int id;

    protected BlackJackGame blackJackGame;//游戏类

    public Person(int id, BlackJackGame blackJackGame) {
        this.id = id;
        this.blackJackGame = blackJackGame;
    }

    public abstract List<Card> discardAll();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Person)) return false;
        if (!super.equals(o)) return false;

        Person person = (Person) o;

        return id == person.id;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + id;
        return result;
    }
}
