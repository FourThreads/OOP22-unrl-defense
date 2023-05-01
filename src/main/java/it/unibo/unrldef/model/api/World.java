package it.unibo.unrldef.model.api;

import java.util.List;
import java.util.Set;

import it.unibo.unrldef.common.Position;

/**
 * the world of a tower defense game.
 * 
 * @author francesco.buda3@studio.unibo.it
 * 
 */
public interface World {

    /**
     * the possible states of the world.
     */
    enum GameState {
        /**
         * the game is in progress.
         */
        PLAYING,
        /**
         * the game is over, and the player won.
         */
        VICTORY,
        /**
         * the game is over, and the player lost.
         */
        DEFEAT
    }

    /**
     * update the state of the world.
     * 
     * @param time time elapsed since last update
     */
    void updateState(long time);

    /**
     * @param pos       the position of the tower to build
     * @param towerName the name of the tower to build
     * @return true if the construction of the tower was successful
     */
    Boolean tryBuildTower(Position pos, String towerName);

    Boolean trySpawnHero(Position pos, String heroName);

    /**
     * 
     * @param enemy the enemy to spawn
     * @param pos   the position where to spawn the enemy
     */
    void spawnEnemy(Enemy enemy, Position pos);

    /**
     * the method finds the enemies in the given circle. the firts enemy
     * of the list is the most advanced in the path.
     * 
     * @param center the center of the circle
     * @param radius the radius of the circle
     * @return the enemies sorrounding the center
     */
    List<Enemy> sorroundingEnemies(Position center, double radius);

    /**
     * the method returns all the entities currently present in the world.
     * enemies are sorted from the one whose y and x coordinates are lowest
     * to the one whose x and y coordinates are highest.
     * 
     * @return the list of the entities in the world
     */
    List<Entity> getSceneEntities();

    /**
     * 
     * @return the castle's integrity
     */
    int getHearts();

    /**
     * 
     * @return a set of the available positions
     */
    Set<Position> getAvailablePositions();

    /**
     * 
     * @return money in the bank
     */
    double getMoney();

    /**
     * 
     * @return a set of the available towers
     */
    Set<Tower> getAvailableTowers();

    /**
     * 
     * @return the world's path
     */
    Path getPath();

    /**
     * 
     * @return the state of the world
     */
    GameState gameState();

}
