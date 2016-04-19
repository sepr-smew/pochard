
package com.superduckinvaders.game.entity.mob;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.superduckinvaders.game.Round;
import com.superduckinvaders.game.ai.AI;
import com.superduckinvaders.game.ai.DummyAI;
import com.superduckinvaders.game.assets.Assets;
import com.superduckinvaders.game.assets.TextureSet;
import com.superduckinvaders.game.entity.Character;
import com.superduckinvaders.game.entity.item.PowerupManager;
import com.superduckinvaders.game.objective.BossObjective;
import com.superduckinvaders.game.objective.KillObjective;
import com.superduckinvaders.game.objective.Objective;

public abstract class Mob extends Character {

    /**
     * The texture set to use for this Mob.
     */
    protected TextureSet walkingTextureSet, swimmingTextureSet;
    protected Texture shadow;
    
    /**
     * AI class for the mob
     */
    private AI ai;
    
    /**
     * checks whether mob should be updated
     */
    private boolean active = false;

    /**
     * The score this mob will give when killed
     */
    private int score;

    /**
     * speed of the mob in pixels per second
     */
    private int speed;


    /**
     * Create a Mob
     * @param parent The round this mob resides in
     * @param x The starting x position
     * @param y The starting y position
     * @param health The health(maximum) of the mob
     * @param speed The speed of the mob
     * @param score The score the mob will give when killed
     * @param walkingTextureSet The textureset of the mob when on land
     * @param swimmingTextureSet The textureset of the mob when on water
     * @param ai The ai for the mob
     */
    public Mob(Round parent, float x, float y, int health, int speed, int score, TextureSet walkingTextureSet, TextureSet swimmingTextureSet, Texture shadow, AI ai) {
        super(parent, x, y, health);

        this.walkingTextureSet = walkingTextureSet;
        this.swimmingTextureSet = swimmingTextureSet;
        this.shadow = shadow;
        this.speed = speed;
        this.score = score;
        this.ai = ai;

        this.enemyBits = PLAYER_BITS;

        createDynamicBody(MOB_BITS, (short)(ALL_BITS & (~MOB_BITS)), MOB_GROUP, false);
        this.body.setLinearDamping(20f);
    }

    
    /**
     * Sets the AI for this Mob.
     * @param ai the new AI to use
     */
    public void setAI(AI ai) {
        this.ai = ai;
    }

    public AI getAI() {
        return ai;
    }

    /**
     * Sets the speed of the mob
     * @param newSpeed the updated speed
     */
    public void setSpeed(int newSpeed){
        this.speed = newSpeed;
    }

    public float getSpeed(){
        return this.speed;
    }


    /**
     * @return The width of the Mob. Uses the smaller swimming sprites
     */
    @Override
    public float getWidth() {
            return walkingTextureSet.getTexture(TextureSet.Facing.FRONT, 0).getRegionWidth();
    }

    /**
     * @return The height of the Mob. Uses the smaller swimming sprites
     */
    @Override
    public float getHeight() {
            return walkingTextureSet.getTexture(TextureSet.Facing.FRONT, 0).getRegionHeight()*3/4;

    }

    /**
     * @return returns the walking texture set of the mob
     */
    public TextureSet getWalkingTextureSet() {
        return walkingTextureSet;
    }
    /**
     * @return returns the swimming texture set of the mob
     */
    public TextureSet getSwimmingTextureSet() {
        return swimmingTextureSet;
    }

    /**
     * @return The score this mob should give
     */
    public int getScore() {
        return score;
    }

    protected void onDeath(){
        float random = MathUtils.random();
        PowerupManager.powerupTypes powerup = null;

        if (random < 0.05) {
            powerup = PowerupManager.powerupTypes.SCORE_MULTIPLIER;
        } else if (random >= 0.05 && random < 0.1) {
            powerup = PowerupManager.powerupTypes.INVULNERABLE;
        } else if (random >= 0.1 && random < 0.15) {
            powerup = PowerupManager.powerupTypes.SUPER_SPEED;
        } else if (random >= 0.15 && random < 0.2) {
            powerup = PowerupManager.powerupTypes.RATE_OF_FIRE;
        }

        if (powerup != null) parent.createPowerup(getX(), getY(), powerup, 10);

        switch(parent.getObjective().getObjectiveType()){
            case KILL:
                KillObjective objective = (KillObjective) parent.getObjective();
                objective.decrementKills();
                break;
            case BOSS:
                parent.spawnRandomMobs(1, 0, 0, 1000, 1000);
                break;
        }
    }

    /**
     * Updates the Mob. Checks for death, updates animation and movement using it's ai
     * @param delta how much time has passed since the last update
     */
    @Override
    public void update(float delta) {
        ai.update(this, delta);

        // Chance of spawning a random powerup.
        if (isDead()) {
            onDeath();
        }

        // Update animation state time.
        if (!getVelocity().isZero(0.1f)) {
            stateTime += delta;
        } else {
            stateTime = 0;
        }

        super.update(delta);
    }

    public void applyVelocity(Vector2 destination){
        Vector2 velocity = destination.sub(getCentre())
                .setLength(getSpeed());
//        if (isStunned()){
//            velocity.scl(0.4f);
//        }
        setVelocityClamped(velocity);
    }

    /**
     * Renders the Mob with correct textures/animations
     * @param spriteBatch the sprite batch on which to render
     */
    @Override
    public void render(SpriteBatch spriteBatch) {
        Vector2 pos = getPosition().cpy();

        Vector2 shadowPos = getCentre().cpy().add(0, -getHeight()/2)
                .add(-shadow.getWidth()/2, -shadow.getHeight()/2);

        TextureRegion texture = (isOnWater() ? swimmingTextureSet : walkingTextureSet)
                .getTexture(facing, stateTime);

        spriteBatch.draw(shadow, shadowPos.x, shadowPos.y);
        spriteBatch.draw(texture, pos.x, pos.y);
    }
}
