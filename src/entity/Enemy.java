package entity;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import main.GamePanel;

public class Enemy extends Entity {
    private GamePanel gamePanel;

    public Enemy(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
        setDeafultValues();
        getImage();
    }

    public void getImage() {
        try {
            sprite = ImageIO.read(getClass().getResourceAsStream("/enemy/enemy.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setDeafultValues() {
        setHitbox();
        setRandomEdgeSpawn();
        updateHitboxPosition();
        health = 4;
        speed = 2;
        damage = 1;
        attackSpeed = 1;
        attackCooldown = (int) (attackSpeed * gamePanel.getTargetFPS());
        attackCounter = attackCooldown;
        isMoving = true;
    }

    public void setHitbox() {
        width = 8 * gamePanel.getScale();
        height = 8 * gamePanel.getScale();
        hitbox = new Rectangle(0, 0, (int) width, (int) height);
    }

    public void setRandomEdgeSpawn() {
        // set x and y to spawn enemy in a random edge tile of the screen
        int randomEdge = (int) (Math.random() * 4);
        switch (randomEdge) {
            case 0: // top
                x = (int) (Math.random() * gamePanel.getScreenWidth());
                y = -height / gamePanel.getScale();
                break;
            case 1: // right
                x = gamePanel.getScreenWidth() - gamePanel.getTileSize() + width / gamePanel.getScale();
                y = (int) (Math.random() * gamePanel.getScreenHeight());
                break;
            case 2: // bottom
                x = (int) (Math.random() * gamePanel.getScreenWidth());
                y = gamePanel.getScreenHeight() - gamePanel.getTileSize() + height / gamePanel.getScale();
                break;
            case 3: // left
                x = -width / gamePanel.getScale();
                y = (int) (Math.random() * gamePanel.getScreenHeight());
                break;
        }
    }

    public void updateHitboxPosition() {
        hitbox.x = (int) (x + gamePanel.getTileSize() / 2 - width / 2);
        hitbox.y = (int) (y + gamePanel.getTileSize() / 2 - height / 2);
    }

    public void moveToPlayer() {
        if (!isMoving) {
            return;
        }
        // move enemy towards player
        float dx = gamePanel.getPlayer().getX() - x;
        float dy = gamePanel.getPlayer().getY() - y;
        float distance = (float) Math.sqrt(dx * dx + dy * dy);

        if (distance > 0) {
            dx /= distance;
            dy /= distance;
        }

        x += dx * speed;
        y += dy * speed;
    }

    public void attackPlayer() {
        // attack player
        if (attackCounter >= attackCooldown) {
            gamePanel.getPlayer().setHealth(gamePanel.getPlayer().getHealth() - damage);
            if (gamePanel.getPlayer().isAlive()) {
                gamePanel.getMusicController().playSoundEffect("music/soundEffects/enemyAttack.wav", 0.0f);
            }
            System.out.println("Player health: " + gamePanel.getPlayer().getHealth());
            attackCounter = 0; // reset the counter
        }
    }

    public void stopMoving() {
        // stop moving
        isMoving = false;
    }

    public Boolean checkCollision(Player player) {
        return hitbox.intersects(player.getHitbox());
    }

    public void update() {
        if (!isAlive()) {
            return;
        }
        moveToPlayer();
        updateHitboxPosition();
        if (checkCollision(gamePanel.getPlayer())) {
            stopMoving();
            attackPlayer();
        }
        attackCounter++;
    }

    public void draw(Graphics2D g2d) {
        BufferedImage image = sprite;
        g2d.drawImage(image, (int) x, (int) y, gamePanel.getTileSize(), gamePanel.getTileSize(), null);

        // Draw hitbox
        g2d.setColor(Color.RED);
        g2d.drawRect(hitbox.x, hitbox.y, hitbox.width, hitbox.height);
    }
}
