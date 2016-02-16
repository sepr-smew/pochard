package com.superduckinvaders.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.superduckinvaders.game.assets.Assets;

/**
 * Screen for displaying when a player has won.
 */
public class WinScreen implements Screen {

    /**
     * The DuckGame this WinScreen belongs to.
     */
    private DuckGame parent;

    /**
     * The sprite batch for rendering.
     */
    private SpriteBatch uiBatch;

    /**
     * Stage for containing the button.
     */
    private Stage stage;

    /**
     * The final score to display on the WinScreen.
     */
    private int score;

    /**
     * Initialises this WinScreen to display the final score.
     *
     * @param parent the game the screen is associated with
     * @param score the final score to display
     */
    public WinScreen(DuckGame parent, int score) {
        this.parent = parent;
        this.score = score;
    }

    /**
     * Shows this GameScreen. Called by libGDX to set up the graphics.
     */
    @Override
    public void show() {
        Gdx.input.setCursorCatched(false);

        uiBatch = new SpriteBatch();

        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        Drawable drawable = new TextureRegionDrawable(Assets.button);
        Button.ButtonStyle buttonStyle = new Button.ButtonStyle(drawable,drawable,drawable);
        buttonStyle.over = new TextureRegionDrawable(Assets.button_hover);

        Button backButton = new Button(buttonStyle);
        backButton.setPosition((stage.getWidth() - backButton.getPrefWidth()) / 2, 220);
        backButton.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(parent.level==8)
                    parent.showGameScreen(new Round(parent, Assets.levels[parent.level-1], Assets.levelMobs[parent.level-1], true));
                else
                    parent.showGameScreen(new Round(parent, Assets.levels[parent.level-1], Assets.levelMobs[parent.level-1], false));
            }
        });

        Label.LabelStyle green = new Label.LabelStyle(Assets.font, Color.GREEN);
        Label.LabelStyle white = new Label.LabelStyle(Assets.font, Color.WHITE);

        Label titleLabel = new Label("You Beat The Round!", green);
        titleLabel.setPosition((stage.getWidth() - titleLabel.getPrefWidth()) / 2, 500);

        Label scoreLabel = new Label("Round Score: " + score, green);
        scoreLabel.setPosition((stage.getWidth() - scoreLabel.getPrefWidth()) / 2, 460);

        Label totalScoreLabel = new Label("Total Score: " + parent.getTotalScore() , green);
        totalScoreLabel.setPosition((stage.getWidth() - totalScoreLabel.getPrefWidth()) / 2, 420);

        Label backLabel = new Label("Next Round", white);
        backLabel.setPosition((stage.getWidth() - backLabel.getPrefWidth()) / 2, 235);
        backLabel.setTouchable(Touchable.disabled);

        stage.addActor(backButton);
        stage.addActor(titleLabel);
        stage.addActor(scoreLabel);
        stage.addActor(backLabel);
        stage.addActor(totalScoreLabel);
    }

    /**
     * Main screen loop.
     *
     * @param delta how much time has passed since the last update
     */
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.getBatch().begin();
        stage.getBatch().draw(Assets.bg, 0, 0);
        stage.getBatch().end();
        stage.act();
        stage.draw();
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
     * Called to dispose libGDX objects used by this GameScreen.
     */
    @Override
    public void dispose() {
    }
}
