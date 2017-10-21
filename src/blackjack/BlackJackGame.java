package blackjack;

import com.google.common.collect.Lists;
import lombok.Data;

import java.util.List;

@Data
public class BlackJackGame {
    private Dealer dealer;
    private List<Player> players;
    private Pile pile;

    public BlackJackGame(Integer deckNum) {
        this.dealer = new Dealer();
        this.pile = new Pile(deckNum);
        this.players = Lists.newArrayList();
    }

    public void addPlayer(Player player) {
        players.add(player);
    }

    public void startGame() {
        if (pile.needReturnCards()) {
            for (Player player : players) {
                player.discardAll(pile);
            }
            dealer.discardAll(pile);
        }
        pile.shuffle();
        for (Player player : players) {
            for (Hand hand : player.getHands()) {
                Card card = player.drawSeenCard(pile);
                hand.addCard(card);
            }
        }
        dealer.drawSeenCard(pile);
        dealer.drawBlindCard(pile);
    }
}
