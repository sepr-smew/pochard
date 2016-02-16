package com.superduckinvaders.game.entity;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.particles.ParallelArray;
import com.superduckinvaders.game.Round;
import com.superduckinvaders.game.assets.Assets;

/**
 * Represents a projectile.
 */
public class Projectile extends Entity {

    /**
     * The owner of this Projectile (i.e. the Entity that fired it).
     */
    private Entity owner;

    /**
     * How much damage this Projectile does to what it hits.
     */
    private int damage;

    /**
     * The angle this Projectile is travelling from it's start point
     */
    private float angle;

    /**
     * Indicates whether the class has just been created, used to ensure the projectile's position doesn't get updated before it is first drawn
     */
    private boolean isFirstUpdate = true;

    /**
     * Initialises this Projectile.
     *
     * @param parent  the round this Projectile belongs to
     * @param x       the initial x coordinate
     * @param y       the initial y coordinate
     * @param targetX the target x coordinate
     * @param targetY the target y coordinate
     * @param speed   how fast the projectile moves
     * @param damage  how much damage the projectile deals
     * @param owner   the owner of the projectile (i.e. the one who fired it)
     */
    public Projectile(Round parent, float x, float y, float targetX, float targetY, float speed, int damage, Entity owner) {
        this(parent, x, y, targetX, targetY, speed, 0, 0, damage, owner);
    }

    /**
     * Initialises this Projectile.
     *
     * @param parent          the round this Projectile belongs to
     * @param x               the initial x coordinate
     * @param y               the initial y coordinate
     * @param targetX         the target x coordinate
     * @param targetY         the target y coordinate
     * @param speed           how fast the projectile moves
     * @param velocityXOffset the offset to the initial X velocity
     * @param velocityYOffset the offset to the initial Y velocity
     * @param damage          how much damage the projectile deals
     * @param owner           the owner of the projectile (i.e. the one who fired it)
     */
    public Projectile(Round parent, float x, float y, float targetX, float targetY, float speed, float velocityXOffset, float velocityYOffset, int damage, Entity owner) {
        super(parent, x, y);

        // Angle between initial position and target.
        this.angle = angleTo(targetX, targetY);
        velocityX = (float) Math.cos(angle) * speed;
        velocityY = (float) Math.sin(angle) * speed;

        this.angle = (float)Math.toDegrees((double)angleTo(targetX, targetY));//Turn angle to degrees for correct render rotation

        // Projectile should only move faster if we're moving in the same direction.
        velocityX += (Math.signum(velocityX) == Math.signum(velocityXOffset) ? velocityXOffset : 0);
        velocityY += (Math.signum(velocityY) == Math.signum(velocityYOffset) ? velocityYOffset : 0);

        this.damage = damage;
        this.owner = owner;
    }

    /**
     * @return the width of this Projectile
     */
    @Override
    public int getWidth() {
        return Assets.projectile.getRegionHeight();
    }

    /**
     * @return the height of this Projectile
     */
    @Override
    public int getHeight() {
        return Assets.projectile.getRegionHeight();
    }

    /**
     * Updates the state of this Projectile.
     *
     * @param delta how much time has passed since the last update
     */
    @Override
    public void update(float delta) {

        // Do manual collision checking in order to remove projectile.

        float deltaX = velocityX * delta;
        float deltaY = velocityY * delta;

        //Ensure that a mobs can't shoot each other & player can't somehow shoot themselves
        for (Entity entity : parent.getEntities()) {
            if (entity instanceof Character && entity != owner) {
                if(owner.getClass()!=entity.getClass()) {
                    if (x > entity.x && x < entity.x + entity.getWidth()) {
                        if (y > entity.y && y < entity.y + entity.getHeight()) {
                            ((Character) entity).damage(damage);
                            removed = true;
                        }
                    }
                }
            }
        }

        //Checks for collisions against the edges of the map and map tiles.
        boolean collided = false;
        if(isFirstUpdate){
            delta=0;
            isFirstUpdate=false;
        } else {

            if (x < 0) {
                collided = true;
            } else if (x > parent.getMapWidth() - getWidth()) {
                collided = true;
            }

            if (y < 0) {
                collided = true;
            } else if (y > parent.getMapHeight() - getHeight()) {
                collided = true;
            }

            if (collidesX(deltaX)) {
                collided = true;
            }
            if (collidesY(deltaY)) {
                collided = true;
            }
        }

        //If collision has occurred, destroy itself and create particle.
        if (collided) {
            // Create explosion particle effect.
            parent.createParticle(x, y , 0.6f, Assets.explosionAnimation);

            removed = true;
        } else {
            x += deltaX;
            y += deltaY;
        }
    }

    /**
     * Renders this Projectile. Rotates to correct angle
     *
     * @param spriteBatch the sprite batch on which to render
     */
    @Override
    public void render(SpriteBatch spriteBatch) {
        spriteBatch.draw(Assets.projectile, x, y, Assets.projectile.getRegionWidth()/2, Assets.projectile.getRegionHeight()/2, Assets.projectile.getRegionWidth(), Assets.projectile.getRegionHeight(), 1,1 , angle);
    }

    /**
     * Set the x coordinate of the projectile
     * @param x The value to set the x coordinate to
     */
    public void setX(float x){this.x=x;}
    /**
     * Set the y coordinate of the projectile
     * @param y The value to set the y coordinate to
     */
    public void setY(float y){this.y=y;}
}
