package controller;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

import model.Card;
import model.GameModel;
import util.SoundManager;
import view.CardButton;
import view.GameView;

public class GameController implements ActionListener {
    private final GameView view;
    private final GameModel model;
    private boolean isProcessing = false;

    public GameController(GameView view) {
        this.view = view;
        this.model = view.getModel();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (isProcessing || !model.isGameStarted()) return;
        
        Object source = e.getSource();
        if (!(source instanceof CardButton)) return;
        
        CardButton clickedButton = (CardButton) source;
        Card clickedCard = clickedButton.getCard();

        if (model.flipCard(clickedCard)) {
            // Phát âm thanh flip
            SoundManager.getInstance().playSound("flip");
            view.updateView();
        }

        if (model.ReadyToCheck()) {
            isProcessing = true;

            final boolean matched = model.checkMatch();
            view.updateView();

            if (!matched) {
                // Phát âm thanh không match
                SoundManager.getInstance().playSound("nomatch");
                Timer timer = new Timer(1000, new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        model.resetUnmatched();
                        view.updateView();
                        isProcessing = false;
                    }
                });
                timer.setRepeats(false);
                timer.start();
            } else {
                // Phát âm thanh match
                SoundManager.getInstance().playSound("match");
                isProcessing = false;
            }

            if (model.isGameOver()) {
                // Phát âm thanh win
                SoundManager.getInstance().playSound("win");
                view.gameOver();
                showGameOverMessage();
            }
        }
    }
    
    private void showGameOverMessage() {
        long gameTime = model.getCurrentGameTime();
        boolean isNewBest = gameTime == model.getBestTime();
        
        // Tạo panel thông báo
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(236, 239, 244));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        // Thêm các thành phần vào panel
        JLabel congratsLabel = new JLabel("Chúc mừng chiến thắng!", JLabel.CENTER);
        congratsLabel.setFont(new Font("Arial", Font.BOLD, 18));
        congratsLabel.setForeground(new Color(46, 52, 64));
        congratsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel timeLabel = new JLabel("Thời gian hoàn thành: " + GameModel.formatTime(gameTime), JLabel.CENTER);
        timeLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        timeLabel.setForeground(new Color(59, 66, 82));
        timeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel bestTimeLabel = new JLabel("Thời gian tốt nhất: " + GameModel.formatTime(model.getBestTime()), JLabel.CENTER);
        bestTimeLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        bestTimeLabel.setForeground(new Color(59, 66, 82));
        bestTimeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        panel.add(Box.createVerticalStrut(10));
        panel.add(congratsLabel);
        panel.add(Box.createVerticalStrut(15));
        panel.add(timeLabel);
        panel.add(Box.createVerticalStrut(5));
        panel.add(bestTimeLabel);
        
        if (isNewBest) {
            JLabel newRecordLabel = new JLabel("🏆 KỶ LỤC MỚI! 🏆", JLabel.CENTER);
            newRecordLabel.setFont(new Font("Arial", Font.BOLD, 16));
            newRecordLabel.setForeground(new Color(208, 135, 112));
            newRecordLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            panel.add(Box.createVerticalStrut(15));
            panel.add(newRecordLabel);
        }
        
        panel.add(Box.createVerticalStrut(10));
        
        JOptionPane.showMessageDialog(
            view, 
            panel, 
            "Kết thúc trò chơi",
            JOptionPane.PLAIN_MESSAGE
        );
    }
}