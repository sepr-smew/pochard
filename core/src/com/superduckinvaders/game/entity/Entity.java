package com.superduckinvaders.game.entity;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
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

    /**
     * The x and y velocity of this MobileEntity in pixels per second.
     */
    protected float velocityX = 0, velocityY = 0;

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
     * Returns the x velocity of the entity
     * @return the x velocity of this MobileEntity in pixels per second
     */
    public float getVelocityX() {
        return velocityX;
    }

    /**
     * Returns the y velocity of the entity
     * @return the y coordinate of this MobileEntity in pixels per second
     */
    public float getVelocityY() {
        return velocityY;
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
    public boolean intersects(float x, float y, int width, int height) {
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
        return (float) Math.sqrt(Math.pow(x - this.x, 2) + Math.pow(y - this.y, 2));
    }

    /**
     * Returns the angle between this Entity and the specified coordinates.
     *
     * @param x the x coordinate to compare with
     * @param y the y coordinate to compare with
     * @return the angle between this Entity and the coordinates, in radians
     */
    public float angleTo(float x, float y) {
        return (float) Math.atan2(y - (this.y + this.getHeight()/2), x - (this.x + this.getWidth()/2));
    }

    /**
     * Returns the direction to the specified coordinates from this Entity (one of the FACING_ constants in TexutreSet).
     *
     * @param x the x coordinate to compare with
     * @param y the y coordinate to compare with
     * @return the direction the coordinates are in relative to this Entity
     */
    public int directionTo(float x, float y) {
        float angle = angleTo(x, y);

        if (angle < Math.PI * 3/8 && angle >= Math.PI / 8) {
            return TextureSet.FACING_BACK_RIGHT;

        } else if (angle < Math.PI * 5 / 8 && angle >= Math.PI * 3/8) {
                return TextureSet.FACING_BACK;

        } else if (angle < Math.PI * 7/8 && angle >= Math.PI * 5/8) {
            return TextureSet.FACING_BACK_LEFT;

        } else if (angle < -Math.PI * 5/8 && angle >= -Math.PI * 7/8) {
            return TextureSet.FACING_FRONT_LEFT;

        } else if (angle < -Math.PI * 3/8 && angle >= -Math.PI * 5/8) {
            return TextureSet.FACING_FRONT;

        } else if (angle < -Math.PI * 1/8 && angle >= -Math.PI * 3/8) {
            return TextureSet.FACING_FRONT_RIGHT;

        } else if (angle < Math.PI * 1/8 && angle >= -Math.PI * 1/8) {
            return TextureSet.FACING_RIGHT;
        } else {
            return TextureSet.FACING_LEFT;
        }
    }

    /**
     * Returns the width of the entity
     * @return the width of this Entity
     */
    public abstract int getWidth();

    /**
     * Returns the height of the entity
     * @return the height of this Entity
     */
    public abstract int getHeight();


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

    /**
     * Gets whether the specified x delta will cause a collision on the left or right.
     *
     * @param deltaX the x delta
     * @return whether a collision would occur on the left or right
     */
    public boolean collidesX(float deltaX) {
        // Check for entity collisions.
        for (Entity entity : parent.getEntities()) {
            if (entity == this || this instanceof Projectile) {
                continue;
            }

            if (entity instanceof Character && entity.intersects(x + deltaX, y, getWidth(), getHeight())) {
                return true;
            }
        }

        // Check for tile collisions.
        return collidesLeft(deltaX) || collidesRight(deltaX);
    }

    /**
     * Gets whether the specified y delta will cause a collision on the bottom or top.
     *
     * @param deltaY the y delta
     * @return whether a collision would occur on the bottom or top
     */
    public boolean collidesY(float deltaY) {
        // Check for entity collisions.
        for (Entity entity : parent.getEntities()) {
            // Don't damage my owner.
            if (entity == this || this instanceof Projectile) {
                continue;
            }

            if (entity instanceof Character && entity.intersects(x, y + deltaY, getWidth(), getHeight())) {
                return true;
            }
        }

        // Check for tile collisions.
        return collidesBottom(deltaY) || collidesTop(deltaY);
    }

    /**
     * Gets whether specified x delta will cause a collision from an arbitrary position
     * Used in AI path detection.
     *
     * @param deltaX the x delta
     * @param fromX  arbitrary x position
     * @param fromY  arbitrary y position
     * @return whether collides
     */
    public boolean collidesXfrom(float deltaX, float fromX, float fromY) {
        float tempX = this.x;
        float tempY = this.y;
        this.x = fromX;
        this.y = fromY;
        boolean result = collidesLeft(deltaX) || collidesRight(deltaX);
        this.x = tempX;
        this.y = tempY;
        return result;
    }

    /**
     * Gets whether specified y delta will cause a collision from an arbitrary position
     * Used in AI path detection.
     *
     * @param deltaY the y delta
     * @param fromX  arbitrary x position
     * @param fromY  arbitrary y position
     * @return whether collides
     */
    public boolean collidesYfrom(float deltaY, float fromX, float fromY) {
        float tempX = this.x;
        float tempY = this.y;
        this.x = fromX;
        this.y = fromY;
        boolean result = collidesTop(deltaY) || collidesBottom(deltaY);
        this.x = tempX;
        this.y = tempY;
        return result;
    }

    /**
     * Gets whether the specified x delta will cause a collision on the left.
     *
     * @param deltaX the x delta
     * @return whether a collision would occur on the left
     */
    private boolean collidesLeft(float deltaX) {
        // If entity is smaller than tile we can just check to see if each corner collides instead of all points along the edge.
        if (getHeight() <= parent.getTileHeight()) {
            return parent.isTileBlocked((int) Math.floor(x + deltaX), (int) y) || parent.isTileBlocked((int) Math.floor(x + deltaX), (int) y + getHeight());
        } else {
            for (int i = (int) y; i < y + getHeight(); i++) {
                if (parent.isTileBlocked((int) Math.floor(x + deltaX), i)) {
                    return true;
                }
            }

            return false;
        }
    }

    /**
     * Gets whether the specified x delta will cause a collision on the right.
     *
     * @param deltaX the x delta
     * @return whether a collision would occur on the right
     */
    private boolean collidesRight(float deltaX) {
        // If entity is smaller than tile we can just check to see if each corner collides instead of all points along the edge.
        if (getHeight() <= parent.getTileHeight()) {
            return parent.isTileBlocked((int) Math.floor(x + getWidth() + deltaX), (int) y) || parent.isTileBlocked((int) Math.floor(x + getWidth() + deltaX), (int) y + getHeight());
        } else {
            for (int i = (int) y; i < y + getHeight(); i++) {
                if (parent.isTileBlocked((int) Math.ceil(x + getWidth() - 1 + deltaX), i)) {
                    return true;
                }
            }

            return false;
        }
    }

    /**
     * Gets whether the specified y delta will cause a collision on the bottom.
     *
     * @param deltaY the y delta
     * @return whether a collision would occur on the bottom
     */
    private boolean collidesBottom(float deltaY) {
        // If entity is smaller than tile we can just check to see if each corner collides instead of all points along the edge.
        if (getWidth() <= parent.getTileWidth()) {
            return parent.isTileBlocked((int) x, (int) Math.floor(y + deltaY)) || parent.isTileBlocked((int) x + getWidth(), (int) Math.floor(y + deltaY));
        } else {
            for (int i = (int) x; i < x + getWidth(); i++) {
                if (parent.isTileBlocked(i, (int) Math.floor(y + deltaY))) {
                    return true;
                }
            }

            return false;
        }
    }


    /**
     * Gets whether the specified y delta will cause a collision on the top.
     *
     * @param deltaY the y delta
     * @return whether a collision would occur on the top
     */
    private boolean collidesTop(float deltaY) {
        // If entity is smaller than tile we can just check to see if each corner collides instead of all points along the edge.
        if (getWidth() <= parent.getTileWidth()) {
            return parent.isTileBlocked((int) x, (int) Math.floor(y + getHeight() + deltaY)) || parent.isTileBlocked((int) x + getWidth(), (int) Math.floor(y + getHeight() + deltaY));
        } else {
            for (int i = (int) x; i < x + getWidth(); i++) {
                if (parent.isTileBlocked(i, (int) Math.ceil(y + getHeight() - 1 + deltaY))) {
                    return true;
                }
            }

            return false;
        }
    }

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
