package com.superduckinvaders.game.entity.item;

import com.superduckinvaders.game.Round;
import com.superduckinvaders.game.entity.Player;

public class Upgrade extends Item {

    /**
     * The upgrade that this Upgrade gives to the player.
     */
    private Player.Upgrade upgrade;

    /**
     *
     * @param parent the round in which this upgrade resides
     * @param x The x location of this upgrade
     * @param y The y location of this upgrade
     * @param upgrade The type of the upgrade
     */
    public Upgrade(Round parent, int x, int y, Player.Upgrade upgrade) {
        super(parent, x, y, Player.Upgrade.getTextureForUpgrade(upgrade));

        this.upgrade = upgrade;
    }

    /**
     * Update the upgrade, check for being picked up
     * @param delta how much time has passed since the last update
     */
    @Override
    public void update(float delta) {
        super.update(delta);
        Player player = parent.getPlayer();

        if (this.intersects(player.getX(), player.getY(), player.getWidth(), player.getHeight())) {
            player.setUpgrade(upgrade);
            removed = true;
        }
    }
}
