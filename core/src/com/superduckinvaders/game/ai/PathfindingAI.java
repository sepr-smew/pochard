
package com.superduckinvaders.game.ai;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.superduckinvaders.game.Round;
import com.superduckinvaders.game.entity.mob.Mob;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;

/**
 * AI that follows and attacks the player within a certain range.
 */
public class PathfindingAI extends AI {
    // Assessment 4: large changes to AI to coordinate with move to box2d and
    // for performance and quality reasons.
    // Attack logic has been moved into the mob subclasses, because it makes more sense.

    /**
     * How many iterations to use in the pathfinding algorithm.
     */
    public final static int PATHFINDING_ITERATION_LIMIT = 40;

    /**
     * How often to update the AI.
     */
    public final static float PATHFINDING_RATE = (float) 0.4f;

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
     * Used to calculate rate of pathfinding.
     */
    private float deltaOffsetLimit = 0;

    /**
     * Used to track when to recalculate AI.
     */
    private float currentOffset = 0;

    /**
     * How far away from the player this PathfindingAI can attack.
     */
    private float targetRange;

    /**
     * The Coordinate for the AI to find a path for.
     */
    public Coordinate target;
    public List<SearchNode> path_DEBUG;

    protected Vector2 playerPos;

    /**
     * Initialises this PathfindingAI.
     *
     * @param round       the round the Mob this AI controls is a part of
     * @param targetRange how close to the player this PathfindingAI tries to get
     */
    public PathfindingAI(Round round, float targetRange) {
        super(round);

        this.tileWidth = round.getTileWidth();
        this.tileHeight = round.getTileHeight();
        this.targetRange = targetRange;
    }

    /**
     * Updates this PathfindingAI.
     *
     * @param mob   the Mob using this AI
     * @param delta time since the previous update
     */
    @Override
    public void update(Mob mob, float delta) {
        playerPos = round.getPlayer().getCentre();

        float distanceToPlayer = mob.distanceTo(playerPos);
        float distanceToTargetTile = (target != null) ? mob.getCentre().sub(target.vector()).len() : 0f;

        currentOffset += delta;
        if ((currentOffset >= deltaOffsetLimit || distanceToTargetTile < 2) && (int) distanceToPlayer < 1280 / 4) {
            deltaOffsetLimit = PATHFINDING_RATE + (MathUtils.random() % PATHFINDING_RATE_OFFSET);
            currentOffset = 0;
            target = FindPath(mob);
        }

        if (target != null) {
            mob.applyVelocity(target.vector());
        }
    }

    /**
     * A variation of A* algorithm. Returns a meaningful target coordinate as a pair of integers.
     * Recalculated every tick as player might move and change pathfinding coordinates.
     *
     * @param mob Mob that a path is being generated for
     * @return Returns a Coordinate for the path finding
     */
    private Coordinate FindPath(Mob mob) {
        path_DEBUG = null;

        Vector2 mobCentre = mob.getCentre();
        Vector2 mobPos = mob.getPosition();
        Vector2 mobSize = mob.getSize();
        Vector2 halfSize = mobSize.cpy().scl(0.5f);

        Coordinate startCoord = roundToTile(mobCentre);
        Coordinate finalCoord = roundToTile(playerPos);
        boolean finalFound = false;

        if (round.cornersCanSeeTarget(mobCentre, mobSize, playerPos)){
            if (new Vector2(playerPos).sub(mobCentre).len() < targetRange){
                return null;
            }
            else {

                currentOffset = deltaOffsetLimit;
                return new Coordinate(playerPos);
            }
        }

        PriorityQueue<Coordinate> fringe = new PriorityQueue<>();
        HashMap<Coordinate, SearchNode> visitedStates = new HashMap<>();
        fringe.add(startCoord);
        visitedStates.put(startCoord, new SearchNode(null, startCoord, 0));

        while (!fringe.isEmpty()) {

            Coordinate currentCoord = fringe.poll();
            SearchNode currentState = visitedStates.get(currentCoord);

            if (currentState.iteration >= PATHFINDING_ITERATION_LIMIT) {
                continue;
            }

//            if (currentCoord.inSameTile(finalCoord)) {
            if (round.cornersCanSeeTarget(currentCoord.vector(), mobSize, finalCoord.vector())) {
                finalCoord = currentCoord;
                finalFound = true;
                break;
            }

            //work out N, E, S, W permutations
            Coordinate[] perm = {
                    new Coordinate(currentCoord.x,             currentCoord.y + tileHeight),
                    new Coordinate(currentCoord.x + tileWidth, currentCoord.y             ),
                    new Coordinate(currentCoord.x,             currentCoord.y - tileHeight),
                    new Coordinate(currentCoord.x - tileWidth, currentCoord.y             )
            };

            for (Coordinate currentPerm : perm) {
//                if (!visitedStates.containsKey(currentPerm) && !round.collidePoint(currentPerm.vector())) {
                if (!visitedStates.containsKey(currentPerm) && !round.collideArea(currentPerm.vector().sub(halfSize.cpy()), mobSize.cpy())) {
                    fringe.add(currentPerm);
                    visitedStates.put(currentPerm, new SearchNode(currentState, currentPerm, currentState.iteration + 1));
                }
            }
        }

        if (!finalFound) {
            return null;
        } else {
            SearchNode resultNode;
            List<SearchNode> path = new ArrayList<>();
            path.add(visitedStates.get(finalCoord));
            while (path.get(path.size() - 1) != visitedStates.get(startCoord)) {
                SearchNode pred = path.get(path.size() - 1).predecessor;
                if (pred == null){
                    break;
                }
                path.add(pred);
            }
            int index = path.size()-1;
            while (index > 0) {
                SearchNode tempNode = path.get(index-1);
                if(!round.pathIsClear(mobCentre, mobSize, tempNode.coord.vector())){
                    break;
                }
                index--;
            }
            resultNode = path.get(index);
            path_DEBUG=path;

            return resultNode.coord;
        }
    }

    /**
     * Round an exact position to the nearest tile coordinate.
     * @param pos the position vector
     * @return the Coordinate of the tile
     */
    public Coordinate roundToTile(Vector2 pos){
        return roundToTile(pos.x, pos.y);

    }

    /**
     * Round an exact position to the nearest tile coordinate.
     * @param x the x position
     * @param y the y position
     * @return the Coordinate of the tile
     */
    public Coordinate roundToTile(float x, float y){
//        int nx = (int)(((int)(x/tileWidth)+0.5f) * tileWidth);
//        int ny = (int)(((int)(y/tileHeight)+0.5f) * tileHeight);
        int nx = (int)(Math.round(x/tileWidth) * tileWidth);
        int ny = (int)(Math.round(y/tileHeight) * tileHeight);
        return new Coordinate(nx, ny);
    }

    /**
     * Represents a pair of coordinates.
     */
    public class Coordinate implements Comparable<Coordinate> {
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
         * @param point The point vector.
         */
        public Coordinate(Vector2 point) {
            this((int) point.x, (int) point.y);

        }

        /**
         * Initialises this Coordinate.
         *
         * @param x the x position.
         * @param y the y position.
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
            float playerDistanceA = this.vector().sub(playerPos).len();
            float playerDistanceB =    o.vector().sub(playerPos).len();
            return new Float(playerDistanceA).compareTo(playerDistanceB);
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

        public Vector2 vector() {
            return new Vector2(x, y);
        }
    }

    /**
     * Represents a node in the A* search tree.
     */
    public class SearchNode {
        /**
         * The predecessor node in the search tree.
         */
        public SearchNode predecessor;
        public Coordinate coord;
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
        public SearchNode(SearchNode predecessor, Coordinate coord, int iteration) {
            this.predecessor = predecessor;
            this.coord = coord;
            this.iteration = iteration;
        }
    }
}
