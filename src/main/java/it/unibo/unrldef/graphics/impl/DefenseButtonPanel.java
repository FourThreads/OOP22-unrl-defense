package it.unibo.unrldef.graphics.impl;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;

import it.unibo.unrldef.model.impl.FireBall;
import it.unibo.unrldef.model.impl.Hunter;
import it.unibo.unrldef.model.impl.SnowStorm;
import it.unibo.unrldef.model.impl.Cannon;
import it.unibo.unrldef.model.impl.SpellImpl;
import it.unibo.unrldef.model.impl.TowerImpl;
import it.unibo.unrldef.input.api.Input;
import it.unibo.unrldef.model.api.Entity;
import it.unibo.unrldef.model.api.Tower;
import it.unibo.unrldef.model.api.World;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Builds the panel used for the buttons in the game.
 * @author tommaso.severi2@studio.unibo.it
 * @author danilo.maglia@studio.unibo.it
 */
public final class DefenseButtonPanel extends JPanel {

    private static final long serialVersionUID = 1L;
    /**
     * The width used by this type of buttons.
     * It's used a 16th of the total screen width so that the buttons are always visible.
     */
    public static final int WIDTH = (int) (Toolkit.getDefaultToolkit().getScreenSize().getWidth() / 16);
    /**
     * The height used by this type of buttons.
     * It's the same as the width since the buttons are squares.
     */
    public static final int HEIGHT = WIDTH;
    private static final Color BACKGROUND_COLOR = new Color(255, 255, 255);
    private static final String ASSETS_FOLDER = "resources/assets" + File.separator;
    private World world;
    private final Map<String, JButton> buttons = new HashMap<>();

    /**
     * Builds a new Panel for the defensive buttons.
     * @param gamePanel the game panel
     * @param world the world of the game
     * @param inputHandler the input handler of the game
     */
    public DefenseButtonPanel(final GamePanel gamePanel, final World world, final Input inputHandler) {
        super();
        this.setWorld(world);
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setBackground(BACKGROUND_COLOR);
        JButton cannon = null;
        JButton hunter = null;
        JButton fireBall = null;
        JButton snowStorm = null;
        try {
            cannon = this.placeDefenseButton(GamePanel.ViewState.TOWER_SELECTED, Cannon.NAME, gamePanel, 
                    new ImageIcon(ImageIO.read(new File(ASSETS_FOLDER + "cannonIcon.png"))));
            hunter = this.placeDefenseButton(GamePanel.ViewState.TOWER_SELECTED, Hunter.NAME, gamePanel, 
                    new ImageIcon(ImageIO.read(new File(ASSETS_FOLDER + "hunterIcon.png"))));
            fireBall = this.placeDefenseButton(GamePanel.ViewState.SPELL_SELECTED, FireBall.NAME, gamePanel, 
                    new ImageIcon(ImageIO.read(new File(ASSETS_FOLDER + "fireballIcon.png"))));
            snowStorm = this.placeDefenseButton(GamePanel.ViewState.SPELL_SELECTED, SnowStorm.NAME, gamePanel, 
                    new ImageIcon(ImageIO.read(new File(ASSETS_FOLDER + "snowstormIcon.png"))));
        } catch (IOException e) {
            new ErrorDialog("Error reading icon's images", inputHandler).showDialog();
        }
        this.add(cannon);
        this.buttons.put(Cannon.NAME, cannon);
        this.add(hunter);
        this.buttons.put(Hunter.NAME, hunter);
        this.add(fireBall);
        this.buttons.put(FireBall.NAME, fireBall);
        this.add(snowStorm);
        this.buttons.put(SnowStorm.NAME, snowStorm);
    }

    /**
     * Creates a defensive type button and sets its parameters.
     * @param state the state the button sets when pressed
     * @param selectedEntity the entity selected by the button
     * @param gamePanel the game panel
     * @param icon the icon of the button
     * @return the defense button
     */
    private JButton placeDefenseButton(final GamePanel.ViewState state, final String selectedEntity, 
            final GamePanel gamePanel, final ImageIcon icon) {
        final JButton button = new JButton(new ImageIcon(icon.getImage()
                .getScaledInstance(WIDTH, HEIGHT, java.awt.Image.SCALE_SMOOTH)));
        button.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                gamePanel.setState(state);
                gamePanel.setSelectedEntity(selectedEntity);
            }
        });
        return button;
    }

    /**
     * Updates the state of the buttons.
     * @param referenceEntities the entities from which are taken the informations used to update the buttons
     */
    public void update(final Set<Entity> referenceEntities) {
        for (final Entity entity : referenceEntities) {
            boolean enableState;
            JButton respectiveButton = new JButton();
            if (entity instanceof Tower) {
                enableState = ((TowerImpl) entity).getCost() <= this.world.getMoney();
                switch (entity.getName()) {
                    case Hunter.NAME:
                        respectiveButton = this.buttons.get(Hunter.NAME);
                        break;
                    case Cannon.NAME:
                        respectiveButton = this.buttons.get(Cannon.NAME);
                        break;
                    default:
                        break;
                }
            } else {
                enableState = ((SpellImpl) entity).isReady();
                switch (entity.getName()) {
                    case FireBall.NAME:
                        respectiveButton = this.buttons.get(FireBall.NAME);
                        break;
                    case SnowStorm.NAME:
                        respectiveButton = this.buttons.get(SnowStorm.NAME);
                        break;
                    default:
                        break;
                }
            }
            respectiveButton.setEnabled(enableState);
        }
    }

    /**
     * Sets the world of the game.
     * @param world
     */
    public void setWorld(final World world) {
        this.world = world;
    }

    /**
     * Disable all the buttons in the panel.
     */
    public void disableAllButtons() {
        this.buttons.values().forEach(b -> b.setEnabled(false));
    }
}
