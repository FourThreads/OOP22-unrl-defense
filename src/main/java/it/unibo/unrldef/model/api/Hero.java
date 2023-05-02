package it.unibo.unrldef.model.api;

import it.unibo.unrldef.common.Position;

public interface Hero extends Entity {
    
    /**
     * @return the cost of the hero
     */
    int getCost();

    /**
     * @return a copy of the hero
     */
    Hero copy();


    boolean ifPossibleActivate(Position position);

    boolean isActive();

    boolean isReady();
    
}
