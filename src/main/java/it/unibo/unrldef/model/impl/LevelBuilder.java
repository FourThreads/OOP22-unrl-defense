package it.unibo.unrldef.model.impl;

import it.unibo.unrldef.common.Position;
import it.unibo.unrldef.model.api.Enemy;
import it.unibo.unrldef.model.api.Hero;
import it.unibo.unrldef.model.api.Player;
import it.unibo.unrldef.model.api.Spell;
import it.unibo.unrldef.model.api.Tower;
import it.unibo.unrldef.model.api.World;
import it.unibo.unrldef.model.api.Path.Direction;

import org.json.simple.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.parser.ParseException;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import org.json.simple.parser.JSONParser;

/**
 * Class that builds the levels of the game.
 * 
 * @author francesco.buda3@studio.unibo.it
 * @author tommaso.ceredi@studio.unibo.it
 *
 */
public final class LevelBuilder {
    private final Player player;

    /**
     * the constructor of the class.
     * 
     * @param player
     */
    @SuppressFBWarnings(value = "EI_EXPOSE_REP2", justification = "Player is meant to be the same as the other objects")
    public LevelBuilder(final Player player) {
        this.player = player;
    }

    /**
     * Method that creates a world from a JSON config file.
     * 
     * @author danilo.maglia@studio.unibo.it
     *
     * @param fileName the json file path
     * @return the world created from the file
     */
    public World fromFile(final String fileName) {
        final JSONParser parser = new JSONParser();

        JSONObject json = null;
        String fileContent;
        try {
            // read the whole file passed as argument and put the content in a string
            final InputStream stream = this.getClass().getResourceAsStream(fileName);
            fileContent = new String(stream.readAllBytes(),
                    StandardCharsets.UTF_8);
            stream.close();
            json = (JSONObject) parser.parse(fileContent);
        } catch (IOException | ParseException e) {
            e.printStackTrace(); // NOPMD
        }

        if (json == null) {
            return null;
        }
        final WorldImpl.Builder worldBuilder = this.loadBuilderFromJson(json);
        this.loadPathFromJson(json, worldBuilder);
        this.loadWavesFromJson(json, worldBuilder);
        this.loadAvailableSpells(json);
        this.loadAvailableTowers(json, worldBuilder);
        this.loadTowerBuildingSpacesFromJson(json, worldBuilder);
        this.loadAvailableHeros(json);

        final World world = worldBuilder.build();
        for (final Spell spell : this.player.getSpells()) {
            spell.setParentWorld(world);
        }
        return world;
    }

    private WorldImpl.Builder loadBuilderFromJson(final JSONObject json) {
        final JSONObject position = (JSONObject) json.get("startingPosition");
        final String worldName = (String) json.get("worldName");
        final int castleHearth = ((Long) json.get("castleHearts")).intValue();
        final int startingMoney = ((Long) json.get("startingMoney")).intValue();
        return new WorldImpl.Builder(worldName, this.player,
                new Position(((Long) position.get("x")).doubleValue(), ((Long) position.get("y")).doubleValue()),
                castleHearth, startingMoney);
    }

    private void loadPathFromJson(final JSONObject json, final WorldImpl.Builder worldBuilder) {
        final JSONArray path = (JSONArray) json.get("path");
        for (final Object p : path) {
            final JSONObject pathSegment = (JSONObject) p;
            final String direction = (String) pathSegment.get("direction");
            final int length = ((Long) pathSegment.get("length")).intValue();
            worldBuilder.addPathSegment(Direction.valueOf(direction), length);
        }
    }

    private void loadWavesFromJson(final JSONObject json, final WorldImpl.Builder worldBuilder) {
        final JSONArray waves = (JSONArray) json.get("waves");
        int waveIndex = 0;
        for (final Object wave : waves) {
            final JSONObject waveObj = (JSONObject) wave;
            final JSONArray hordes = (JSONArray) waveObj.get("hordes");
            worldBuilder.addWave();

            // loading hordes
            int hordeIndex = 0;
            for (final Object horde : hordes) {
                final JSONObject hordeObj = (JSONObject) horde;
                final int delay = ((Long) hordeObj.get("delay")).intValue();
                worldBuilder.addHordeToWave(waveIndex, delay);
                final JSONArray enemies = (JSONArray) hordeObj.get("enemies");
                // loading enemies
                for (final Object enemy : enemies) {
                    final JSONObject enemyObj = (JSONObject) enemy;
                    final String enemyName = (String) enemyObj.get("type");
                    final int enemyNumber = ((Long) enemyObj.get("count")).intValue();
                    Enemy enemyType = null;
                    switch (enemyName) {
                        case "orc":
                            enemyType = new Orc();
                            break;
                        case "goblin":
                            enemyType = new Goblin();
                            break;
                        default:
                            break;
                    }
                    worldBuilder.addMultipleEnemiesToHorde(waveIndex, hordeIndex, enemyType,
                            (short) enemyNumber);
                }
                hordeIndex++;
            }
            waveIndex++;
        }
    }

    private void loadTowerBuildingSpacesFromJson(final JSONObject json, final WorldImpl.Builder worldBuilder) {
        final JSONArray towerBuildingSpaces = (JSONArray) json.get("towerBuildingSpaces");
        for (final Object space : towerBuildingSpaces) {
            final JSONObject spaceObj = (JSONObject) space;
            final int x = ((Long) spaceObj.get("x")).intValue();
            final int y = ((Long) spaceObj.get("y")).intValue();
            worldBuilder.addTowerBuildingSpace(x, y);
        }
    }

    private void loadAvailableSpells(final JSONObject json) {
        final JSONArray availableSpells = (JSONArray) json.get("availableSpells");
        final Set<Spell> spells = new HashSet<>();
        for (final Object spell : availableSpells) {
            final String spellName = (String) spell;
            Spell spellType = null;
            switch (spellName) {
                case "fireBall":
                    spellType = new FireBall();
                    break;
                case "snowStorm":
                    spellType = new SnowStorm();
                    break;
                default:
                    break;
            }
            spells.add(spellType);
        }
        this.player.setSpells(spells);
    }

    private void loadAvailableTowers(final JSONObject json, final WorldImpl.Builder worldBuilder) {
        final JSONArray availableTowers = (JSONArray) json.get("availableTowers");
        for (final Object tower : availableTowers) {
            final String towerName = (String) tower;
            Tower towerType = null;
            switch (towerName) {
                case "cannon":
                    towerType = new Cannon();
                    break;
                case "hunter":
                    towerType = new Hunter();
                    break;
                default:
                    break;
            }
            worldBuilder.addAvailableTower(towerName, towerType);
        }
    }

    private void loadAvailableHeros(final JSONObject json) {
        final JSONArray availableHeros = (JSONArray) json.get("availableHeros");
        final Set<Hero> heros = new HashSet<>();
        for (final Object hero : availableHeros) {
            final String heroName = (String) hero;
            Hero heroType = null;
            switch (heroName) {
                case "cesare":
                    heroType = new Cesare();
                    break;
                default:
                    break;
            }
            heros.add(heroType);
        }
        this.player.setHero(heros);
    }
}
