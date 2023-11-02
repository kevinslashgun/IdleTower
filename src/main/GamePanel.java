package main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JPanel;

import entity.Enemy;
import entity.Player;
import tiles.TileManager;

public class GamePanel extends JPanel implements Runnable {

    // Screen settings
    protected final int originalTileSize = 16; // This is the typical tile size for a 16-bit game
    protected final int scale = 2; // This is the scale of the game, usefull to make the game bigger
    protected final int tileSize = originalTileSize * scale; // This is the real tile size
    protected final int maxScreenCols = 21;
    protected final int maxScreenRows = 21;
    protected final int screenWidth = maxScreenCols * tileSize;
    protected final int screenHeight = maxScreenRows * tileSize;
    protected final int targetFPS = 60;

    private boolean isGameOver = false;
    private boolean gameOverSoundPlayed = false;
    private boolean isRunning;
    
    // Font
    protected Font customFont;

    // Create music controller
    protected MusicController musicController = new MusicController();

    // Create tile manager
    protected TileManager tileManager = new TileManager(this);

    // Create player
    protected Player player = new Player(this);

    // Create enemy
    protected List<Enemy> enemies = new ArrayList<Enemy>();

    private Thread gameThread; // This is the thread that will run the game, helps to keep the game running at
                               // the same speed on different computers

    // Constructor
    public GamePanel() {
        setPreferredSize(new Dimension(screenWidth, screenHeight));
        setBackground(Color.BLACK);
        setDoubleBuffered(true); // This helps to avoid flickering
        loadFont();
        spawnEnemy();
        musicController.playMusic("music/gameloop/stranger-things.wav", -10.0f);
    }

    // Getters
    public int getScale() {
        return scale;
    }

    public int getOriginalTileSize() {
        return originalTileSize;
    }

    public int getTileSize() {
        return tileSize;
    }

    public int getMaxScreenCols() {
        return maxScreenCols;
    }

    public int getMaxScreenRows() {
        return maxScreenRows;
    }

    public int getScreenWidth() {
        return screenWidth;
    }

    public int getScreenHeight() {
        return screenHeight;
    }

    public Player getPlayer() {
        return player;
    }

    public List<Enemy> getEnemies() {
        return enemies;
    }

    public int getTargetFPS() {
        return targetFPS;
    }

    public TileManager getTileManager() {
        return tileManager;
    }

    public MusicController getMusicController() {
        return musicController;
    }

    // This method starts the game thread
    public void startGameThread() {
        if (gameThread == null) {
            isRunning = true;
            gameThread = new Thread(this);
            gameThread.start();
        }
    }

    // This method loads the font
    public void loadFont() {
        try {
            // Load the font
            customFont = Font.createFont(Font.TRUETYPE_FONT, new File("assets/fonts/TrueType/Blazma-Regular.ttf"));

            // Set the font size
            customFont = customFont.deriveFont(40f); // 40 point size

            // Register the font
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(customFont);

            // Now you can use the custom font
            // For example, in a Swing component:
            // JLabel label = new JLabel("Hello, world!");
            // label.setFont(customFont);
        } catch (IOException | FontFormatException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        // This is the game loop, it will run as long as the game is running
        // It will run at the same speed on different computers (targetFPS
        double targetTime = 1000000000 / targetFPS;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;
        long timer = 0;
        long frames = 0;
    
        long enemySpawnTimer = 0;
        long enemySpawnCooldown = 1000000000; // Two seconds in nanoseconds
    
        while (isRunning) {
            currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / targetTime;
            timer += currentTime - lastTime;
            enemySpawnTimer += currentTime - lastTime;
            lastTime = currentTime;
    
            if (delta >= 1) {
                update();
                repaint();
                delta--;
                frames++;
            }
    
            if (enemySpawnTimer >= enemySpawnCooldown) {
                spawnEnemy(); // Replace with your method to spawn an enemy
                enemySpawnTimer = 0;
            }
    
            if (timer >= 1000000000) {
                System.out.println("FPS: " + frames);
                frames = 0;
                timer = 0;
            }
        }
    }

    public void spawnEnemy() {
        Enemy enemy = new Enemy(this);
        enemies.add(enemy);
    }

    public void update() {
        player.update();
        Iterator<Enemy> enemyIterator = enemies.iterator();
        while (enemyIterator.hasNext()) {
            Enemy enemy = enemyIterator.next();
            enemy.update();
            // Assuming you have a method to check if the enemy should be removed
            if (!enemy.isAlive()) {
                enemyIterator.remove();
                System.out.println("Enemy removed");
            }
        }
    }

    public void drawGrid(Graphics2D g2d) {
        // Draw columns
        for (int i = 0; i < maxScreenCols; i++) {
            g2d.setColor(Color.WHITE);
            g2d.drawLine(i * tileSize, 0, i * tileSize, screenHeight);
        }

        // Draw rows
        for (int i = 0; i < maxScreenRows; i++) {
            g2d.setColor(Color.WHITE);
            g2d.drawLine(0, i * tileSize, screenWidth, i * tileSize);
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
    
        if (isGameOver) {
            String gameOver = "Game Over";
            g2d.setFont(customFont);
            int stringLen = (int) g2d.getFontMetrics().getStringBounds(gameOver, g2d).getWidth();
            int stringHeight = (int) g2d.getFontMetrics().getStringBounds(gameOver, g2d).getHeight();
            g2d.setColor(Color.RED);
            g2d.drawString(gameOver, screenWidth / 2 - stringLen / 2, screenHeight / 2 - stringHeight / 2);
        } else {
            tileManager.draw(g2d);
            player.draw(g2d);
            Iterator<Enemy> enemyIterator = enemies.iterator();
            while (enemyIterator.hasNext()) {
                Enemy enemy = enemyIterator.next();
                enemy.draw(g2d);
            }
            g2d.dispose();
        }
        // drawGrid(g2d);
    }

    public void gameOver() {
        isGameOver = true;
        isRunning = false;
        musicController.stopMusic();
            if (!gameOverSoundPlayed){
                musicController.playSoundEffect("music/soundEffects/dead.wav", -10.0f);
                gameOverSoundPlayed = true;
            }
    }
}