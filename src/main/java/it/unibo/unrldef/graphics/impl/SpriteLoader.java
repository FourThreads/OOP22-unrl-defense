package it.unibo.unrldef.graphics.impl;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import java.awt.Image;
import java.awt.Toolkit;

import javax.imageio.ImageIO;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import it.unibo.unrldef.input.api.InputHandler;

/**
 * Class that loads from file and contains the data of the sprites.
 * 
 * @author danilo.maglia@studio.unibo.it
 * @author francesco.buda3@studio.unibo.it
 * @author tommaso.ceredi@studio.unibo.it
 */
public final class SpriteLoader {
    /**
     * the folder where the assets are stored.
     */
    private static final String ASSETS_FOLDER = "/assets/";
    /**
     * the name of the sprite that represents the first map.
     */
    public static final String FIRST_MAP = "firstMap";
    /**
     * the name of the sprite that represents the fire ball.
     */
    public static final String FIRE_BALL = "fireBall";
    /**
     * the name of the sprite that represents the fire tower.
     */
    public static final String SNOW_STORM = "snowStorm";
    /**
     * the name of the sprite that represents the orc.
     */
    public static final String ORC = "orc";
    /**
     * the name of the sprite that represents the goblin.
     */
    public static final String GOBLIN = "goblin";
    /**
     * the name of the sprite that represents the hunter.
     */
    public static final String HUNTER = "hunter";
    /**
     * the name of the sprite that represents the cannon.
     */
    public static final String CANNON = "cannon";
    /**
     * the name of the sprite that represents the shooting hunter.
     */
    public static final String SHOOTING_HUNTER = "shootingHunter";
    /**
     * the name of the sprite that represents the shooting cannon.
     */
    public static final String SHOOTING_CANNON = "shootingCannon";
    /**
     * the name of the sprite that represents the explosion.
     */
    public static final String EXPLOSION = "explosion";
    /**
     * the name of the sprite that represents the tower place.
     */
    public static final String TOWER_PLACE = "towerPlace";
    /**
     * the name of the sprite that represents the heart.
     */
    public static final String HEART = "heart";
    /**
     * the name of the sprite that represents the money.
     */
    public static final String MONEY = "money";
    /**
     * the name of the sprite that represents a missing asset.
     */
    public static final String MISSING_ASSET = "missingAsset";

    /**
     * the name of the sprite that represents the cesare.
     */
    public static final String CESARE = "cesare";

    private final Map<String, Sprite> sprites = new HashMap<>();

    /**
     * loads the sprites from the JSON file passed as argument.
     * 
     * @param fileName
     * @param inputHandler
     */
    public void loadSpritesFromFile(final String fileName, final InputHandler inputHandler) {
        final JSONParser parser = new JSONParser();
        String fileContent;
        JSONObject json = new JSONObject();

        try {
            // read the whole file passed as argument and put the content in a string
            final InputStream stream = this.getClass().getResourceAsStream(fileName);
            fileContent = new String(stream.readAllBytes(),
                    StandardCharsets.UTF_8);
            stream.close();
            json = (JSONObject) parser.parse(fileContent);
        } catch (ParseException | IOException e) {
            new ErrorDialog("Error loading the sprites", inputHandler).showDialog();
        }

        final JSONArray sprites = (JSONArray) json.get("sprites");

        // for each sprite in the json file, create a new sprite and put it in the map
        for (final Object s : sprites) {
            final JSONObject sprite = (JSONObject) s;
            final int width = Integer.parseInt(sprite.get("width").toString());
            final int height = Integer.parseInt(sprite.get("height").toString());
            Image spriteImage = Toolkit.getDefaultToolkit().createImage("");
            try {
                spriteImage = ImageIO
                        .read(this.getClass().getResourceAsStream(ASSETS_FOLDER + sprite.get("fileName").toString()));
            } catch (IOException e) {
                new ErrorDialog("Error loading the sprite " + sprite.get("name").toString(), inputHandler).showDialog();
            }
            final Sprite newSprite = new Sprite(width, height, spriteImage);
            this.sprites.put(sprite.get("name").toString(), newSprite);
        }

    }

    /**
     * 
     * @param name
     * @return the sprite with the given name
     */
    public Sprite getSprite(final String name) {
        return this.sprites.get(name);
    }
}
