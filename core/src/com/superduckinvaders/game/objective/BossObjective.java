package com.superduckinvaders.game.objective;

import com.superduckinvaders.game.Round;

/**
 * Created by hjt517 on 16/02/2016.
 */
public class BossObjective extends Objective{


    /**
     * Initialises this BossObjective.
     *
     * @param parent the round this KillObjective belongs to
     */
    public BossObjective(Round parent, objectiveType type) {
        super(parent, type);
    }

    /**
     * Gets a string describing this BossObjective to be printed on screen.
     *
     * @return a string describing this BossObjective
     */
    @Override
    public String getObjectiveString() {
        return "Defeat the Deathtank";
    }

    /**
     * Sets the BossObjective to completed
     */
    public void setCompleted(){
        status = OBJECTIVE_COMPLETED;
    }

    /**
     * Updates the status towards this BossObjective.
     *
     * @param delta how much time has passed since the last update
     */
    @Override
    public void update(float delta) {

    }
}




