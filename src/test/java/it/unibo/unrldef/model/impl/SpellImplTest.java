package it.unibo.unrldef.model.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import it.unibo.unrldef.common.Position;
import it.unibo.unrldef.model.api.Enemy;
import it.unibo.unrldef.model.api.World;
import it.unibo.unrldef.model.api.Path.Direction;

/**
 * Test class for SpellImpl.
 * 
 * @author tommaso.severi2@studio.unibo.it
 */
class SpellImplTest {

    private static final double TEST_RADIUS = 5;
    private static final double TEST_DAMAGE = 4;
    private static final long TEST_LINGERING_DAMAGE = 1;
    private final long testRechargeTime = 3 * 1000;
    private final long testLingeringEffectTime = 2 * 1000;
    private final long testLingeringEffectFrequency = 1 * 1000;
    private SpellImpl testSpell;
    private World testWorld;

    /**
     * Initializes the values before each test.
     */
    @BeforeEach
    public void init() {
        this.testWorld = new WorldImpl.Builder("testWorld", new PlayerImpl(), new Position(0, 0), 0, 0)
                .addPathSegment(Direction.END, 0)
                .build();
        this.testSpell = new SpellImpl("test", TEST_RADIUS, TEST_DAMAGE,
                this.testRechargeTime, this.testLingeringEffectTime, this.testLingeringEffectFrequency) {
            @Override
            protected void effect(final Enemy enemy) {
                // This effect is only used as an example
                enemy.reduceHealth(TEST_LINGERING_DAMAGE);
            }

            @Override
            protected void resetEffect() {
            }
        };
        this.testSpell.setParentWorld(testWorld);
    }

    /**
     * Test if the activation, attack, effect and deactivation methods work.
     */
    @Test
    void testActivation() {
        // Checks if the spell activates before time
        // An empty position is used since it doesn't matter
        final Position testPosition = new Position(0, 0);
        this.testSpell.updateState(this.testRechargeTime - 1 * 1000);
        assert !this.testSpell.ifPossibleActivate(testPosition);
        this.testSpell.updateState(1 * 1000);
        assert this.testSpell.isReady();
        // Once ready it spawns an enemy with the exact amount of health 
        // so that the enemy dies only after all the damage possible is dealt by the spell
        final double targetStartingHealth = TEST_DAMAGE 
                + (TEST_LINGERING_DAMAGE * (this.testLingeringEffectTime / this.testLingeringEffectFrequency));
        final Enemy testTarget = new EnemyImpl("orc", targetStartingHealth, 0, 1, 1, 1, 1) {
            @Override
            public EnemyImpl copy() {
                return null;
            }
        };
        this.testWorld.spawnEnemy(testTarget, testPosition);
        // places the spell on the enemy
        assert this.testSpell.ifPossibleActivate(testTarget.getPosition().get());
        // Checks if the enemy targeted actually took the main damage
        assert testTarget.getHealth() == targetStartingHealth - TEST_DAMAGE;
        // Checks if the enemy targeted actually took the lingering damage
        this.testSpell.updateState(this.testLingeringEffectFrequency);
        assert testTarget.getHealth() == targetStartingHealth - TEST_DAMAGE - TEST_LINGERING_DAMAGE;
        // Cheks if the enemy targeted is dead after the remaining time
        this.testSpell.updateState(this.testLingeringEffectTime - this.testLingeringEffectFrequency);
        assert testTarget.isDead();
        // After the spell activation time has passed the spell should deactivate
        assert !this.testSpell.isActive();
    }
}
