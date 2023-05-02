package it.unibo.unrldef.model.impl;

import java.util.Objects;

/**
 * A model of a defensive type entity in a strategic game.
 * 
 * @author tommaso.severi2@studio.unibo.it
 * @author tommaso.ceredi@studio.unibo.it
 */
public abstract class DefenseEntity_ extends EntityImpl {

    private final double radius;
    private final double damage;
    private final long attackRate;
    private long timeSinceLastAction;
    private boolean isAttacking;

    /**
     * Crates a new defensive entinty.
     * 
     * @param name       its name
     * @param radius     the radius it can deal damage from
     * @param damage     the damage it inflicts to an enemy
     * @param attackRate the rate at which it deals damage
     */
    public DefenseEntity_(final String name, final double radius,
            final double damage, final long attackRate) {
        super(name);
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
}
