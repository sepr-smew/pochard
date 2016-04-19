package com.superduckinvaders.game.entity.mob;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.superduckinvaders.game.Round;
import com.superduckinvaders.game.ai.RangedAI;
import com.superduckinvaders.game.assets.Assets;

public class RangedMob extends Mob {

    protected static float RANGE = 300f;


    public RangedMob(Round parent, float x, float y, int health, int speed, int score) {
        super(parent, x, y, health, speed, score, Assets.rangedBadGuy, Assets.rangedBadGuySwimming, Assets.playerShadow, new RangedAI(parent, RANGE));
        RANGED_ATTACK_COOLDOWN = 1.5f;
    }

    @Override
    public void update(float delta) {
        super.update(delta);

        Vector2 playerPos = parent.getPlayer().getCentre();

        float distanceFromPlayer = distanceTo(playerPos);

        //Update timers
        if(rangedAttackTimer > RANGED_ATTACK_COOLDOWN && distanceFromPlayer <= RANGE) {
            if (parent.rayCast(getCentre(), playerPos)) {
                fireAt(vectorTo(playerPos).setLength(PROJECTILE_SPEED));
                rangedAttackTimer = 0;
            }
        }
    }

    // Did do more than this, but by making render and update more generic this became less useful
}
