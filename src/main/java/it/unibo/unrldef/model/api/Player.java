package it.unibo.unrldef.model.api;

import java.util.Set;

import it.unibo.unrldef.common.Position;

/**
 * A player in a strategic game where his position is irrelevant and spells can
 * be used.
 * 
 * @author tommaso.severi2@studio.unibo.it
 * @author tommaso.ceredi@studio.unibo.it
 */
public interface Player {
    /**
     * @return the name of the player
     */
    String getName();

    /**
     * Sets the name of the current player.
     * @param name the name of the player
     */
    void setName(String name);

    /**
     * @return the map where the player is currently playing
     */
    World getGameWorld();

    /**
     * Sets the world where the player will be playing.
     * 
     * @param world the playing world
     */
    void setGameWorld(World world);

    /**
     * @return a set containing all the spells that the player uses
     */
    Set<Spell> getSpells();

    Set<Hero> getHeros();

    /**
     * Sets the spells that the player can use in game.
     * 
     * @param spells a set of spells
     */
    void setSpells(Set<Spell> spells);

    void setHeros(Set<Hero> hero);

    /**
     * Places a new tower on the world map if the player has enough money.
     * 
     * @param pos       the position where to place it
     * @param towerName the type of tower to build
     * @return true if the tower has been built, false otherwise
     */
    boolean buildTower(Position pos, String towerName);

    /**
     * Places a new spell on the world map effecting enemies.
     * 
     * @param name spell name
     * @param pos  place position
     * @return true if the spell has been thrown, false otherwise
     */
    boolean throwSpell(Position pos, String name);

    boolean spawnHero(Position pos, String name);

    /**
     * Updates the state of the spells.
     * 
     * @param elapsed time passed since last update
     */
    void updateSpellState(long elapsed);

    void updateHeroState(long elapsed);
}
