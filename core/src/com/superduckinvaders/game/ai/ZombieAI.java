package com.superduckinvaders.game.ai;

import com.badlogic.gdx.math.Vector2;
import com.superduckinvaders.game.Round;
import com.superduckinvaders.game.entity.mob.Mob;
import com.superduckinvaders.game.entity.Player;

/**
 * Created by james on 10/03/16.
 */
public class ZombieAI extends PathfindingAI {

    public ZombieAI(Round round) {
            super(round, 0);
    }

    @Override
    public void update(Mob mob, float delta){
        super.update(mob, delta);
    }
}
