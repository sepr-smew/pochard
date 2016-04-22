package com.superduckinvaders.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.World;
import com.superduckinvaders.game.assets.Assets;
import com.superduckinvaders.game.entity.*;
import com.superduckinvaders.game.entity.item.*;
import com.superduckinvaders.game.entity.mob.*;
import com.superduckinvaders.game.objective.BossObjective;
import com.superduckinvaders.game.objective.CollectObjective;
import com.superduckinvaders.game.objective.KillObjective;
import com.superduckinvaders.game.objective.Objective;
import com.superduckinvaders.game.ui.FloatyNumbersManager;
import com.superduckinvaders.game.util.KeySequenceListener;
import com.superduckinvaders.game.util.Collision;
import com.superduckinvaders.game.util.CustomContactListener;
import com.superduckinvaders.game.util.RayCast;

import java.util.*;

/**
 * Represents a round of the game played on one level with a single objective.
 */
public class Round {

    private Constructor createObstacle = new Constructor() {
        @Override
        public Entity construct(Round round, TiledMapTileLayer.Cell cell, float x, float y, float w, float h){
            return new Obstacle(round, x, y, w, h);
        }
    };


    private Constructor createWater = new Constructor() {
        @Override
        public Entity construct(Round round, TiledMapTileLayer.Cell cell, float x, float y, float w, float h){
            if (cell.getTile().getProperties().get("water") != null) {
                return new WaterEntity(round, x, y, w, h);
            }
            else {
                return null;
            }
        }
    };

    /**
     * How near entities must be to the player to get updated in the game loop.
     */
    public static final int UPDATE_DISTANCE_X = DuckGame.GAME_WIDTH*2;
    /**
     * How near entities must be to the player to get updated in the game loop.
     */
    public static final int UPDATE_DISTANCE_Y = DuckGame.GAME_HEIGHT*2;

    /**
     * The GameTest instance this Round belongs to.
     */
    private DuckGame parent;

    /**
     * The Round's map.
     */
    private TiledMap map;

    /**
     * Map layer containing randomly-chosen layer of predefined obstacles.
     */
    private TiledMapTileLayer obstaclesLayer;
    private TiledMapTileLayer collisionLayer;

    /**
     * The player.
     */
    private Player player;

    /**
     * Array of all entities currently in the Round.
     */
    private List<Entity> entities;
    private List<Entity> newEntities;

    private Comparator<Entity> entityComparator = new Entity.EntityComparator();

    /**
     * The current objective.
     */
    private Objective objective;

    /**
     * The manager for the player's powerups.
     */
    public PowerupManager powerUpManager = new PowerupManager();

    /**
     * The manager for floaty numbers on the map.
     */
    public FloatyNumbersManager floatyNumbersManager = new FloatyNumbersManager();

    public World world;

    /**
     * Whether the super-damage cheat is enabled.
     */
    public boolean cheatSuperDamage = false;

    /**
     * Whether the infinite fire cheat is enabled.
     */
    public boolean cheatInfiniteFire = false;

    /**
     * Initialises a new Round with the specified map.
     *
     * @param parent the game the round is associated with
     * @param map the Round's map
     * @param mobCount the number of random mobs to spawn.
     */
    public Round(DuckGame parent, TiledMap map, int mobCount, boolean isBoss) {
        this.parent = parent;
        this.map = map;


        world = new World(Vector2.Zero.cpy(), true);
        world.setContactListener(new CustomContactListener());

        // Choose which obstacles to use.
        obstaclesLayer = chooseObstacles();
        collisionLayer = getCollisionLayer();

        createEnvironmentBodies();

        // Determine starting coordinates for player (0, 0 default).
        int startX = Integer.parseInt(map.getProperties().get("StartX", "0", String.class)) * getTileWidth();
        int startY = Integer.parseInt(map.getProperties().get("StartY", "0", String.class)) * getTileHeight();

        player = new Player(this, startX, startY);

        entities = new ArrayList<Entity>();
        newEntities = new ArrayList<Entity>();
        addEntity(player);

        spawnRandomMobs(mobCount, 0, 0, getMapWidth(), getMapHeight());

        //Set the objective to the boss objective if is a boss round, also spawn boss
        if(isBoss){
            addEntity(new BossMob(this, 50*16, 50*16, 2500, 100, 5000));
            setObjective(new BossObjective(this, Objective.objectiveType.BOSS));
        }
        else {

            //Decide on objective
            int objectiveRandom = MathUtils.random(0, 1);
            switch (Objective.objectiveType.values()[objectiveRandom]) {
                case COLLECT: {
                    // Determine where to spawn the objective.
                    int objectiveX = Integer.parseInt(map.getProperties().get("ObjectiveX", "10", String.class)) * getTileWidth();
                    int objectiveY = Integer.parseInt(map.getProperties().get("ObjectiveY", "10", String.class)) * getTileHeight();

                    Item objective = new CollectItem(this, objectiveX, objectiveY, Assets.flag);
                    setObjective(new CollectObjective(this, Objective.objectiveType.COLLECT, objective));

                    addEntity(objective);
                    break;
                }
                case KILL: {
                    setObjective(new KillObjective(this, Objective.objectiveType.KILL, MathUtils.random(mobCount / 2, mobCount)));
                }
            }
        }
    }

    /**
     * Randomly selects and returns a set of predefined obstacles from the map.
     *
     * @return the map layer containing the obstacles
     */
    private TiledMapTileLayer chooseObstacles() {
        int count = 0;

        // First count how many obstacle layers we have.
        while (map.getLayers().get("Obstacles"+count) != null) {
            count++;
        }

        // Choose a random layer or return null if there are no layers.
        if (count == 0) {
            return null;
        } else {
            return (TiledMapTileLayer) map.getLayers().get("Obstacles"+MathUtils.random(0, count - 1));
        }
    }

    private interface Constructor {
        Entity construct(Round round, TiledMapTileLayer.Cell cell, float x, float y, float w, float h);
    }

    private void layerMap(TiledMapTileLayer layer, Constructor constructor){
        if (layer == null){
            return;
        }

        float tw = collisionLayer.getTileWidth();
        float th = collisionLayer.getTileHeight();

        for (int x = 0; x < layer.getWidth(); x++) {
            for (int y = 0; y < layer.getHeight(); y++) {
                TiledMapTileLayer.Cell cell = layer.getCell(x, y);
                if (cell != null) {
                    float tileX = x * tw;
                    float tileY = y * th;
                    constructor.construct(this, cell, tileX, tileY, tw, th);
                }
            }
        }
    }


    private void createEnvironmentBodies() {
        layerMap(getCollisionLayer(), createObstacle);
        layerMap(getObstaclesLayer(), createObstacle);
        layerMap(getBaseLayer(),     createWater   );


        float mapHeight = getMapHeight();
        float mapWidth = getMapWidth();

        //Assumes square tiles!
        float tw = collisionLayer.getTileWidth();

        short bounds = PhysicsEntity.BOUNDS_BITS | PhysicsEntity.WORLD_BITS;
        
        // 4 map edge objects
        new Obstacle(this, -tw,      -tw,       tw,          mapHeight+tw, bounds);
        new Obstacle(this, -tw,      -tw,       mapWidth+tw, tw,           bounds);
        new Obstacle(this, -tw,      mapHeight, mapWidth+tw, tw,           bounds);
        new Obstacle(this, mapWidth, -tw,       tw,          mapHeight+tw, bounds);
    }

    /**
     * Spawns a number of random mobs the specified distance from the player.
     * @param amount how many random mobs to spawn
     * @param minX the minimum x distance from the player to spawn the mobs
     * @param minY the minimum y distance from the player to spawn the mobs
     * @param maxX the maximum x distance from the player to spawn the mobs
     * @param maxY the maximum y distance from the player to spawn the mobs
     */

    public void spawnRandomMobs(int amount, int minX, int minY, int maxX, int maxY) {
        Player player = getPlayer();
        for (int i = 0; i < amount;) {
            int x = MathUtils.random(minX, maxX);
            int y = MathUtils.random(minY, maxY);
            if (!collidePoint(x, y) && (player.distanceTo(x, y) > 500))
                if (MathUtils.random()>0.8) {
                    addEntity(new DementedMob(this, x, y, 100, 200, 15));
                }
                else if (MathUtils.random()>0.2) {
                    addEntity(new MeleeMob(this, x, y, 100, 100, 15));
                }
                else {
                    addEntity(new RangedMob(this, x, y, 100, 100, 25));
                }
            i++;
        }
    }

    /**
     * Gets the current map
     * @return this Round's map
     */
    public TiledMap getMap() {
        return map;
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

    /**
     * Tests if a point resides inside a body
     * @param x x
     * @param y y
     * @return whether the point is in the body
     */
    public boolean collidePoint(float x, float y) {
        return collidePoint(new Vector2(x, y));
    }
    public boolean collidePoint(Vector2 p) {
        return collidePoint(p, PhysicsEntity.WORLD_BITS);
    }
    public boolean collidePoint(Vector2 p, short maskBits) {
        p.scl(PhysicsEntity.METRES_PER_PIXEL);
        Collision.Query q = new Collision.QueryPoint(world, p, maskBits);
        return q.query();
    }

    public boolean collideArea(Vector2 pos, Vector2 size) {
        return collideArea(pos, size, PhysicsEntity.WORLD_BITS);
    }
    public boolean collideArea(Vector2 pos, Vector2 size, short maskBits) {
        pos.scl(PhysicsEntity.METRES_PER_PIXEL);
        size.scl(PhysicsEntity.METRES_PER_PIXEL);
        Collision.Query q = new Collision.QueryArea(world, pos, size, maskBits);
        return q.query();
    }

    public boolean rayCast(Vector2 pos1, Vector2 pos2){
        return rayCast(pos1, pos2, PhysicsEntity.WORLD_BITS);
    }
    public boolean rayCast(Vector2 pos1, Vector2 pos2, short maskBits) {
        RayCast.RayCastCB r = new RayCast.RayCastCB(maskBits);
        world.rayCast(
                r,
                pos1.cpy().scl(PhysicsEntity.METRES_PER_PIXEL),
                pos2.cpy().scl(PhysicsEntity.METRES_PER_PIXEL)
        );
        return r.clear;
    }

    public boolean pathIsClear(Vector2 pos, Vector2 size, Vector2 target){
        float width  = size.x;
        float height = size.y;
        Vector2[] corners = {new Vector2( width/2,  height/2),
                new Vector2(-width/2,  height/2),
                new Vector2(-width/2,  -height/2),
                new Vector2(width/2,  -height/2),
                Vector2.Zero.cpy()
        };

        boolean result = true;

        for (Vector2 corner : corners){
            result = result && rayCast(corner.cpy().add(pos.cpy()), corner.cpy().add(target.cpy()));
        }

        return result;
    }
    public boolean cornersCanSeeTarget(Vector2 pos, Vector2 size, Vector2 target){
        float width  = size.x;
        float height = size.y;
        Vector2[] corners = {new Vector2( width/2,  height/2),
                new Vector2(-width/2,  height/2),
                new Vector2(-width/2,  -height/2),
                new Vector2(width/2,  -height/2),
                Vector2.Zero.cpy()
        };

        boolean result = true;

        for (Vector2 corner : corners){
            result = result && rayCast(corner.cpy().add(pos.cpy()), target.cpy());
        }

        return result;
    }

    /**
     * Converts screen coordinates to world coordinates.
     *
     * @param x the x coordinate on screen
     * @param y the y coordinate on screen
     * @return a Vector3 containing the world coordinates (x and y)
     */
    public Vector3 unproject(int x, int y) {
        return parent.getGameScreen().unproject(x, y);
    }

    /**
     * Gets the player in the round
     * @return this Round's player
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Gets all entities in the round
     * @return the list of all entities currently in the Round
     */
    public Collection<Entity> getEntities() {
        return entities;
    }

    /**
     * Adds an entity to the entity list.
     *
     * @param newEntity new entity of any type
     */
    public void addEntity(Entity newEntity) {
        newEntities.add(newEntity);
    }

    private void addAddNewEntities() {
        entities.addAll(newEntities);
        newEntities.clear();
    }

    /**
     * Gets the current objective of this Round.
     *
     * @return the current objective
     */
    public Objective getObjective() {
        return objective;
    }

    /**
     * @return The layer for spawn positions
     */
    public TiledMapTileLayer getSpawnLayer(){
        return (TiledMapTileLayer) getMap().getLayers().get("Spawn");
    }

    /**
     * Sets the current objective of this Round.
     *
     * @param objective the new objective
     */
    public void setObjective(Objective objective) {
        this.objective = objective;
    }

    /**
     * Creates a new projectile and adds it to the list of entities.
     *
     * @param damage          how much damage the projectile deals
     * @param owner           the owner of the projectile (i.e. the one who fired it)
     */
    public void createProjectile(Vector2 pos, Vector2 velocity, int damage, PhysicsEntity owner) {
        addEntity(new Projectile(this, pos, velocity, damage, owner));
    }
    public void createProjectile(float x, float y, float dirX, float dirY, float speed, float velocityXOffset, float velocityYOffset, int damage, PhysicsEntity owner) {
        createProjectile(new Vector2(x, y), new Vector2(dirX, dirY).setLength(speed).add(velocityXOffset, velocityYOffset), damage, owner);
        Assets.laser.play(0.1f);
    }

    /**
     * Creates a new particle effect and adds it to the list of entities.
     *
     * @param x         the x coordinate of the center of the particle effect
     * @param y         the y coordinate of the center of the particle effect
     * @param duration  how long the particle effect should last for
     * @param animation the animation to use for the particle effect
     */
    public void createParticle(float x, float y, float duration, Animation animation) {
        addEntity(new Particle(this, x , y, duration, animation));
    }

    /**
     * Creates a powerup on the floor and adds it to the list of entities.
     *
     * @param x       the x coordinate of the powerup
     * @param y       the y coordinate of the powerup
     * @param powerup the powerup to grant to the player
     * @param time    how long the powerup should last for
     */
    public void createPowerup(float x, float y, PowerupManager.powerupTypes powerup, float time) {
        addEntity(new PowerupItem(this, x, y, powerup, time));
    }

    /**
     * Creates an upgrade on the floor and adds it to the list of entities.
     *
     * @param x       the x coordinate of the upgrade
     * @param y       the y coordinate of the upgrade
     * @param upgrade the upgrade to grant to the player
     */
    public void createUpgrade(int x, int y, Player.Upgrade upgrade) {
        addEntity(new Upgrade(this, x, y, upgrade));
    }

    private void updateEntities(float delta){
        Iterator<Entity> iterator = entities.iterator();
        while (iterator.hasNext()) {
            Entity entity = iterator.next();
            Vector2 vector = entity.vectorTo(player.getCentre());

            if (entity.isRemoved()) {
                if (entity instanceof Mob && ((Mob) entity).isDead()) {
                    int score = (int) (((Mob) entity).getScore()* (powerUpManager.getIsActive(PowerupManager.powerupTypes.SCORE_MULTIPLIER) ? Player.PLAYER_SCORE_MULTIPLIER : 1));
                    player.addScore(score);
                    floatyNumbersManager.createScoreNumber(score, entity.getX(), entity.getY());
                }
                entity.dispose();
                iterator.remove();
            } else if (vector.x < UPDATE_DISTANCE_X && vector.y < UPDATE_DISTANCE_Y){
                // Don't bother updating entities that aren't on screen.
                entity.update(delta);
            }
        }
    }


    /**
     * Updates all entities in this Round.
     *
     * @param delta the time elapsed since the last update
     */
    public void update(float delta) {
        if (player.isDead()) {
            parent.showLoseScreen();
        }

        world.step(delta, 6, 2);

        powerUpManager.update(delta);
        floatyNumbersManager.update(delta);

        updateEntities(delta);

        if (objective != null) {
            objective.update(delta);
            if (objective.getStatus() == Objective.ObjectiveStatus.COMPLETED) {
                parent.showWinScreen(player.getScore());
            }
        }
        addAddNewEntities();
        Collections.sort(entities,entityComparator);
    }


}
