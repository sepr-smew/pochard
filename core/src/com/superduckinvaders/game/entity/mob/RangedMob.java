package com.superduckinvaders.game.entity.mob;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.superduckinvaders.game.Round;
import com.superduckinvaders.game.ai.RangedAI;
import com.superduckinvaders.game.assets.Assets;

public class RangedMob extends Mob {

    public RangedMob(Round parent, float x, float y, int health, int speed, int score) {
        super(parent, x, y, health, speed, score, Assets.rangedBadGuy, Assets.rangedBadGuySwimming, Assets.playerShadow, new RangedAI(parent, 300, 300));
    }

    // Did do more than this, but by making render and update more generic this became less useful
}
