package it.unibo.unrldef.model.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import it.unibo.unrldef.common.Position;
import it.unibo.unrldef.model.api.Hero;
import it.unibo.unrldef.model.api.Player;
import it.unibo.unrldef.model.api.Spell;
import it.unibo.unrldef.model.api.World;

/**
 * The main player in a tower defense game.
 * 
 * @author tommaso.severi2@studio.unibo.it
 * @author tommaso.ceredi@studio.unibo.it
 */
public final class PlayerImpl implements Player {

    private World currentWorld;
    private String name;
    private final Map<String, Spell> spells;
    private final Map<String, Hero> heros;

    /**
     * Creates a new player.
     */
    public PlayerImpl() {
        this.currentWorld = null;
        this.name = "";
        this.spells = new HashMap<>();
        this.heros = new HashMap<>();
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void setName(final String name) {
        this.name = Objects.requireNonNull(name);
    }

    @Override
    public World getGameWorld() {
        return this.currentWorld;
    }

    @Override
    public void setGameWorld(final World world) {
        this.currentWorld = Objects.requireNonNull(world);
    }

    @Override
    public Set<Spell> getSpells() {
        return new HashSet<Spell>(this.spells.values());
    }

    @Override
    public void setSpells(final Set<Spell> spells) {
        spells.forEach(sp -> this.spells.put(sp.getName(), sp));
    }

    @Override
    public boolean buildTower(final Position pos, final String towerName) {
        return this.getGameWorld().tryBuildTower(pos, towerName);
    }

    @Override
    public boolean throwSpell(final Position pos, final String name) {
        return this.spells.containsKey(name) && this.spells.get(name).ifPossibleActivate(pos);
    }

    @Override
    public void updateSpellState(final long elapsed) {
        this.getSpells().forEach(sp -> sp.updateState(elapsed));
    }

    /**
     * @return a set of spells that have been activeted
     */
    public Set<Spell> getActiveSpells() {
        return new HashSet<Spell>(this.getSpells().stream()
                .filter(Spell::isActive)
                .toList());
    }

    /**
     * @return a set of heros that have been activeted
     */
    public Set<Hero> getActiveHeros() {
        return new HashSet<Hero>(this.getHeros().stream()
                .filter(Hero::isActive)
                .toList());
    }

    @Override
    public boolean spawnHero(final Position pos, final String name) {
        return this.heros.containsKey(name) && this.heros.get(name).ifPossibleActivate(pos);
    }

    @Override
    public Set<Hero> getHeros() {
        return new HashSet<Hero>(this.heros.values());
    }

    @Override
    public void updateHeroState(final long elapsed) {
        this.getHeros().forEach(hero -> hero.updateState(elapsed));
    }

    @Override
    public void setHeros(final Set<Hero> hero) {
        hero.forEach(e -> this.heros.put(e.getName(), e));
    }

}
