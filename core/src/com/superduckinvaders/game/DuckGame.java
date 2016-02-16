package com.superduckinvaders.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.superduckinvaders.game.assets.Assets;
import com.superduckinvaders.game.entity.Player;

/**
 * The Website for the documentation: http://www.teampochard.co.uk/game-releases/
 */
public class DuckGame extends Game {
	
    /**
     * The width of the game window.
     */
    public static final int GAME_WIDTH = 1280;
    /**
     * The height of the game window.
     */
    public static final int GAME_HEIGHT = 720;
    /**
     * stores whether the game is in a main game state
     */
    public boolean onGameScreen = false;
    /**
     * Stores the screen displayed before the start screen
     */
    private OpeningCrawlScreen openingCrawlScreen = null;
    /**
     * Stores the Screen displayed at the start of the game
     */
    private StartScreen startScreen = null;
    /**
     * Stores the Screen displayed when a level has begun
     */
    private GameScreen gameScreen = null;
    /**
     * Stores the Screen displayed when a level has been won
     */
    private WinScreen winScreen = null;

    /**
     * Stores the Screen displayed when all rounds have been completed.
     */
    private FinalWinScreen finalWinScreen;
    /**
     * Stores the Screen displayed when the player has lost the level
     */
    private LoseScreen loseScreen = null;

    /**
     * Stores the total score the player has accrued so far
     */
    private int totalScore;

    /**
     * Current level of the game, starts at level 1.
     */
    public int level = 1;


    /**
     * Initialises the startScreen. Called by libGDX to set up the graphics.
     */
    @Override
    public void create() {
        Assets.load();
        Gdx.graphics.setCursor(Gdx.graphics.newCursor(Assets.cursor, 8, 8));
        totalScore = 0;
        showOpeningCrawlScreen();
        //showStartScreen();
    }

    /**
     * Initialises the opening crawl screen and sets the current screen to it.
     */
    public void showOpeningCrawlScreen(){
        if (openingCrawlScreen != null) {
            openingCrawlScreen.dispose();
        }

        setScreen(openingCrawlScreen = new OpeningCrawlScreen(this));
        Assets.title.setVolume(0.1f);
        Assets.title.play();

    }

    /**
     * Sets the current screen to the startScreen.
     */
    public void showStartScreen() {
        if (startScreen != null) {
            startScreen.dispose();
        }

        setScreen(startScreen = new StartScreen(this));
    }

    /**
     * Sets the current screen to the gameScreen.
     * @param round The round to be displayed on the game screen
     */
    public void showGameScreen(Round round) {
        Assets.title.stop();
        Assets.title.dispose();
        Assets.main.setVolume(0.05f);
        Assets.main.play();
        if (gameScreen != null) {
            gameScreen.dispose();
        }
        onGameScreen = true;
        setScreen(gameScreen = new GameScreen(round, level));
    }

    /**
     * Sets the current screen to the winScreen.
     * @param score The final score the player had, to be displayed on the win screen
     */
    public void showWinScreen(int score) {
        if (winScreen != null) {
            winScreen.dispose();
        }
        if (finalWinScreen != null) {
            finalWinScreen.dispose();
        }

        totalScore += score;
        level++;
        if (level > 8) {
            level = 1;
            totalScore = 0;
            setScreen(finalWinScreen = new FinalWinScreen(this, score));
        } else {
            setScreen(winScreen = new WinScreen(this, score));
        }
    }

    /**
     * Sets the current screen to the loseScreen.
     */
    public void showLoseScreen() {
        if (loseScreen != null) {
            loseScreen.dispose();
        }

        setScreen(loseScreen = new LoseScreen(this));
    }
    
    /**
     * Returns the current round being displayed by the gameScreen
     *
     * @return Round being displayed by the GameScreen
     */
    public Round getRound(){
        return gameScreen.getRound();
    }
    
    /**
     * Called by libGDX to set up the graphics.
     */
    @Override
    public void render() {
        super.render();
    }

    /**
     * Returns the current GameScreen being displayed
     *
     * @return GameScreen being displayed
     */
    public GameScreen getGameScreen() {
        return gameScreen;
    }

    public int getTotalScore() {
        return totalScore;
    }
}
