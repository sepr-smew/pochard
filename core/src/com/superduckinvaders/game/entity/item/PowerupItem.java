package com.superduckinvaders.game.entity.item;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.superduckinvaders.game.Round;
import com.superduckinvaders.game.assets.Assets;
import com.superduckinvaders.game.entity.Player;

/**
 * Represents a powerup item ingame, on the floor
 * Extends the Item class
 */
public class PowerupItem extends Item {

    /**
     * The type of powerup this powerup is.
     */
    private PowerupManager.powerupTypes type;

    /**
     * How long the powerup will last for.
     */
    private final float DURATION;

    /**
     *
     * @param parent
     * @param x The x position to spawn the powerup at
     * @param y The y position to spawn the powerup at
     * @param type The type of powerup to spawn
     * @param duration The amount of time the powerup should last for
     */
    public PowerupItem(Round parent, float x, float y, PowerupManager.powerupTypes type, float duration) {
        super(parent, x, y, getTextureForPowerup(type));

        this.type = type;
        this.DURATION = duration;
    }


    /**
     * Updates the position of the powerup and checks if the player is colliding with it
     * If the player does collide, adds the powerup in the PowerupManager and flags itself for removal from the entity list
     */
    @Override
    public void update(float delta) {
        super.update(delta);
        Player player = parent.getPlayer();

        if (this.intersects(player.getX(), player.getY(), player.getWidth(), player.getHeight())) {
            parent.powerUpManager.addPowerup(type, DURATION);
            removed = true;
            Assets.pickup.play(0.3f);
        }
    }

    /**
     * Gets a texture for this powerup's floor item.
     *
     * @param powerup the powerup
     * @return the texture for the floor item
     */
    public static TextureRegion getTextureForPowerup(PowerupManager.powerupTypes powerup) {
        switch (powerup) {
            case SCORE_MULTIPLIER:
                return Assets.floorItemScore;
            case SUPER_SPEED:
                return Assets.floorItemSpeed;
            case RATE_OF_FIRE:
                return Assets.floorItemFireRate;
            case INVULNERABLE:
                return Assets.floorItemInvulnerable;
            default:
                return null;
        }
    }
}
