package it.unibo.unrldef.model.impl;

import it.unibo.unrldef.model.api.Hero;

public class HeroImpl extends DefenseEntity implements Hero {

    private final int cost;

    public HeroImpl(final String name, final double radius, final double damage, final long attackRate, final int cost) {
        super(name, radius, damage, attackRate);
        this.cost = cost;
    }

    @Override
    protected void attack() {
        
    }

    @Override
    public void updateState(long time) {
        
    }
    
}
