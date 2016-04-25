package com.superduckinvaders.game.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.superduckinvaders.game.DuckGame;
import com.superduckinvaders.game.screen.GameScreen;
import com.superduckinvaders.game.assets.Assets;
import com.superduckinvaders.game.entity.mob.Mob;

/**
 * Created by james on 19/04/16.
 */
public class Minimap {

    static float UPDATE_FOW_INTERVAL = 1/25f;

    float FOWCounter = 0;

    GameScreen gameScreen;

    OrthographicCamera camera;
    Viewport viewport;

    Vector2 posMin;
    Vector2 posMax;

    Vector2 position;

    int x;
    int y;
    int width;
    int height;

    int mapWidth;
    int mapHeight;
    int maskScale = 8;
    
    int maskWidth;
    int maskHeight;

    FrameBuffer maskBuffer;

    FrameBuffer mapBuffer;



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

        mapWidth = gameScreen.getRound().mapWrapper.getMapWidth();
        mapHeight = gameScreen.getRound().mapWrapper.getMapHeight();
        
        maskWidth = mapWidth / maskScale;
        maskHeight = mapHeight / maskScale;


        posMax = new Vector2(mapWidth - posMin.x,
                             mapHeight - posMin.y);

        mapBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, width, height, false);
    }

    public void initialise(SpriteBatch spriteBatch){
        maskBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, maskWidth, maskHeight, false);
        Pixmap pixmap = new Pixmap(maskWidth, maskHeight, Pixmap.Format.RGBA8888);
        pixmap.setColor(1, 1, 1, 1);
        pixmap.fill();

        maskBuffer.begin();
        spriteBatch.begin();
        spriteBatch.setProjectionMatrix(new Matrix4().setToOrtho2D(0, 0, maskBuffer.getWidth(), maskBuffer.getHeight()));
        spriteBatch.draw(new Texture(pixmap), 0, 0, maskWidth, maskHeight);
        spriteBatch.end();
        maskBuffer.end();
        pixmap.dispose();
    }

    public void updatePosition(Vector2 position){
        this.position = position;
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

    public void render(float delta, ShapeRenderer shapeRenderer, SpriteBatch spriteBatch){
        FOWCounter += delta;
        while (FOWCounter>UPDATE_FOW_INTERVAL) {
            FOWCounter -= UPDATE_FOW_INTERVAL;
            int rWidth = Assets.minimapRadius.getWidth();
            int rHeight = Assets.minimapRadius.getHeight();

            spriteBatch.setProjectionMatrix(new Matrix4().setToOrtho2D(0, 0, maskBuffer.getWidth(), maskBuffer.getHeight()));
            spriteBatch.setBlendFunction(GL20.GL_DST_COLOR, GL20.GL_ZERO);

            maskBuffer.begin();
            spriteBatch.begin();
            spriteBatch.draw(Assets.minimapRadius, (int) position.x / maskScale - rWidth / 2, (int) position.y / maskScale - rHeight / 2, rWidth, rHeight, 0, 0, 1, 1);
            spriteBatch.end();
            maskBuffer.end();

            spriteBatch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        }


        gameScreen.uiViewport.apply();

        mapBuffer.begin();
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        shapeRenderer.setProjectionMatrix(new Matrix4().setToOrtho2D(0, 0, width, height));
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0.4f, 0.4f, 0.4f, 1f);

        //Minimap underlay
        shapeRenderer.rect(0, 0, width, height);

        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);


        viewport.setScreenBounds(3,
                3,
                width-6,
                height-6);
        viewport.apply();

        spriteBatch.begin();

        gameScreen.mapRenderer.setView(camera);

        gameScreen.renderMapLower();
        gameScreen.renderMapOverhang();

        Vector2 playerPos = gameScreen.getRound().getPlayer().getPosition();
        int playerWidth = Assets.minimapHead.getRegionWidth()*4;
        int playerHeight = Assets.minimapHead.getRegionHeight()*4;

        spriteBatch.setColor(Color.WHITE);
        spriteBatch.draw(Assets.minimapHead, playerPos.x-playerWidth/2, playerPos.y-playerHeight/2, playerWidth, playerHeight);
        spriteBatch.end();


        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0.9f, 0.2f, 0.2f, 0.8f);

        for (Mob mob : gameScreen.mobs) {
                Vector2 pos = mob.getCentre();
                shapeRenderer.circle(pos.x, pos.y, 10f);
                shapeRenderer.x(pos.x, pos.y, 5f);
        }
        shapeRenderer.end();

        spriteBatch.begin();
        spriteBatch.draw(maskBuffer.getColorBufferTexture(), 0, 0, mapWidth, mapHeight, 0, 0, 1, 1);
        spriteBatch.flush();

        mapBuffer.end();

        Vector3 screenPos = gameScreen.uiViewport.project(new Vector3(x, y, 0));

        // strange maths to accommodate non-uniform projections.
        Vector3 screenSize = gameScreen.uiViewport.project(
                new Vector3(x+playerWidth, y+playerHeight, 0)
        ).sub(screenPos);

        spriteBatch.setProjectionMatrix(gameScreen.uiCamera.combined.cpy());

        spriteBatch.setColor(1, 1, 1, 0.8f);
        spriteBatch.draw(mapBuffer.getColorBufferTexture(), x, y, width, height, 0, 0, 1, 1);
        spriteBatch.end();
        spriteBatch.setColor(Color.WHITE);

    }
}
