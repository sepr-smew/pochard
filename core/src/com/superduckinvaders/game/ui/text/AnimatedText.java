package com.superduckinvaders.game.ui.text;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

import java.util.List;

/**
 * Created by james on 20/04/16.
 */
public class AnimatedText extends EphemeralText {
    Color startColor;
    Color endColor;
    Interpolator xInterp;
    Interpolator yInterp;
    public AnimatedText(String text, Color startColor, Color endColor, float duration, float[] xs, float[] ys) {
        super(text, startColor, duration, xs[0], ys[0]);
        this.startColor = startColor;
        this.endColor = endColor;
        xInterp = new Interpolator(xs);
        yInterp = new Interpolator(ys);

    }

    @Override
    public void update(float delta) {
        super.update(delta);
        float fraction = elapsed/duration;
        color = startColor.lerp(endColor, fraction);
        x = xInterp.interpolate(fraction);
        y = yInterp.interpolate(fraction);



    }

    protected class Interpolator {
        float[] values;
        public Interpolator(float[] values) {
            this.values = values;
        }

        public float interpolate(float t){
            t = Math.max(0f, Math.min(1f, t));
            int index = (int)Math.floor(t*(values.length-1));
            if (index+1<values.length) {
                float start = values[index];
                float end = values[index+1];
                float fraction = (t - (index/(float)values.length))*values.length;
                return start + (end-start)*fraction;
            }
            else
                return values[values.length-1];
        }

    }
}
