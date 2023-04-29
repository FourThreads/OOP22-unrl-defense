package it.unibo.unrldef.graphics.impl;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import it.unibo.unrldef.graphics.api.View;
import it.unibo.unrldef.input.api.InputHandler;
import it.unibo.unrldef.input.api.Input.InputType;
import it.unibo.unrldef.input.impl.InputImpl;
import it.unibo.unrldef.model.api.Entity;
import it.unibo.unrldef.model.api.Player;
import it.unibo.unrldef.model.api.World;
import it.unibo.unrldef.model.api.World.GameState;

import java.util.HashSet;
import java.util.Set;

/**
 * Implements the view of the game.
 * 
 * @author danilo.maglia@studio.unibo.it
 * @author tommaso.severi2@studio.unibo.it
 */
public final class ViewImpl implements View {

    private static final String ASSETS_FOLDER = "/assets/";
    private static final int MAX_NAME_LENGTH = 8;

    private final GamePanel gamePanel;
    private final DefenseButtonPanel buttonPanel;
    private final JFrame frame;
    private final Player player;
    private final World world;
    private final MenuPanel menuPanel;
    private final InputHandler inputHandler;

    /**
     * Builds the view of the game starting with the menu.
     * 
     * @param player       the player of the game
     * @param world        the world of the game
     * @param inputHandler the input handler of the game
     */
    @SuppressFBWarnings(value = "EI_EXPOSE_REP2", 
            justification = "Input and player are meant to be the same as the other objects")
    public ViewImpl(final Player player, final World world, final InputHandler inputHandler) {
        this.player = player;
        this.world = world;
        this.inputHandler = inputHandler;
        this.frame = new JFrame("Unreal Defense");
        this.gamePanel = new GamePanel(world, inputHandler);
        this.buttonPanel = new DefenseButtonPanel(this.gamePanel, this.world, this.inputHandler);
        this.menuPanel = new MenuPanel(inputHandler);
        this.frame.getContentPane().add(this.menuPanel);
        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.frame.setSize(this.menuPanel.getPreferredSize());
        this.frame.setMinimumSize(this.menuPanel.getPreferredSize());
        this.frame.setLocationRelativeTo(null);
        this.frame.pack();
        this.frame.setVisible(true);
    }

    @Override
    public void initGame() {
        this.frame.getContentPane().remove(this.menuPanel);

        //this.buttonPanel.add(this.createExitButton());
        this.buttonPanel.add(this.addPlayerName());
        this.frame.getContentPane().add(gamePanel, BorderLayout.CENTER);
        this.frame.getContentPane().add(this.buttonPanel, BorderLayout.EAST);
        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.frame.setSize(this.frame.getPreferredSize());
        this.frame.setMinimumSize(this.frame.getPreferredSize());
        this.frame.setLocationRelativeTo(null);
        this.frame.pack();
        this.frame.setVisible(true);
    }

    @Override
    public void renderGame() {
        this.updateHUD();
        this.gamePanel.repaint();
    }

    @Override
    public void renderMenu() {
        this.menuPanel.repaint();
    }

    /**
     * Renders the name of the player with a max of 8 characters.
     * 
     * @return the label with the name of the player
     */
    private JLabel addPlayerName() {
        final JLabel playerName = new JLabel();
        final String name = this.player.getName().length() > MAX_NAME_LENGTH 
                ? this.player.getName().substring(0, MAX_NAME_LENGTH) + "..." 
                : this.player.getName();
        playerName.setText("Player: " + name);
        return playerName;
    }

    /**
     * Creates a new button to exit the game.
     * 
     * @return the button to exit the game
     */
    private JButton createExitButton() {
        JButton exit = null;
        try {
            exit = new JButton(new ImageIcon(
                    new ImageIcon(ImageIO.read(this.getClass().getResourceAsStream(ASSETS_FOLDER + "exit.png")))
                            .getImage()
                            .getScaledInstance(DefenseButtonPanel.WIDTH, DefenseButtonPanel.HEIGHT,
                                    java.awt.Image.SCALE_SMOOTH)));
            exit.setPreferredSize(new Dimension(DefenseButtonPanel.WIDTH, DefenseButtonPanel.HEIGHT));
            exit.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(final ActionEvent e) {
                    inputHandler.addInput(new InputImpl(InputType.EXIT_GAME));
                }
            });
        } catch (IOException e) {
            new ErrorDialog("Error reading the images files", this.inputHandler).showDialog();
        }
        return exit;
    }

    /**
     * Updates the HUD state.
     */
    private void updateHUD() {
        final Set<Entity> buttonsEntities = new HashSet<>(this.world.getAvailableTowers());
        buttonsEntities.addAll(this.player.getSpells());
        this.buttonPanel.update(buttonsEntities);
    }

    @Override
    public void renderEndGame(final GameState state) {
        final Graphics g = this.frame.getGraphics();
        // the font is setted to be 1/8 of the width of the frame just
        // as a reference so that it is always readable
        g.setFont(new Font("Serif", Font.PLAIN, this.frame.getWidth() / 8));
        String displayState = "";
        switch (state) {
            case DEFEAT:
                g.setColor(Color.BLACK);
                displayState = "GAME OVER";
                break;
            case VICTORY:
                g.setColor(Color.RED);
                displayState = "YOU WON!";
                break;
            default:
                break;
        }
        this.buttonPanel.disableAllButtons();
        // the text is centered in the middle of the frame using the height and
        // the width of the frame as a reference so that it is always centered
        g.drawString(displayState, this.frame.getWidth() / 10, this.frame.getHeight() / 2);
        g.setColor(Color.GREEN);
    }

    @Override
    public void exitGame() {
        this.frame.dispose();
    }
}
