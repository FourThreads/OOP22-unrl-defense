package it.unibo.unrldef.model.api;

public interface Hero extends Entity {
    
    /**
     * @return the cost of the hero
     */
    int getCost();

    /**
     * @return a copy of the hero
     */
    Hero copy();

}
