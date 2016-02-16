
package com.superduckinvaders.game.ai;

import com.badlogic.gdx.math.MathUtils;
import com.superduckinvaders.game.Round;
import com.superduckinvaders.game.entity.Mob;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;

/**
 * AI that follows and attacks the player within a certain range.
 */
public class ZombieAI extends AI {

    /**
     * How many seconds between attacks?
     */
    public static final float ATTACK_DELAY = 1;
    /**
     * How many iterations to use in the pathfinding algorithm.
     */
    public final static int PATHFINDING_ITERATION_LIMIT = 20;
    /**
     * How often to update the AI.
     */
    public final static float PATHFINDING_RATE = (float) 0.2;
    /**
     * The random offset to be added or taken from the base pathfinding rate.
     */
    public final static float PATHFINDING_RATE_OFFSET = (float) 0.05;
    /**
     * Width of one tile in the map.
     */
    private int tileWidth;
    /**
     * Height of one tile in the map.
     */
    private int tileHeight;
    /**
     * Player's last X coordinate.
     */
    protected int playerX;
    /**
     * Player's last Y coordinate.
     */
    protected int playerY;
    /**
     * Used to calculate rate of pathfinding.
     */
    protected float deltaOffsetLimit = 0;
    /**
     * Used to track when to recalculate AI.
     */
    protected float currentOffset = 0;
    /**
     * How far away from the player this ZombieAI can attack.
     */
    protected int attackRange;
    /**
     * How long before we can attack again.
     */
    protected float attackTimer = 0;

    /**
     * Initialises this ZombieAI.
     *
     * @param round       the round the Mob this AI controls is a part of
     * @param attackRange how far away from the player can this ZombieAI attack
     */
    public ZombieAI(Round round, int attackRange) {
        super(round);

        this.tileWidth = round.getTileWidth();
        this.tileHeight = round.getTileHeight();
        this.attackRange = attackRange;
    }

    /**
     * Updates this ZombieAI with the player's last coordinates.
     */
    protected void updatePlayerCoords() {
        playerX = (int) round.getPlayer().getX();
        playerY = (int) round.getPlayer().getY();
    }

    /**
     * Updates this ZombieAI.
     *
     * @param mob   pointer to the Mob using this AI
     * @param delta time since the previous update
     */
    @Override
    public void update(Mob mob, float delta) {
        updatePlayerCoords();

        float distanceX = mob.getX() - playerX;
        float distanceY = mob.getY() - playerY;
        float distanceFromPlayer = mob.distanceTo(playerX, playerY);

        currentOffset += delta;
        if (currentOffset >= deltaOffsetLimit && (int) distanceFromPlayer < 1280 / 2) {
            deltaOffsetLimit = PATHFINDING_RATE + (MathUtils.random() % PATHFINDING_RATE_OFFSET);
            currentOffset = 0;
            Coordinate targetCoord = FindPath(mob);
            Coordinate targetDir = new Coordinate((int) (targetCoord.x - mob.getX()), (int) (targetCoord.y - mob.getY()));
            mob.setVelocity(targetDir.x, targetDir.y);
        }

        // Damage player.
        if ((int) distanceFromPlayer < attackRange && attackTimer <= 0) {
            round.getPlayer().damage(1);
            attackTimer = ATTACK_DELAY;
        } else if (attackTimer > 0) {
            attackTimer -= delta;
        }
    }

    /**
     * A variation of A* algorithm. Returns a meaningful target coordinate as a pair of integers.
     * Recalculated every tick as player might move and change pathfinding coordinates.
     *
     * @param mob Mob that a path is being generated for
     * @return Returns a Coordinate for the path finding
     */
    protected Coordinate FindPath(Mob mob) {
        Coordinate startCoord = new Coordinate((int) mob.getX(), (int) mob.getY());
        Coordinate finalCoord = new Coordinate(playerX, playerY);
        boolean finalFound = false;

        PriorityQueue<Coordinate> fringe = new PriorityQueue<Coordinate>();
        HashMap<Coordinate, SearchNode> visitedStates = new HashMap<Coordinate, SearchNode>();
        fringe.add(startCoord);
        visitedStates.put(startCoord, new SearchNode(null, 0));

        while (!fringe.isEmpty()) {

            Coordinate currentCoord = fringe.poll();
            SearchNode currentState = visitedStates.get(currentCoord);

            if (currentState.iteration >= PATHFINDING_ITERATION_LIMIT) {
                continue;
            }

            //work out N, E, S, W permutations
            Coordinate[] perm = new Coordinate[4];
            perm[0] = new Coordinate(currentCoord.x, currentCoord.y + tileHeight);
            perm[1] = new Coordinate(currentCoord.x + tileWidth, currentCoord.y);
            perm[2] = new Coordinate(currentCoord.x, currentCoord.y - tileHeight);
            perm[3] = new Coordinate(currentCoord.x - tileWidth, currentCoord.y);

            for (Coordinate currentPerm : perm) {
                if (!(mob.collidesXfrom(currentPerm.x - currentCoord.x, currentCoord.x, currentCoord.y) ||
                        mob.collidesYfrom(currentPerm.y - currentCoord.y, currentCoord.x, currentCoord.y) ||
                        visitedStates.containsKey(currentPerm))) {
                    fringe.add(currentPerm);
                    visitedStates.put(currentPerm, new SearchNode(currentState, currentState.iteration + 1));
                }
                if (currentPerm.inSameTile(finalCoord)) {

                    visitedStates.put(currentPerm, new SearchNode(currentState, currentState.iteration + 1));
                    finalCoord = currentPerm;
                    finalFound = true;
                    break;
                }
            }
            if (finalFound) break;
        }
        if (!finalFound) {
            return startCoord;

        } else {
            SearchNode resultNode = null;
            List<SearchNode> path = new ArrayList<SearchNode>();
            path.add(visitedStates.get(finalCoord));
            while (path.get(path.size() - 1) != visitedStates.get(startCoord)) {
                path.add(path.get(path.size() - 1).predecessor);
            }
            switch (path.size()) {
                case 1:
                    resultNode = path.get(path.size() - 1);
                    break;
                default:
                    resultNode = path.get(path.size() - 2);
                    break;
            }
            //for loop below will terminate after at most 4 iterations
            for (Coordinate key : visitedStates.keySet()) {
                if (visitedStates.get(key) == resultNode)
                    return key;
            }
        }
        return startCoord;
    }

    /**
     * Represents a pair of coordinates.
     */
    class Coordinate implements Comparable<Coordinate> {
        /**
         * The X coordinate.
         */
        public int x;
        /**
         * The Y coordinate.
         */
        public int y;

        /**
         * Initialises this Coordinate.
         *
         * @param x the x coordinate
         * @param y the y coordinate
         */
        public Coordinate(int x, int y) {
            this.x = x;
            this.y = y;
        }

        /**
         * Compares this Coordinate to another Coordinate.
         *
         * @param o the coordinate to compare to
         * @return the result of the comparison
         */
        @Override
        public int compareTo(Coordinate o) {
            float playerDistanceA = (float) Math.sqrt(Math.pow((x - playerX), 2) + Math.pow((y - playerY), 2));
            float playerDistanceB = (float) Math.sqrt(Math.pow((o.x - playerX), 2) + Math.pow((o.y - playerY), 2));
            float x = playerDistanceA - playerDistanceB;
            if (x > 0) {
                return 1;
            } else if (x == 0) {
                return 0;
            } else {
                return -1;
            }
        }

        /**
         * Tests this Coordinate with another object for equality.
         *
         * @param o the object to compare to
         * @return true of the objects are equal, false if not
         */
        @Override
        public boolean equals(Object o) {
            if (o == null) return false;
            if (getClass() != o.getClass()) return false;
            final Coordinate other = (Coordinate) o;
            return (this.x == other.x && this.y == other.y);
        }

        /**
         * Gets a unique hash code for this coordinate.
         *
         * @return the hash code
         */
        @Override
        public int hashCode() {
            int hash = 17;
            hash = hash * 31 + this.x;
            hash = hash * 31 + this.y;
            return hash;
        }

        /**
         * Gets whether this Coordinate is in the same map tile as another.
         *
         * @param b the coordinate to compare with
         * @return true if the coordinates are in the same map tile, false if not
         */
        public boolean inSameTile(Coordinate b) {
            return (this.x / tileWidth == b.x / tileWidth && this.y / tileHeight == b.y / tileHeight);
        }

        /**
         * Returns a string representation of this Coordinate.
         *
         * @return a string representation of this Coordinate
         */
        public String toString() {
            return ("(" + Integer.toString(this.x) + ", " + Integer.toString(this.y) + ")");
        }
    }

    /**
     * Represents a node in the A* search tree.
     */
    class SearchNode {
        /**
         * The predecessor node in the search tree.
         */
        public SearchNode predecessor;
        /**
         * The iteration this node is a part of.
         */
        public int iteration;

        /**
         * Initialises this SearchNode.
         *
         * @param predecessor the predecessor node
         * @param iteration   the iteration of this node
         */
        public SearchNode(SearchNode predecessor, int iteration) {
            this.predecessor = predecessor;
            this.iteration = iteration;
        }
    }
}