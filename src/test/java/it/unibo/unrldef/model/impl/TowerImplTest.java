package it.unibo.unrldef.model.impl;

import it.unibo.unrldef.common.Position;
import it.unibo.unrldef.model.api.Enemy;
import it.unibo.unrldef.model.api.Tower;
import it.unibo.unrldef.model.api.World;
import it.unibo.unrldef.model.api.Path.Direction;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test class for TowerImpl.
 * 
 * @author tommaso.ceredi@studio.unibo.it
 */
class TowerImplTest {

    private World testWorld;
    private static final long TEST_AFS = 1000;
    private static final int TEST_COST = 100;

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
    }

    /**
     * Test the attack method.
     */
    @Test
    void testAttack() {
        final Position testPosition = new Position(0, 0);
        final int testStartingHealth = 100;
        final int testSpeed = 5;
        this.testWorld.tryBuildTower(testPosition, Hunter.NAME);
        final Tower testTower = (Tower) this.testWorld.getSceneEntities().stream()
                .filter(e -> e instanceof Tower).findFirst().get();
        final Enemy testEnemy = new EnemyImpl("test", testStartingHealth, testSpeed, 0, 0, 0, 0) {
            @Override
            public EnemyImpl copy() {
                return null;
            }
        };
        final double startingHealth = testEnemy.getHealth();
        this.testWorld.spawnEnemy(testEnemy, testPosition);
        testTower.updateState(TEST_AFS);
        assert testEnemy.getHealth() < startingHealth;
    }
}
