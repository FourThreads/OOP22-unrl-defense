package it.unibo.unrldef.model.api;

import it.unibo.unrldef.common.Position;

/**
 * A hero that can be used by a player in a strategic game.
 * 
 * @author tommaso.ceredi@studio.unibo.it
 */
public interface Hero extends Entity {

    /**
     * @return a copy of the hero
     */
    Hero copy();

    /**
     * Tries to set the hero in its activated state.
     * @param position the desired place to throw th spell at
     * @return true if the hero is ready to be used, false otherwise
     */
    boolean ifPossibleActivate(Position position);

    /**
     * @return true if the hero is being used, false otherwise
     */
    boolean isActive();

    /**
     * @return true if the hero is ready to be used, false otherwise
     */
    boolean isReady();

    /**
     * @return the health of the hero
     */
    double getHealth();

    /**
     * @param damage the damage to deal to the hero
     */
    void reduceHealth(double damage);

}
