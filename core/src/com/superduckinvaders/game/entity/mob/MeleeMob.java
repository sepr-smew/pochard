package com.superduckinvaders.game.entity.mob;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.superduckinvaders.game.Round;
import com.superduckinvaders.game.ai.ZombieAI;
import com.superduckinvaders.game.assets.Assets;

public class MeleeMob extends Mob {
    public MeleeMob(Round parent, float x, float y, int health, int speed, int score) {
        super(parent, x, y, health, speed, score, Assets.badGuyNormal, Assets.badGuySwimming, Assets.mobShadow, new ZombieAI(parent, 40));
    }

    // Did do more than this, but by making render and update more generic this became less useful
}
