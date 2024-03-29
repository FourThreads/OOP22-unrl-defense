package it.unibo.unrldef.model.impl;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import it.unibo.unrldef.common.Position;
import it.unibo.unrldef.model.api.Enemy;
import it.unibo.unrldef.model.api.World;
import it.unibo.unrldef.model.api.Path.Direction;

/**
 * Test class for WorldImpl.
 */
class WorldImplTest {

    private World testWorld;

    /**
     * Initialize the world.
     */
    @BeforeEach
    public void init() {
        final int segment1 = 14;
        final int segment2 = 50;
        final int segment3 = 20;
        final int segment4 = 24;
        final int towerTestX1 = 4;
        final int towerTestY1 = 4;
        final int towerTestX2 = 16;
        final int towerTestY2 = 16;
        final int testSpawnX = 60;
        final int testSpawnY = 0;
        final int testMoney = 200;

        this.testWorld = new WorldImpl.Builder("testWorld", new PlayerImpl(), new Position(testSpawnX, testSpawnY), 0,
                testMoney)
                .addPathSegment(Direction.DOWN, segment1)
                .addPathSegment(Direction.LEFT, segment2)
                .addPathSegment(Direction.DOWN, segment3)
                .addPathSegment(Direction.RIGHT, segment4)
                .addPathSegment(Direction.DOWN, segment4)
                .addPathSegment(Direction.END, 0)
                .addTowerBuildingSpace(segment3, segment4)
                .addAvailableTower(Hunter.NAME, new Hunter())
                .addAvailableTower(Cannon.NAME, new Cannon())
                .addTowerBuildingSpace(towerTestX1, towerTestY1)
                .addTowerBuildingSpace(towerTestX2, towerTestY2)
                .build();

    }

    @Test
    void testEnemiesInRange() {
        final int testX1 = 14;
        final int testY1 = 14;
        final int testX2 = 10;
        final int testY2 = 24;
        final int testX3 = 4;
        final int testY3 = 4;
        final int testX4 = 16;
        final int testY4 = 34;
        final int testX5 = 34;
        final int testY5 = 68;
        final int testX6 = 18;
        final int testY6 = 24;

        final Position testPos1 = new Position(testX1, testY1);
        final Position testPos2 = new Position(testX2, testY2);
        final Position testPos3 = new Position(testX3, testY3);
        final Position testPos4 = new Position(testX4, testY4);
        final Position testPos5 = new Position(testX5, testY5);
        final Position testPos6 = new Position(testX6, testY6);

        final Enemy testEnemy1 = new Orc();
        final Enemy testEnemy2 = new Orc();
        final Enemy testEnemy3 = new Goblin();
        final Enemy testEnemy4 = new Goblin();
        final Enemy testEnemy5 = new Goblin();

        final int testRadius = 14;

        this.testWorld.spawnEnemy(testEnemy1, testPos1); // spawning the first enemy in the path
        this.testWorld.spawnEnemy(testEnemy2, testPos2); // spawning the second enemy in the path
        this.testWorld.spawnEnemy(testEnemy3, testPos3); // spawning the third enemy out of the path
        this.testWorld.spawnEnemy(testEnemy4, testPos4); // spawning the fourth enemy in the path and more advanced than
                                                         // the others
        this.testWorld.spawnEnemy(testEnemy5, testPos5); // spawning the fifth enemy in the path, but too far

        final var sorroundingEnemies = this.testWorld.sorroundingEnemies(testPos6, testRadius);

        assert sorroundingEnemies.containsAll(List.of(testEnemy1, testEnemy2, testEnemy4));
        assert !sorroundingEnemies.contains(testEnemy3);
        assert !sorroundingEnemies.contains(testEnemy5);
        assert sorroundingEnemies.get(0).equals(testEnemy4);
    }

    @Test
    void testTryBuildTower() {
        final int testX1 = 14;
        final int testY1 = 14;
        final int testX2 = 4;
        final int testY2 = 4;
        final int testX3 = 16;
        final int testY3 = 16;

        assert !this.testWorld.tryBuildTower(new Position(testX1, testY1), Hunter.NAME); // trying to build a tower in a
                                                                                        // non-building space
        assert this.testWorld.tryBuildTower(new Position(testX2, testY2), Hunter.NAME); // trying to build a tower in a
                                                                                       // building space
        assert !this.testWorld.tryBuildTower(new Position(testX2, testY2), Hunter.NAME); // trying to build a tower in a
                                                                                        // building space already
                                                                                        // occupied
        assert !this.testWorld.tryBuildTower(new Position(testX3, testY3), Cannon.NAME); // trying to build a tower
                                                                                        // having not enough money

    }
}
