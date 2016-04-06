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
    protected TextureSet.Facing facing = TextureSet.Facing.FRONT;

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
    protected static float RANGED_RANGE = 30f;
    protected static float RANGED_ATTACK_COOLDOWN = 0.2f;
    protected float rangedAttackTimer = 0f;
    protected static int RANGED_DAMAGE = 1;

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
    public TextureSet.Facing getFacing() {
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
        shouldCheckCollision = true;
    }

    /**
     * Disables character collision
     */
    public void disableCollision(){
        shouldCheckCollision = false;
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

    public void fireAt(Vector2 velocity) {
        fireAt(getCentre(), velocity);

    }

    public void fireAt(Vector2 position, Vector2 velocity) {
        parent.createProjectile(position, velocity, RANGED_DAMAGE, this);
    }

    @Override
    public void preSolve(PhysicsEntity other, Contact contact, Manifold manifold) {
        super.preSolve(other, contact, manifold);
        // Disabling contact here rather than changing collision mask so we can still
        // tell when the player is contacting something to prevent re-enabling while inside an object
        if (contact.isEnabled() && !shouldCheckCollision){
            contact.setEnabled(false);
        }
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

            if (enemiesInRange.isEmpty()) return false;
            else meleeAttackTimer = 0f;

            for (PhysicsEntity entity : enemiesInRange) {
                if (directionTo(entity.getCentre()) == facing) {
                    if (entity instanceof Character) {
                        Character character = (Character) entity;
                        character.damage(damage);
                        character.setVelocity(direction.cpy().setLength(400f));
                    } else if (entity instanceof Projectile){
                        Projectile projectile = (Projectile) entity;
                        float speed = projectile.getVelocity().len();
                        Vector2 newVelocity = vectorTo(projectile.getCentre()).setLength(speed*2);
                        projectile.setOwner(this);
                        projectile.setDamage(RANGED_DAMAGE*2);
                        projectile.setVelocity(newVelocity);
                    }
                }
            }
            return true;
        }
        return false;
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

        if (Math.abs(velocity.y) > Math.abs(velocity.x))
            facing = velocity.y > 0 ?  TextureSet.Facing.BACK : TextureSet.Facing.FRONT;
        else if (Math.abs(velocity.y) < Math.abs(velocity.x))
            facing = velocity.x > 0 ?  TextureSet.Facing.RIGHT : TextureSet.Facing.LEFT;

        if (isDead()) {
            removed = true;
        }

        super.update(delta);
    }
}
