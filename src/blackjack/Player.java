package blackjack;

import com.google.common.collect.Lists;
import lombok.Data;

import java.util.LinkedList;
import java.util.List;

/**
 * 玩家类
 */
@Data
public class Player extends Person {
    private String name;
    private List<Hand> hands;
    private Integer money;
    private Integer bet;
    private boolean hasBuyInsurance = false;

    public Player(int id, String name, Integer money, BlackJackGame blackJackGame) {
        super(id, blackJackGame);
        this.blackJackGame = blackJackGame;
        this.name = name;
        this.money = money;
        this.hands = Lists.newArrayList();
        this.bet = 0;
    }

    public void init() {
        hands.add(new Hand(this));
        hasBuyInsurance = false;
        System.out.println("Player " + name + " 开始新的一轮");
    }

    /**
     * 是否可以购买保险
     *
     * @return
     */
    public boolean buyInsurance() {
        if (bet / 2 > money) {
            return false;
        }
        money -= bet / 2;
        System.out.println("Player " + name + " 买了 " + bet / 2 + " 的保险");
        return true;
    }

    public boolean addBet(Integer amount) {
        if (amount > money) {
            return false;
        } else {
            money -= amount;
            bet += amount;
            System.out.println("Player " + name + " 下了 " + amount + " 的赌注");
            return true;
        }
    }

    public boolean doubleOperation() {
        return addBet(bet);
    }

    public void getWinnerMoney(Double multiple) {
        money += (int) (multiple * bet);
        money += bet;
        System.out.println("Player " + name + " 赢得 " + (multiple + 1) + " 倍赌注");
    }

    public void spilt() {
        Hand hand = hands.get(0);
        if (hands.size() != 1 || !hand.canSpilt()) {
            throw new RuntimeException();
        }
        hands.clear();
        List<Card> cardList = hand.getCards();
        Hand newHand1 = new Hand(this);
        newHand1.addCard(cardList.get(0));
        newHand1.drawCard();
        Hand newHand2 = new Hand(this);
        newHand2.addCard(cardList.get(1));
        newHand2.drawCard();
        hands.add(newHand1);
        hands.add(newHand2);
        ((LinkedList<Hand>) blackJackGame.getHands()).addFirst(newHand2);
        ((LinkedList<Hand>) blackJackGame.getHands()).addFirst(newHand1);
    }

    @Override
    public List<Card> discardAll() {
        List<Card> cardList = Lists.newArrayList();
        for (Hand hand : hands) {
            cardList.addAll(hand.returnAllCards());
        }
        hands.clear();
        return cardList;
    }

    public Card drawSeenCard(Pile pile) {
        Card card = pile.getTopCard();
        card.flop();
        return card;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Player)) return false;
        if (!super.equals(o)) return false;

        Player player = (Player) o;

        if (hasBuyInsurance != player.hasBuyInsurance) return false;
        if (name != null ? !name.equals(player.name) : player.name != null) return false;
        if (money != null ? !money.equals(player.money) : player.money != null) return false;
        return bet != null ? bet.equals(player.bet) : player.bet == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (money != null ? money.hashCode() : 0);
        result = 31 * result + (bet != null ? bet.hashCode() : 0);
        result = 31 * result + (hasBuyInsurance ? 1 : 0);
        return result;
    }
}
