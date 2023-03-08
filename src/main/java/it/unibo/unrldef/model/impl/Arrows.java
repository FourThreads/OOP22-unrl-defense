package it.unibo.unrldef.model.impl;

import it.unibo.unrldef.model.api.World;

/**
 * An arrows spell used in a tower defense game
 * @author tommaso.severi2@studio.unibo.it
 */
public class Arrows extends SpellImpl {
    
    private static final String NAME = "arrows";
    private static final double ATTACK_RATE = 0.0;
    private static final double DMG = 15.0;
    private static final double RAD = 20.0;
    private static final double WAIT_TIME = 8.0;

    /**
     * Creates a new spell of type fireball 
     */
    public Arrows(final World parentWorld) {
        super(NAME, parentWorld, RAD, DMG, ATTACK_RATE, WAIT_TIME);
    }
}
