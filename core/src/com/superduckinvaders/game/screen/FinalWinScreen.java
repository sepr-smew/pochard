package com.superduckinvaders.game.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.superduckinvaders.game.DuckGame;
import com.superduckinvaders.game.Round;
import com.superduckinvaders.game.assets.Assets;

/**
 * Screen for displaying when a player has won.
 */
public class FinalWinScreen extends Scene2dScreen {
    // Assessment 4: now inherits from Scene2dScreen, removing stub methods

    /**
     * The DuckGame this WinScreen belongs to.
     */
    private DuckGame parent;

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
    public FinalWinScreen(DuckGame parent, int score) {
        super();

        this.parent = parent;
        this.score = score;
    }

    /**
     * Shows this GameScreen. Called by libGDX to set up the graphics.
     */
    @Override
    public void show() {
        super.show();

        Drawable drawable = new TextureRegionDrawable(Assets.button);
        Button.ButtonStyle buttonStyle = new Button.ButtonStyle(drawable,drawable,drawable);
        buttonStyle.over = new TextureRegionDrawable(Assets.button_hover);

        Button backButton = new Button(buttonStyle);
        backButton.setPosition((stage.getWidth() - backButton.getPrefWidth()) / 2, 220);
        backButton.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
                parent.showGameScreen(new Round(parent, Assets.levels[0], Assets.levelMobs[0], false));
            }
        });

        Label.LabelStyle green = new Label.LabelStyle(Assets.font, Color.GREEN);
        Label.LabelStyle white = new Label.LabelStyle(Assets.font, Color.WHITE);

        Label titleLabel = new Label("You Beat The Game!", green);
        titleLabel.setPosition((stage.getWidth() - titleLabel.getPrefWidth()) / 2, 500);

        Label scoreLabel = new Label("Round Score: " + score, green);
        scoreLabel.setPosition((stage.getWidth() - scoreLabel.getPrefWidth()) / 2, 460);

        Label totalScoreLabel = new Label("Total Score: " + parent.getTotalScore() , green);
        totalScoreLabel.setPosition((stage.getWidth() - totalScoreLabel.getPrefWidth()) / 2, 420);

        Label backLabel = new Label("Play Again", white);
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
        stage.getBatch().begin();
        stage.getBatch().draw(Assets.bg, 0, 0);
        stage.getBatch().end();
        stage.act();
        stage.draw();
    }
}
