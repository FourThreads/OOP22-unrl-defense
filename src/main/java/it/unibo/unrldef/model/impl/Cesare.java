package it.unibo.unrldef.model.impl;

import it.unibo.unrldef.common.Position;
import it.unibo.unrldef.model.api.Enemy;
import it.unibo.unrldef.model.api.Hero;

public final class Cesare extends HeroImpl {

    public static final double RADIUS = 10;
    private static final long ATTACK_RATE = 1000;
    private static final double DAMAGE = 10;
    public static final String NAME = "cesare";
    public static final double HEALTH = 100;

    public Cesare() {
        super(NAME, RADIUS, DAMAGE, ATTACK_RATE, HEALTH);
    }

    @Override
    public Hero copy() {
        return new Cesare();
    }

    @Override
    protected void additionAttack(Enemy target) {
        
    }
    
}
