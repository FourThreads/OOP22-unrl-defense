package it.unibo.unrldef.model.impl;

import java.util.Optional;

import it.unibo.unrldef.common.Position;
import it.unibo.unrldef.model.api.Enemy;
import it.unibo.unrldef.model.api.Hero;

public abstract class HeroImpl extends DefenseEntity implements Hero {

    private final int cost;
    private boolean active;
    private Optional<Enemy> target = Optional.empty();

    public HeroImpl(final String name, final double radius, final double damage, final long attackRate, final int cost) {
        super(name, radius, damage, attackRate);
        this.cost = cost;
    }

    @Override
    public void updateState(long time) {
        
    }

    @Override
    public abstract Hero copy();

    @Override
    public int getCost() {
        return this.cost;
    }

    @Override
    protected void attack() {
        
    }

    @Override
    public final boolean isActive() {
        return this.active;
    }
    
}
