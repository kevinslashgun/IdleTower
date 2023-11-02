package entity;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;

public class Entity {
    // Properties
    protected float x;
    protected float y;
    protected float width;
    protected float height;
    protected int speed;
    protected float damage;
    protected float attackSpeed;
    protected float health;
    protected BufferedImage sprite;
    protected Rectangle hitbox;
    protected int attackCooldown;
    protected int attackCounter;
    protected boolean isMoving;

    // Methods
    public float getX() { return x; }
    public void setX(float x) { this.x = x; }

    public float getY() { return y; }
    public void setY(float y) { this.y = y; }

    public float getWidth() { return width; }
    public void setWidth(float width) { this.width = width; }

    public float getHeight() { return height; }
    public void setHeight(float height) { this.height = height; }

    public int getSpeed() { return speed; }
    public void setSpeed(int speed) { this.speed = speed; }

    public float getDamage() { return damage; }
    public void setDamage(float damage) { this.damage = damage; }

    public float getAttackSpeed() { return attackSpeed; }
    public void setAttackSpeed(float attackSpeed) { this.attackSpeed = attackSpeed; }

    public float getHealth() { return health; }
    public void setHealth(float health) { this.health = health; }

    public Rectangle getHitbox() { return hitbox; }
    public void setHitbox(Rectangle hitbox) { this.hitbox = hitbox; }

    public int getAttackCooldown() { return attackCooldown; }
    public void setAttackCooldown(int attackCooldown) { this.attackCooldown = attackCooldown; }

    public int getAttackCounter() { return attackCounter; }
    public void setAttackCounter(int attackCounter) { this.attackCounter = attackCounter; }

    public boolean isMoving() { return isMoving; }
    public void setMoving(boolean isMoving) { this.isMoving = isMoving; }



    public boolean isAlive() { return health > 0; }

}