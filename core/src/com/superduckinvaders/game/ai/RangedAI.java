package com.superduckinvaders.game.ai;

import com.superduckinvaders.game.Round;
import com.superduckinvaders.game.entity.mob.Mob;

/**
 * Ai that will shoot at the player and move towards them.
 * Ideally will only shoot if the shot will not hit a collision tile
 */
public class RangedAI extends PathfindingAI {
    /**
     * Creates a new RangedAI
     * @param round The round that this AI resides in
     */
    public RangedAI(Round round, float attackRange){
        super(round, attackRange);
    }

    /**
     * Updates The AI, causing movement and attacks
     * @param mob  pointer to the Mob using this AI
     * @param delta time since the previous update
     */
    public void update(Mob mob,float delta) {
        super.update(mob, delta);


    }

}
