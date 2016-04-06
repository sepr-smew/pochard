package com.superduckinvaders.game.assets;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Represents a standard set of textures for character facing and animation.
 */
public final class TextureSet {
    
    public enum Facing {
        FRONT       (0),
        FRONT_LEFT  (1),
        LEFT        (2),
        BACK_LEFT   (3),
        BACK        (4),
        BACK_RIGHT  (5),
        RIGHT       (6),
        FRONT_RIGHT (7);

        private int index;

        Facing(int index){
            this.index = index;
        }

        public int index(){
            return index;
        }
    }

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
        this(front, back, left, right,
                new Animation(0, front), new Animation(0, back), new Animation(0, left), new Animation(0, right));
    }

    /**
     * Initilaises this TextureSet with textures for facing and movement animations.
     *
     * @param front       the forward facing texture
     * @param back        the backward facing texture
     * @param left        the left facing texture
     * @param right       the right facing texture
     * @param walkingFront the walking forward animation
     * @param walkingBack  the walking backward animation
     * @param walkingLeft  the walking left animation
     * @param walkingRight the walking right animation
     */
    public TextureSet(TextureRegion front, TextureRegion back, TextureRegion left, TextureRegion right,
                      Animation walkingFront, Animation walkingBack, Animation walkingLeft, Animation walkingRight) {
        this(front, null, back, null, left, null, right, null,
                walkingFront, null, walkingLeft, null, walkingBack, null, walkingRight, null);
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
        this(front, frontLeft, left, backLeft, back, backRight, right, frontRight,
                walkingFront, null, walkingLeft, null, walkingBack, null, walkingRight, null);
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
        idleTextures = new TextureRegion[]{
                front,
                frontLeft,
                left,
                backLeft,
                back,
                backRight,
                right,
                frontRight
        };
        
        movementAnimations = new Animation[]{
                walkingFront,
                walkingFrontLeft,
                walkingLeft,
                walkingBackLeft,
                walkingBack,
                walkingBackRight,
                walkingRight,
                walkingFrontRight
        };
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
    public TextureRegion getTexture(Facing facing, float stateTime) {
        if (stateTime > 0) {
            return movementAnimations[facing.index()].getKeyFrame(stateTime, true);
        } else {
            return idleTextures[facing.index()];
        }
    }

    /**
     * Returns whether the animation for a given direction has finished
     * @param facing the direction to get the animation for
     * @param stateTime The statetime to check if finished for
     * @return true if animation has finished, false otherwise
     */
    public boolean isAnimationFinished(Facing facing, float stateTime){
        return movementAnimations[facing.index()].isAnimationFinished(stateTime);
    }

    /**
     * @param facing the direction to receive result for
     * @return the duration of the animation for the given direction
     */
    public float getAnimationDuration(Facing facing){
        return movementAnimations[facing.index()].getAnimationDuration();
    }

    /**
     * @param facing the direction to receive result for
     * @return the duration of each frame for the given direction
     */
    public float getFrameDuration(Facing facing){
        return movementAnimations[facing.index()].getFrameDuration();
    }

}
