package it.unibo.unrldef.model.impl;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import it.unibo.unrldef.common.Position;
import it.unibo.unrldef.model.api.Enemy;
import it.unibo.unrldef.model.api.Hero;

public abstract class HeroImpl extends EntityImpl implements Hero {

    private boolean active;
    private double health;
    private final double startingHealth;
    private Optional<Enemy> target = Optional.empty();
    private final double movementeRange;
    private Position startingPosition = null;

    public HeroImpl(final String name, final double radius, final double damage, final long attackRate,
            final double health, final double movementRange) {
        super(name, radius, damage, attackRate);
        this.health = Objects.requireNonNull(health);
        this.startingHealth = Objects.requireNonNull(health);
        this.movementeRange = Objects.requireNonNull(movementRange);
    }

    @Override
    public void updateState(long time) {
        this.incrementTime(time);
        if (this.health <= 0) {
            this.deactivate();
        } else if (this.isActive()) {
            if (this.startingPosition == null) {
                this.startingPosition = this.getPosition().get();
            }
            final List<Enemy> enemiesInRange = this.getParentWorld().sorroundingEnemies(this.getPosition().get(),
                    this.movementeRange);
            if (!enemiesInRange.isEmpty()) {
                final List<Enemy> enemiesToAttack = this.getParentWorld().sorroundingEnemies(this.getPosition().get(),
                        this.getRadius());
                if (!enemiesToAttack.isEmpty()) {
                    this.checkAttack();
                } else {
                    this.target = Optional.of(enemiesInRange.get(0));
                    this.move();
                }
            } else {
                this.target = Optional.empty();
            }
        }
    }

    @Override
    public final boolean isReady() {
        // System.out.println("time since last action: " + this.getTimeSinceLastAction()
        // + " attack rate: " + this.getAttackRate() + " active: " + this.isActive());
        return this.getTimeSinceLastAction() >= this.getAttackRate() && !this.isActive();
    }

    @Override
    public abstract Hero copy();

    @Override
    protected void attack() {
        final List<Enemy> enemiesInRange = this.getParentWorld().sorroundingEnemies(this.getPosition().get(),
                this.getRadius());
        if (!enemiesInRange.isEmpty()) {
            if (this.target.isEmpty() || !enemiesInRange.contains(this.target.get())) {
                this.target = Optional.of(enemiesInRange.get(0));
            }
            this.target.get().setSpeed(0);
            this.target.get().reduceHealth(this.getDamage());
            this.additionAttack(this.target.get());
            // System.out.println("attacked " + this.target.get().getName() + " with " +
            // this.getDamage() + " damage");
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
        this.health = this.startingHealth;
    }

    protected abstract void additionAttack(Enemy target);

    public final double getHealth() {
        return this.health;
    }

    @Override
    public void reduceHealth(final double amount) {
        this.health -= amount;
    }

    private void move() {
        final int ex = (int) this.target.get().getPosition().get().getX();
        final int ey = (int) this.target.get().getPosition().get().getY();
        final int x = (int) this.getPosition().get().getX();
        final int y = (int) this.getPosition().get().getY();
        if (ex == x && ey <= y && this.startingPosition.getY() - this.movementeRange >= y) {
            this.setPosition(x, y - 1);
        } else if (ex == x && ey >= y && this.startingPosition.getY() - this.movementeRange <= y) {
            this.setPosition(x, y + 1);
        } else if (ex <= x && ey == y && this.startingPosition.getX() + this.movementeRange >= x) {
            this.setPosition(x - 1, y);
        } else if (ex >= x && ey == y && this.startingPosition.getX() - this.movementeRange <= x) {
            this.setPosition(x + 1, y);
        } else if (ex <= x && ey <= y) {
            this.setPosition(x - 1, y - 1);
        } else if (ex <= x && ey >= y) {
            this.setPosition(x - 1, y + 1);
        } else if (ex >= x && ey <= y && this.startingPosition.getX() + this.movementeRange >= x) {
            this.setPosition(x + 1, y - 1);
        } else if (ex >= x && ey >= y && this.startingPosition.getX() + this.movementeRange >= x) {
            this.setPosition(x + 1, y + 1);
        }
    }
}
