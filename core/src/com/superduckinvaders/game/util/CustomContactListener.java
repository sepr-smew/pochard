package com.superduckinvaders.game.util;

import com.badlogic.gdx.physics.box2d.*;
import com.superduckinvaders.game.entity.PhysicsEntity;

/**
 * Helper class to make contact checks between two PhysicsEntities easier.
 */
public class CustomContactListener implements ContactListener {
    @Override
    public void beginContact(Contact contact) {
        applyCallback(contact,
                (PhysicsEntity ea, PhysicsEntity eb, boolean sensorA, boolean sensorB) -> {
                    if (sensorA) {
                        ea.beginSensorContact(eb, contact);
                    } else if (!sensorB) {
                        ea.beginCollision(eb, contact);
                    }
                }
        );
    }

    @Override
    public void endContact(Contact contact) {
        applyCallback(contact,
                (PhysicsEntity ea, PhysicsEntity eb, boolean sensorA, boolean sensorB) -> {
                    if (sensorA) {
                        ea.endSensorContact(eb, contact);
                    } else if (!sensorB) {
                        ea.endCollision(eb, contact);
                    }
                }
        );
    }

    @Override
    public void preSolve(Contact contact, Manifold manifold) {
        applyCallback(contact,
                (PhysicsEntity ea, PhysicsEntity eb, boolean sensorA, boolean sensorB) -> ea.preSolve(eb, contact, manifold)
        );
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse contactImpulse) {
        applyCallback(contact,
                (PhysicsEntity ea, PhysicsEntity eb, boolean sensorA, boolean sensorB) -> ea.postSolve(eb, contact, contactImpulse)
        );
    }

    public void applyCallback(Contact contact, Callback cb) {
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();
        Object a = fixtureA.getBody().getUserData();
        Object b = fixtureB.getBody().getUserData();
        if (a instanceof PhysicsEntity && b instanceof PhysicsEntity) {
            PhysicsEntity ea = (PhysicsEntity) a;
            PhysicsEntity eb = (PhysicsEntity) b;
            cb.callback(ea, eb, fixtureA.isSensor(), fixtureB.isSensor());
            cb.callback(eb, ea, fixtureB.isSensor(), fixtureA.isSensor());
        }
    }

    interface Callback {
        void callback(PhysicsEntity entity, PhysicsEntity other, boolean sensorA, boolean sensorB);
    }
}
