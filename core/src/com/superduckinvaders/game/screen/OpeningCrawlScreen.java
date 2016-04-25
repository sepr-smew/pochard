package com.superduckinvaders.game.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.superduckinvaders.game.DuckGame;
import com.superduckinvaders.game.assets.Assets;

/**
 * Created by tp727 on 12/02/2016.
 */
public class OpeningCrawlScreen extends BaseScreen {
    // Assessment 4: now inherits from Scene2dScreen, removing stub methods



    private float totalTime;

    /**
     * The DuckGame this StartScreen belongs to.
     */
    private DuckGame parent;

    /**
     * Draws the opening crawl animation
     */
    private SpriteBatch batch;

    /**
     * Initialises this StartScreen.
     * @param parent the game the screen is associated with
     */
    public OpeningCrawlScreen(DuckGame parent) {
        super();

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
        Assets.font.draw(batch, "PRESS 'SPACE' TO SKIP.", 20, DuckGame.GAME_HEIGHT - 20);
        batch.end();
        if (Assets.openingCrawl.isAnimationFinished(totalTime) || Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            parent.showStartScreen();
        }
    }
}
