package com.superduckinvaders.game.entity;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.superduckinvaders.game.Round;
import com.superduckinvaders.game.ai.AI;
import com.superduckinvaders.game.ai.BossAI;
import com.superduckinvaders.game.assets.Assets;
import com.superduckinvaders.game.assets.TextureSet;
import com.superduckinvaders.game.objective.BossObjective;

public class BossMob extends Mob {
    public BossMob(Round parent, float x, float y, int health, int speed, int score) {
        super(parent, x, y, health, speed, score, Assets.bossBadGuy, Assets.bossBadGuy, new BossAI(parent));
        disableCollision();
    }

    @Override
    protected void onDeath() {
        super.onDeath();
        ((BossObjective)parent.getObjective()).setCompleted();
    }

    @Override
    public void update(float delta) {
        super.update(delta);
    }

    @Override
    public void render(SpriteBatch spriteBatch) {
        super.render(spriteBatch);
        
        Vector2 pos = getPosition();
        
        spriteBatch.draw(Assets.bossShadow, pos.x-10, pos.y-20);
        spriteBatch.draw(walkingTextureSet.getTexture(facing, stateTime), pos.x, pos.y);
        
    }
}
