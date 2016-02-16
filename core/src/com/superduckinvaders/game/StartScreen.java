package com.superduckinvaders.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
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

public class StartScreen implements Screen {

    /**
     * The DuckGame this StartScreen belongs to.
     */
    private DuckGame parent;

    /**
     * Stage for containing the button.
     */
    private Stage stage;

    /**
     * Initialises this StartScreen.
     * @param parent the game the screen is associated with
     */
    public StartScreen(DuckGame parent) {
        this.parent = parent;
    }

    /**
     * Shows this GameScreen. Called by libGDX to set up the graphics.
     */
    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        Image logoImage = new Image(Assets.logo);
        logoImage.setPosition((stage.getWidth() - logoImage.getPrefWidth()) / 2, 400);

        Drawable button = new TextureRegionDrawable(Assets.button);

        Button.ButtonStyle buttonStyle = new Button.ButtonStyle(button,button,button);
        buttonStyle.over = new TextureRegionDrawable(Assets.button_hover);

        Button playButton = new Button(buttonStyle);

        playButton.setPosition((stage.getWidth() - playButton.getPrefWidth()) / 2, 300);
        playButton.addListener(new ClickListener() {

            public void clicked(InputEvent event, float x, float y) {
                parent.showGameScreen(new Round(parent, Assets.levels[0], Assets.levelMobs[0], false));
            }
        });


        Label.LabelStyle white = new Label.LabelStyle(Assets.font, Color.WHITE);
        Label.LabelStyle black = new Label.LabelStyle(Assets.font, Color.BLACK);

        Label playLabel = new Label("Start", black);
        playLabel.setPosition((stage.getWidth() - playLabel.getPrefWidth()) / 2, 315);
        playLabel.setTouchable(Touchable.disabled);

        stage.addActor(logoImage);
        stage.addActor(playButton);
        stage.addActor(playLabel);


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
        stage.getBatch().draw(Assets.bg, 0,0);
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
     * Called to dispose libGDX objects used by this StartScreen.
     */
    @Override
    public void dispose() {
    }
}
