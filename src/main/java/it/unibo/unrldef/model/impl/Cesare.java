package it.unibo.unrldef.model.impl;

public final class Cesare extends HeroImpl {

    private static final int COST = 100;
    private static final double RADIUS = 0.5;
    private static final long ATTACK_RATE = 1000;
    private static final double DAMAGE = 10;
    private static final String NAME = "cesare";

    public Cesare() {
        super(NAME, RADIUS, DAMAGE, ATTACK_RATE, COST);
    }
    
}
