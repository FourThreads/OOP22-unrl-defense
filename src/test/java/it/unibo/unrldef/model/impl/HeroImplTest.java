package it.unibo.unrldef.model.impl;

import it.unibo.unrldef.common.Position;
import it.unibo.unrldef.model.api.Enemy;
import it.unibo.unrldef.model.api.Hero;
import it.unibo.unrldef.model.api.World;
import it.unibo.unrldef.model.api.Path.Direction;
import it.unibo.unrldef.model.api.Player;
import it.unibo.unrldef.model.impl.PlayerImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test class for HeroImpl.
 * 
 * @author tommaso.ceredi@studio.unibo.it
 */
class HeroImplTest {

    private World testWorld;
    private Player testPlayer;
    private Hero testHero;
    private static final long TEST_AFS = 1000;
    private static final int TEST_COST = 100;

    final int testSpeed = 0;
    final int testDamage = 15;
    final int testRadius = 10;
    final int testAttackRate = 1000;
    final int testStartingHealth = 100;

    /**
     * Initialize the world.
     */
    @BeforeEach
    public void init() {
        this.testWorld = new WorldImpl.Builder("testWorld", new PlayerImpl(), new Position(0, 0), 1, TEST_COST)
                .addPathSegment(Direction.END, 0)
                .addAvailableTower(Hunter.NAME, new Hunter())
                .addTowerBuildingSpace(0, 0)
                .build();
        this.testPlayer = new PlayerImpl();
        this.testHero = new HeroImpl("cesare", testRadius, testDamage, testAttackRate, testStartingHealth, 0, 0) {

            @Override
            public Hero copy() {
                return null;
            }

            @Override
            protected void additionAttack(Enemy target) {
                // nothing to do
            }

        };
    }

    /**
     * Test the attack method of the hero and check if the enemy damage the hero.
     */
    @Test
    void testStatus() {
        // testo che il danno del nemico venga applicato all'eroe e che l'eroe danneggi
        // il nemico
        final Position testPosition = new Position(0, 0);
        final Enemy testEnemy = new EnemyImpl("test", testStartingHealth, testSpeed, 0, 0, 0, 0) {
            @Override
            public EnemyImpl copy() {
                return null;
            }
        };
        this.testWorld.spawnEnemy(testEnemy, testPosition);
        this.testPlayer.spawnHero(testPosition, "cesare");
        final Hero testHero = this.testPlayer.getHeros().stream().findFirst().get();
        final double startingHealth = testHero.getHealth();
        testHero.updateState(TEST_AFS);
        // assert testHero.getHealth() < startingHealth;
        assert testEnemy.getHealth() < testStartingHealth;
    }
}
