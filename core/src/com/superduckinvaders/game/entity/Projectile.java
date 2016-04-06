package com.superduckinvaders.game.entity;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.particles.ParallelArray;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Contact;
import com.superduckinvaders.game.Round;
import com.superduckinvaders.game.assets.Assets;

/**
 * Represents a projectile.
 */
public class Projectile extends PhysicsEntity {

    /**
     * The owner of this Projectile (i.e. the Entity that fired it).
     */
    private PhysicsEntity owner;

    /**
     * How much damage this Projectile does to what it hits.
     */
    private int damage;

    /**
     * The angle this Projectile is travelling from it's start point
     */
    private float angle;

    /**
     * Indicates whether the class has just been created, used to ensure the projectile's position doesn't get updated before it is first drawn
     */
    private boolean isFirstUpdate = true;

    /**
     * Initialises this Projectile.
     *
     * @param parent          the round this Projectile belongs to
     * @param damage          how much damage the projectile deals
     * @param owner           the owner of the projectile (i.e. the one who fired it)
     */
    public Projectile(Round parent, Vector2 pos, Vector2 velocity, int damage, PhysicsEntity owner) {
        super(parent, pos);

        createDynamicBody(PROJECTILE_BITS, (short) ~owner.getCategoryBits(), NO_GROUP, false);
        body.setBullet(true);


        setVelocity(velocity);

        this.damage = damage;
        this.owner = owner;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    /**
     * Set the owner of the projectile to a new PhysicsEntity.
     * @param owner the new owner.
     */
    public void setOwner(PhysicsEntity owner) {
        this.owner = owner;
        setMaskBits((short) ~owner.getCategoryBits());
    }

    /**
     * @return the current projectile owner.
     */
    public PhysicsEntity getOwner() {
        return owner;
    }

    /**
     * @return the width of this Projectile
     */
    @Override
    public float getWidth() {
        return Assets.projectile.getRegionHeight();
    }

    /**
     * @return the height of this Projectile
     */
    @Override
    public float getHeight() {
        return Assets.projectile.getRegionHeight();
    }

    @Override
    public void beginCollision(PhysicsEntity other, Contact contact){
        parent.createParticle(getX(), getY() , 0.6f, Assets.explosionAnimation);
        removed = true;
        if (other instanceof Character && other != owner) {
            ((Character) other).damage(damage);
        }
    }

    /**
     * Updates the state of this Projectile.
     *
     * @param delta how much time has passed since the last update
     */
    @Override
    public void update(float delta) {
    }

    /**
     * Renders this Projectile. Rotates to correct angle
     *
     * @param spriteBatch the sprite batch on which to render
     */
    @Override
    public void render(SpriteBatch spriteBatch) {
        spriteBatch.draw(Assets.projectile, getX(), getY(), Assets.projectile.getRegionWidth()/2, Assets.projectile.getRegionHeight()/2, Assets.projectile.getRegionWidth(), Assets.projectile.getRegionHeight(), 1,1 , getVelocity().angle());
    }
}
