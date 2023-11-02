package main;

import javax.swing.JFrame;

public class Main {
    // This is the main method, creates the window and adds the game panel
    public static void main(String[] args) {
        // Create a new JFrame object and set its properties
        JFrame window = new JFrame("Idle Tower");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);

        // Create a new GamePanel object and add it to the JFrame
        GamePanel gamePanel = new GamePanel();
        window.add(gamePanel);

        // Pack the JFrame and set its location and visibility
        window.pack();
        window.setLocationRelativeTo(null);
        window.setVisible(true);

        // Start the game thread by calling the startGameThread method on the GamePanel object
        gamePanel.startGameThread();
    }
}