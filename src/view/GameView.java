package view;

import java.awt.*;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import controller.GameController;
import model.Card;
import model.GameModel;
import util.SoundManager;

public class GameView extends JFrame {
    private final GameModel model;
    private final JPanel gamePanel;
    private final JPanel controlPanel;
    private final List<CardButton> buttons = new ArrayList<>();
    
    // UI components
    private JLabel timeLabel;
    private JLabel bestTimeLabel;
    private JButton startButton;
    private JButton restartButton;
    private Timer uiTimer;

    // Màu sắc
    private static final Color BACKGROUND_COLOR = new Color(41, 50, 65);
    private static final Color PANEL_COLOR = new Color(69, 89, 122);
    private static final Color CONTROL_COLOR = new Color(94, 129, 172);
    private static final Color TEXT_COLOR = new Color(236, 239, 244);
    private static final Color BUTTON_COLOR = new Color(129, 161, 193);
    private static final Color BUTTON_TEXT_COLOR = new Color(46, 52, 64);
    private static final Color HIGHLIGHT_COLOR = new Color(235, 203, 139);

    public GameView() {
        this.model = new GameModel(4, 4);
        setTitle("Memory Game");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(500, 600);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BACKGROUND_COLOR);
        
        // Panel cho các nút điều khiển và hiển thị thời gian
        controlPanel = new JPanel();
        controlPanel.setLayout(new GridLayout(4, 1, 5, 5));
        controlPanel.setBackground(CONTROL_COLOR);
        controlPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Thiết lập font và màu sắc cho các thành phần
        Font labelFont = new Font("Arial", Font.BOLD, 16);
        
        // Tạo các thành phần UI
        timeLabel = new JLabel("Thời gian: 00:00.000", JLabel.CENTER);
        timeLabel.setFont(labelFont);
        timeLabel.setForeground(TEXT_COLOR);
        
        bestTimeLabel = new JLabel("Thời gian tốt nhất: --:--:---", JLabel.CENTER);
        bestTimeLabel.setFont(labelFont);
        bestTimeLabel.setForeground(TEXT_COLOR);
        
        startButton = createStyledButton("Bắt đầu chơi", labelFont);
        restartButton = createStyledButton("Chơi lại", labelFont);
        restartButton.setEnabled(false);
        
        // Thêm sự kiện cho các nút
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startGame();
            }
        });
        
        restartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                restartGame();
            }
        });
        
     // Thêm một nút bật/tắt âm thanh vào controlPanel 
        JButton soundButton = createStyledButton("Âm thanh: Bật", labelFont);
        soundButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SoundManager soundManager = SoundManager.getInstance();
                soundManager.toggleSound();
                if (soundManager.isSoundEnabled()) {
                    soundButton.setText("Âm thanh: Bật");
                } else {
                    soundButton.setText("Âm thanh: Tắt");
                }
            }
        });
        
        // Thêm vào panel điều khiển
        controlPanel.add(soundButton);
        controlPanel.add(timeLabel);
        controlPanel.add(bestTimeLabel);
        controlPanel.add(startButton);
        controlPanel.add(restartButton);
        
        // Timer để cập nhật thời gian hiển thị
        uiTimer = new Timer(100, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateTimeDisplay();
            }
        });

        // Panel cho bảng trò chơi
        gamePanel = new JPanel(new GridLayout(model.getRows(), model.getCols(), 5, 5));
        gamePanel.setBackground(PANEL_COLOR);
        gamePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Khởi tạo các nút thẻ bài
        for (int i = 0; i < model.getRows(); i++) {
            for (int j = 0; j < model.getCols(); j++) {
                int index = i * model.getCols() + j;
                Card card = model.getCards().get(index);
                CardButton cardButton = new CardButton(card);
                buttons.add(cardButton);
                gamePanel.add(cardButton);
                cardButton.setEnabled(false); // Vô hiệu hóa thẻ khi chưa bắt đầu
            }
        }
        
        // Main layout với padding
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        mainPanel.add(controlPanel, BorderLayout.NORTH);
        mainPanel.add(gamePanel, BorderLayout.CENTER);
        
        setContentPane(mainPanel);
        setVisible(true);

        // Gắn controller
        GameController controller = new GameController(this);
        attachController(controller);
     // Thêm vào constructor của GameView:
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                // Khi frame được resize, cập nhật lại view
                SwingUtilities.invokeLater(() -> updateView());
            }
        });
    }
    
    private JButton createStyledButton(String text, Font font) {
        JButton button = new JButton(text);
        button.setFont(font);
        button.setBackground(BUTTON_COLOR);
        button.setForeground(BUTTON_TEXT_COLOR);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(true);
        button.setOpaque(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Hiệu ứng hover
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (button.isEnabled()) {
                    button.setBackground(HIGHLIGHT_COLOR);
                }
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (button.isEnabled()) {
                    button.setBackground(BUTTON_COLOR);
                }
            }
        });
        
        return button;
    }

    public void updateView() {
        for (CardButton bt : buttons) {
            bt.updateAppearance();
            bt.setEnabled(model.isGameStarted() && !bt.getCard().isMatched() && !bt.getCard().isRevealed());
        }
    }
    
    private void updateTimeDisplay() {
        if (model.isGameStarted()) {
            timeLabel.setText("Thời gian: " + GameModel.formatTime(model.getCurrentGameTime()));
        }
        
        if (model.getBestTime() < Long.MAX_VALUE) {
            bestTimeLabel.setText("Thời gian tốt nhất: " + GameModel.formatTime(model.getBestTime()));
        }
    }
    
    public void startGame() {
        model.startGame();
        // Phát âm thanh khi bắt đầu game
        SoundManager.getInstance().playSound("start");
        startButton.setEnabled(false);
        startButton.setBackground(new Color(128, 128, 128));
        restartButton.setEnabled(true);
        restartButton.setBackground(BUTTON_COLOR);
        updateView();
        uiTimer.start();
    }
    
    public void restartGame() {
        model.resetGame();
        startButton.setEnabled(true);
        startButton.setBackground(BUTTON_COLOR);
        restartButton.setEnabled(false);
        restartButton.setBackground(new Color(128, 128, 128));
        uiTimer.stop();
        timeLabel.setText("Thời gian: 00:00.000");
        updateView();
    }
    
    public void gameOver() {
        uiTimer.stop();
        startButton.setEnabled(false);
        startButton.setBackground(new Color(128, 128, 128));
        restartButton.setEnabled(true);
        restartButton.setBackground(BUTTON_COLOR);
        updateTimeDisplay();
    }

    public GameModel getModel() {
        return model;
    }

    public void attachController(GameController controller) {
        for (CardButton bt : buttons) {
            bt.addActionListener(controller);
        }
    }
}