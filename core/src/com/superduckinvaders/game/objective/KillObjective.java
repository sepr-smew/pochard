package com.superduckinvaders.game.objective;

import com.superduckinvaders.game.Round;
import com.superduckinvaders.game.entity.item.Item;

/**
 * Created by hjt517 on 02/02/2016.
 */
public class KillObjective extends Objective {

    private int killCounter;

    /**
     * Initialises this KillObjective.
     *
     * @param parent the round this KillObjective belongs to
     */
    public KillObjective(Round parent, objectiveType type, int amount) {
        super(parent, type);
        this.killCounter = amount;

    }

    /**
     * Gets a string describing this KillObjective to be printed on screen.
     *
     * @return a string describing this KillObjective
     */
    @Override
    public String getObjectiveString() {
        return "Defeat "+killCounter+" enemies";
    }

    /**
     * Decrement the number of kills remaining
     */
    public void decrementKills(){
        killCounter--;
    }

    /**
     * Updates the status towards this KillObjective.
     *
     * @param delta how much time has passed since the last update
     */
    @Override
    public void update(float delta) {
        if(killCounter<=0)
            status = OBJECTIVE_COMPLETED;
    }
}
