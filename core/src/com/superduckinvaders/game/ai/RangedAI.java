package com.superduckinvaders.game.ai;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.superduckinvaders.game.Round;
import com.superduckinvaders.game.entity.Mob;
import com.superduckinvaders.game.entity.PhysicsEntity;
import com.superduckinvaders.game.entity.Projectile;

/**
 * Ai that will shoot at the player and move towards them.
 * Ideally will only shoot if the shot will not hit a collision tile
 */
public class RangedAI extends PathfindingAI {
    ;
    public static float ATTACK_DELAY = 3f;
    public float attackTimer = 0f;
    public float attackRange;

    /**
     * The speed that this AI should fire it's projectiles with
     */
    private final float PROJECTILE_SPEED;
    /**
     * The frequency that the AI should raycast to check whether it should fire
     */

    /**
     * Creates a new RangedAI
     * @param round The round that this AI resides in
     * @param projectileSpeed The speed this AI shall fire projectiles at
     */
    public RangedAI(Round round, float attackRange, float projectileSpeed){
        super(round, attackRange);
        this.attackRange = attackRange;

        this.PROJECTILE_SPEED=projectileSpeed;
    }

    /**
     * Updates The AI, causing movement and attacks
     * @param mob  pointer to the Mob using this AI
     * @param delta time since the previous update
     */
    public void update(Mob mob,float delta) {
        super.update(mob, delta);

        float distanceFromPlayer = mob.distanceTo(playerPos);

        //Update timers
        attackTimer-=delta;
        if(attackTimer <= 0 && distanceFromPlayer <= attackRange) {
            if (round.rayCast(mob.getCentre(), playerPos)) {
                mob.fireAt(playerPos, PROJECTILE_SPEED, 1);
                attackTimer = ATTACK_DELAY;
            }
        }
    }

}
