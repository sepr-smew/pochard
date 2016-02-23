package com.superduckinvaders.game.ai;

import com.badlogic.gdx.math.Vector2;
import com.superduckinvaders.game.Round;
import com.superduckinvaders.game.entity.Mob;

/**
 * Defines movement and attacking behaviour for Mobs.
 */
public abstract class AI {
    /**
     * The round the Mob this AI controls is a part of.
     */
    protected Round round;

    /**
     * Initialises this AI.
     *
     * @param round the round the Mob this AI controls is a part of
     */
    public AI(Round round) {
        this.round = round;
    }


    /**
     * Updates this AI.
     * @param mob pointer to the Mob using this AI
     * @param delta time since the previous update
     */
    public abstract void update(Mob mob, float delta);
}
