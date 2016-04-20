package com.superduckinvaders.game.ui.text;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.superduckinvaders.game.assets.Assets;

/**
 * Created by james on 20/04/16.
 */
public class EphemeralText {
    String text;
    Color color;
    final float duration;
    float elapsed = 0;
    float x;
    float y;

    public EphemeralText(String text, Color color, float duration, float x, float y) {
        this.text = text;
        this.color = color;
        this.duration = duration;
        this.x = x;
        this.y = y;
    }

    public boolean isExpired(){
        return elapsed>=duration;
    }

    public void update(float delta){
        elapsed+=delta;
    }

    public void render(SpriteBatch spriteBatch){
        Assets.font.setColor(color);
        Assets.font.draw(spriteBatch, text, x, y);
    }
}
