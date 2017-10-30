package blackjack;

import com.google.common.collect.Lists;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicBorders;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * main类和绘制UI
 */
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
    public static PlayRoom playRoom;

    public static void main(String[] args) {
        System.out.println("Welcome to BlackJack Game!");
        playRoom = new PlayRoom();
        playRoom.setContentPane(playRoom.main);
        playRoom.setBounds(200, 100, 1400, 850);
        playRoom.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        playRoom.setVisible(true);
        playRoom.playGame();
    }


    public PlayRoom() {
        hitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                disableAllButton();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        currentHand.drawCard();
                        hitButton.setEnabled(true);
                        standButton.setEnabled(true);
                        if (!validateCurrentHand()) {
                            updateCurrentHand();
                        }
                    }
                }).start();
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
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            currentHand.drawCard();
                            validateCurrentHand();
                            updateCurrentHand();
                        }
                    }).start();
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
        continueGame.setFont(new Font("微软雅黑", Font.PLAIN, 16));
        continueGame.setSize(60, 40);
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
        //牌堆由几副牌构成
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
                            Player player = new Player(i + 1, name, 10000, blackJackGame);
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

        //多态
        display = new HandPanel(blackJackGame);
    }

    /**
     * 启动游戏
     */
    public void playGame() {
        boolean isContinue = true;
        while (isContinue) {
            synchronized (isPause) {
                while (isPause) {
                    refresh();
                }
            }
            List<Player> players = blackJackGame.getPlayers();
            for (Player player : players) {//轮流下注
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
            //开始一轮游戏
            canBuyInsurance = blackJackGame.startTurn();
            refresh();
            updateCurrentHand();
            while (!blackJackGame.isThisTurnOver()) {
                refresh();
            }
            try {
                blackJackGame.dealerTurn();//庄家结算（自动）
            } catch (RuntimeException re) {
                //表示需要庄家决定是否抽牌
                JOptionPane.showMessageDialog(null, "请庄家操作！");
                hitButton.setEnabled(true);
                standButton.setEnabled(true);
                synchronized (currentHand) {
                    while (currentHand != null) {
                        refresh();
                    }
                }
            }
            refresh();
            blackJackGame.balance();//结算钱
            refresh();
            blackJackGame.endTurn();//结束一轮弃牌
            switch (JOptionPane.showConfirmDialog(null, "本局已结束，是否开始下一局？")) {
                case JOptionPane.YES_OPTION:
                    isPause = false;//直接继续
                    break;
                case JOptionPane.NO_OPTION:
                    isPause = true;//暂停
                    break;
                default:
                    isContinue = false;//退出
                    break;
            }
        }
        System.exit(0);
    }

    /**
     * 刷新ui的函数
     */
    public void refresh() {
        int index = 0;
        Font font = new Font("微软雅黑", Font.PLAIN, 18);
        for (Player player : blackJackGame.getPlayers()) {
            JLabel jLabel = playerLabels.get(index);
            jLabel.setFont(font);
            jLabel.setText(" 玩家: " + player.getName() + "  筹码: " + player.getMoney());
            if (currentHand != null && player.equals(currentHand.getOwner())) {//标识当前操作的用户
                jLabel.setBorder(BorderFactory.createLineBorder(Color.red));
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
            Thread.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 换到下一组操作手牌
     */
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
            buyInsuranceButton.setEnabled(canBuyInsurance && !((Player) currentHand.getOwner()).isHasBuyInsurance());//是否可以买保险
            if (!validateCurrentHand()) {//例牌直接再次切换
                updateCurrentHand();
            }
        }
    }

    /**
     * 检查当前手牌是否是例牌，并对例牌做出直接赔付
     *
     * @return 是否是例牌
     */
    public boolean validateCurrentHand() {
        if (currentHand.getOwner() instanceof Player) {
            Player player = (Player) currentHand.getOwner();
            if (currentHand.isBust()) {
                JOptionPane.showMessageDialog(null, "你爆牌了!");
                player.setBet(0);//玩家爆牌直接输
                return false;
            } else if (currentHand.isBlackJack()) {
                JOptionPane.showMessageDialog(null, "你已经BlackJack了!");
                return false;
            } else if (currentHand.isFiveDragon()) {
                JOptionPane.showMessageDialog(null, "你已经是五小龙了!");
                player.getWinnerMoney(2.0);
                player.setBet(0);//玩家五小龙直接胜利获得2倍赔付
                return false;
            } else {
                return true;
            }
        } else {
            if (currentHand.isBust()) {
                JOptionPane.showMessageDialog(null, "庄家爆牌了!");
                return false;
            } else if (currentHand.isBlackJack()) {
                JOptionPane.showMessageDialog(null, "庄家已经BlackJack了!");
                return false;
            } else {
                return true;
            }
        }
    }

    public void disableAllButton() {
        hitButton.setEnabled(false);
        standButton.setEnabled(false);
        doubleButton.setEnabled(false);
        spiltButton.setEnabled(false);
        buyInsuranceButton.setEnabled(false);
    }

    public void setDealingHand(Hand dealingHand) {
        int totalTimes = 24;//(int) (totalTime * 10);
        ((HandPanel) display).setDealingHand(dealingHand, totalTimes);
        for (int i = 0; i < totalTimes; i++) {
            refresh();
        }
    }

    /**
     * 内部画图的Panel类，负责画出牌和桌面
     */
    class HandPanel extends JPanel {
        private static final double cardRatio = 2.0 / 3.0;//牌的缩放比例
        private static final int tableStartY = 110;//桌面的起始位置
        private static final int r = 400;//桌面半径
        private static final int pileX = 700;
        private static final int pileY = 40;
        private Hand dealerHand;//庄家的牌（只可能有一个）
        private List<Player> players;//玩家的list

        private Hand dealingHand;
        private int currentTime;
        private int totalTime;

        /**
         * 构造函数，初始化dealerHand和players
         *
         * @param blackJackGame 游戏类
         */
        public HandPanel(BlackJackGame blackJackGame) {
            this.dealerHand = blackJackGame.getDealer().getHand();
            this.players = blackJackGame.getPlayers();
            this.dealingHand = null;
            this.totalTime = 0;
            this.currentTime = 0;
        }

        public void setDealingHand(Hand dealingHand, int totalTime) {
            this.totalTime = totalTime;
            this.dealingHand = dealingHand;
            this.currentTime = 0;
        }

        /**
         * repaint会调用这个函数
         *
         * @param g 绘图的Graphics对象
         */
        public void paint(Graphics g) {
            //调用的super.paint(g),让父类做一些事前的工作，如刷新屏幕
            super.paint(g);
            //背景绘制
            ImageIcon background = new ImageIcon("mytable.jpg");
            g.drawImage(background.getImage(), 0, 20, background.getIconWidth(), background.getIconHeight(), background.getImageObserver());

            ImageIcon pile = new ImageIcon("pukeImage/back.jpg");
            g.drawImage(pile.getImage(), pileX, tableStartY + pileY, (int) (pile.getIconWidth() * cardRatio), (int) (pile.getIconHeight() * cardRatio), pile.getImageObserver());

            //统计Hand数
            int total = 0;
            for (Player player : players) {
                for (Hand hand : player.getHands()) {
                    total++;
                }
            }

            //计算每个Hand占多少角度
            double angle = 180.0 / total;
            int index = 0;
            for (Player player : players) {
                for (Hand hand : player.getHands()) {
                    double angles = angle * (2 * index + 1) / 2.0;//计算绘制的集体角度
                    double rad = Math.toRadians(angles);//转换角度和弧度
                    index++;
                    List<Card> cardList = hand.getCards();
                    //通过sin和cos函数计算出画图位置
                    double x = 600 - r * Math.cos(rad) - 50;
                    double y = Math.sin(rad) * r;
                    //画出Hand中的Card
                    for (int i = 0; i < cardList.size(); i++) {
                        Card card = cardList.get(i);
                        printCard(card, i * 20 * cardRatio + x, tableStartY + y, g);
                        //g.drawImage(imageIcon.getImage(), 0 + i * 20, 600, imageIcon.getIconWidth(), imageIcon.getIconHeight(), imageIcon.getImageObserver());
                    }
                    //用黄色的框标识当前操作的牌
                    if (hand.equals(currentHand)) {
                        ImageIcon imageIcon = new ImageIcon("pukeImage/back.jpg");
                        g.setColor(Color.YELLOW);
                        g.drawRect((int) x, tableStartY + (int) y, (int) ((cardList.size() - 1) * 20 * cardRatio + imageIcon.getIconWidth() * cardRatio), (int) (imageIcon.getIconHeight() * cardRatio));
                    }
                    if (dealingHand != null && hand.equals(dealingHand)) {
                        g.drawImage(pile.getImage(), (int) ((x - pileX) / totalTime * currentTime) + pileX, (int) ((y - pileY) / totalTime * currentTime) + pileY + tableStartY, (int) (pile.getIconWidth() * cardRatio), (int) (pile.getIconHeight() * cardRatio), pile.getImageObserver());
                        currentTime++;
                        if (currentTime == totalTime) {
                            dealingHand = null;
                            totalTime = 0;
                            currentTime = 0;
                        }
                    }
                }
            }

            //画出庄家的Hand
            List<Card> cardList = dealerHand.getCards();
            for (int i = 0; i < cardList.size(); i++) {
                Card card = cardList.get(i);
                printCard(card, 500 + i * 20 * cardRatio, tableStartY, g);
                //g.drawImage(imageIcon.getImage(), 500 + i * 20, 200, imageIcon.getIconWidth(), imageIcon.getIconHeight(), imageIcon.getImageObserver());
            }
            if (dealingHand != null && dealerHand.equals(dealingHand)) {
                g.drawImage(pile.getImage(), (int) ((500 - pileX) / totalTime * currentTime) + pileX, (int) ((pileY - tableStartY) / totalTime * currentTime) + pileY + tableStartY, (int) (pile.getIconWidth() * cardRatio), (int) (pile.getIconHeight() * cardRatio), pile.getImageObserver());
                currentTime++;
                if (currentTime == totalTime) {
                    dealingHand = null;
                    totalTime = 0;
                    currentTime = 0;
                }
            }
        }

        /**
         * 画出单张牌的函数
         *
         * @param card
         * @param x
         * @param y
         * @param g
         */
        private void printCard(Card card, double x, double y, Graphics g) {
            Suit suit = card.getSuit();
            String fileName = card.isSeen() ? String.valueOf((suit.ordinal() * 13) + card.getFaceValue()) : "back";
            ImageIcon imageIcon = new ImageIcon("pukeImage/" + fileName + ".jpg");
            g.drawImage(imageIcon.getImage(), (int) x, (int) y, (int) (imageIcon.getIconWidth() * cardRatio), (int) (imageIcon.getIconHeight() * cardRatio), imageIcon.getImageObserver());
        }
    }
}

