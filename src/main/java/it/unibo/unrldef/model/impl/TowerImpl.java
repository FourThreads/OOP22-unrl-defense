package it.unibo.unrldef.model.impl;

import java.util.List;
import java.util.Optional;

import it.unibo.unrldef.model.api.Enemy;
import it.unibo.unrldef.model.api.Tower;

/**
 * A tower that can be placed in a world.
 * 
 * @author tommaso.ceredi@studio.unibo.it
 */
public abstract class TowerImpl extends EntityImpl implements Tower {

    private final int cost;
    private Optional<Enemy> target = Optional.empty();

    /**
     * Constructor of TowerImpl.
     * 
     * @param name       name of the tower
     * @param radius     radius of the tower
     * @param damage     damage of the tower
     * @param attackRate attack rate of the tower
     * @param cost       cost of the tower
     */
    public TowerImpl(final String name, final double radius, final double damage,
            final long attackRate, final int cost) {
        super(name, radius, damage, attackRate);
        this.cost = cost;
    }

    @Override
    public abstract Tower copy();

    @Override
    public final int getCost() {
        return this.cost;
    }

    @Override
    public final void updateState(final long time) {
        this.incrementTime(time);
        this.checkAttack();
    }

    @Override
    protected final void attack() {
        final List<Enemy> enemiesInRange = this.getParentWorld().sorroundingEnemies(this.getPosition().get(),
                this.getRadius());
        if (!enemiesInRange.isEmpty()) {
            if (this.target.isEmpty() || !enemiesInRange.contains(this.target.get())) {
                this.target = Optional.of(enemiesInRange.get(0));
            }
            this.target.get().reduceHealth(this.getDamage());
            this.additionAttack(this.target.get());
        } else {
            this.target = Optional.empty();
        }
    }

    @Override
    public final Optional<Enemy> getTarget() {
        if (this.target != null) {
            return this.isAttacking() ? this.target : Optional.empty();
        }
        return Optional.empty();
    }

    /**
     * Additional attack of the tower.
     * 
     * @param target target of the attack
     */
    protected abstract void additionAttack(Enemy target);
}
