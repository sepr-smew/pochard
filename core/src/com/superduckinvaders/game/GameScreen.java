package com.superduckinvaders.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.superduckinvaders.game.ai.PathfindingAI;
import com.superduckinvaders.game.assets.Assets;
import com.superduckinvaders.game.entity.Entity;
import com.superduckinvaders.game.entity.PhysicsEntity;
import com.superduckinvaders.game.entity.Player;
import com.superduckinvaders.game.entity.mob.BossMob;
import com.superduckinvaders.game.entity.mob.Mob;
import com.superduckinvaders.game.entity.mob.RangedMob;
import com.superduckinvaders.game.ui.Minimap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Screen for interaction with the game.
 */
public class GameScreen implements Screen {

    /**
     * The scale of the game pixels.
     */
    private static final float SCALE = 2;

    /**
     * The game camera.
     */
    private OrthographicCamera camera;
    public Viewport viewport;

    /**
     * The renderer for the tile map.
     */
    public OrthogonalTiledMapRenderer mapRenderer;

    /**
     * The sprite batches for rendering.
     */
    private SpriteBatch spriteBatch;

    /**
     * The Round this GameScreen renders.
     */
    private Round round;

    /**
     * How constrained the camera is to the player.
     */
    private final float PLAYER_CAMERA_BOUND = 8f;

    /**
     * A list of water cells in the map.
     */
    ArrayList<TiledMapTileLayer.Cell> waterCellsInScene;

    /**
     * A map of water tiles.
     */
    Map<String,TiledMapTile> waterTiles;

    /**
     * The time that has elapsed since the animation.
     */
    float elapsedSinceAnimation = 0.0f;

    /**
     * A timer for the current round.
     */
    float roundTimer = 0f;

    /**
     * The current level of the round.
     */
    private int level;

    Box2DDebugRenderer debugRenderer;
    Matrix4 debugMatrix;

    ShapeRenderer shapeRenderer;

    ShaderProgram shaderDistort;
    ShaderProgram shaderColor;

    FrameBuffer frameBuffer;
    FrameBuffer accFrameBuffer;

    Minimap minimap;

    float shaderTimer = 0f;




    /**
     * Initialises this GameScreen for the specified round.
     *
     * @param round the round to be displayed
     */
    public GameScreen(Round round, int level) {
        this.round = round;
        this.level = level;

        debugRenderer = new Box2DDebugRenderer();
        shapeRenderer = new ShapeRenderer();

        camera = new OrthographicCamera();
        viewport = new FitViewport(DuckGame.GAME_WIDTH, DuckGame.GAME_HEIGHT, camera);

        minimap = new Minimap(this, 10, 10, 200, 200);

        shaderDistort = new ShaderProgram(Gdx.files.internal("shaders/default.vsh"), Gdx.files.internal("shaders/distort.fsh"));
        if (!shaderDistort.isCompiled())
            System.out.print(shaderDistort.getLog());

        shaderColor = new ShaderProgram(Gdx.files.internal("shaders/default.vsh"), Gdx.files.internal("shaders/colour.fsh"));
        if (!shaderColor.isCompiled())
            System.out.print(shaderColor.getLog());

        spriteBatch = new SpriteBatch();

        initialiseFrameBuffer(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        camera = new OrthographicCamera(DuckGame.GAME_WIDTH/SCALE, DuckGame.GAME_HEIGHT/SCALE);
//        camera.zoom -= 0.5;


        mapRenderer = new OrthogonalTiledMapRenderer(round.getMap(), spriteBatch);

        // We created a second set of tiles for Water animations
        // For the record, this is bad for performance, use a single tileset if you can help it
        // Get a reference to the tileset named "Water"
        TiledMapTileSet tileset =  round.getMap().getTileSets().getTileSet("Tileset");


        // Now we are going to loop through all of the tiles in the Water tileset
        // and get any TiledMapTile with the property "WaterFrame" set
        // We then store it in a map with the frame as the key and the Tile as the value
        waterTiles = new HashMap<>();
        for(TiledMapTile tile:tileset){
            Object property = tile.getProperties().get("water");
            if(property != null)
                waterTiles.put((String)property,tile);
        }

        // Now we want to get a reference to every single cell ( Tile instance ) in the map
        // that refers to a water cell.  Loop through the entire world, checking if a cell's tile
        // contains the WaterFrame property.  If it does, add to the waterCellsInScene array
        // Note, this only pays attention to the very first layer of tiles.
        // If you want to support animation across multiple layers you will have to loop through each
        waterCellsInScene = new ArrayList<>();
        TiledMapTileLayer layer = (TiledMapTileLayer) round.getMap().getLayers().get(0);
        for(int x = 0; x < layer.getWidth();x++){
            for(int y = 0; y < layer.getHeight();y++){
                TiledMapTileLayer.Cell cell = layer.getCell(x,y);
                Object property = cell.getTile().getProperties().get("water");
                if(property != null){
                    waterCellsInScene.add(cell);
                }
            }
        }


    }

    /**
     * Converts screen coordinates to world coordinates.
     *
     * @param x the x coordinate on screen
     * @param y the y coordinate on screen
     * @return a Vector3 containing the world coordinates (x and y)
     */
    public Vector3 unproject(int x, int y) {
        return camera.unproject(new Vector3(x, y, 0));
    }

    /**
     * @return the Round currently on this GameScreen
     */
    public Round getRound() {
        return round;
    }

    /**
     * Shows this GameScreen. Called by libGDX to set up the graphics.
     */
    @Override
    public void show() {
        Gdx.input.setInputProcessor(null);
    }

    public void initialiseFrameBuffer(int screenWidth, int screenHeight){
        if(frameBuffer != null) frameBuffer.dispose();
        if(accFrameBuffer != null) accFrameBuffer.dispose();

        frameBuffer = new FrameBuffer(Pixmap.Format.RGB888, screenWidth, screenHeight, false);
        frameBuffer.getColorBufferTexture().setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

        accFrameBuffer = new FrameBuffer(Pixmap.Format.RGB888, screenWidth, screenHeight, false);
        accFrameBuffer.getColorBufferTexture().setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

//        if(fbBatch != null) fbBatch.dispose();
//        fbBatch = new SpriteBatch();
    }

    public void renderMapLower(){
        // Render base and collision layers.
        mapRenderer.renderTileLayer(round.getBaseLayer());
        mapRenderer.renderTileLayer(round.getCollisionLayer());
        mapRenderer.renderTileLayer(round.getWaterEdgeLayer());

        // Render randomly-chosen obstacles layer.
        if (round.getObstaclesLayer() != null) {
            mapRenderer.renderTileLayer(round.getObstaclesLayer());
        }

    }

    public void renderMapOverhang(){

        if (round.getOverhangLayer() != null) {
            mapRenderer.renderTileLayer(round.getOverhangLayer());
        }
    }


    /**
     * Main game loop.
     * @param delta how much time has passed since the last update
     */
    @Override
    public void render(float delta) {
        round.update(delta);
        shaderTimer += delta;

        minimap.updatePosition(round.getPlayer().getCentre());

        viewport.apply();

        shaderDistort.begin();
        shaderDistort.setUniformf("sinOmega", 5 + (float) (Math.sin(shaderTimer*2) * 2));
        shaderDistort.setUniformf("sinAlpha", (float) (shaderTimer % (Math.PI*2)));
        shaderDistort.setUniformf("magnitude", (float) (0.10f * Math.sin(shaderTimer * 3)));
        shaderDistort.setUniformf("factor", 1);
        shaderDistort.end();

        shaderColor.begin();
        shaderColor.setUniformf("colorDelta", (shaderTimer/6f) % 1f);
        shaderColor.setUniformf("factor", 1);
        shaderColor.end();

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Centre the camera on the player.
        updateCamera();

        frameBuffer.begin();

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        updateWaterAnimations(delta);

        debugMatrix = new Matrix4(camera.combined);
        debugMatrix.scale(PhysicsEntity.PIXELS_PER_METRE, PhysicsEntity.PIXELS_PER_METRE, 1f);


        List<Mob> mobs = new ArrayList<>();
        List<Mob> dementedMobs = new ArrayList<>();

        boolean isDemented = round.getPlayer().isDemented();
        float playerDementedFactor = round.getPlayer().getDementedFactor();

        // Draw all entities.
        spriteBatch.begin();

        spriteBatch.setShader(isDemented ? shaderColor : null);
        shaderColor.setUniformf("factor", playerDementedFactor);


        mapRenderer.setView(camera);
        renderMapLower();

        spriteBatch.setProjectionMatrix(camera.combined.cpy());

        for (Entity entity : round.getEntities()) {
            if (entity instanceof Mob) {
                Mob mob = (Mob)entity;
                mobs.add(mob);

                if (mob.isDemented()) {
                    dementedMobs.add(mob);
                    continue; // do not render demented mobs yet
                }
            }

            entity.render(spriteBatch);
        }

        spriteBatch.setShader(shaderColor);
        shaderColor.setUniformf("factor", 1);
        for (Mob mob : dementedMobs)
            mob.render(spriteBatch);

        spriteBatch.setShader(isDemented ? shaderColor : null);
        shaderColor.setUniformf("factor", playerDementedFactor);

        renderMapOverhang();


        spriteBatch.setProjectionMatrix(camera.combined.cpy().scl(0.5f));
        round.floatyNumbersManager.render(spriteBatch);

        //Render health bars above enemies
        for (Entity entity : round.getEntities()) {
            if (entity instanceof Mob) {
                Mob mob = (Mob) entity;
                float offsetX = mob.getX() * 2 - mob.getWidth() / 2;
                float offsetY = mob.getY() * 2 + mob.getHeight() * 2;


                // don't like but cba fixing - damn you pochard.
                if (mob instanceof BossMob) {
                    offsetX += 40;
                    offsetY += 15;
                } else if (mob instanceof RangedMob) {
                    offsetX -= 5;
                    offsetY += 30;
                } else {
                    offsetX -= 17;
                    offsetY += 10;
                }

                spriteBatch.draw(Assets.healthEmpty, offsetX, offsetY);
                Assets.healthFull.setRegionWidth((int) Math.max(0, ((float) mob.getCurrentHealth() / mob.getMaximumHealth()) * 100));
                spriteBatch.draw(Assets.healthFull, offsetX, offsetY);
            }
        }


        debugRenderer.render(round.world, debugMatrix);

        spriteBatch.end();

        //DEBUGGING

        shapeRenderer.setProjectionMatrix(camera.combined.cpy());
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

        for (Mob mob : mobs) {
            PathfindingAI.Coordinate c = ((PathfindingAI) mob.getAI()).target;
            List<PathfindingAI.SearchNode> l = ((PathfindingAI) mob.getAI()).path_DEBUG;
            shapeRenderer.setColor(1, 0, 1, 1);
            if (l != null) {
                for (PathfindingAI.SearchNode s : l) {
                    shapeRenderer.x(s.coord.vector(), 7);
                }
            }
            shapeRenderer.setColor(0, 1, 1, 1);
            if (c != null) {
                shapeRenderer.x(c.vector(), 10);
            }
        }

        shapeRenderer.end();
        // END

        frameBuffer.end();

        spriteBatch.begin();

        spriteBatch.setShader(isDemented ? shaderDistort : null);

        spriteBatch.setProjectionMatrix(new Matrix4().setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));

        spriteBatch.draw(frameBuffer.getColorBufferTexture(), 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), 0, 0, 1, 1);
        spriteBatch.flush();


        // motion blur
        if (isDemented) {
            shaderDistort.setUniformf("factor", playerDementedFactor);
            accFrameBuffer.begin();
            Color c = spriteBatch.getColor();
            spriteBatch.setColor(1f, 1f, 1f, 0.15f);
            spriteBatch.draw(frameBuffer.getColorBufferTexture(), 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), 0, 0, 1, 1);
            spriteBatch.flush();
            accFrameBuffer.end();
            spriteBatch.setColor(c);

            spriteBatch.setShader(null);
            spriteBatch.draw(accFrameBuffer.getColorBufferTexture(), 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), 0, 0, 1, 1);
            spriteBatch.flush();
        }
//

        // TODO: finish UI
        Assets.font.setColor(0f, 0f, 0f, 1.0f);
        Assets.font.draw(spriteBatch, "Objective: " + round.getObjective().getObjectiveString(), 10, 708);
        Assets.font.draw(spriteBatch, "Score: " + round.getPlayer().getScore(), 10, 678);
        Assets.font.draw(spriteBatch, Gdx.graphics.getFramesPerSecond() + " FPS", Gdx.graphics.getWidth()-10, Gdx.graphics.getHeight()-12, 0, Align.right, false);

        Assets.font.setColor(1.0f, 1.0f, 1.0f, 1.0f);
        Assets.font.draw(spriteBatch, "Objective: " + round.getObjective().getObjectiveString(), 10, 710);
        Assets.font.draw(spriteBatch, "Score: " + round.getPlayer().getScore(), 10, 680);
        Assets.font.draw(spriteBatch, Gdx.graphics.getFramesPerSecond() + " FPS", Gdx.graphics.getWidth()-10, Gdx.graphics.getHeight()-10, 0, Align.right, false);

        // Draw stamina bar (for flight);
		spriteBatch.draw(Assets.staminaEmpty, 1080, 10);
        if (round.getPlayer().getFlyingTimer() > 0) {
            Assets.staminaFull.setRegionWidth((int) Math.max(0, Math.min(192, round.getPlayer().getFlyingTimer() / Player.PLAYER_MAX_FLIGHT_TIME * 192)));
        } else {
            Assets.staminaFull.setRegionWidth(0);
        }
		spriteBatch.draw(Assets.staminaFull, 1080, 10);

        // Draw powerup bar.
        round.powerUpManager.render(spriteBatch);

        //Draw health.
        int x = 0;
        while(x < round.getPlayer().getMaximumHealth()) {
        	if(x+2 <= round.getPlayer().getCurrentHealth())
        		spriteBatch.draw(Assets.heartFull, x * 18 + (Gdx.graphics.getWidth()/2 - 50), 10);
        	else if(x+1 <= round.getPlayer().getCurrentHealth())
        		spriteBatch.draw(Assets.heartHalf, x * 18 + (Gdx.graphics.getWidth()/2 - 50), 10);
        	else
        		spriteBatch.draw(Assets.heartEmpty, x * 18 + (Gdx.graphics.getWidth()/2 - 50), 10);
        	x += 2;
        }

        // Draw round text at start of round.
        if (roundTimer < 3f) {
            roundTimer += delta;
            spriteBatch.draw(Assets.roundText, (Gdx.graphics.getWidth() - Assets.roundText.getWidth() - Assets.roundNums[level].getWidth())/2,
                    (Gdx.graphics.getHeight() - Assets.roundText.getHeight())/2);
            spriteBatch.draw(Assets.roundNums[level], (Gdx.graphics.getWidth() + Assets.roundText.getWidth())/2,
                    (Gdx.graphics.getHeight() - Assets.roundText.getHeight())/2);
        }

        spriteBatch.end();

        minimap.render(shapeRenderer, spriteBatch);

        viewport.apply();
    }

    /**
     * Called every half a second to update animated water tiles.
     */
    private void updateWaterAnimations(float delta){
        // Wait for half a second to elapse then call updateWaterAnimations
        // This could certainly be handled using an Action if you are using Scene2D
        elapsedSinceAnimation += delta;
        if(elapsedSinceAnimation > 0.5f){
            for(TiledMapTileLayer.Cell cell : waterCellsInScene){
                String property = (String) cell.getTile().getProperties().get("water");
                Integer currentAnimationFrame = Integer.parseInt(property);

                currentAnimationFrame++;
                if(currentAnimationFrame > waterTiles.size())
                    currentAnimationFrame = 1;

                TiledMapTile newTile = waterTiles.get(currentAnimationFrame.toString());
                cell.setTile(newTile);
            }
            elapsedSinceAnimation = 0.0f;
        }
    }


    /**
     * Updates the camera to be constrained to the player and to stay within the map.
     */
    private void updateCamera() {

        Player player = round.getPlayer();

        Vector2 playerPos = player.getPosition();
        Vector2 playerSize = player.getSize();

//      Constrain camera to player
        if ((player.getX() + player.getWidth() > camera.position.x + camera.viewportWidth / PLAYER_CAMERA_BOUND))
            camera.position.x = ((player.getX() + player.getWidth())) - (camera.viewportWidth / PLAYER_CAMERA_BOUND);
        if ((player.getX() < camera.position.x - camera.viewportWidth / PLAYER_CAMERA_BOUND))
            camera.position.x = (player.getX()) + (camera.viewportWidth / PLAYER_CAMERA_BOUND);


        if ((player.getY() + player.getHeight() > camera.position.y + camera.viewportHeight / PLAYER_CAMERA_BOUND))
            camera.position.y = ((player.getY() + player.getHeight())) - (camera.viewportHeight / PLAYER_CAMERA_BOUND);
        if ((player.getY() < camera.position.y - camera.viewportHeight / PLAYER_CAMERA_BOUND))
            camera.position.y = (player.getY()) + (camera.viewportHeight / PLAYER_CAMERA_BOUND);

//      Constrain camera to map
        if (camera.position.x + camera.viewportWidth / 2f > round.getMapWidth())
            camera.position.x = round.getMapWidth() - camera.viewportWidth / 2f;
        if (camera.position.x < camera.viewportWidth / 2f)
            camera.position.x = camera.viewportWidth / 2f;
        if (camera.position.y + camera.viewportHeight / 2f > round.getMapHeight())
            camera.position.y = round.getMapHeight() - camera.viewportHeight / 2f;
        if (camera.position.y < camera.viewportHeight / 2f)
            camera.position.y = camera.viewportHeight / 2f;

        camera.update();
    }

    /**
     * Not used since the game window cannot be resized.
     * Actually, this is still called initially and is useful
     * for setting the screen size
     */
    @Override
    public void resize(int width, int height) {
        initialiseFrameBuffer(width, height);
        viewport.update(width, height, true);
        minimap.update(width, height, false);
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
        mapRenderer.dispose();
        spriteBatch.dispose();
        shaderColor.dispose();
        shaderDistort.dispose();
    }

}
