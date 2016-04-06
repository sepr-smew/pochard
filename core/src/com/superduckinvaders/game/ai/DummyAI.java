package com.superduckinvaders.game.ai;

import com.superduckinvaders.game.Round;
import com.superduckinvaders.game.entity.mob.Mob;

/**
 * AI that does nothing.
 */
public class DummyAI extends AI {

    /**
     * Initialises this DummyAI.
     *
     * @param round the round the Mob this AI controls is a part of
     */
    public DummyAI(Round round) {
        super(round);
    }

    /**
     * Updates this DummyAI.Commented.
     *
     * @param mob   pointer to the Mob using this AI
     * @param delta time since the previous update
     */
    @Override
    public void update(Mob mob, float delta) {
    }

}
