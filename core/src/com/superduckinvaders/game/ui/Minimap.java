package com.superduckinvaders.game.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.superduckinvaders.game.DuckGame;
import com.superduckinvaders.game.screen.GameScreen;
import com.superduckinvaders.game.assets.Assets;
import com.superduckinvaders.game.entity.mob.Mob;

/**
 * Assessment 4: Added to meet requirements
 */
public class Minimap {

    GameScreen gameScreen;

    OrthographicCamera camera;
    Viewport viewport;

    Vector2 posMin;
    Vector2 posMax;

    int x;
    int y;
    int width;
    int height;

    public Minimap(GameScreen gameScreen, int x, int y, int width, int height){
        this.gameScreen = gameScreen;

        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;

        camera = new OrthographicCamera();
        camera.zoom = 2f;

        // anonymous subclass of Viewport to allow instantiation.
        viewport = new Viewport(){};
        viewport.setCamera(camera);

        viewport.setWorldSize(DuckGame.GAME_HEIGHT / 2, DuckGame.GAME_HEIGHT / 2);

        posMin = new Vector2(viewport.getWorldWidth(),
                                       viewport.getWorldHeight());

        posMax = new Vector2(gameScreen.getRound().getMapWidth() - posMin.x,
                             gameScreen.getRound().getMapHeight() - posMin.y);
    }

    public void updatePosition(Vector2 position){
        camera.position.set(
                Math.max(posMin.x, Math.min(position.x, posMax.x)),
                Math.max(posMin.y, Math.min(position.y, posMax.y)),
                0
        );
        camera.update();
    }

    public void update(int width, int height, boolean center){
        viewport.update(width, height, center);
    }

    public void render(ShapeRenderer shapeRenderer, SpriteBatch spriteBatch){
        gameScreen.uiViewport.apply();
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        shapeRenderer.setProjectionMatrix(gameScreen.uiCamera.combined.cpy());
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0.3f, 0.3f, 0.3f, 0.5f);

        //Minimap underlay
        shapeRenderer.rect(x-3, y-3, width+6, height+6);

        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);

        Vector3 screenPos = gameScreen.uiViewport.project(new Vector3(x, y, 0));

        // strange maths to accommodate non-uniform projections.
        Vector3 screenSize = gameScreen.uiViewport.project(
                new Vector3(x+width, y+height, 0)
        ).sub(screenPos);

        viewport.setScreenBounds(Math.round(screenPos.x),
                Math.round(screenPos.y),
                Math.round(screenSize.x),
                Math.round(screenSize.y));
        viewport.apply();

        spriteBatch.begin();
        spriteBatch.setColor(1, 1, 1, 0.8f);

        gameScreen.mapRenderer.setView(camera);

        gameScreen.renderMapLower();
        gameScreen.renderMapOverhang();

        Vector2 playerPos = gameScreen.getRound().getPlayer().getPosition();
        int width = Assets.minimapHead.getRegionWidth()*4;
        int height = Assets.minimapHead.getRegionHeight()*4;

        spriteBatch.draw(Assets.minimapHead, playerPos.x-width/2, playerPos.y-height/2, width, height);

        spriteBatch.end();
        spriteBatch.setColor(Color.WHITE);

        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0.9f, 0.2f, 0.2f, 0.7f);

        for (Mob mob : gameScreen.mobs) {
                Vector2 pos = mob.getCentre();
                shapeRenderer.circle(pos.x, pos.y, 10f);
                shapeRenderer.x(pos.x, pos.y, 5f);
        }
        shapeRenderer.end();

    }
}
