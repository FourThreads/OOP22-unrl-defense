package it.unibo.unrldef.model.impl;

import it.unibo.unrldef.model.api.Enemy;
import it.unibo.unrldef.model.api.Hero;

/**
 * A hero that can attack enemies.
 * 
 * @author tommaso.ceredi@studio.unibo.it
 */
public final class Cesare extends HeroImpl {

    private static final double RADIUS = 2;
    private static final long ATTACK_RATE = 1000;
    private static final double DAMAGE = 10;
    /**
     * The name of the hero.
     */
    public static final String NAME = "cesare";
    /**
     * The health of the hero.
     */
    public static final double HEALTH = 1000;
    /**
     * The movement range of the hero.
     */
    public static final double MOVEMENT_RANGE = 10;
    private static final double SPEED = 5.0;
    private static final long RECHARGE_TIME = 6000;

    /**
     * Constructor of Cesare.
     */
    public Cesare() {
        super(NAME, RADIUS, DAMAGE, ATTACK_RATE, HEALTH, MOVEMENT_RANGE, SPEED, RECHARGE_TIME);
    }

    @Override
    public Hero copy() {
        return new Cesare();
    }

    @Override
    protected void additionAttack(final Enemy target) {
    }

}
