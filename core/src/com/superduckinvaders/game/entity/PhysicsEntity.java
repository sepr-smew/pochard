package com.superduckinvaders.game.entity;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.superduckinvaders.game.Round;

/**
 * Represents an object in the game.
 */
public abstract class PhysicsEntity extends Entity {
    public static final short WORLD_BITS       = 0x1;
    public static final short PLAYER_BITS      = 0x2;
    public static final short MOB_BITS         = 0x4;
    public static final short PROJECTILE_BITS  = 0x8;
    public static final short ITEM_BITS        = 0x10;
    public static final short WATER_BITS        = 0x10;
    public static final short ALL_BITS         = WORLD_BITS | PLAYER_BITS | MOB_BITS |
            PROJECTILE_BITS | ITEM_BITS | WATER_BITS;
    public static final short NO_GROUP         = 0;
    public static final short MOB_GROUP        = -1;
    public static final short SENSOR_GROUP        = -2;

    public short categoryBits = PLAYER_BITS;

    public Body body;
    public static final float METRES_PER_PIXEL = 1/16f;
    public static final float PIXELS_PER_METRE = 1/METRES_PER_PIXEL;

    /**
     * Initialises this Entity with zero initial coordinates.
     *
     * @param parent the round this Entity belongs to
     */
    public PhysicsEntity(Round parent) {
        this(parent, 0, 0);
    }

    public PhysicsEntity(Round parent, Vector2 pos) {
        this(parent, pos.x, pos.y);

    }

    /**
     * Initialises this Entity with the specified initial coordinates.
     *
     * @param parent the round this Entity belongs to
     * @param x      the initial x coordinate
     * @param y      the initial y coordinate
     */
    public PhysicsEntity(Round parent, float x, float y) {
        super(parent, x, y);
    }

    public void createStaticBody(short categoryBits, short maskBits, short groupIndex, boolean isSensor){
        createBody(BodyDef.BodyType.StaticBody, categoryBits, maskBits, groupIndex, isSensor);
    }
    public void createDynamicBody(short categoryBits, short maskBits, short groupIndex, boolean isSensor){
        createBody(BodyDef.BodyType.DynamicBody, categoryBits, maskBits, groupIndex, isSensor);
    }
    public void createBody(BodyDef.BodyType bodyType){
        createBody(bodyType, WORLD_BITS, WORLD_BITS, NO_GROUP, false);
    }
    public void createBody(BodyDef.BodyType bodyType, short categoryBits, short maskBits, short groupIndex, boolean isSensor){
        float width = getWidth();
        float height = getHeight();
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = bodyType;
        // bodyDef.linearDamping = 20f;
        bodyDef.position.set((x+(width/2))*METRES_PER_PIXEL,
                (y+(height/2))*METRES_PER_PIXEL);

        PolygonShape boundingBox = new PolygonShape();
        boundingBox.setAsBox(((width/2))*METRES_PER_PIXEL,
                ((height/2))*METRES_PER_PIXEL);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = boundingBox;
        fixtureDef.isSensor = isSensor;

        fixtureDef.filter.categoryBits = categoryBits;
        fixtureDef.filter.maskBits = maskBits;
        fixtureDef.filter.groupIndex = groupIndex;

        body = parent.world.createBody(bodyDef);
        Fixture fixture = body.createFixture(fixtureDef);
        fixture.setUserData(this);
        body.setUserData(this);
        boundingBox.dispose();
    }


    /**
     * Returns the x coordinate of the entity
     * @return the x coordinate of this Entity
     */
    @Override
    public float getX() {
        return getPosition().x;
    }

    /**
     * Returns the y coordinate of the entity
     * @return the y coordinate of this Entity
     */
    @Override
    public float getY() {
        return getPosition().y;
    }

    @Override
    public Vector2 getPosition() {
        return getCentre()
                .sub(getWidth()/2f, getHeight()/2f);
    }

    @Override
    public Vector2 getCentre(){
        return body.getPosition().scl(PIXELS_PER_METRE);
    }

    /**
     * Returns the x velocity of the entity
     * @return the x velocity of this PhysicsEntity in pixels per second
     */
    public float getVelocityX() {
        return getVelocity().x;
    }

    /**
     * Returns the y velocity of the entity
     * @return the y coordinate of this PhysicsEntity in pixels per second
     */
    public float getVelocityY() {
        return getVelocity().y;
    }

    public Vector2 getVelocity() {
        return getPhysicsVelocity().scl(PIXELS_PER_METRE);
    }

    public Vector2 getPhysicsVelocity() {
        return body.getLinearVelocity().cpy();
    }

    public void setVelocity(Vector2 targetVelocity) {
        setVelocity(targetVelocity, 0f);
    }
    public void setVelocityClamped(Vector2 targetVelocity) {
        setVelocity(targetVelocity, 4f);
    }
    public void setVelocity(Vector2 targetVelocity, float limit) {
        Vector2 deltaVelocity = targetVelocity.scl(METRES_PER_PIXEL).sub(body.getLinearVelocity());
        if (limit>0){
            deltaVelocity.clamp(0, limit);
        }
        Vector2 impulse = deltaVelocity.scl(body.getMass());
        body.applyLinearImpulse(impulse, body.getWorldCenter(), true);
    }

    public short getMaskBits(){
        Fixture fixture = body.getFixtureList().get(0);
        return fixture.getFilterData().maskBits;
    }
    public void setMaskBits(short maskBits){
        Fixture fixture = body.getFixtureList().get(0);
        Filter filter = fixture.getFilterData();
        filter.maskBits = maskBits;
        fixture.setFilterData(filter);
    }

    @Override
    public void dispose(){
        if (body != null){
            parent.world.destroyBody(body);
        }
    }

    public void beginCollision(PhysicsEntity other, Contact contact) {
    }

    public void endCollision(PhysicsEntity other, Contact contact) {
    }

    public void beginSensorContact(PhysicsEntity other, Contact contact) {
    }

    public void endSensorContact(PhysicsEntity other, Contact contact) {
    }

    public void preSolve(PhysicsEntity other , Contact contact, Manifold manifold) {
    }

    public void postSolve(PhysicsEntity other , Contact contact, ContactImpulse contactImpulse) {
    }
}
