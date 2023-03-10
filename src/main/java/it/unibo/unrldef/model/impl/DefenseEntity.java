package it.unibo.unrldef.model.impl;

import java.util.Objects;
import java.util.Optional;

import it.unibo.unrldef.common.Position;

/**
 * A model of a defensive type entity in a strategic game
 * @author tommaso.severi2@studio.unibo.it
 * @author tommaso.ceredi@studio.unibo.it
 */
public abstract class DefenseEntity extends EntityImpl {

    private final double radius;
    private final double damage;
    private final long attackRate;
    private long timeSinceLastAction;
    private boolean isAttacking = false;

    /**
     * Crates a new defensive entinty
     * @param position where it'll placed
     * @param name its name
     * @param radius the radius it can deal damage from
     * @param damage the damage it inflicts to an enemy
     * @param attackRate the rate at which it deals damage
     */
    public DefenseEntity(final Optional<Position> position, final String name, final double radius, 
            final double damage, final long attackRate) {
        super(position, name);
        this.radius = Objects.requireNonNull(radius);
        this.damage = Objects.requireNonNull(damage);
        this.attackRate = Objects.requireNonNull(attackRate);
        this.timeSinceLastAction = 0;
    }
    
    /**
     * 
     * @return the time elapsed since the last action of the entity was performed in milliseconds
     */
    public long getTimeSinceLastAction() {
        return this.timeSinceLastAction;
    }

    /**
     * 
     * @param amount increase the time elapsed in milliseconds since the last action
     */
    public void incrementTime(long amount) {
        this.timeSinceLastAction += amount;
    }

    /**
     * Reset elapsed time
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
     * checks if the entity can attack
     */
    public void checkAttack(){
        if (this.getTimeSinceLastAction() >= this.attackRate){
            this.resetElapsedTime();
            this.attack();
            this.isAttacking = true;
        } else {
            this.isAttacking = false;
        }
    }

    /**
     * this method is called when is time to attack
     */
    protected abstract void attack();

    /**
     * @return true if the entity is attacking
     */
    protected boolean isAttacking() {
        return this.isAttacking;
    }
}
