package com.superduckinvaders.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.superduckinvaders.game.assets.Assets;

/**
 * Created by tp727 on 12/02/2016.
 */
public class OpeningCrawlScreen implements Screen {



    private float totalTime;

    /**
     * The DuckGame this StartScreen belongs to.
     */
    private DuckGame parent;

    /**
     * Stage for containing the button.
     */
    private Stage stage;

    /**
     * Draws the opening crawl animation
     */
    private SpriteBatch batch;

    /**
     * Initialises this StartScreen.
     * @param parent the game the screen is associated with
     */
    public OpeningCrawlScreen(DuckGame parent) {
        this.parent = parent;
    }

    /**
     * Shows this GameScreen. Called by libGDX to set up the graphics.
     */
    @Override
    public void show() {
        totalTime = 0f;
        batch = new SpriteBatch();
    }

    /**
     * Main screen loop.
     *
     * @param delta how much time has passed since the last update
     */
    @Override
    public void render(float delta) {
        totalTime += delta;
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        batch.draw(Assets.openingCrawl.getKeyFrame(totalTime),0,0,1280,720);
        Assets.font.draw(batch, "PRESS 'SPACE' TO SKIP.", 20, Gdx.graphics.getHeight() - 20);
        batch.end();
        if (Assets.openingCrawl.isAnimationFinished(totalTime) || Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            parent.showStartScreen();
        }
    }

    /**
     * Not used since the game window cannot be resized.
     */
    @Override
    public void resize(int width, int height) {
    }

    /**
     * Not used.
     */
    @Override
    public void pause() {
    }

    /**
     * Not used.
     */
    @Override
    public void resume() {
    }

    /**
     * Not used.
     */
    @Override
    public void hide() {
    }

    /**
     * Called to dispose libGDX objects used by this StartScreen.
     */
    @Override
    public void dispose() {

    }
}
