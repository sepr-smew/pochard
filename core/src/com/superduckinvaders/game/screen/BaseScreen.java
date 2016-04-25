package com.superduckinvaders.game.screen;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.superduckinvaders.game.DuckGame;

/**
 * Created by james on 25/04/16.
 */
public abstract class BaseScreen extends ScreenAdapter {

    protected Viewport viewport;

    public BaseScreen() {
        viewport = new FitViewport(DuckGame.GAME_WIDTH, DuckGame.GAME_HEIGHT);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }
}
