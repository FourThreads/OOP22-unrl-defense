package it.unibo.unrldef.model.impl;

import java.util.Objects;
import java.util.Optional;

import it.unibo.unrldef.common.Position;
import it.unibo.unrldef.model.api.Enemy;
import it.unibo.unrldef.model.api.Hero;

public abstract class HeroImpl extends EntityImpl implements Hero {

    private boolean active;
    private final double health;
    private Optional<Enemy> target = Optional.empty();

    public HeroImpl(final String name, final double radius, final double damage, final long attackRate, final double health) {
        super(name, radius, damage, attackRate);
        this.health = Objects.requireNonNull(health);
    }

    @Override
    public void updateState(long time) {
        this.incrementTime(time);
        if (this.isActive()) {
            this.checkAttack();
        }
    }

    @Override
    public final boolean isReady() {
        //System.out.println("time since last action: " + this.getTimeSinceLastAction() + " attack rate: " + this.getAttackRate());
        return this.getTimeSinceLastAction() >= this.getAttackRate() && !this.isActive();
    }

    @Override
    public abstract Hero copy();

    @Override
    protected void attack() {
        System.out.println("Hero attack");
    }

    @Override
    public final boolean isActive() {
        return this.active;
    }
    
    @Override
    public final boolean ifPossibleActivate(final Position position) {
        if (!this.isActive() && this.isReady()) {
            this.activate();
            super.setPosition(position.getX(), position.getY());
            this.checkAttack();
            return true;
        }
        return false;
    }

    private void activate() {
        this.active = true;
    }
}
