package com.superduckinvaders.game.util;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

/**
 * Collision utilities.
 */
public class Collision {

    public abstract static class Query implements QueryCallback {
        public boolean result = false;
        public World world;
        public short maskBits;

        public Query(World world, short maskBits) {
            this.maskBits = maskBits;
            this.world = world;
        }

        public abstract boolean query();

        public abstract boolean reportFixture(Fixture fixture);

    }

    public static class QueryPoint extends Query {
        public Vector2 p;

        public QueryPoint(World world, Vector2 p, short maskBits){
            super(world, maskBits);
            this.p = p;
        }

        public boolean query(){
            world.QueryAABB(this, p.x, p.y, p.x+1, p.y+1);
            return result;
        }

        public boolean reportFixture(Fixture fixture){
            if ((fixture.getFilterData().categoryBits & maskBits) != 0 && fixture.testPoint(p)) {
                result = true; // we collided
                return false; // ends the query
            }
            return true; // keep searching
        }
    }

    public static class QueryArea extends Query {
        Vector2 p1;
        Vector2 p2;

        public QueryArea(World world, Vector2 pos, Vector2 size, short maskBits){
            super(world, maskBits);
            this.p1 = pos;
            this.p2 = size.add(pos);

        }

        public boolean query(){
            world.QueryAABB(this, p1.x, p1.y, p2.x, p2.y);
            return result;
        }


        public boolean reportFixture(Fixture fixture){
            if ((fixture.getFilterData().categoryBits & maskBits) != 0) {
                result = true; // we collided
                return false; // ends the query
            }
            return true; // keep searching
        }
    }
}

