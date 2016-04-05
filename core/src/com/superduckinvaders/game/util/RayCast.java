package com.superduckinvaders.game.util;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;

/**
 * Helper Raycasting class. We use raycasting to simplify AI.
 */
public class RayCast {
    public static class RayCastCB implements RayCastCallback {
        public float fraction;
        public boolean clear = true;
        public short maskBits;

        public RayCastCB(short maskBits){
            fraction = 1f;
            this.maskBits = maskBits;

        }
        @Override
        public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction){
            /* if multiple fixtures are found, because we return the fraction, subsequent fixtures
             * will always be closer than the previous ones, but we may not always see more distant fixures
             * past the first intersection found.
             */
            this.clear = (fixture.getFilterData().categoryBits & maskBits) == 0;
            this.fraction = fraction;
            /* this reduces the length of the ray to the currently found intersection
             * this is done because fixtures are not necessarily reported in
             * in any order, and we only care about the closest intersection
             */
            return fraction;
        }
    }
}
