package com.superduckinvaders.game.entity;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.superduckinvaders.game.Round;
import com.superduckinvaders.game.assets.TextureSet;

import java.util.ArrayList;

/**
 * Represents a character in the game.
 */
public abstract class Character extends PhysicsEntity {

    /**
     * The direction the Character is facing.
     */
    protected int facing = TextureSet.FACING_FRONT;

    /**
     * Determines whether a character should collide with objects.
     */
    protected boolean shouldCheckCollision = true;

    /**
     * The state time for the animation. Set to 0 for not moving.
     */
    protected float stateTime = 0;

    /**
     * Current health and the maximum health of this Character.
     */
    protected int maximumHealth, currentHealth;

    protected static float MELEE_RANGE = 30f;
    protected static float MELEE_ATTACK_COOLDOWN = 0.2f;
    protected float meleeAttackTimer = 0f;
    protected short enemyBits = 0;
    protected ArrayList<PhysicsEntity> enemiesInRange;

    public int waterBlockCount = 0;

    /**
     * Initialises this Character.
     *
     * @param parent        the round this Character belongs to
     * @param x             the initial x coordinate
     * @param y             the initial y coordinate
     * @param maximumHealth the maximum (and initial) health of this Character
     */
    public Character(Round parent, float x, float y, int maximumHealth) {
        super(parent, x, y);

        this.maximumHealth = this.currentHealth = maximumHealth;
        enemiesInRange = new ArrayList<>();
    }

    @Override
    public void createBody(BodyDef.BodyType bodyType, short categoryBits, short maskBits, short groupIndex, boolean isSensor){
        super.createBody(bodyType, categoryBits, maskBits, groupIndex, isSensor);

        CircleShape meleeSensorShape = new CircleShape();
        meleeSensorShape.setRadius(MELEE_RANGE / PIXELS_PER_METRE);

        FixtureDef meleeFixtureDef = new FixtureDef();
        meleeFixtureDef.shape = meleeSensorShape;
        meleeFixtureDef.isSensor = true;

        meleeFixtureDef.filter.categoryBits = categoryBits;
        meleeFixtureDef.filter.maskBits = enemyBits;
        meleeFixtureDef.filter.groupIndex = SENSOR_GROUP;

        Fixture meleeFixture = body.createFixture(meleeFixtureDef);
        meleeFixture.setUserData(this);

        meleeSensorShape.dispose();
    }

    /**
     * Gets the direction the character is facing
     * @return the direction this Character is facing (one of the FACING_ constants in TextureSet)
     */
    public int getFacing() {
        return facing;
    }

    /**
     * Gets the current health of this Character.
     *
     * @return the current health of this Character
     */
    public int getCurrentHealth() {
        return currentHealth;
    }

    /**
     * Gets the maximum health of this Character.
     *
     * @return the maximum health of this Character
     */
    public int getMaximumHealth() {
        return maximumHealth;
    }

    /**
     * Enables character collision
     */
    public void enableCollision(){
        shouldCheckCollision=true;
    }

    /**
     * Disables character collision
     */
    public void disableCollision(){
        shouldCheckCollision=false;
    }


    /**
     * Heals this Character's current health by the specified number of points.
     *
     * @param health the number of health points to heal
     */
    public void heal(int health) {
        this.currentHealth += health;

        if (currentHealth > maximumHealth) {
            currentHealth = maximumHealth;
        }
    }

    /**
     * Damages this Character's health by the specified number of points.
     *
     * @param health the number of points to damage
     */
    public abstract void damage(int health);

    /**
     * Returns if the character is dead
     * @return whether this Character is dead (i.e. its health is 0)
     */
    public boolean isDead() {
        return currentHealth <= 0;
    }

    /**
     * Checks if the player is on a water tile or not
     *
     * @return true if player is on water tile, otherwise false
     */
    protected boolean isOnWater(){
        return waterBlockCount > 0;
    }

    public void fireAt(Vector2 direction, float projectileSpeed, int damage) {
        Vector2 velocity = direction.setLength(projectileSpeed).add(getPhysicsVelocity());
        velocity.setLength(Math.max(projectileSpeed, velocity.len()));
        fireAt(Vector2.Zero.cpy(), velocity, damage);

    }
    public void fireAt(int startx, int starty, float targetx, float targety, float speed, int damage) {
        Vector2 offset = new Vector2(startx, starty);
        Vector2 startPos = offset.cpy().add(getPosition());
        fireAt(offset, new Vector2(targetx, targety).sub(startPos).setLength(speed), damage);
    }

    public void fireAt(Vector2 offset, Vector2 velocity, int damage) {
        parent.createProjectile(offset.add(getPosition()), velocity, damage, this);
    }

    @Override
    public void beginSensorContact(PhysicsEntity other, Contact contact) {
        super.beginSensorContact(other, contact);
        if (other instanceof Character || (other instanceof Projectile && ((Projectile)other).getOwner() != this)) {
            enemiesInRange.add(other);
        }

    }

    @Override
    public void endSensorContact(PhysicsEntity other, Contact contact) {
        super.endSensorContact(other, contact);
        if (enemiesInRange.contains(other)) {
            enemiesInRange.remove(other);
        }

    }

    /**
     * Causes this Character to use a melee attack.
     *
     * @param direction the attack direction.
     * @param damage how much damage the attack deals.
     * @return whether the attack has occured.
     */
    protected boolean meleeAttack(Vector2 direction, int damage) {
//        if (isStunned()) {
//            return false;
//        }
//        if (meleeAttackTimer > MELEE_ATTACK_COOLDOWN && !enemiesInRange.isEmpty()){
        if (meleeAttackTimer > MELEE_ATTACK_COOLDOWN){
            if (enemiesInRange.isEmpty()) {
                return false;
            }
            else {
                meleeAttackTimer = 0f;
            }

            for (PhysicsEntity entity : enemiesInRange) {
                if (directionTo(entity.getCentre()) == facing) {
                    if (entity instanceof Character) {
                        Character character = (Character) entity;
                        character.damage(damage);
                        character.setVelocity(direction.cpy().setLength(40f));
                    } else if (entity instanceof Projectile){
                        Projectile projectile = (Projectile) entity;
                        float speed = projectile.getPhysicsVelocity().len();
                        Vector2 newVelocity = vectorTo(projectile.getCentre()).setLength(speed*2);
                        projectile.setOwner(this);
                        projectile.setVelocity(newVelocity);
                    }
                }
            }
            return true;
        }
        return false;
    }

    /**
     * Causes this Character to use a melee attack.
     *
     * @param range  how far the attack reaches in pixels
     * @param damage how much damage the attack deals
     */
    protected void melee(float range, int damage) {
        // Don't let mobs melee other mobs (for now).
        if (this instanceof Mob) {
            Player player = parent.getPlayer();

            if (distanceTo(player.getX(), player.getY()) <= range && directionTo(player.getX(), player.getY()) == facing) {
                player.damage(damage);
            }
        } else {
            // Attack the closest Character within the range.
            Character closest = null;

            for (Entity entity : parent.getEntities()) {
                // Disregard entity if it's me or it isn't a Character.
                if (this == entity || !(entity instanceof Character)) {
                    continue;
                }

                float x = entity.getX(), y = entity.getY();
                if (distanceTo(x, y) <= range && directionTo(x, y) == facing && (closest == null || distanceTo(x, y) < distanceTo(closest.getX(), closest.getY()))) {
                    closest = (Character) entity;
                }
            }

            // Can't attack if nothing in range.
            if (closest != null) {
                closest.damage(damage);
            }
        }
    }

    /**
     * Updates the state of this Character. Will check collision if shouldCheckCollision is true
     *
     * @param delta how much time has passed since the last update
     */
    @Override
    public void update(float delta) {

        meleeAttackTimer += delta;

        // Update Character facing.
        Vector2 velocity = getVelocity();
        if (velocity.x < 0) {
            facing = TextureSet.FACING_LEFT;
        } else if (velocity.x > 0) {
            facing = TextureSet.FACING_RIGHT;
        }

        if (velocity.y < 0) {
            facing = TextureSet.FACING_FRONT;
        }
        else if (velocity.x > 0) {
            facing = TextureSet.FACING_BACK;
        }

        if (isDead()) {
            removed = true;
        }

        super.update(delta);
    }
}
