package entity;

import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.Rectangle;

import main.GamePanel;

public class Bullet extends Entity {
    private GamePanel gamePanel;
    private boolean isVisible = false;
    private boolean shouldBeRemoved = false;
    private float targetX;
    private float targetY;
    private Enemy enemy;

    public Bullet(GamePanel gamePanel, Enemy enemy) {
        this.gamePanel = gamePanel;
        this.enemy = enemy;
        this.targetX = enemy.getX() + enemy.getWidth() / 2 * gamePanel.getScale();
        this.targetY = enemy.getY() + enemy.getHeight() / 2 * gamePanel.getScale();
        this.width = 2;
        this.height = 2;
        this.x = gamePanel.getPlayer().getX() + gamePanel.getPlayer().getWidth() / 2;
        this.y = gamePanel.getPlayer().getY() + gamePanel.getPlayer().getHeight() / 2;
        this.speed = 5;
        this.hitbox = new Rectangle((int) x, (int) y, (int) width, (int) height);
    }

    public void moveToTarget(){
        float dx = targetX - x;
        float dy = targetY - y;
        float distance = (float) Math.sqrt(dx * dx + dy * dy);

        if (distance > 0) {
            dx /= distance;
            dy /= distance;
        }

        x += dx * speed;
        y += dy * speed;
    }

    public void updateHitboxPosition() {
        hitbox.x = (int) x;
        hitbox.y = (int) y;
    }

    public void attackEnemy() {
        enemy.setHealth(enemy.getHealth() - gamePanel.getPlayer().getDamage());
        System.out.println("Enemy health: " + enemy.getHealth());
    }

    public boolean shouldBeRemoved() {
        return shouldBeRemoved;
    }

    public void update() {
        // check if is colliding with player
        if (!isColliding(gamePanel.getPlayer())) {
            isVisible = true;
        }
        else {
            isVisible = false;
        }
        moveToTarget();
        updateHitboxPosition();
        if (isColliding(enemy)) {
            System.out.println("Colliding with enemy");
            attackEnemy();
            shouldBeRemoved = true;
        }
    }

    public boolean isOutOfBounds(int screenWidth, int screenHeight) {
        return x < 0 || x > screenWidth || y < 0 || y > screenHeight;
    }

    public boolean isColliding(Entity entity) {
        return this.hitbox.intersects(entity.getHitbox());
    }

    public void draw(Graphics2D g2d) {
        if (isVisible) {
            g2d.setColor(Color.WHITE);
            g2d.fillRect((int) x, (int) y, 2, 2);
        }
        // Draw hitbox
        g2d.setColor(Color.YELLOW);
        g2d.drawRect(hitbox.x, hitbox.y, hitbox.width, hitbox.height);
    }

}
