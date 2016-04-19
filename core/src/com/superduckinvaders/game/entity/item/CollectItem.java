package com.superduckinvaders.game.entity.item;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Contact;
import com.superduckinvaders.game.Round;
import com.superduckinvaders.game.assets.Assets;
import com.superduckinvaders.game.entity.PhysicsEntity;
import com.superduckinvaders.game.entity.Player;

/**
 * An item that tracks whether a player has touched it.
 * Useful for CollectObjective, which is completed when the Player touches a flag.
 */
public class CollectItem extends Item {

    /**
     * Create the CollectItem
     * @param parent the parent round.
     * @param x the x position to spawn in.
     * @param y the y position to spawn in.
     */
    public CollectItem(Round parent, float x, float y, TextureRegion texture) {
        super(parent, x, y, texture);
    }

    @Override
    public void beginSensorContact(PhysicsEntity other, Contact contact){
        if (other instanceof Player) {
            removed = true;
        }
    }
}

