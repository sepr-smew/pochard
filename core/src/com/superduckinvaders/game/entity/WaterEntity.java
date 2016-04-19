package com.superduckinvaders.game.entity;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.superduckinvaders.game.Round;

/**
 * A water block. Lets the player know when to swim.
 */
public class WaterEntity extends PhysicsEntity {

    /**
     * Create a new WaterEntity.
     * @param parent the parent round.
     * @param x      the initial x position.
     * @param y      the initial y position.
     * @param width  the block's width.
     * @param height the block's height.
     */
    public WaterEntity(Round parent, float x, float y, float width, float height) {
        super(parent, x, y);
        this.width = width;
        this.height = height;
        createBody(BodyDef.BodyType.StaticBody, WATER_BITS, (short)(ALL_BITS ^ PROJECTILE_BITS), NO_GROUP, false);
    }

    @Override
    public void beginCollision(PhysicsEntity other, Contact contact){
        if (other instanceof Character){
            ((Character) other).waterBlockCount++;
        }
    }

    @Override
    public void endCollision(PhysicsEntity other, Contact contact){
        if (other instanceof Character){
            ((Character) other).waterBlockCount--;
        }
    }

    @Override
    public void preSolve(PhysicsEntity other, Contact contact, Manifold manifold) {
        super.preSolve(other, contact, manifold);
        if (other instanceof Character){
            contact.setEnabled(false);
        }
    }

    @Override
    public void render(SpriteBatch spriteBatch) {}

}
