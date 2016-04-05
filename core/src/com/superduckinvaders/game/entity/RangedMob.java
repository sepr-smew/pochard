package com.superduckinvaders.game.entity;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.superduckinvaders.game.Round;
import com.superduckinvaders.game.ai.AI;
import com.superduckinvaders.game.ai.RangedAI;
import com.superduckinvaders.game.assets.Assets;
import com.superduckinvaders.game.assets.TextureSet;

public class RangedMob extends Mob {

    public RangedMob(Round parent, float x, float y, int health, int speed, int score) {
        super(parent, x, y, health, speed, score, Assets.rangedBadGuy, Assets.rangedBadGuySwimming, new RangedAI(parent, 300, 300));
    }

    @Override
    public void update(float delta) {
        super.update(delta);
    }

    @Override
    public void render(SpriteBatch spriteBatch) {
        super.render(spriteBatch);
        
        Vector2 pos = getPosition();

        if(isOnWater()){
            spriteBatch.draw(Assets.shadow2, pos.x, pos.y+3);
            spriteBatch.draw(swimmingTextureSet.getTexture(facing, stateTime), pos.x, pos.y );
        }
        else{
            spriteBatch.draw(Assets.shadow2, pos.x, pos.y+3);
            spriteBatch.draw(walkingTextureSet.getTexture(facing, stateTime), pos.x, pos.y );
        }
    }
}
