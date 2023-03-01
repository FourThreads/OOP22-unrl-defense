package it.unibo.unrldef.model.impl;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import it.unibo.unrldef.common.Position;
import it.unibo.unrldef.model.api.Enemy;
import it.unibo.unrldef.model.api.World;

/**
 * A model of a defensive type entity in a strategic game
 * @author tommaso.severi2@studio.unibo.it
 * @author tommaso.ceredi@studio.unibo.it
 */
public abstract class DefenseEntity extends EntityImpl {

    private final double radius;
    private final double damage;
    private final double attackRate;
    private List<Enemy> targetedEnemies;
    private long timeSinceLastAction;

    /**
     * Crates a new defensive entinty
     * @param position where it'll placed
     * @param name its name
     * @param radius the radius it can deal damage from
     * @param damage the damage it inflicts to an enemy
     * @param attackRate the rate at which it attacks
     */
    public DefenseEntity(final Optional<Position> position, final String name, final World parentWorld, final double radius, final double damage, final double attackRate) {
        super(position, name, parentWorld);
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
        if (this.getTimeSinceLastAction() >= this.attackRate && !this.targetedEnemies.isEmpty()){
            this.resetElapsedTime();
            this.attack();
        }
    }

    /**
     * this method is called when is time to attack
     */
    protected abstract void attack();
    
    /**
     * Sets the enemies in range
     */
    public void setEnemiesInRange(final List<Enemy> enemies){
        this.targetedEnemies = enemies;
    }

    /**
     * Gets the enemies in range
     * @return the targeted enemies
     */
    public List<Enemy> getTargetedEnemies() {
        return this.targetedEnemies;
    }
}
