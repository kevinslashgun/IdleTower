package entity;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.imageio.ImageIO;

import main.GamePanel;

public class Player extends Entity {
    // Properties
    private GamePanel gamePanel;
    private List<Bullet> bullets = new ArrayList<Bullet>();
    private double attackRadius = 100;

    // Constructor
    public Player(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
        setHitbox();
        setDeafultValues();
        updateHitboxPosition();
        getImage();
    }

    // Methods
    public GamePanel getGamePanel() {
        return gamePanel;
    }

    public void getImage() {
        try {
            sprite = ImageIO.read(getClass().getResourceAsStream("/player/character.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Rectangle getHitbox() {
        return hitbox;
    }

    private void setDeafultValues() {
        setHitbox();
        setDefaultSpawn();
        updateHitboxPosition();
        health = 10;
        damage = 2;
        attackSpeed = 1;
        attackCooldown = (int) (attackSpeed * gamePanel.getTargetFPS());
        attackCounter = attackCooldown;
        isMoving = false;
    }

    private void setHitbox() {
        width = gamePanel.getTileSize();
        height = gamePanel.getTileSize();
        hitbox = new Rectangle(0, 0, (int) width, (int) height);
    }

    private void setDefaultSpawn() {
        // set x and y to spawn enemy in the center tile of the screen
        x = gamePanel.getScreenWidth() / 2 - gamePanel.getTileSize() / 2;
        y = gamePanel.getScreenHeight() / 2 - gamePanel.getTileSize() / 2;
    }

    public void updateHitboxPosition() {
        hitbox.x = (int) x;
        hitbox.y = (int) y;
    }

    public boolean isWithinAttackRadius(Enemy enemy) {
        double dx = enemy.getX() - x;
        double dy = enemy.getY() - y;
        double distance = Math.sqrt(dx * dx + dy * dy);
        return distance <= attackRadius;
    }

    public void shoot(Enemy enemy) {
        if (attackCounter >= attackCooldown && isWithinAttackRadius(enemy)) {
            int bulletsNeeded = (int) Math.ceil((double) enemy.getHealth() / damage);
            if (bullets.size() < bulletsNeeded) {
                Bullet bullet = new Bullet(gamePanel, enemy);
                bullets.add(bullet);
            }
            attackCounter = 0;
        }
    }

    public boolean enemiesAlive() {
        return gamePanel.getEnemies().size() > 0;
    }

    private Enemy getClosestEnemy() {
        List<Enemy> enemies = gamePanel.getEnemies();
        Enemy closestEnemy = null;
        double closestDistance = Double.MAX_VALUE;
        for (Enemy enemy : enemies) {
            double dx = enemy.getX() - this.getX();
            double dy = enemy.getY() - this.getY();
            double distance = Math.sqrt(dx * dx + dy * dy);
            if (distance < closestDistance) {
                closestEnemy = enemy;
                closestDistance = distance;
            }
        }
        return closestEnemy;
    }

    public void update() {
        if (!isAlive()) {
            gamePanel.gameOver();
            return;
        }
        if (enemiesAlive()) {
            Enemy closestEnemy = getClosestEnemy();
            if (closestEnemy != null) {
                shoot(closestEnemy);
            }
        }
        Iterator<Bullet> bulletIterator = bullets.iterator();
        while (bulletIterator.hasNext()) {
            Bullet bullet = bulletIterator.next();
            bullet.update();
            // Assuming you have a method to check if the bullet should be removed
            if (bullet.shouldBeRemoved()) {
                bulletIterator.remove();
                System.out.println("Bullet removed");
            }
        }
        attackCounter++;
    }

    public void draw(Graphics2D g2d) {
        BufferedImage image = sprite;
        g2d.drawImage(image, (int) getX(), (int) getY(), gamePanel.getTileSize(), gamePanel.getTileSize(), null);

        Iterator<Bullet> bulletIterator = bullets.iterator();
        while (bulletIterator.hasNext()) {
            Bullet bullet = bulletIterator.next();
            bullet.draw(g2d);
        }

        // Draw attack radius
        g2d.setColor(Color.WHITE);
        g2d.drawOval((int) (getX() - attackRadius + getWidth() / 2), (int) (getY() - attackRadius + getHeight() / 2),
                (int) (2 * attackRadius), (int) (2 * attackRadius));

        // Draw hitbox
        g2d.setColor(Color.YELLOW);
        g2d.drawRect(hitbox.x, hitbox.y, hitbox.width, hitbox.height);
    }
}