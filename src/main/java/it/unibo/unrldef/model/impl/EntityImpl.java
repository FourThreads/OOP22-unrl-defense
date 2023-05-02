package it.unibo.unrldef.model.impl;

import java.util.Objects;
import java.util.Optional;

import it.unibo.unrldef.common.Position;
import it.unibo.unrldef.model.api.Entity;
import it.unibo.unrldef.model.api.World;

/**
 * An entity in a game with a position and a name.
 * 
 * @author danilo.maglia@studio.unibo.it
 * @author tommaso.ceredi@studio.unibo.it
 */
public abstract class EntityImpl implements Entity {
    private Optional<Position> position;
    private final String name;
    private World parentWorld;

    private final double radius;
    private final double damage;
    private final long attackRate;
    private long timeSinceLastAction;
    private boolean isAttacking;

    // ==============================================================
    // ====================== DEFENSE ENTITY METHODS ================
    // ==============================================================

    /**
     * Crates a new defensive entinty.
     * 
     * @param name       its name
     * @param radius     the radius it can deal damage from
     * @param damage     the damage it inflicts to an enemy
     * @param attackRate the rate at which it deals damage
     */
    public EntityImpl(final String name, final double radius,
            final double damage, final long attackRate) {
        this.name = name;
        this.radius = Objects.requireNonNull(radius);
        this.damage = Objects.requireNonNull(damage);
        this.attackRate = Objects.requireNonNull(attackRate);
        this.timeSinceLastAction = 0;
    }

    /**
     * 
     * @return the time elapsed since the last action of the entity was performed in
     *         milliseconds
     */
    public long getTimeSinceLastAction() {
        return this.timeSinceLastAction;
    }

    /**
     * 
     * @param amount increase the time elapsed in milliseconds since the last action
     */
    public void incrementTime(final long amount) {
        this.timeSinceLastAction += amount;
    }

    /**
     * Reset elapsed time.
     */
    public void resetElapsedTime() {
        this.timeSinceLastAction = 0;
    }

    /**
     * @return the radius of the defensive entity
     */
    public double getRadius() {
        return this.radius;
    }

    /**
     * @return the damage of the defensive entity
     */
    public double getDamage() {
        return this.damage;
    }

    /**
     * @return the attack rate of the defensive entity
     */
    public double getAttackRate() {
        return this.attackRate;
    }

    /**
     * checks if the entity can attack.
     */
    public void checkAttack() {
        if (this.getTimeSinceLastAction() >= this.getAttackRate()) {
            this.resetElapsedTime();
            this.attack();
            this.isAttacking = true;
        } else {
            this.isAttacking = false;
        }
    }

    /**
     * this method is called when is time to attack.
     */
    protected abstract void attack();

    /**
     * @return true if the entity is attacking
     */
    protected boolean isAttacking() {
        return this.isAttacking;
    }


    // =============================================================
    // ====================== ENTITY METHODS =======================
    // =============================================================


    /**
     * Create a new entity with a name.
     * @param name the name of the entity
     */
    public EntityImpl(final String name) {
        this.position = Optional.empty();
        this.name = name;
        
        this.radius = 0;
        this.damage = 0;
        this.attackRate = 0;
    }

    /**
     * Set the parent world of the entity.
     * @param parentWorld the parent world to be set to the entity
     */
    @Override
    public void setParentWorld(final World parentWorld) {
        this.parentWorld = parentWorld;
    }

    /**
     * Get the position of the entity.
     * @return the position of the entity
     */
    @Override
    public Optional<Position> getPosition() {
        return this.position;
    }

    /**
     * Get the name of the entity.
     * @return the name of the entity
     */
    @Override
    public String getName() {
        return this.name;
    }

    /**
     * Get the parent world of the entity.
     * @return the parent world of the entity
     */
    @Override
    public World getParentWorld() {
        return this.parentWorld;
    }

    /**
     * Set the position of the entity.
     * @param x the x position to be set to the entity
     * @param y the y position to be set to the entity
     */
    @Override
    public void setPosition(final double x, final double y) {
        this.position = Optional.of(new Position(x, y));
    }

    @Override
    public abstract void updateState(long time);
}
