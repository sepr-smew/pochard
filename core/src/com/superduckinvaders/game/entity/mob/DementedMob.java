package com.superduckinvaders.game.entity.mob;

import com.superduckinvaders.game.Round;

/**
 * Created by james on 19/04/16.
 */
public class DementedMob extends MeleeMob {
    public DementedMob(Round parent, float x, float y, int health, int speed, int score) {
        super(parent, x, y, health, speed, score);
    }

    @Override
    public boolean isDemented() {
        return true;
    }

    @Override
    public float getDementedFactor() {
        return 1f;
    }
}
