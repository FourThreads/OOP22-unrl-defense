package it.unibo.unrldef.model.api;

import java.util.Optional;

/**
 * A tower that can be placed in a world.
 * 
 * @author tommaso.ceredi@studio.unibo.it
 */
public interface Tower extends Entity {

    /**
     * @return the cost of the tower
     */
    int getCost();

    /**
     * @return a copy of the tower
     */
    Tower copy();

    /**
     * @return the Enemy that the tower is attacking
     */
    Optional<Enemy> getTarget();
}
