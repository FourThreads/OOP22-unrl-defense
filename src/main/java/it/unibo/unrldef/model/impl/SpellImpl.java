package it.unibo.unrldef.model.impl;

import java.util.Objects;

import it.unibo.unrldef.common.Position;
import it.unibo.unrldef.model.api.Enemy;
import it.unibo.unrldef.model.api.Spell;

/**
 * Implementation of a generic spell in a tower defense game.
 * 
 * @author tommaso.severi2@studio.unibo.it
 */
public abstract class SpellImpl extends EntityImpl implements Spell {

    private boolean active;
    private final long lingeringEffectTime;
    private final long lingeringEffectFrequency;
    private long lingerTime;

    /**
     * Creates a new spell.
     * 
     * @param name                     the name of the spell
     * @param radius                   the radius of the spell
     * @param damage                   the damage of the spell
     * @param rechargeTime             the recharge time of the spell
     * @param lingeringEffectTime      the time the spell will be active
     * @param lingeringEffectFrequency the frequency in which the effect is applied
     */
    public SpellImpl(final String name, final double radius,
            final double damage, final long rechargeTime, final long lingeringEffectTime,
            final long lingeringEffectFrequency) {
        super(name, radius, damage, rechargeTime);
        this.lingeringEffectTime = Objects.requireNonNull(lingeringEffectTime);
        this.lingeringEffectFrequency = Objects.requireNonNull(lingeringEffectFrequency);
        this.active = false;
        this.lingerTime = 0;
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

    @Override
    public final boolean isActive() {
        return this.active;
    }

    @Override
    protected final void attack() {
        this.getParentWorld().sorroundingEnemies(this.getPosition().get(), this.getRadius())
                .forEach(e -> e.reduceHealth(this.getDamage()));
    }

    @Override
    public final void updateState(final long time) {
        this.incrementTime(time);
        if (this.isActive()) {
            this.lingerTime += time;
            this.ifPossibleApplyEffect();
        }
    }

    @Override
    public final boolean isReady() {
        return this.getTimeSinceLastAction() >= this.getAttackRate() && !this.isActive();
    }

    /**
     * Activates the spell.
     */
    private void activate() {
        this.active = true;
    }

    /**
     * Sets the spell back to its waiting state after dealing damage.
     */
    private void deactivate() {
        this.active = false;
        this.lingerTime = 0;
        this.resetElapsedTime();
        this.resetEffect();
    }

    /**
     * Applies the affect of the spell to the enemies in range if possible.
     */
    private void ifPossibleApplyEffect() {
        if (this.lingerTime >= this.lingeringEffectFrequency) {
            this.getParentWorld().sorroundingEnemies(this.getPosition().get(), this.getRadius())
                    .forEach(e -> this.effect(e));
            this.lingerTime -= this.lingeringEffectFrequency;
        }
        if (this.getTimeSinceLastAction() >= this.lingeringEffectTime) {
            this.deactivate();
        }
    }

    /**
     * The effect of the spell while lingering.
     * 
     * @param target the target to apply the effect to
     */
    protected abstract void effect(Enemy target);

    /**
     * Resets the effect applied by the spell after deactivating.
     */
    protected abstract void resetEffect();
}
