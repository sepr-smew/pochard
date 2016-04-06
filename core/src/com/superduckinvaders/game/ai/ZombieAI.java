package com.superduckinvaders.game.ai;

import com.badlogic.gdx.math.Vector2;
import com.superduckinvaders.game.Round;
import com.superduckinvaders.game.entity.mob.Mob;
import com.superduckinvaders.game.entity.Player;

/**
 * Created by james on 10/03/16.
 */
public class ZombieAI extends PathfindingAI {

    public static final float ATTACK_DELAY = 1;

    /**
     * How far away from the player this ZombieAI can attack.
     */
    protected int attackRange;
    /**
     * How long before we can attack again.
     */
    protected float attackTimer = 0;

    public ZombieAI(Round round, int attackRange) {
            super(round, 0);
            this.attackRange = attackRange;
    }

    @Override
    public void update(Mob mob, float delta){
        super.update(mob, delta);
        if ((int) mob.distanceTo(playerPos) < attackRange && attackTimer <= 0) {
            Player player = round.getPlayer();
            player.damage(1);
            Vector2 knockback = mob.vectorTo(player.getCentre()).cpy().setLength(600f);
            player.setVelocity(knockback);
            mob.setVelocity(knockback.scl(0.3f));
            attackTimer = ATTACK_DELAY;
        } else if (attackTimer > 0) {
            attackTimer -= delta;
        }
    }
}
