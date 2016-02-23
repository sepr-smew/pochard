package com.superduckinvaders.game.entity.item;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.superduckinvaders.game.Round;
import com.superduckinvaders.game.assets.Assets;
import com.superduckinvaders.game.entity.Entity;
import com.superduckinvaders.game.entity.PhysicsEntity;

public class Item extends PhysicsEntity {

    /**
     * The texture for this Item.
     */
    protected TextureRegion texture;

    /**
     * The total time the item has existed for
     */
    private float runningTime;

    /**
     * Creates an item at a specified x, y position.
     * @param parent The current round.
     * @param x x-position of the item.
     * @param y y-position of the item.
     * @param texture
     */
    public Item(Round parent, float x, float y, TextureRegion texture) {
        super(parent, x, y);
        runningTime = 0;
        this.texture = texture;
        createStaticBody(ITEM_BITS, PLAYER_BITS, NO_GROUP, true);
    }

    /**
     * @return The width of the item
     */
    @Override
    public float getWidth() {
        return texture.getRegionWidth();
    }

    /**
     * @return The height of the item
     */
    @Override
    public float getHeight() {
        return texture.getRegionHeight();
    }

    /**
     * Updates the item, incrementing the running time
     * @param delta how much time has passed since the last update
     */
    @Override
    public void update(float delta) {
        // Don't do anything...yet.
        this.runningTime += delta;
    }

    /**
     * Render the item
     * @param spriteBatch the sprite batch on which to render
     */
    @Override
    public void render(SpriteBatch spriteBatch) {
        //System.out.println("RENDERING? " + runningTime);
        spriteBatch.draw(Assets.shadow, x-8, y-4);
        spriteBatch.draw(texture, getX(), getY() + (int) (MathUtils.sin(runningTime*2)*6) + 6);
    }

}
