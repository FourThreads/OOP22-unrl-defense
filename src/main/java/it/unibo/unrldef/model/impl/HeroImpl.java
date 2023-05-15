package it.unibo.unrldef.model.impl;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import it.unibo.unrldef.common.Position;
import it.unibo.unrldef.model.api.Enemy;
import it.unibo.unrldef.model.api.Hero;

/**
 * A hero that can be placed in a world.
 * 
 * @author tommaso.ceredi@studio.unibo.it
 */
public abstract class HeroImpl extends EntityImpl implements Hero {

    private boolean active;
    private double health;
    private final double startingHealth;
    private Optional<Enemy> target = Optional.empty();
    private final double movementeRange;
    private Position startingPosition;
    private final double speed;
    private long lastAction;
    private static final int TIME_TO_DESPAWN = 10_000;
    private final long rechargeRate;

    /**
     * Constructor of HeroImpl.
     * 
     * @param name          name of the hero
     * @param radius        radius of the hero
     * @param damage        damage of the hero
     * @param attackRate    attack rate of the hero
     * @param health        health of the hero
     * @param movementRange movement range of the hero
     * @param speed         speed of the hero
     */
    public HeroImpl(final String name, final double radius, final double damage, final long attackRate,
            final double health, final double movementRange, final double speed, final long rechargeRate) {
        super(name, radius, damage, attackRate);
        this.health = Objects.requireNonNull(health);
        this.startingHealth = Objects.requireNonNull(health);
        this.movementeRange = Objects.requireNonNull(movementRange);
        this.speed = speed;
        this.rechargeRate = rechargeRate;
    }

    @Override
    public final void updateState(final long time) {
        this.incrementTime(time);
        if (getHealth() <= 0) {
            this.deactivate();
        } else if (this.isActive()) {
            final List<Enemy> enemiesInRange = getEnemiesInRange(this.startingPosition, this.movementeRange);
            if (!enemiesInRange.isEmpty()) {
                final List<Enemy> enemiesToAttack = this.getParentWorld().sorroundingEnemies(this.getPosition().get(),
                        this.getRadius());
                if (!enemiesToAttack.isEmpty()) {
                    this.checkAttack();
                    this.lastAction = System.currentTimeMillis();
                } else {
                    this.target = Optional.of(enemiesInRange.get(0));
                    this.move(time);
                }
            } else {
                this.target = Optional.empty();
            }
            if (lastAction < System.currentTimeMillis() - TIME_TO_DESPAWN) {
                this.deactivate();
            }
        }
    }

    private long getRechargeRate() {
        return this.rechargeRate;
    }

    private List<Enemy> getEnemiesInRange(final Position pos, final double range) {
        return this.getParentWorld().sorroundingEnemies(pos, range);
    }

    @Override
    public final boolean isReady() {
        return this.getTimeSinceLastAction() >= this.getRechargeRate() && !this.isActive();
    }

    @Override
    protected final void attack() {
        final List<Enemy> enemiesInRange = this.getEnemiesInRange(this.getPosition().get(), this.getRadius());
        if (!enemiesInRange.isEmpty()) {
            if (this.target.isEmpty() || !enemiesInRange.contains(this.target.get())) {
                this.target = Optional.of(enemiesInRange.get(0));
            }
            this.target.get().setSpeed(0);
            this.target.get().reduceHealth(this.getDamage());
            this.additionAttack(this.target.get());
        } else {
            this.target = Optional.empty();
        }
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
            this.startingPosition = this.getPosition().get();
            this.lastAction = System.currentTimeMillis();
            this.checkAttack();
            return true;
        }
        return false;
    }

    private void activate() {
        this.active = true;
    }

    private void deactivate() {
        this.active = false;
        this.resetElapsedTime();
        this.target.ifPresent(t -> t.resetSpeed());
        this.startingPosition = null;
        this.health = this.startingHealth;
    }

    /**
     * Additional attack of the hero.
     * 
     * @param target target of the attack
     */
    protected abstract void additionAttack(Enemy target);

    @Override
    public final double getHealth() {
        return this.health;
    }

    @Override
    public final void reduceHealth(final double amount) {
        this.health -= amount;
    }

    private void move(final long time) {
        final double ex = this.target.get().getPosition().get().getX();
        final double ey = this.target.get().getPosition().get().getY();
        final double x = this.getPosition().get().getX();
        final double y = this.getPosition().get().getY();

        final double dx = ex - x;
        final double dy = ey - y;

        final double distance = Math.sqrt(dx * dx + dy * dy);
        final double maxStep = this.speed * (time / 1000.0);
        final double stepSize = Math.min(distance, maxStep);

        final double nx = x + (stepSize / distance) * dx;
        final double ny = y + (stepSize / distance) * dy;

        this.setPosition(nx, ny);

    }
}
