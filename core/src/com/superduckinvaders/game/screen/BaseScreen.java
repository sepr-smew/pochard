package com.superduckinvaders.game.screen;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.superduckinvaders.game.DuckGame;

/**
 * Created by james on 25/04/16.
 */
public abstract class BaseScreen extends ScreenAdapter {

    protected Viewport viewport;
    /**
     * The DuckGame this Screen belongs to.
     */
    protected DuckGame parent;

    /**
     * The sprite batches for rendering.
     */
    protected SpriteBatch spriteBatch;

    public BaseScreen(DuckGame parent) {
        viewport = new FitViewport(DuckGame.GAME_WIDTH, DuckGame.GAME_HEIGHT);
        this.parent = parent;
        this.spriteBatch = parent.spriteBatch;
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }
}
