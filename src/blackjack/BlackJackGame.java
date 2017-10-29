package blackjack;

import com.google.common.collect.Lists;
import lombok.Data;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;

@Data
public class BlackJackGame {
    private Dealer dealer;
    private List<Player> players;
    private Pile pile;
    private Queue<Hand> hands;
    private List<Card> discards;

    public BlackJackGame(Integer deckNum) {
        this.dealer = new Dealer(this);
        this.pile = new Pile(deckNum);
        this.players = Lists.newArrayList();
        this.hands = new LinkedList<>();
        this.discards = Lists.newArrayList();
    }

    /**
     * 添加玩家
     * @param player
     * @return
     */
    public boolean addPlayer(Player player) {
        return players.size() < 5 && players.add(player);
    }

    /**
     * 开始一轮
     * @return 是否需要考虑保险的问题
     */
    public boolean startTurn() {
        //是否需要返回弃牌
        if (pile.needReturnCards()) {
            pile.returnCards(discards);
            discards.clear();
        }
        pile.shuffle();//弃牌
        for (Player player : players) {//初始化玩家
            player.init();
            for (Hand hand : player.getHands()) {//玩家抽2张明牌
                for (int i = 0; i < 2; i++) {
                    Card card = player.drawSeenCard(pile);
                    hand.addCard(card);
                }
                hands.offer(hand);
            }
        }
        //庄家抽牌，一明一暗
        Card card = dealer.drawSeenCard(pile);
        dealer.drawBlindCard(pile);
        hands.offer(dealer.getHand());
        return card.getFaceValue().equals(1);//是否需要考虑保险的问题
    }

    /**
     * 结束一轮
     */
    public void endTurn() {
        for (Player player : players) {
            discards.addAll(player.discardAll());
        }
        discards.addAll(dealer.discardAll());
    }

    /**
     * 庄家处理
     */
    public void dealerTurn() {
        dealer.showHand();
        dealer.drawCards(pile);
    }

    /**
     * 结算
     */
    public void balance() {
        Hand dealerHand = dealer.getHand();
        int dealerValue = dealerHand.getAvailableMaxValue();
        List<Player> balancePlayers = players.stream().filter(player -> player.getBet() > 0).collect(Collectors.toList());
        for (Player player : balancePlayers) {
            if (dealerHand.isBlackJack()) {//庄家是BlackJack
                if (player.isHasBuyInsurance()) {
                    player.getWinnerMoney(0.0);//买保险赔付2倍保险金（就是一倍赌注）
                }
                for (Hand hand : player.getHands()) {
                    if (hand.isBlackJack()) {//玩家也是BlackJack
                        player.getWinnerMoney(0.0);//平局退回赌注
                    }
                }
            } else {
                for (Hand hand : player.getHands()) {
                    int temp = hand.balance(dealerValue);//计算点数差距
                    if (temp > 0) {//点数大获得1倍赌注
                        player.getWinnerMoney(1.0);
                    }
                    if (hand.isBlackJack()) {
                        if (temp == 0) {
                            player.getWinnerMoney(1.0);//庄家21点玩家BlackJack额外获得1倍赌注（2倍赔付）
                        }
                        player.getWinnerMoney(0.0);//BlackJack额外获得1倍赌注（2倍赔付）
                    }
                }
            }
            player.setBet(0);//清空赌注
        }
    }

    /**
     * 切换一手牌
     * @return
     */
    public Hand continueGame() {
        return hands.size() == 0 ? null : hands.poll();
    }

    /**
     * 这一轮是否结束
     * @return
     */
    public boolean isThisTurnOver() {
        return hands.size() == 0;
    }
}
