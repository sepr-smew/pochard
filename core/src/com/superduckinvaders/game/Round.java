package com.superduckinvaders.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.superduckinvaders.game.ai.BossAI;
import com.superduckinvaders.game.ai.MovementAI;
import com.superduckinvaders.game.ai.RangedAI;
import com.superduckinvaders.game.assets.Assets;
import com.superduckinvaders.game.assets.TextureSet;
import com.superduckinvaders.game.entity.Character;
import com.superduckinvaders.game.entity.*;
import com.superduckinvaders.game.entity.item.Item;
import com.superduckinvaders.game.entity.item.PowerupItem;
import com.superduckinvaders.game.entity.item.PowerupManager;
import com.superduckinvaders.game.entity.item.Upgrade;
import com.superduckinvaders.game.objective.BossObjective;
import com.superduckinvaders.game.objective.CollectObjective;
import com.superduckinvaders.game.objective.KillObjective;
import com.superduckinvaders.game.objective.Objective;
import com.superduckinvaders.game.ui.FloatyNumbersManager;
import com.superduckinvaders.game.util.KeySequenceListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a round of the game played on one level with a single objective.
 */
public class Round {

    /**
     * How near entities must be to the player to get updated in the game loop.
     */
    public static final int UPDATE_DISTANCE_X = DuckGame.GAME_WIDTH;
    /**
     * How near entities must be to the player to get updated in the game loop.
     */
    public static final int UPDATE_DISTANCE_Y = DuckGame.GAME_HEIGHT;

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

    /**
     * The player.
     */
    private Player player;

    /**
     * Array of all entities currently in the Round.
     */
    private List<Entity> entities;

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
     * @param mobs the number of random mobs to spawn.
     */
    public Round(DuckGame parent, TiledMap map, int mobs, boolean isBoss) {
        this.parent = parent;
        this.map = map;

        // Choose which obstacles to use.
        obstaclesLayer = chooseObstacles();

        // Determine starting coordinates for player (0, 0 default).
        int startX = Integer.parseInt(map.getProperties().get("StartX", "0", String.class)) * getTileWidth();
        int startY = Integer.parseInt(map.getProperties().get("StartY", "0", String.class)) * getTileHeight();

        player = new Player(this, startX, startY);

        entities = new ArrayList<Entity>(128);
        entities.add(player);

        spawnRandomMobs(mobs, 200, 200, 1000, 1000);

        //Set the objective to the boss objective if is a boss round, also spawn boss
        if(isBoss){
            addEntity(new Mob(this, 50*16, 50*16, 2500, 100, 5000, Assets.bossBadGuy, Assets.bossBadGuy, new BossAI(this), Mob.MobType.BOSS));
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

                    Item objective = new Item(this, objectiveX, objectiveY, Assets.flag);
                    setObjective(new CollectObjective(this, Objective.objectiveType.COLLECT, objective));

                    entities.add(objective);
                    break;
                }
                case KILL: {
                    setObjective(new KillObjective(this, Objective.objectiveType.KILL, MathUtils.random(mobs / 2, mobs)));
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
     * Spawns a number of random mobs the specified distance from the player.
     * @param amount how many random mobs to spawn
     * @param minX the minimum x distance from the player to spawn the mobs
     * @param minY the minimum y distance from the player to spawn the mobs
     * @param maxX the maximum x distance from the player to spawn the mobs
     * @param maxY the maximum y distance from the player to spawn the mobs
     */
    private void spawnRandomMobs(int amount, int minX, int minY, int maxX, int maxY) {
        while(amount > 0) {
            int x = MathUtils.random(minX, maxX) * (MathUtils.randomBoolean() ? -1 : 1);
            int y = MathUtils.random(minY, maxY) * (MathUtils.randomBoolean() ? -1 : 1);

            Mob mob1 = new Mob(this, getPlayer().getX() + x, getPlayer().getY() + y, 100, 100, 15, Assets.badGuyNormal, Assets.badGuySwimming, new MovementAI(this, 48), Mob.MobType.MELEE);
            //amount -= spawnMob(mob1) ? 1 : 0;
            Mob mob2 = new Mob(this, getPlayer().getX() + x, getPlayer().getY() + y, 100, 100, 25, Assets.rangedBadGuy, Assets.rangedBadGuySwimming, new RangedAI(this, 300, 300), Mob.MobType.RANGED);
            //amount -= spawnMob(mob2) ? 1 : 0;
            if(MathUtils.random(0,3)==0)
                amount -= spawnMob(mob2) ? 1 : 0;
            else
                amount -= spawnMob(mob1) ? 1 : 0;

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
     * Gets whether the map tile at the specified coordinates is blocked or not.
     *
     * @param x the x coordinate of the map tile
     * @param y the y coordinate of the map tile
     * @return whether or not the map tile is blocked
     */
    public boolean isTileBlocked(int x, int y) {
        int tileX = x / getTileWidth();
        int tileY = y / getTileHeight();
        
        return getCollisionLayer().getCell(tileX, tileY) != null || (getObstaclesLayer() != null && getObstaclesLayer().getCell(tileX, tileY) != null);
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
    public List<Entity> getEntities() {
        return entities;
    }

    /**
     * Adds an entity to the entity list.
     *
     * @param newEntity new entity of any type
     */
    public void addEntity(Entity newEntity) {
        entities.add(newEntity);
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
     * @param x               the initial x coordinate
     * @param y               the initial y coordinate
     * @param targetX         the target x coordinate
     * @param targetY         the target y coordinate
     * @param speed           how fast the projectile moves
     * @param velocityXOffset the offset to the initial X velocity
     * @param velocityYOffset the offset to the initial Y velocity
     * @param damage          how much damage the projectile deals
     * @param owner           the owner of the projectile (i.e. the one who fired it)
     */
    public void createProjectile(float x, float y, float targetX, float targetY, float speed, float velocityXOffset, float velocityYOffset, int damage, Entity owner) {
        entities.add(new Projectile(this, x, y, targetX, targetY, speed, velocityXOffset, velocityYOffset, damage, owner));
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
        entities.add(new Particle(this, x , y, duration, animation));
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
        entities.add(new PowerupItem(this, x, y, powerup, time));
    }

    /**
     * Creates an upgrade on the floor and adds it to the list of entities.
     *
     * @param x       the x coordinate of the upgrade
     * @param y       the y coordinate of the upgrade
     * @param upgrade the upgrade to grant to the player
     */
    public void createUpgrade(int x, int y, Player.Upgrade upgrade) {
        entities.add(new Upgrade(this, x, y, upgrade));
    }

    /**
     * Creates a mob and adds it to the list of entities, but only if it doesn't intersect with another character.
     * @param x the initial x coordinate
     * @param y the initial y coordinate
     * @param health the initial health of the mob
     * @param textureSet the texture set to use
     * @param speed how fast the mob moves in pixels per second
     * @return true if the mob was successfully added, false if there was an intersection and the mob wasn't added
     */
    public boolean createMob(Mob mob, float x, float y, int health, TextureSet textureSet, int speed) {

        // Check mob isn't out of bounds.
        if (x < 0 || x > getMapWidth() - textureSet.getWidth() || y > getMapHeight() - textureSet.getHeight()) {
            return false;
        }

        // Check mob doesn't intersect anything.
        for (Entity entity : entities) {
            if (entity instanceof Character
                    && (mob.intersects(entity.getX(), entity.getY(), entity.getWidth(), entity.getHeight()) || mob.collidesX(0) || mob.collidesY(0))) {
                return false;
            }
        }

        entities.add(mob);
        return true;
    }

    /**
     * Spawns a mob somewhere on the map. Ensures it doesn't intersect anything and is on a spawn tile
     * @param mob
     * @return
     */
    public boolean spawnMob(Mob mob){
        float x = mob.getX();
        float y = mob.getY();
        TextureSet textureSet = mob.getWalkingTextureSet();

        // Check mob isn't out of bounds.
        if (x < 0 || x > getMapWidth() - textureSet.getWidth() || y > getMapHeight() - textureSet.getHeight()) {
            return false;
        }

        // Check mob doesn't intersect anything.
        for (Entity entity : entities) {
            if (entity instanceof Character
                    && (mob.intersects(entity.getX(), entity.getY(), entity.getWidth(), entity.getHeight()) || mob.collidesX(0) || mob.collidesY(0))) {
                return false;
            }
        }

        if (getSpawnLayer().getCell((int)x / getTileWidth(), (int)y / getTileHeight()) == null){
            return false;
        }

        entities.add(mob);
        return true;
    }

    /**
     * Updates all entities in this Round.
     *
     * @param delta the time elapsed since the last update
     */
    public void update(float delta) {

        powerUpManager.update(delta);
        floatyNumbersManager.update(delta);

        if (objective != null) {
            objective.update(delta);

            if (objective.getStatus() == Objective.OBJECTIVE_COMPLETED) {
                parent.showWinScreen(player.getScore());
            } else if (player.isDead()) {
                parent.showLoseScreen();
            }
        }

        //int updateNumber =0, totalNumber=entities.size(), numMobs=0;
        for (int i = 0; i < entities.size(); i++) {
            Entity entity = entities.get(i);
/*            if(entity instanceof Mob)
                numMobs++;*/

            if (entity.isRemoved()) {
                if (entity instanceof Mob && ((Mob) entity).isDead()) {
                    int score = (int) (((Mob) entity).getScore()* (powerUpManager.getIsActive(PowerupManager.powerupTypes.SCORE_MULTIPLIER) ? Player.PLAYER_SCORE_MULTIPLIER : 1));
                    player.addScore(score);
                    floatyNumbersManager.createScoreNumber(score, entity.getX(), entity.getY());
                    if(objective.getObjectiveType()== Objective.objectiveType.BOSS){
                        spawnRandomMobs(1, 0, 0, 1000, 1000);
                    }

                }

                entities.remove(i);
            } else if ((entity.distanceTo(player.getX(), player.getY()) < UPDATE_DISTANCE_X)&&(entity.distanceTo(player.getX(), player.getY()) < UPDATE_DISTANCE_Y)){
                // Don't bother updating entities that aren't on screen.
                entity.update(delta);
                //updateNumber++;
            }
        }
    }


}
