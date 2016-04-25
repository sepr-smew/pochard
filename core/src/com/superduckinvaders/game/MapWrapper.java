package com.superduckinvaders.game;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.MathUtils;

/**
 * Created by james on 25/04/16.
 */
public class MapWrapper {

    private TiledMap map;
    private TiledMapTileLayer obstaclesLayer;

    public MapWrapper(TiledMap map) {
        this.map = map;
        obstaclesLayer = chooseObstacles();
    }

    /**
     * Gets the current map
     * @return this Round's map
     */
    public TiledMap getMap() {
        return map;
    }

    /**
     * Randomly selects and returns a set of predefined obstacles from the map.
     *
     * @return the map layer containing the obstacles
     */
    private TiledMapTileLayer chooseObstacles() {
        int count = 0;

        // First count how many obstacle layers we have.
        while (map.getLayers().get(String.format("Obstacles%d", count)) != null) {
            count++;
        }

        // Choose a random layer or return null if there are no layers.
        if (count == 0) {
            return null;
        } else {
            return (TiledMapTileLayer) map.getLayers().get(String.format("Obstacles%d", MathUtils.random(0, count - 1)));
        }
    }

    /**
     * Gets the base layer of the map
     * @return this Round's base layer (used for calculating map width/height)
     */
    public TiledMapTileLayer getBaseLayer() {
        return (TiledMapTileLayer) getMap().getLayers().get("Base");
    }

    /**
     * Gets the collision layer of the map
     * @return this Round's collision map layer
     */
    public TiledMapTileLayer getCollisionLayer() {
        return (TiledMapTileLayer) getMap().getLayers().get("Collision");
    }

    /**
     * Gets the obstacles layer of the map
     * @return this Round's obstacles map layer or null if there isn't one
     */
    public TiledMapTileLayer getObstaclesLayer() {
        return obstaclesLayer;
    }

    /**
     * gets the overhang layer of the map
     * @return this Round's overhang map layer (rendered over entities)
     */
    public TiledMapTileLayer getOverhangLayer() {
        return (TiledMapTileLayer) getMap().getLayers().get("Overhang");
    }

    /**
     * gets the water edge layer of the map
     * @return this Round's water edge map layer (rendered over entities)
     */
    public TiledMapTileLayer getWaterEdgeLayer() {
        return (TiledMapTileLayer) getMap().getLayers().get("WaterEdge");
    }

    /**
     * @return The layer for spawn positions
     */
    public TiledMapTileLayer getSpawnLayer(){
        return (TiledMapTileLayer) getMap().getLayers().get("Spawn");
    }

    /**
     * Gets the width of the map in pixels
     * @return the width of this Round's map in pixels
     */
    public int getMapWidth() {
        return (int) (getBaseLayer().getWidth() * getBaseLayer().getTileWidth());
    }

    /**
     * Gets the height of the map in pixels
     * @return the height of this Round's map in pixels
     */
    public int getMapHeight() {
        return (int) (getBaseLayer().getHeight() * getBaseLayer().getTileHeight());
    }

    /**
     * Gets the width of each tile
     * @return the width of one tile in this Round's map
     */
    public int getTileWidth() {
        return (int) getBaseLayer().getTileWidth();
    }

    /**
     * Gets the height of each tile
     * @return the height of one tile in this Round's map
     */
    public int getTileHeight() {
        return (int) getBaseLayer().getTileHeight();
    }
}
