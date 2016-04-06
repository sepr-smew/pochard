package com.superduckinvaders.game.ai;

import com.badlogic.gdx.math.Vector2;
import com.superduckinvaders.game.Round;
import com.superduckinvaders.game.entity.mob.Mob;

/**
 * Ai for a boss enemy.
 * Moves left then right for 2 body-lengths at a time.
 * Shoots a burst of projectiles at the player
 */
public class BossAI extends AI {

    /**
     * The starting X position of the Mob
     */
    private float startX;
    /**
     * The starting y position of the Mob
     */
    private float startY;

    /**
     * Stores if this AI has just been created. Used to perform different actions on the first update
     */
    private boolean isFirstUpdate = true;

    /**
     * Decides which direction the mob is currently moving in
     */
    private boolean isMoveLeft = true;

    /**
     * The speed to set velocity at
     */
    private final float SPEED = 100;

    /**
     * The speed to fire projectiles at
     */
    private final float PROJECTILE_SPEED = 30;

    /**
     * The frequency to fire projectile bursts at
     */
    private final float ATTACK_RATE = 1;

    /**
     * Used to track the attack frequency
     */
    private float attackTimer = 0;

    /**
     * The size the burst of projectiles can be
     * This value is 5 times bigger than the number of projectiles to fire
     * A modulo of 5 is used to better time the burst
     */
    private final int BURST_SIZE = 25;
    /**
     * Keeps track of the remaining projectiles to fire for the current burst
     */
    private int burstRemaining;

    private Vector2 playerPos;

    /**
     * Create an instance of BossAi
     * @param round The round that this AI resides in
     */
    public BossAI(Round round){
        super(round);
    }

    /**
     * Update the movement of the mob and fire projectiles
     * @param mob pointer to the Mob using this AI
     * @param delta time since the previous update
     */
    public void update(Mob mob, float delta){
        //Fills the x and y coordinates on the first update
        if (isFirstUpdate){
            startX=mob.getX();
            startY=mob.getY();
            isFirstUpdate=false;
        }

        attackTimer-=delta;//Updates the attackTimer

        playerPos = round.getPlayer().getCentre();
        //float distanceFromPlayer = mob.distanceTo(playerX, playerY);

        //Moves left or right based on isMoveLeft
        if (isMoveLeft){
            if(mob.getX()<startX-mob.getWidth()*2){
                isMoveLeft=false;
            }
            else{
                mob.applyVelocity(new Vector2(-SPEED, 0));
            }
        }
        else{
            if(mob.getX()>startX+mob.getWidth()*3){
                isMoveLeft=true;
            }
            else{
                mob.setVelocity(new Vector2(SPEED, 0));
            }
        }

        //Attack if enough time has passed since last attack
        if(attackTimer <= 0 /*&& distanceFromPlayer<=attackRange*/) {
            burstRemaining=BURST_SIZE;
            attackTimer = ATTACK_RATE;
        }
        if(burstRemaining>=0){
            if (burstRemaining % 5 == 0) {
                mob.fireAt(mob.vectorTo(playerPos).setLength(PROJECTILE_SPEED), 1);
            }
            burstRemaining--;
        }

    }
}
