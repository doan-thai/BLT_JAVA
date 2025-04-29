package model;

public class Card {
	private String symbol;
	private String imagePath; 
	private boolean matched = false;
	private boolean revealed = false;

	public Card(String symbol, String imagePath) {
		this.symbol = symbol;
		this.imagePath = imagePath;
	}

	public String getSymbol() {
		return symbol;
	}

	public String getImagePath() {
		return imagePath;
	}

	public boolean isMatched() {
		return matched;
	}

	public void setMatched(boolean matched) {
		this.matched = matched;
	}

	public boolean isRevealed() {
		return revealed;
	}

	public void setRevealed(boolean revealed) {
		this.revealed = revealed;
	}
}