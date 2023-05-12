package it.unibo.unrldef.model.impl;

import it.unibo.unrldef.common.Pair;
import it.unibo.unrldef.model.api.Enemy;
import it.unibo.unrldef.model.api.Hero;
import it.unibo.unrldef.model.api.Path;

/**
 * Implementation of an Enemy in the game Unreal Defense.
 * 
 * @author danilo.maglia@studio.unibo.it
 * @author tommaso.ceredi@studio.unibo.it
 */
public abstract class EnemyImpl extends EntityImpl implements Enemy {
    private double health;
    private double speed;
    private final double dropAmount;
    private final double defaultSpeed;
    private final double rangeAttack;
    private final double damageAttack;
    private int currentDirectionIndex;
    private Pair<Path.Direction, Double> currentDirection;

    /**
     * Create a new enemy.
     * 
     * @param name
     *                       the name of the enemy
     * @param startingHealth
     *                       the starting health of the enemy
     * @param speed
     *                       the speed of the enemy
     * @param dropAmount
     *                       the amount of money that the enemy drops when it dies
     * @param rangeAttack
     *                       the range of the attack of the enemy
     * @param damageAttack
     *                       the damage of the attack of the enemy
     * @param attackRate
     *                       the rate of the attack of the enemy
     */
    public EnemyImpl(final String name, final double startingHealth, final double speed, final double dropAmount,
            final double rangeAttack, final double damageAttack, final long attackRate) {
        super(name, rangeAttack, damageAttack, attackRate);
        this.health = startingHealth;
        this.speed = speed;
        this.dropAmount = dropAmount;
        this.defaultSpeed = speed;
        this.currentDirectionIndex = 0;
        this.currentDirection = new Pair<>(Path.Direction.DOWN, 0.0);
        this.rangeAttack = rangeAttack;
        this.damageAttack = damageAttack;
    }

    /**
     * Get the health of the enemy.
     * 
     * @return the health of the enemy
     */
    @Override
    public double getHealth() {
        return this.health;
    }

    /**
     * Get the speed of the enemy.
     * 
     * @return the speed of the enemy
     */
    @Override
    public double getSpeed() {
        return this.speed;
    }

    /**
     * Get the amount of money that the enemy drops when it dies.
     * 
     * @return the amount of money that the enemy drops when it dies
     */
    @Override
    public double getDropAmount() {
        return this.dropAmount;
    }

    /**
     * Reduce the health of the enemy.
     * 
     * @param amount the amount of health to reduce
     */
    @Override
    public void reduceHealth(final double amount) {
        this.health -= amount;
    }

    /**
     * Set the speed of the enemy.
     * 
     * @param speed the new speed of the enemy
     */
    @Override
    public void setSpeed(final double speed) {
        this.speed = speed;
    }

    /**
     * Reset the speed of the enemy to the default speed.
     */
    @Override
    public void resetSpeed() {
        this.speed = this.defaultSpeed;
    }

    /**
     * Check if the enemy is dead.
     * 
     * @return true if the enemy is dead, false otherwise
     */
    @Override
    public boolean isDead() {
        return this.getHealth() <= 0;
    }

    /**
     * Check if the enemy has reached the end of the path.
     * 
     * @return true if the enemy has reached the end of the path, false otherwise
     */
    @Override
    public boolean hasReachedEndOfPath() {
        return this.currentDirection.getFirst() == Path.Direction.END;
    }

    /**
     * Update the state of the enemy.
     * 
     * @param time the time elapsed since the last update
     */
    @Override
    public void updateState(final long time) {
        this.incrementTime(time);
        if (!this.hasReachedEndOfPath()) {
            if (this.getParentWorld().getSceneEntities().stream().filter(e -> e instanceof Hero).count() > 0) {
                this.getParentWorld().getSceneEntities().stream().filter(e -> e instanceof Hero).forEach(e -> {
                    final double distance = Math
                            .sqrt(Math.pow(this.getPosition().get().getX() - e.getPosition().get().getX(), 2)
                                    + Math.pow(this.getPosition().get().getY() - e.getPosition().get().getY(), 2));
                    if (distance <= this.rangeAttack) {
                        this.checkAttack();
                    }
                });
            }

            if (this.currentDirection.getSecond() <= 0) {
                this.currentDirection = this.getParentWorld().getPath().getDirection(this.currentDirectionIndex);
                this.currentDirectionIndex++;
            }

            this.move(time);
        }

    }

    /**
     * Move the enemy.
     * 
     * @param time the time elapsed since the last update
     */
    @Override
    public void move(final long time) {
        final Path.Direction direction = this.currentDirection.getFirst();
        final Double units = this.currentDirection.getSecond();
        double x = this.getPosition().get().getX();
        double y = this.getPosition().get().getY();
        final double actualSpeed = this.speed * (time / 1000.0);
        final double stepSize = (units - actualSpeed) < 0 ? units : actualSpeed; // This prevents the enemy from
                                                                                 // stepping out of bounds
        switch (direction) {
            case DOWN:
                y += stepSize;
                break;
            case UP:
                y -= stepSize;
                break;
            case LEFT:
                x -= stepSize;
                break;
            case RIGHT:
                x += stepSize;
                break;
            default:
                break;
        }
        this.setPosition(x, y);
        this.currentDirection.setSecondElement(units - stepSize);
    }

    @Override
    protected final void attack() {
        this.getParentWorld().getSceneEntities().stream().filter(e -> e instanceof Hero).forEach(e -> {
            ((Hero) e).reduceHealth(this.damageAttack);
        });
    }

    @Override
    public abstract EnemyImpl copy();

}
