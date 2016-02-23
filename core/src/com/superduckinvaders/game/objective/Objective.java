package com.superduckinvaders.game.objective;

import com.superduckinvaders.game.Round;

/**
 * Represents an objective that needs to be completed in order to advance.
 */
public abstract class Objective {


    public enum ObjectiveStatus {
        ONGOING,
        COMPLETED,
        FAILED
    }

    /**
     * The round that this Objective belongs to.
     */
    protected Round parent;

    /**
     * The current status of this Objective.
     */
    protected ObjectiveStatus status = ObjectiveStatus.ONGOING;

    /**
     * The type of the objective
     */
    protected objectiveType objectiveType;

    /**
     * Initialises this Objective in the specified round.
     *
     * @param parent the round that this Objective belongs to
     */
    public Objective(Round parent, objectiveType type) {
        this.parent = parent;
        this.objectiveType = type;
    }

    /**
     * Gets the current status of the Objective.
     *
     * @return the current status of the Objective (one of the OBJECTIVE_ constants);
     */
    public ObjectiveStatus getStatus() {
        return status;
    }

    /**
     * Gets a string describing this Objective to be printed on screen.
     *
     * @return a string describing this Objective
     */
    public abstract String getObjectiveString();

    /**
     * Updates the status towards this Objective.
     *
     * @param delta how much time has passed since the last update
     */
    public abstract void update(float delta);

    /**
     * @return The objective type of this objective
     */
    public Objective.objectiveType getObjectiveType() {
        return objectiveType;
    }

    /**
     * Enum of possible types of objectives
     */
    public enum objectiveType{
        COLLECT, KILL, BOSS
    }
}
