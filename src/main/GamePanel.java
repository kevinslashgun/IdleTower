package main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JPanel;

import entity.Enemy;
import entity.Player;
import tiles.TileManager;

public class GamePanel extends JPanel implements Runnable {

    // Screen settings
    protected final int originalTileSize = 16;
    protected final int scale = 2;
    protected final int tileSize = originalTileSize * scale;
    protected final int maxScreenCols = 21;
    protected final int maxScreenRows = 21;
    protected final int screenWidth = maxScreenCols * tileSize;
    protected final int screenHeight = maxScreenRows * tileSize;

    // FPS
    private static final int FPS_DISPLAY_X = 10;
    private static final int FPS_DISPLAY_Y = 20;
    private static final long NANOSECONDS_IN_SECOND = 1_000_000_000L;
    protected final int targetFPS = 60;
    protected long currentFPS = 60;

    // Music and sound effects
    private static final String GAME_LOOP_MUSIC = "music/gameloop/stranger-things.wav";
    private static final String GAME_OVER_SOUND_EFFECT = "music/soundEffects/dead.wav";
    private static final float MUSIC_VOLUME = -10.0f;
    private static final float SOUND_EFFECT_VOLUME = -10.0f;

    // Game state
    private boolean isGameOver = false;
    private boolean gameOverSoundPlayed = false;
    private boolean isRunning;

    // Font
    private static final float FONT_SIZE = 40f;
    private static final String FONT_PATH = "/fonts/TrueType/Blazma-Regular.ttf";
    protected Font customFont;

    // Controllers
    protected MusicController musicController = new MusicController();
    protected TileManager tileManager = new TileManager(this);

    // Entities
    protected Player player = new Player(this);
    protected List<Enemy> enemies = new ArrayList<Enemy>();

    // Game thread
    private Thread gameThread;

    /**
     * Constructor for the GamePanel class.
     * It sets the preferred size of the panel, the background color, and enables
     * double buffering.
     * It also loads a custom font, spawns an enemy, and starts playing the game
     * loop music.
     */
    public GamePanel() {
        setPreferredSize(new Dimension(screenWidth, screenHeight));
        setBackground(Color.BLACK);
        setDoubleBuffered(true);
        customFont = loadFont(FONT_PATH).deriveFont(FONT_SIZE);
        spawnEnemy();
        musicController.playMusic(GAME_LOOP_MUSIC, MUSIC_VOLUME);
    }

    /**
     * Getter methods for the GamePanel class.
     * These methods are used to access the private variables of the class.
     */

    // Returns the scale of the game panel
    public int getScale() {
        return scale;
    }

    // Returns the original size of the tiles
    public int getOriginalTileSize() {
        return originalTileSize;
    }

    // Returns the current size of the tiles
    public int getTileSize() {
        return tileSize;
    }

    // Returns the maximum number of columns on the screen
    public int getMaxScreenCols() {
        return maxScreenCols;
    }

    // Returns the maximum number of rows on the screen
    public int getMaxScreenRows() {
        return maxScreenRows;
    }

    // Returns the width of the screen
    public int getScreenWidth() {
        return screenWidth;
    }

    // Returns the height of the screen
    public int getScreenHeight() {
        return screenHeight;
    }

    // Returns the player object
    public Player getPlayer() {
        return player;
    }

    // Returns the list of enemies
    public List<Enemy> getEnemies() {
        return enemies;
    }

    // Returns the target frames per second
    public int getTargetFPS() {
        return targetFPS;
    }

    // Returns the tile manager object
    public TileManager getTileManager() {
        return tileManager;
    }

    // Returns the music controller object
    public MusicController getMusicController() {
        return musicController;
    }

    /**
     * This method is responsible for starting the game thread.
     * It first checks if the game thread is null, indicating that the game is not
     * currently running.
     * If the game thread is null, it sets the isRunning flag to true, creates a new
     * Thread object,
     * and starts the new thread.
     */
    public void startGameThread() {
        if (gameThread == null) {
            isRunning = true;
            gameThread = new Thread(this);
            gameThread.start();
        }
    }

    /**
     * This method is responsible for stopping the game thread.
     * It sets the isRunning flag to false and then waits for the game thread to
     * terminate.
     * If the thread is interrupted while waiting, it prints the stack trace of the
     * InterruptedException.
     */
    public void stopGameThread() {
        if (gameThread != null) {
            isRunning = false;
            try {
                gameThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * This method is responsible for loading a font from a given path.
     * If the font cannot be loaded due to a FontFormatException or IOException,
     * it returns a default Arial font.
     *
     * @param path The path to the font file.
     * @return The loaded Font object, or a default Arial font if the font cannot be
     *         loaded.
     */
    private Font loadFont(String path) {
        try (InputStream is = GamePanel.class.getResourceAsStream(path)) {
            return Font.createFont(Font.TRUETYPE_FONT, is);
        } catch (FontFormatException | IOException e) {
            System.err.println("Error loading font: " + e.getMessage());
            return new Font("Arial", Font.PLAIN, 14);
        }
    }

    /**
     * This method is the main game loop. It runs as long as the game is running.
     * It updates the game state, repaints the game panel, and spawns new enemies at
     * regular intervals.
     * It also calculates the current frames per second (FPS) for performance
     * monitoring.
     */
    @Override
    public void run() {
        double targetTime = NANOSECONDS_IN_SECOND / targetFPS;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;
        long timer = 0;
        long frames = 0;
        long enemySpawnTimer = 0;
        long enemySpawnCooldown = NANOSECONDS_IN_SECOND;

        // Main game loop
        while (isRunning) {
            currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / targetTime;
            timer += currentTime - lastTime;
            enemySpawnTimer += currentTime - lastTime;
            lastTime = currentTime;

            // Update game state and repaint game panel for each frame
            if (delta >= 1) {
                update();
                repaint();
                delta--;
                frames++;
            }
            if (enemySpawnTimer >= enemySpawnCooldown) {
                spawnEnemy();
                enemySpawnTimer = 0;
            }
            if (timer >= NANOSECONDS_IN_SECOND) {
                currentFPS = frames;
                frames = 0;
                timer = 0;
            }
        }
        stopGameThread();
    }

    /**
     * This method is responsible for spawning a new enemy in the game.
     * It creates a new Enemy object and adds it to the list of enemies.
     */
    public void spawnEnemy() {
        Enemy enemy = new Enemy(this);
        enemies.add(enemy);
    }

    /**
     * This method is responsible for updating the state of the game.
     * It updates the player's state, and then updates the state of each enemy.
     * If an enemy is no longer alive, it is removed from the game.
     */
    public void update() {
        player.update();
        Iterator<Enemy> enemyIterator = enemies.iterator();
        while (enemyIterator.hasNext()) {
            Enemy enemy = enemyIterator.next();
            enemy.update();
            if (!enemy.isAlive()) {
                enemyIterator.remove();
                System.out.println("Enemy removed");
            }
        }
    }

    /**
     * This method is responsible for drawing a grid on the game panel.
     * It draws vertical lines for the columns and horizontal lines for the rows.
     * This can be useful for debugging purposes, to see the exact layout of the
     * game tiles.
     *
     * @param g2d The Graphics2D object to draw on.
     */
    public void drawGrid(Graphics2D g2d) {
        for (int i = 0; i < maxScreenCols; i++) {
            g2d.setColor(Color.WHITE);
            g2d.drawLine(i * tileSize, 0, i * tileSize, screenHeight);
        }
        for (int i = 0; i < maxScreenRows; i++) {
            g2d.setColor(Color.WHITE);
            g2d.drawLine(0, i * tileSize, screenWidth, i * tileSize);
        }
    }

    /**
     * This method is responsible for drawing all the game elements on the game
     * panel.
     * It first draws the game tiles, then the player, and finally the enemies.
     * If the game is over, it draws the game over message instead.
     *
     * @param g The Graphics object to protect.
     */
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
            g2d.setColor(Color.WHITE);
            g2d.drawString("FPS: " + currentFPS, FPS_DISPLAY_X, FPS_DISPLAY_Y);
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

    /**
     * This method is called when the game is over. It stops the game loop,
     * stops the background music, and plays the game over sound effect.
     */
    public void gameOver() {
        isGameOver = true;
        isRunning = false;
        musicController.stopMusic();
        if (!gameOverSoundPlayed) {
            musicController.playSoundEffect(GAME_OVER_SOUND_EFFECT, SOUND_EFFECT_VOLUME);
            gameOverSoundPlayed = true;
        }
    }

}