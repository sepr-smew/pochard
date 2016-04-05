package com.superduckinvaders.game.entity.item;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.superduckinvaders.game.assets.Assets;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages the active powerups with methods for updating and rendering the powerup bars
 * Maintains an instance for each powerup type
 * The effects of the powerups are distributed throughout the code in the relevant areas (movement speed powerup is applied in the player's movement logic)
 * This class acts as a central point to manage which powerups are currently active and activate new ones
 */
public class PowerupManager {

    /**
     * List holding up to one of each different type of powerup
     */
    private List<Powerup> powerups = new ArrayList<Powerup>();

    /**
     * Instanciate the PowerupManager and fill it's list with an instance for each type of powerup
     */
    public PowerupManager() {
        for(powerupTypes thisType : powerupTypes.values()){
            powerups.add(new Powerup(thisType));
        }
    }

    /**
     * Refreshes the duration of the given powerup with the given duration
     * Activates the powerup if it was not already active
     * @param type The type of powerup to add/refresh
     * @param duration The duration to give the powerup
     */
    public void addPowerup(powerupTypes type, float duration){
        for(Powerup thisPowerup : powerups){
            if(thisPowerup.type==type){
                thisPowerup.refresh(duration);
            }
        }
    }

    /**
     * Update each powerup in the list including their durations
     * @param delta The time passed between frames
     */
    public void update(float delta){
        for(Powerup thisPowerup : powerups){
            if(thisPowerup.isActive)
                thisPowerup.update(delta);
        }
    }

    /**
     * Renders a bar and icon for each active powerup at the bottom-right of the screen
     * The bars are rendered from the bottom to the top of the screen
     * @param uiBatch The Spritebatch to draw to
     */
    public void render(SpriteBatch uiBatch){

        float poweupBarPointer=0;
        float powerupBarX = Gdx.graphics.getWidth()-Assets.small_powerupEmpty.getRegionWidth()-15;

        //Iterate through powerups and render if it is active
        for(Powerup thisPowerup : powerups){
            if(thisPowerup.isActive){

                //Render the timer bar
                uiBatch.draw(Assets.small_powerupEmpty, powerupBarX, 50-poweupBarPointer);
                Assets.small_powerupFull.setRegionWidth((int) Math.max(0, thisPowerup.currentDuration / thisPowerup.duration * 96));
                uiBatch.draw(Assets.small_powerupFull, powerupBarX, 50-poweupBarPointer);

                //Render the icon of the powerup next to the bar
                TextureRegion powerupIcon = PowerupItem.getTextureForPowerup(thisPowerup.type);
                uiBatch.draw(powerupIcon, powerupBarX-powerupIcon.getRegionWidth()*1.5f, 50-poweupBarPointer-3, 0, 0, powerupIcon.getRegionWidth(), powerupIcon.getRegionHeight(), 1.5f,1.5f, 0);
                poweupBarPointer-=14;
            }
        }

    }

    /**
     * Checks if the powerup of the given type is active (if it's duration &gt; 0)
     * @param type The type of powerup to check
     * @return Returns true if is active, false otherwise
     */
    public boolean getIsActive(powerupTypes type){
        for(Powerup thisPowerup : powerups){
            if(thisPowerup.type==type)
                return thisPowerup.isActive;
        }
        return false;
    }

    /**
     * Inner private class representing a single powerup of a given type
     * Used for managing multiple powerups being active at once
     */
    private class Powerup{

        private boolean isActive;
        private float duration, currentDuration;
        private powerupTypes type;

        /**
         * Instanciate a new Powerup with a given type
         * @param type the type of powerup to create
         */
        private Powerup(powerupTypes type){
            isActive=false;
            this.type = type;

        }

        /**
         * Refreshes the duration of the powerup with the new duration
         * This is not additive, the new duration will overwrite the old maximum duration
         * @param duration The duration to refresh the powerup with
         */
        private void refresh(float duration){
            this.duration=duration;
            this.currentDuration=duration;
            isActive=true;
        }

        /**
         * Updates the powerup, decreasing the timer using delta
         * @param delta The amount of time between frames
         */
        private void update(float delta){
            currentDuration-=delta;
            if(currentDuration<=0){
                currentDuration=0;
                isActive=false;
            }
        }


    }

    /**
     * Enum of the different availible powerup types
     */
    public enum powerupTypes {

        SCORE_MULTIPLIER,
        SUPER_SPEED,
        RATE_OF_FIRE,
        INVULNERABLE

    }


}
