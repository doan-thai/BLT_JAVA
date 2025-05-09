package model;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameModel {
    private final int rows;
    private final int cols;
    private final List<Card> cards;
    private Card firstPick = null;
    private Card secondPick = null;
    
    private long startTime;
    private long endTime;
    private long bestTime = Long.MAX_VALUE;
    private boolean gameStarted = false;

    public GameModel(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.cards = generateCards();
    }
    
    private List<Card> generateCards() {
        int pairCount = (rows * cols) / 2;
        List<Card> cardList = new ArrayList<>();
        
        // Danh sách các tên hình ảnh
        String[] imageNames = {"apple.png", "banana.png", "starfruit.png", "grape.png", 
                             "lemon.png", "orange.png", "pineapple.png", "watermelon.png"};
        
        for (int i = 0; i < pairCount; i++) {
            String symbol = String.valueOf((char)('A' + i));
            String imagePath = "image/" + imageNames[i % imageNames.length];
            
            // In ra đường dẫn để debug
            System.out.println("Tạo thẻ với hình ảnh: " + imagePath);
            System.out.println("Đường dẫn tuyệt đối: " + new File(imagePath).getAbsolutePath());
            cardList.add(new Card(symbol, imagePath));
            cardList.add(new Card(symbol, imagePath));
        }
        Collections.shuffle(cardList);
        return cardList;
    }
    
    public void startGame() {
        resetGame();
        gameStarted = true;
        startTime = System.currentTimeMillis();
    }
    
    public void resetGame() {
        for (Card card : cards) {
            card.setMatched(false);
            card.setRevealed(false);
        }
        Collections.shuffle(cards);
        firstPick = null;
        secondPick = null;
        gameStarted = false;
    }

    public List<Card> getCards() { return cards; }
    public int getRows() { return rows; }
    public int getCols() { return cols; }
    public boolean isGameStarted() { return gameStarted; }

    public boolean flipCard(Card card) {
        if (!gameStarted || card.isMatched() || card.isRevealed()) return false;

        card.setRevealed(true);
        if (firstPick == null) {
            firstPick = card;
        } else {
            secondPick = card;
        }
        return true;
    }

    public boolean ReadyToCheck() {
        return firstPick != null && secondPick != null;
    }

    public boolean checkMatch() {
        if (firstPick != null && secondPick != null) {
            boolean match = firstPick.getSymbol().equals(secondPick.getSymbol());
            if (match) {
                firstPick.setMatched(true);
                secondPick.setMatched(true);
                firstPick = null;
                secondPick = null;
            }
            return match;
        }
        return false;
    }

    public void resetUnmatched() {
        if (firstPick != null && !firstPick.isMatched()) firstPick.setRevealed(false);
        if (secondPick != null && !secondPick.isMatched()) secondPick.setRevealed(false);
        firstPick = null;
        secondPick = null;
    }

    public boolean isGameOver() {
        for (Card c : cards) {
            if (!c.isMatched()) return false;
        }
        
        if (gameStarted) {
            endTime = System.currentTimeMillis();
            long currentGameTime = endTime - startTime;
            
            if (currentGameTime < bestTime) {
                bestTime = currentGameTime;
            }
            
            gameStarted = false;
        }
        return true;
    }
    
    public long getCurrentGameTime() {
        if (!gameStarted) {
            return 0;
        }
        return System.currentTimeMillis() - startTime;
    }
    
    public long getBestTime() { return bestTime; }
    
    public static String formatTime(long timeInMillis) {
        long minutes = (timeInMillis / 1000) / 60;
        long seconds = (timeInMillis / 1000) % 60;
        long millis = timeInMillis % 1000;
        return String.format("%02d:%02d.%03d", minutes, seconds, millis);
    }
}
