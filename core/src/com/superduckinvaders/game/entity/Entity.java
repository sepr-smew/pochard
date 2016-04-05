package com.superduckinvaders.game.entity;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.superduckinvaders.game.Round;
import com.superduckinvaders.game.assets.TextureSet;
import com.superduckinvaders.game.entity.item.Item;

import java.util.Comparator;

/**
 * Represents an object in the game.
 */
public abstract class Entity {

    /**
     * The round that this Entity is in.
     */
    protected Round parent;

    /**
     * The x and y coordinates of this Entity.
     */
    protected float x, y;
    protected float width, height;

    /**
     * Whether or not to remove this Entity on the next frame.
     */
    protected boolean removed = false;

    /**
     * Initialises this Entity with zero initial coordinates.
     *
     * @param parent the round this Entity belongs to
     */
    public Entity(Round parent) {
        this(parent, 0, 0);
    }

    /**
     * Initialises this Entity with the specified initial coordinates.
     *
     * @param parent the round this Entity belongs to
     * @param x      the initial x coordinate
     * @param y      the initial y coordinate
     */
    public Entity(Round parent, float x, float y) {
        this.parent = parent;
        this.x = x;
        this.y = y;
    }

    public Vector2 getPosition() {
        return new Vector2(getX(), getY());
    }

    public Vector2 getCentre(){
        return getPosition()
                .add(getWidth()/2f, getHeight()/2f);
    }

    /**
     * Returns the x coordinate of the entity
     * @return the x coordinate of this Entity
     */
    public float getX() {
        return x;
    }

    /**
     * Returns the y coordinate of the entity
     * @return the y coordinate of this Entity
     */
    public float getY() {
        return y;
    }

    /**
     * Returns true if the specified rectangle intersects this Entity.
     *
     * @param x      the x coordinate of the rectangle's bottom left corner
     * @param y      the y coordinate of the rectangle's bottom left corner
     * @param width  the width of the rectangle
     * @param height the height of the rectangle
     * @return whether the specified rectangle intersects this Entity
     */
    public boolean intersects(float x, float y, float width, float height) {
        return this.x < x + width && this.x + getWidth() > x && this.y < y + height && this.y + getHeight() > y;
    }

    /**
     * Returns the distance between this Entity and the specified coordinates.
     *
     * @param x the x coordinate to compare with
     * @param y the y coordinate to compare with
     * @return the distance between this Entity and the coordinates, in pixels
     */
    public float distanceTo(float x, float y) {
        return distanceTo(new Vector2(x, y));
    }
    public float distanceTo(Vector2 pos) {
        return getCentre().sub(pos).len();
    }

    /**
     * Returns the angle between this Entity and the specified coordinates.
     *
     * @param x the x coordinate to compare with
     * @param y the y coordinate to compare with
     * @return the angle between this Entity and the coordinates, in radians
     */
    public float angleTo(float x, float y) {
        //return (float) Math.atan2(y - (this.y + this.getHeight()/2), x - (this.x + this.getWidth()/2));
        return angleTo(new Vector2(x, y));
    }
    public float angleTo(Vector2 pos){
        return vectorTo(pos).angle() % 360f;
    }
    public float angleRadTo(float x, float y) {
        //return (float) Math.atan2(y - (this.y + this.getHeight()/2), x - (this.x + this.getWidth()/2));
        return angleRadTo(new Vector2(x, y));
    }
    public float angleRadTo(Vector2 pos){
        return vectorTo(pos).angleRad() % ((float)Math.PI * 2 );
    }

    public Vector2 vectorTo(Vector2 pos){
        return pos.cpy().sub(getCentre());
    }

    /**
     * Returns the direction to the specified coordinates from this Entity (one of the FACING_ constants in TexutreSet).
     *
     * @param pos the coordinates to compare with
     * @return the direction the coordinates are in relative to this Entity
     */
    public TextureSet.Facing directionTo(Vector2 pos) {
        return directionTo(pos.x, pos.y);
    }

    public TextureSet.Facing directionTo(float x, float y) {
        float angle = angleRadTo(x, y);

        if (angle < Math.PI * 3/8 && angle >= Math.PI / 8) {
            return TextureSet.Facing.BACK_RIGHT;

        } else if (angle < Math.PI * 5 / 8 && angle >= Math.PI * 3/8) {
                return TextureSet.Facing.BACK;

        } else if (angle < Math.PI * 7/8 && angle >= Math.PI * 5/8) {
            return TextureSet.Facing.BACK_LEFT;

        } else if (angle < -Math.PI * 5/8 && angle >= -Math.PI * 7/8) {
            return TextureSet.Facing.FRONT_LEFT;

        } else if (angle < -Math.PI * 3/8 && angle >= -Math.PI * 5/8) {
            return TextureSet.Facing.FRONT;

        } else if (angle < -Math.PI * 1/8 && angle >= -Math.PI * 3/8) {
            return TextureSet.Facing.FRONT_RIGHT;

        } else if (angle < Math.PI * 1/8 && angle >= -Math.PI * 1/8) {
            return TextureSet.Facing.RIGHT;
        } else {
            return TextureSet.Facing.LEFT;
        }
    }

    /**
     * Returns the width of the entity
     * @return the width of this Entity
     */
    public float getWidth() {
        return this.width;
    }

    /**
     * Returns the height of the entity
     * @return the height of this Entity
     */
    public float getHeight() {
        return this.height;
    }

    public Vector2 getSize() {
        return new Vector2(getWidth(), getHeight());
    }


    /**
     * Gets the x position of the collision bounds.
     * @return returns the BoundsX of the entity
     */
    public int getBoundsX() {
        return 0;
    }

    /**
     * Gets the y position of the collision bounds.
     * @return returns the BoundsY of the entity
     */
    public int getBoundsY() { return 0; }


    /**
     * Returns if this entity should be removed
     * @return whether this Entity has been removed
     */
    public boolean isRemoved() {
        return removed;
    }

    /**
     * Ensures that this MobileEntity stays within the map area.
     */
    protected void checkBounds() {
        if (x < 0) {
            x = 0;
        } else if (x > parent.getMapWidth() - getWidth()) {
            x = parent.getMapWidth() - getWidth();
        }

        if (y < 0) {
            y = 0;
        } else if (y > parent.getMapHeight() - getHeight()) {
            y = parent.getMapHeight() - getHeight();
        }
    }

    public void dispose(){}

    /**
     * Updates the state of this Entity.
     *
     * @param delta how much time has passed since the last update
     */
    public void update(float delta) {

        checkBounds();
    }

    /**
     * Renders this Entity.
     *
     * @param spriteBatch the sprite batch on which to render
     */
    public abstract void render(SpriteBatch spriteBatch);

    /**
     * Used to determine the order that entities are rendered.
     */
    public static class EntityComparator implements Comparator<Entity> {

        @Override
        public int compare(Entity o1, Entity o2) {
            //front of list rendered first
            if (o1 instanceof Character) {
                if (o2 instanceof Character || o2 instanceof Item) {
                    //calc height
                    float x = o2.getY() - o1.getY();
                    if (x > 0) {
                        return 1;
                    } else if (x == 0) {
                        return 0;
                    } else {
                        return -1;
                    }
                } else {
                    return 0;
                }
            } if (o1 instanceof Item) {
                    //calc height
                    float x = o2.getY() - o1.getY();
                    if (x > 0) {
                        return 1;
                    } else if (x == 0) {
                        return 0;
                    } else {
                        return -1;
                    }
            } else {
                if (o2 instanceof Character) {
                    return 0;
                } else {
                    //calc height
                    float x = o2.getY() - o1.getY();
                    if (x > 0) {
                        return 1;
                    } else if (x == 0) {
                        return 0;
                    } else {
                        return -1;
                    }
                }
            }
        }

    }

}
