package it.unibo.unrldef.model.api;

import it.unibo.unrldef.common.Position;

public interface Hero extends Entity {

    /**
     * @return a copy of the hero
     */
    Hero copy();


    boolean ifPossibleActivate(Position position);

    boolean isActive();

    boolean isReady();

    double getHealth();

    void reduceHealth(double damage);
    
}
