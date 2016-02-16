package com.superduckinvaders.game.entity;

import com.superduckinvaders.game.Round;
import com.superduckinvaders.game.assets.TextureSet;

/**
 * Represents a character in the game.
 */
public abstract class Character extends Entity {

    /**
     * The direction the Character is facing.
     */
    protected int facing = TextureSet.FACING_FRONT;

    /**
     * Determines whether a character should collide with objects.
     */
    protected boolean shouldCheckCollision = true;

    /**
     * The state time for the animation. Set to 0 for not moving.
     */
    protected float stateTime = 0;

    /**
     * Current health and the maximum health of this Character.
     */
    protected int maximumHealth, currentHealth;

    /**
     * Initialises this Character.
     *
     * @param parent        the round this Character belongs to
     * @param x             the initial x coordinate
     * @param y             the initial y coordinate
     * @param maximumHealth the maximum (and initial) health of this Character
     */
    public Character(Round parent, float x, float y, int maximumHealth) {
        super(parent, x, y);

        this.maximumHealth = this.currentHealth = maximumHealth;
    }

    /**
     * Gets the direction the character is facing
     * @return the direction this Character is facing (one of the FACING_ constants in TextureSet)
     */
    public int getFacing() {
        return facing;
    }

    /**
     * Gets the current health of this Character.
     *
     * @return the current health of this Character
     */
    public int getCurrentHealth() {
        return currentHealth;
    }

    /**
     * Gets the maximum health of this Character.
     *
     * @return the maximum health of this Character
     */
    public int getMaximumHealth() {
        return maximumHealth;
    }

    /**
     * Enables character collision
     */
    public void enableCollision(){
        shouldCheckCollision=true;
    }

    /**
     * Disables character collision
     */
    public void disableCollision(){
        shouldCheckCollision=false;
    }


    /**
     * Heals this Character's current health by the specified number of points.
     *
     * @param health the number of health points to heal
     */
    public void heal(int health) {
        this.currentHealth += health;

        if (currentHealth > maximumHealth) {
            currentHealth = maximumHealth;
        }
    }

    /**
     * Damages this Character's health by the specified number of points.
     *
     * @param health the number of points to damage
     */
    public abstract void damage(int health);

    /**
     * Returns if the character is dead
     * @return whether this Character is dead (i.e. its health is 0)
     */
    public boolean isDead() {
        return currentHealth <= 0;
    }

    /**
     * Checks if the player is on a water tile or not
     *
     * @return true if player is on water tile, otherwise false
     */
    protected boolean isOnWater(){
        int tileX = ((int) x+getWidth()/2) / parent.getTileWidth();
        int tileY = (int) y / parent.getTileHeight();
        Object property = parent.getBaseLayer().getCell(tileX,tileY).getTile().getProperties().get("water");
        return property!=null ? true : false;
    }
    /**
     * Causes this Character to fire a projectile at the specified coordinates.
     *
     * @param targetx      the target x coordinate
     * @param targety      the target y coordinate
     * @param speed  how fast the projectile moves
     * @param damage how much damage the projectile deals
     */
    public void fireAt(float targetx, float targety, int speed, int damage) {
        parent.createProjectile(this.x + getWidth() / 2, this.y + getHeight() / 2, targetx, targety, speed, 0 ,0, damage, this);
    }

    /**
     * Causes this Character to fire a projectile at the specified coordinates.
     *
     * @param targetx      the target x coordinate
     * @param targety      the target y coordinate
     * @param speed  how fast the projectile moves
     * @param damage how much damage the projectile deals
     */
    public void fireAt(int startx, int starty, float targetx, float targety, int speed, int damage) {
        parent.createProjectile(this.x + startx, this.y + starty, targetx, targety, speed, 0, 0, damage, this);
    }

    /**
     * Causes this Character to use a melee attack.
     *
     * @param range  how far the attack reaches in pixels
     * @param damage how much damage the attack deals
     */
    protected void melee(float range, int damage) {
        // Don't let mobs melee other mobs (for now).
        if (this instanceof Mob) {
            Player player = parent.getPlayer();

            if (distanceTo(player.getX(), player.getY()) <= range && directionTo(player.getX(), player.getY()) == facing) {
                player.damage(damage);
            }
        } else {
            // Attack the closest Character within the range.
            Character closest = null;

            for (Entity entity : parent.getEntities()) {
                // Disregard entity if it's me or it isn't a Character.
                if (this == entity || !(entity instanceof Character)) {
                    continue;
                }

                float x = entity.getX(), y = entity.getY();
                if (distanceTo(x, y) <= range && directionTo(x, y) == facing && (closest == null || distanceTo(x, y) < distanceTo(closest.getX(), closest.getY()))) {
                    closest = (Character) entity;
                }
            }

            // Can't attack if nothing in range.
            if (closest != null) {
                closest.damage(damage);
            }
        }
    }

    /**
     * Updates the state of this Character. Will check collision if shouldCheckCollision is true
     *
     * @param delta how much time has passed since the last update
     */
    @Override
    public void update(float delta) {
        // Update Character facing.
        if (velocityX < 0) {
            facing = TextureSet.FACING_LEFT;
        } else if (velocityX > 0) {
            facing = TextureSet.FACING_RIGHT;
        }

        if (velocityY < 0) {
            facing = TextureSet.FACING_FRONT;
        } else if (velocityY > 0) {
            facing = TextureSet.FACING_BACK;
        }


        float deltaX = velocityX * delta;
        float deltaY = velocityY * delta;

        //Check Collision if should
        if(shouldCheckCollision) {

            if (collidesX(deltaX)) {
                deltaX = 0;
            }

            if (collidesY(deltaY)) {
                deltaY = 0;
            }
        }

        //Update position
        x += (int) deltaX;
        y += (int) deltaY;

        if (isDead()) {
            removed = true;
        }

        super.update(delta);
    }
}
