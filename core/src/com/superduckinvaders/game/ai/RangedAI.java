package com.superduckinvaders.game.ai;

import com.badlogic.gdx.math.MathUtils;
import com.superduckinvaders.game.Round;
import com.superduckinvaders.game.entity.Mob;
import com.superduckinvaders.game.entity.Projectile;

/**
 * Ai that will shoot at the player and move towards them.
 * Ideally will only shoot if the shot will not hit a collision tile
 */
public class RangedAI extends ZombieAI {

    /**
     * The speed that this AI should fire it's projectiles with
     */
    private final float PROJECTILE_SPEED;
    /**
     * The frequency that the AI should raycast to check whether it should fire
     */
    private final float RAYCAST_RATE= 0.5f;
    /**
     * The current time since the last raycast
     */
    private float raycast_timer = 0;
    /**
     * The result of the previous raycast
     */
    private boolean raycastResult=true;

    /**
     * Creates a new RangedAI
     * @param round The round that this AI resides in
     * @param attackRange The attack range of this AI
     * @param projectileSpeed The speed this AI shall fire projectiles at
     */
    public RangedAI(Round round, int attackRange, float projectileSpeed){
        super(round,attackRange);

        this.PROJECTILE_SPEED=projectileSpeed;
    }

    /**
     * Updates The AI, causing movement and attacks
     * @param mob  pointer to the Mob using this AI
     * @param delta time since the previous update
     */
    public void update(Mob mob,float delta) {
        updatePlayerCoords();

        float distanceFromPlayer = mob.distanceTo(playerX, playerY);

        //Update timers
        raycast_timer -=delta;
        attackTimer-=delta;

        currentOffset += delta;
        if (currentOffset >= deltaOffsetLimit && (int) distanceFromPlayer < 1280 / 2) {
            deltaOffsetLimit = PATHFINDING_RATE + (MathUtils.random() % PATHFINDING_RATE_OFFSET);
            currentOffset = 0;

            //If raycast collided then call the pathfinding function
            if(rayCastProjectileCollides(mob.getX(), mob.getY(), playerX, playerY)) {
                Coordinate targetCoord = FindPath(mob);
                Coordinate targetDir = new Coordinate((int) (targetCoord.x - mob.getX()), (int) (targetCoord.y - mob.getY()));
                mob.setVelocity(targetDir.x, targetDir.y);
            }
            //if raycast didn't collide then attack if possible
            else {
                if(attackTimer <= 0 && distanceFromPlayer<=attackRange) {
                    mob.fireAt(playerX, playerY+20, (int) PROJECTILE_SPEED, 1);
                    attackTimer = ATTACK_DELAY;
                }

            }
        }
    }

    /**
     * A rudimentary raycast to check for collision between start and target points
     * Function could do with some improvements. Needs to visit more intervening points and stop if moves past the target
     * @param startX The starting x coordinate for the raycast
     * @param startY The starting y coordinate for the raycast
     * @param targetX The target x coordinate for the raycast
     * @param targetY The target y coordinate for the raycast
     * @return true if the raycast collides with a wall, false otherwise
     */
    private boolean rayCastProjectileCollides(float startX, float startY, float targetX, float targetY){
        //Limit rate of actually performing algorithm by returning older result
        if(raycast_timer >0){
            return raycastResult;
        }
        //Shoot a projectile from the start coordinates towards the target
        //Check if it collides on the way
        else {
            raycast_timer =RAYCAST_RATE;

            Projectile projectile = new Projectile(round, startX, startY, targetX, targetY, 100, 0, null);

            boolean collided = false;
            int num = 0;
            while (!(round.getPlayer().intersects(projectile.getX(), projectile.getHeight(), projectile.getWidth(), projectile.getHeight()))) {
                num++;
                if (num >= 20)
                    break;

                projectile.setX(projectile.getX() + projectile.getVelocityX());
                projectile.setY(projectile.getY() + projectile.getVelocityY());

                boolean collidedX = projectile.collidesX(0);
                boolean collidedY = projectile.collidesY(0);

                collided = collidedX || collidedY;

                //System.out.println("Casting: X,Y=" + projectile.getX() + "," + projectile.getY() + " collided=" + collided);

                if (collided) {
                    break;
                }
            }
            raycastResult=collided;
            return collided;
        }
    }
}
