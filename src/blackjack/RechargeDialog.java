package blackjack;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class RechargeDialog extends JDialog implements Runnable{
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JPanel panel1;

    public RechargeDialog() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        new Thread(this).start();
    }

    private void onOK() {
        // add your code here
        dispose();
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    private void createUIComponents() {
        panel1 = new MyJPanel();
    }

    @Override
    public void run() {
        try {
            Thread.sleep(2000);
            dispose();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public class MyJPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            ImageIcon background = new ImageIcon("付款码.png");
            g.drawImage(background.getImage(), 0, 0, 200, 300, background.getImageObserver());
        }
    }
}
