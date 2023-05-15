package it.unibo.unrldef.model.impl;

import it.unibo.unrldef.common.Position;
import it.unibo.unrldef.model.api.Enemy;
import it.unibo.unrldef.model.api.Hero;
import it.unibo.unrldef.model.api.World;
import it.unibo.unrldef.model.api.Path.Direction;
import it.unibo.unrldef.model.api.Path;
import it.unibo.unrldef.model.api.Player;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test class for HeroImpl.
 * 
 * @author tommaso.ceredi@studio.unibo.it
 */
class HeroImplTest {

    private World testWorld;
    private Hero testHero;
    private Player testPlayer;
    private final long testRechargeRate = 2 * 1000;
    private static final long TEST_AFS = 1000;
    private static final int TEST_COST = 100;
    private static final int PATH_RIGHT = 10;
    private static final int PATH_BOTTOM = 10;

    private static final int TEST_DAMAGE = 15;
    private static final int TARGET_DAMAGE = 10;
    private static final int TEST_RADIUS = 10;
    private static final int TARGET_RADIUS = 10;
    private static final int TEST_ATTACKRATE = 1000;
    private static final int TARGET_ATTACKRATE = 1000;
    private static final int TEST_STARTHEALTH = 1000;
    private static final int TARGET_STARTHEALTH = 100;

    /**
     * Initialize the world.
     */
    @BeforeEach
    public void init() {
        this.testPlayer = new PlayerImpl();
        this.testWorld = new WorldImpl.Builder("testWorld", this.testPlayer, new Position(0, 0), 1, TEST_COST)
                .addPathSegment(Path.Direction.DOWN, PATH_BOTTOM)
                .addPathSegment(Path.Direction.RIGHT, PATH_RIGHT)
                .addPathSegment(Direction.END, 0)
                .addAvailableTower(Hunter.NAME, new Hunter())
                .addTowerBuildingSpace(0, 0)
                .build();
        this.testHero = new HeroImpl("cesare", TEST_RADIUS, TEST_DAMAGE, TEST_ATTACKRATE, TEST_STARTHEALTH, 0, 0,
                testRechargeRate) {
            @Override
            public Hero copy() {
                return null;
            }

            @Override
            protected void additionAttack(Enemy target) {
                // nothing to do
            }
        };
        this.testHero.setParentWorld(testWorld);
    }

    /**
     * Test the attack method of the hero and check if the enemy damage the hero.
     */
    @Test
    void testStatus() {
        
        final Enemy testTarget = new EnemyImpl("orc", TARGET_STARTHEALTH, 0, 1, TARGET_RADIUS, TARGET_DAMAGE,
                TARGET_ATTACKRATE) {
            @Override
            public EnemyImpl copy() {
                return null;
            }
        };
        testTarget.setParentWorld(testWorld);
        final Position testPosition = new Position(0, 0);
        this.testHero.updateState(this.testRechargeRate - 1 * TEST_AFS);
        assert !this.testHero.ifPossibleActivate(testPosition);
        this.testHero.updateState(1 * TEST_AFS);
        assert this.testHero.ifPossibleActivate(testPosition);
        this.testPlayer.setHeros(Set.of(this.testHero));
        this.testWorld.spawnEnemy(testTarget, testPosition);
        testTarget.updateState(1 * TEST_AFS);
        this.testHero.updateState(1 * TEST_AFS);
        assert testTarget.getHealth() == TARGET_STARTHEALTH - TEST_DAMAGE;
        assert this.testHero.getHealth() == TEST_STARTHEALTH - TARGET_DAMAGE;
        assert this.testHero.getPosition().equals(testTarget.getPosition());
    }
}
