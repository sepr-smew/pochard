package com.superduckinvaders.game.entity.mob;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.superduckinvaders.game.Round;
import com.superduckinvaders.game.ai.BossAI;
import com.superduckinvaders.game.assets.Assets;
import com.superduckinvaders.game.objective.BossObjective;

public class BossMob extends Mob {
    public BossMob(Round parent, float x, float y, int health, int speed, int score) {
        super(parent, x, y, health, speed, score, Assets.bossBadGuy, Assets.bossBadGuy, Assets.bossShadow, new BossAI(parent));
        disableCollision();
    }

    @Override
    protected void onDeath() {
        super.onDeath();
        ((BossObjective)parent.getObjective()).setCompleted();
    }

    // Did do more than this, but by making render and update more generic this became less useful
}
