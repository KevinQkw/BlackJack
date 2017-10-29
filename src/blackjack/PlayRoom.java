package blackjack;

import com.google.common.collect.Lists;
import javafx.util.Pair;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import static java.lang.Math.toRadians;

public class PlayRoom extends JFrame {
    private JTextField textField1;
    JPanel main;
    private JButton hitButton;
    private JButton standButton;
    private JButton doubleButton;
    private JButton spiltButton;
    private JButton buyInsuranceButton;
    private JButton button1;
    private JPanel display;
    private JLabel player1;
    private JLabel player2;
    private JLabel player3;
    private JLabel player4;
    private JLabel player5;
    private JButton continueGame;
    private JPanel playersInfo;

    private Hand currentHand;
    private BlackJackGame blackJackGame;
    private JTextField textField;

    private Boolean isPause = true;
    private Boolean canBuyInsurance = false;
    private List<JLabel> playerLabels;

    public static void main(String[] args) {
        System.out.println("Welcome to BlackJack Game!");
        PlayRoom frame = new PlayRoom();
        frame.setContentPane(frame.main);
        frame.setBounds(200, 100, 1400, 850);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        frame.playGame();
    }


    public PlayRoom() {
        hitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                currentHand.drawCard();
                if (!validateCurrentHand()) {
                    updateCurrentHand();
                }
                doubleButton.setEnabled(false);
                spiltButton.setEnabled(false);
            }
        });
        standButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateCurrentHand();
            }
        });
        doubleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentHand.getOwner() instanceof Player) {
                    ((Player) currentHand.getOwner()).doubleOperation();
                    currentHand.drawCard();
                    updateCurrentHand();
                }
            }
        });
        spiltButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ((Player) currentHand.getOwner()).spilt();
                updateCurrentHand();
            }
        });
        continueGame.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                isPause = false;
            }
        });
        playerLabels = Lists.newArrayList(player1, player2, player3, player4, player5);
        buyInsuranceButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ((Player) currentHand.getOwner()).buyInsurance();
                buyInsuranceButton.setEnabled(false);
            }
        });
    }

    private void createUIComponents() {
        /*while (true) {
            try {
                String deckNum = JOptionPane.showInputDialog("几副牌作为牌堆？");
                int deckNumber = Integer.parseInt(deckNum);
                blackJackGame = new BlackJackGame(deckNumber);
                break;
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "请输入合法数字！");
            }
        }*/
        playersInfo = new JPanel();
        playersInfo.setBounds(0, 0, 1200, 200);
        blackJackGame = new BlackJackGame(2);
        while (true) {
            try {
                int peopleNumber = Integer.parseInt(JOptionPane.showInputDialog("游戏人数？"));
                if (peopleNumber > 5) {
                    JOptionPane.showMessageDialog(null, "最多只能五名玩家！");
                } else {
                    for (int i = 0; i < peopleNumber; i++) {
                        try {
                            String name = JOptionPane.showInputDialog("请输入姓名");
                            if (name == null) {
                                throw new RuntimeException();
                            }
                            Player player = new Player(name, 10000, blackJackGame);
                            if (!blackJackGame.addPlayer(player)) {
                                JOptionPane.showMessageDialog(null, "最多只能五名玩家！");
                            }
                        } catch (Exception e) {
                            JOptionPane.showMessageDialog(null, "名字不能为空！");
                        }
                    }
                    break;
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "请输入合法数字！");
            }
        }

        display = new HandPanel(blackJackGame);

        //main.add(label, new Integer(Integer.MIN_VALUE));
    }

    public void playGame() {
        boolean isContinue = true;
        while (isContinue) {
            synchronized (isPause) {
                while (isPause) {
                    refresh();
                }
            }
            List<Player> players = blackJackGame.getPlayers();
            for (Player player : players) {
                /*while (true) {
                    try {
                        refresh();
                        String bet = JOptionPane.showInputDialog(player.getName() + "的下注金额？");
                        int betAmount = Integer.parseInt(bet);
                        if (!player.addBet(betAmount)) {
                            RechargeDialog dialog = new RechargeDialog();
                            dialog.setBounds(200, 200, 250, 400);
                            dialog.setVisible(true);
                            player.setBet(betAmount);
                        }
                        break;
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(null, "请输入合法数字！");
                    }
                }*/
                player.addBet(200);
            }
            canBuyInsurance = blackJackGame.startTurn();
            refresh();
            updateCurrentHand();
            while (!blackJackGame.isThisTurnOver()) {
                refresh();
            }
            try {
                blackJackGame.dealerTurn();
            } catch (RuntimeException re) {
                //表示需要庄家决定是否抽牌
                hitButton.setEnabled(true);
                standButton.setEnabled(true);
                synchronized (currentHand) {
                    while (currentHand != null) {
                        refresh();
                    }
                }
            }
            refresh();
            blackJackGame.balance();
            refresh();
            blackJackGame.endTurn();
            switch (JOptionPane.showConfirmDialog(null, "本局已结束，是否开始下一局？")) {
                case JOptionPane.YES_OPTION:
                    isPause = false;
                    break;
                case JOptionPane.NO_OPTION:
                    isPause = true;
                    break;
                default:
                    isContinue = false;
                    break;
            }
        }
        System.exit(0);
    }

    public void refresh() {
        int index = 0;
        for (Player player : blackJackGame.getPlayers()) {
            JLabel jLabel = playerLabels.get(index);
            jLabel.setText("Player: " + player.getName() + " Money: " + player.getMoney());
            if (currentHand != null && player.equals(currentHand.getOwner())) {
                jLabel.setBorder(BorderFactory.createLineBorder(Color.yellow));
            } else {
                jLabel.setBorder(BorderFactory.createLineBorder(Color.white));
            }
            index++;
        }
        display.repaint();
        if (currentHand != null) {
            textField1.setText(String.valueOf(currentHand.calculateTotalValue()));
        }
        //textField.setText(String.valueOf(currentHand.calculateTotalValue()));
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void updateCurrentHand() {
        disableAllButton();
        currentHand = blackJackGame.continueGame();
        if (currentHand != null && currentHand.getOwner() instanceof Player) {
            hitButton.setEnabled(true);
            standButton.setEnabled(true);
            doubleButton.setEnabled(true);
            if (currentHand.canSpilt()) {
                spiltButton.setEnabled(true);
            }
            refresh();
            buyInsuranceButton.setEnabled(canBuyInsurance && !((Player) currentHand.getOwner()).isHasBuyInsurance());
            if (!validateCurrentHand()) {
                updateCurrentHand();
            }
        }
    }

    public boolean validateCurrentHand() {
        if (currentHand.getOwner() instanceof Player) {
            Player player = (Player) currentHand.getOwner();
            if (currentHand.isBust()) {
                JOptionPane.showMessageDialog(null, "你爆牌了!");
                player.setBet(0);
                return false;
            } else if (currentHand.isBlackJack()) {
                JOptionPane.showMessageDialog(null, "你已经BlackJack了!");
                return false;
            } else if (currentHand.isFiveDragon()) {
                JOptionPane.showMessageDialog(null, "你已经是五小龙了!");
                player.getWinnerMoney(2.0);
                player.setBet(0);
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

    public void disableAllButton() {
        hitButton.setEnabled(false);
        standButton.setEnabled(false);
        doubleButton.setEnabled(false);
        spiltButton.setEnabled(false);
        buyInsuranceButton.setEnabled(false);
    }

    class HandPanel extends JPanel {
        private static final double cardRatio = 2.0 / 3.0;
        private static final int tableStartY = 110;
        private static final int r = 400;
        private Hand hand;
        private List<Player> players;

        public HandPanel(BlackJackGame blackJackGame) {
            this.hand = blackJackGame.getDealer().getHand();
            this.players = blackJackGame.getPlayers();
        }

        public void paint(Graphics g) {
            //调用的super.paint(g),让父类做一些事前的工作，如刷新屏幕
            super.paint(g);
            ImageIcon background = new ImageIcon("table1.JPG");
            g.drawImage(background.getImage(), 0, 0, background.getIconWidth(), background.getIconHeight(), background.getImageObserver());

            int total = 0;
            for (Player player : players) {
                for (Hand hand : player.getHands()) {
                    total++;
                }
            }
            double angle = 180.0 / total;
            int index = 0;
            for (Player player : players) {
                for (Hand hand : player.getHands()) {
                    double angles = angle * (2 * index + 1) / 2.0;
                    double rad = Math.toRadians(angles);
                    index++;
                    List<Card> cardList = hand.getCards();
                    double x = 600 - r * Math.cos(rad) - 50;
                    double y = Math.sin(rad) * r;
                    for (int i = 0; i < cardList.size(); i++) {
                        Card card = cardList.get(i);
                        printCard(card, i * 20 * cardRatio + x, tableStartY + y, g);
                        //g.drawImage(imageIcon.getImage(), 0 + i * 20, 600, imageIcon.getIconWidth(), imageIcon.getIconHeight(), imageIcon.getImageObserver());
                    }
                    if (hand.equals(currentHand)) {
                        ImageIcon imageIcon = new ImageIcon("pukeImage/" + "back" + ".jpg");
                        g.setColor(Color.YELLOW);
                        g.drawRect((int) x, tableStartY + (int) y, (int) (cardList.size() * 20 * cardRatio + imageIcon.getIconWidth() * cardRatio), (int) (imageIcon.getIconHeight() * cardRatio));
                    }
                }
            }

            List<Card> cardList = hand.getCards();
            for (int i = 0; i < cardList.size(); i++) {
                Card card = cardList.get(i);
                printCard(card, 500 + i * 20 * cardRatio, tableStartY, g);
                //g.drawImage(imageIcon.getImage(), 500 + i * 20, 200, imageIcon.getIconWidth(), imageIcon.getIconHeight(), imageIcon.getImageObserver());
            }
        }

        private void printCard(Card card, double x, double y, Graphics g) {
            CardColor cardColor = card.getCardColor();
            String fileName = card.isSeen() ? String.valueOf((cardColor.ordinal() * 13) + card.getFaceValue()) : "back";
            ImageIcon imageIcon = new ImageIcon("pukeImage/" + fileName + ".jpg");
            g.drawImage(imageIcon.getImage(), (int) x, (int) y, (int) (imageIcon.getIconWidth() * cardRatio), (int) (imageIcon.getIconHeight() * cardRatio), imageIcon.getImageObserver());
        }
    }
}

/*class HandPanel extends JPanel {
    private static final double cardRatio = 2.0 / 3.0;
    private static final int tableStartY = 110;
    private static final int r = 400;
    private Hand hand;
    private List<Player> players;

    public HandPanel(BlackJackGame blackJackGame) {
        this.hand = blackJackGame.getDealer().getHand();
        this.players = blackJackGame.getPlayers();
    }

    public void paint(Graphics g) {
        //调用的super.paint(g),让父类做一些事前的工作，如刷新屏幕
        super.paint(g);
        ImageIcon background = new ImageIcon("table1.JPG");
        g.drawImage(background.getImage(), 0, 0, background.getIconWidth(), background.getIconHeight(), background.getImageObserver());

        int total = 0;
        for (Player player : players) {
            for (Hand hand : player.getHands()) {
                total++;
            }
        }
        double angle = 180.0 / total;
        int index = 0;
        for (Player player : players) {
            for (Hand hand : player.getHands()) {
                double angles = angle * (2 * index + 1) / 2.0;
                double rad = Math.toRadians(angles);
                index++;
                List<Card> cardList = hand.getCards();
                double x = 600 - r * Math.cos(rad) - 50;
                double y = Math.sin(rad) * r;
                for (int i = 0; i < cardList.size(); i++) {
                    Card card = cardList.get(i);
                    printCard(card, i * 20 * cardRatio + x, tableStartY + y, g);
                    //g.drawImage(imageIcon.getImage(), 0 + i * 20, 600, imageIcon.getIconWidth(), imageIcon.getIconHeight(), imageIcon.getImageObserver());
                }
            }
        }

        List<Card> cardList = hand.getCards();
        for (int i = 0; i < cardList.size(); i++) {
            Card card = cardList.get(i);
            printCard(card, 500 + i * 20 * cardRatio, tableStartY, g);
            //g.drawImage(imageIcon.getImage(), 500 + i * 20, 200, imageIcon.getIconWidth(), imageIcon.getIconHeight(), imageIcon.getImageObserver());
        }
    }

    private void printCard(Card card, double x, double y, Graphics g) {
        CardColor cardColor = card.getCardColor();
        String fileName = card.isSeen() ? String.valueOf((cardColor.ordinal() * 13) + card.getFaceValue()) : "back";
        ImageIcon imageIcon = new ImageIcon("pukeImage/" + fileName + ".jpg");
        g.drawImage(imageIcon.getImage(), (int) x, (int) y, (int) (imageIcon.getIconWidth() * cardRatio), (int) (imageIcon.getIconHeight() * cardRatio), imageIcon.getImageObserver());
    }
}*/

