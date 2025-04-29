package main;

import javax.swing.UIManager;

import view.GameView;

public class Main {
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			new GameView();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
