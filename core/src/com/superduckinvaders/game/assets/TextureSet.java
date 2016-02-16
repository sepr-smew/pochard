package com.superduckinvaders.game.assets;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Represents a standard set of textures for character facing and animation.
 */
public final class TextureSet {

    /**
     * Constants for which way the character is facing.
     */
    public static final int FACING_FRONT = 0, FACING_FRONT_LEFT = 1, FACING_LEFT = 2, FACING_BACK_LEFT = 3,
                            FACING_BACK = 4, FACING_BACK_RIGHT = 5, FACING_RIGHT = 6, FACING_FRONT_RIGHT = 7;

    /**
     * Textures to use when the character isn't moving.
     */
    private TextureRegion[] idleTextures = new TextureRegion[8];

    /**w
     * Animations to use when the character is moving.
     */
    private Animation[] movementAnimations = new Animation[8];

    /**
     * Initialises this TextureSet with a single texture used for everything.
     *
     * @param all the texture to use for everything
     */
    public TextureSet(TextureRegion all) {
        this(all, all, all, all);
    }

    /**
     * Initialises this TextureSet with textures for facing. No movement animations will be used.
     *
     * @param front the foward facing texture
     * @param back  the backward facing texture
     * @param left  the left facing texture
     * @param right the right facing texture
     */
    public TextureSet(TextureRegion front, TextureRegion back, TextureRegion left, TextureRegion right) {
        this(front, back, left, right, new Animation(0, front),
                new Animation(0, back), new Animation(0, left), new Animation(0, right));
    }

    /**
     * Initilaises this TextureSet with textures for facing and movement animations.
     *
     * @param front       the forward facing texture
     * @param back        the backward facing texture
     * @param left        the left facing texture
     * @param right       the right facing texture
     * @param movingFront the moving forward animation
     * @param movingBack  the moving backward animation
     * @param movingLeft  the moving left animation
     * @param movingRight the moving right animation
     */
    public TextureSet(TextureRegion front, TextureRegion back, TextureRegion left, TextureRegion right,
                      Animation movingFront, Animation movingBack, Animation movingLeft, Animation movingRight) {
        idleTextures[FACING_FRONT] = front;
        idleTextures[FACING_BACK] = back;
        idleTextures[FACING_LEFT] = left;
        idleTextures[FACING_RIGHT] = right;

        movementAnimations[FACING_FRONT] = movingFront;
        movementAnimations[FACING_BACK] = movingBack;
        movementAnimations[FACING_LEFT] = movingLeft;
        movementAnimations[FACING_RIGHT] = movingRight;
    }

    /**
     * Initilaises this TextureSet with textures for facing and movement animations.
     *
     * @param front     the forward facing texture
     * @param frontLeft the frontLeft facing texture
     * @param left      the left facing texture
     * @param backLeft  the backLeft facing texture
     * @param back      the back facing texture
     * @param backRight the backRight facing texture
     * @param right     the right facing texture
     * @param frontRight    the forward facing texture
     * @param walkingFront  the moving front facing texture
     * @param walkingBack   the moving back facing texture
     * @param walkingLeft   the moving left facing texture
     * @param walkingRight  the moving right facing animation
     */
    public TextureSet(TextureRegion front, TextureRegion frontLeft, TextureRegion left, TextureRegion backLeft,
                      TextureRegion back, TextureRegion backRight, TextureRegion right, TextureRegion frontRight,
                      Animation walkingFront, Animation walkingBack, Animation walkingLeft, Animation walkingRight) {
        idleTextures[0] = front;
        idleTextures[1] = frontLeft;
        idleTextures[2] = left;
        idleTextures[3] = backLeft;
        idleTextures[4] = back;
        idleTextures[5] = backRight;
        idleTextures[6] = right;
        idleTextures[7] = frontRight;

        movementAnimations[FACING_FRONT] = walkingFront;
        movementAnimations[FACING_BACK] = walkingBack;
        movementAnimations[FACING_LEFT] = walkingLeft;
        movementAnimations[FACING_RIGHT] = walkingRight;
    }

    /**
     * Initilaises this TextureSet with textures for facing and movement animations.
     *
     * @param front     the forward facing texture
     * @param frontLeft the frontLeft facing texture
     * @param left      the left facing texture
     * @param backLeft  the backLeft facing texture
     * @param back      the back facing texture
     * @param backRight the backRight facing texture
     * @param right     the right facing texture
     * @param frontRight    the forward facing texture
     * @param walkingFront      the moving forward animation
     * @param walkingFrontLeft  the moving front left animation
     * @param walkingLeft       the moving left animation
     * @param walkingBackLeft   the moving back left animation
     * @param walkingBack       the moving back animation
     * @param walkingBackRight  the moving back right animation
     * @param walkingRight      the moving right animation
     * @param walkingFrontRight the moving front right animation
     */
    public TextureSet(TextureRegion front, TextureRegion frontLeft, TextureRegion left, TextureRegion backLeft, TextureRegion back, TextureRegion backRight, TextureRegion right, TextureRegion frontRight,
                      Animation walkingFront, Animation walkingFrontLeft, Animation walkingLeft, Animation walkingBackLeft, Animation walkingBack, Animation walkingBackRight, Animation walkingRight, Animation walkingFrontRight) {
        idleTextures[0] = front;
        idleTextures[1] = frontLeft;
        idleTextures[2] = left;
        idleTextures[3] = backLeft;
        idleTextures[4] = back;
        idleTextures[5] = backRight;
        idleTextures[6] = right;
        idleTextures[7] = frontRight;

        movementAnimations[FACING_FRONT] = walkingFront;
        movementAnimations[FACING_FRONT_LEFT] = walkingFrontLeft;
        movementAnimations[FACING_LEFT] = walkingLeft;
        movementAnimations[FACING_BACK_LEFT] = walkingBackLeft;
        movementAnimations[FACING_BACK] = walkingBack;
        movementAnimations[FACING_BACK_RIGHT] = walkingBackRight;
        movementAnimations[FACING_RIGHT] = walkingRight;
        movementAnimations[FACING_FRONT_RIGHT] = walkingFrontRight;

    }

    /**
     * Gets the representative width of this TextureSet (the front idle texture).
     *
     * @return the width
     */
    public int getWidth() {
        return idleTextures[0].getRegionWidth();
    }

    /**
     * Gets the representative height of this TextureSet (the front idle texture).
     *
     * @return the height
     */
    public int getHeight() {
        return idleTextures[0].getRegionHeight();
    }

    /**
     * Gets the texture for the specified facing at the specified state time. A state time of 0 means not moving; idle.
     *
     * @param facing    the facing
     * @param stateTime the state time
     * @return the appropriate texture
     */
    public TextureRegion getTexture(int facing, float stateTime) {
        if (stateTime > 0) {
            return movementAnimations[facing].getKeyFrame(stateTime, true);
        } else {
            return idleTextures[facing];
        }
    }

    /**
     * Returns whether the animation for a given direction has finished
     * @param facing the direction to get the animation for
     * @param stateTime The statetime to check if finished for
     * @return true if animation has finished, false otherwise
     */
    public boolean isAnimationFinished(int facing, float stateTime){
        return movementAnimations[facing].isAnimationFinished(stateTime);
    }

    /**
     * @param facing the direction to receive result for
     * @return the duration of the animation for the given direction
     */
    public float getAnimationDuration(int facing){
        return movementAnimations[facing].getAnimationDuration();
    }

    /**
     * @param facing the direction to receive result for
     * @return the duration of each frame for the given direction
     */
    public float getFrameDuration(int facing){
        return movementAnimations[facing].getFrameDuration();
    }

}
