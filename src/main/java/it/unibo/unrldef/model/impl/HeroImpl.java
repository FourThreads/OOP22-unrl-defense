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
    private double speed;

    public HeroImpl(final String name, final double radius, final double damage, final long attackRate,
            final double health, final double movementRange, final double speed) {
        super(name, radius, damage, attackRate);
        this.health = Objects.requireNonNull(health);
        this.startingHealth = Objects.requireNonNull(health);
        this.movementeRange = Objects.requireNonNull(movementRange);
        this.speed = speed;
    }

    @Override
    public void updateState(long time) {
        this.incrementTime(time);
        if (this.health <= 0) {
            this.deactivate();
        } else if (this.isActive()) {
            final List<Enemy> enemiesInRange = this.getParentWorld().sorroundingEnemies(this.startingPosition,
                    this.movementeRange);
            if (!enemiesInRange.isEmpty()) {
                final List<Enemy> enemiesToAttack = this.getParentWorld().sorroundingEnemies(this.getPosition().get(),
                        this.getRadius());
                if (!enemiesToAttack.isEmpty()) {
                    this.checkAttack();
                } else {
                    this.target = Optional.of(enemiesInRange.get(0));
                    this.move(time);
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

    public double getSpeed() {
        return this.speed;
    }

    @Override
    public final boolean ifPossibleActivate(final Position position) {
        if (!this.isActive() && this.isReady()) {
            this.activate();
            super.setPosition(position.getX(), position.getY());
            this.startingPosition = this.getPosition().get();
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

    protected abstract void additionAttack(Enemy target);

    public final double getHealth() {
        return this.health;
    }

    @Override
    public void reduceHealth(final double amount) {
        this.health -= amount;
    }

    private void move(final long time) {
        final double ex = this.target.get().getPosition().get().getX();
        final double ey = this.target.get().getPosition().get().getY();
        final double x = this.getPosition().get().getX();
        final double y = this.getPosition().get().getY();
        final double dx = Math.abs(ex - x);
        final double dy = Math.abs(ey - y);
        final double actualSpeed = this.speed * (time / 1000.0);
        final double distance = Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));
        final double stepSize = (distance - actualSpeed) < 0 ? distance : actualSpeed;
        final double angle = Math.atan2(dy, dx);
        double nx = 0;
        double ny = 0;
        if (distance <= this.speed * (time / 1000.0)) {
            this.setPosition(ex, ey);
        } else {
            if (ex == x && ey < y) {
                nx = x;
                ny = y - stepSize;
            } else if (ex == x && ey > y) {
                nx = x;
                ny = y + stepSize;
            } else {
                double movX = stepSize * Math.cos(angle);
                double movY = stepSize * Math.sin(angle);
                if (ex > x && ey <= y) {
                    nx = x + movX;
                    ny = y - movY;
                } else if (ex < x && ey <= y) {
                    nx = x - movX;
                    ny = y - movY;
                } else if (ex < x && ey > y) {
                    nx = x - movX;
                    ny = y + movY;
                } else if (ex > x && ey > y) {
                    nx = x + movX;
                    ny = y + movY;
                }
            }
            this.setPosition(nx, ny);
        }
    }
}
