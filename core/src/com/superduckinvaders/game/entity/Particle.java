package com.superduckinvaders.game.entity;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.superduckinvaders.game.Round;

/**
 * Represents a particle effect using an Animation.
 */
public class Particle extends Entity {

    /**
     * How long this Particle will remain on the screen.
     */
    private float initialDuration, duration;

    /**
     * The animation to use for this Particle.
     */
    private Animation animation;

    /**
     * Initialises this Particle.
     *
     * @param parent    the round this Particle belongs to
     * @param x         the x coordinate
     * @param y         the y coordinate
     * @param duration  how long the particle effect should last for, in seconds
     * @param animation the animation to use for the particle effect
     */
    public Particle(Round parent, float x, float y, float duration, Animation animation) {
        super(parent, x, y);

        this.initialDuration = this.duration = duration;
        this.animation = animation;
    }

    /**
     * Updates the state of this Particle.
     *
     * @param delta how much time has passed since the last update
     */
    @Override
    public void update(float delta) {
        // Do not call super() as particles don't move or collide.
        duration -= delta;

        if (duration <= 0) {
            removed = true;
        }
    }

    /**
     * @return the width of this Particle
     */
    @Override
    public int getWidth() {
        return animation.getKeyFrame(0).getRegionWidth();
    }

    /**
     * @return the height of this Particle
     */
    @Override
    public int getHeight() {
        return animation.getKeyFrame(0).getRegionHeight();
    }

    /**
     * Renders this Particle.
     *
     * @param spriteBatch the sprite batch on which to render
     */
    @Override
    public void render(SpriteBatch spriteBatch) {
        spriteBatch.draw(animation.getKeyFrame((initialDuration - duration)), x , y);
    }
}