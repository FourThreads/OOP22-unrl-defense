package it.unibo.unrldef.graphics.impl;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.IOException;
import java.util.Objects;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import it.unibo.unrldef.common.Pair;
import it.unibo.unrldef.input.api.InputHandler;
import it.unibo.unrldef.input.api.Input.InputType;
import it.unibo.unrldef.input.impl.InputImpl;

/**
 * This class modules the menu panel.
 * 
 * @author tommaso.ceredi@studio.unibo.it
 */
public final class MenuPanel extends JPanel {

    private static final String ASSETS_FOLDER = "/assets/";
    private transient Image title;
    private static final long serialVersionUID = 1L;
    private final JButton exitButton, startButton;
    private final JTextField nameField;
    private final transient InputHandler inputHandler;
    private double xScale = 1;
    private double yScale = 1;
    private static final int DEFAULT_WIDTH = 600;
    private static final int DEFAULT_HEIGHT = 400;
    private static final Color BACKGROUND_COLOR = new Color(255, 255, 255);
    private static final int FONT_SUBTITLE_SIZE = 15;
    private static final int PADDING = 15;
    private static final int BUTTON_WIDTH = 140;
    private static final int BUTTON_HEIGHT = 20;
    private static final int BUTTON_PADDING = 30;
    private static final int CENTER = 70;
    private static final Pair<Integer, Integer> TITLE_CENTER = new Pair<>(85, 80);

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }


    /**
     * Constructor of the MenuPanel.
     * 
     * @param inputHandler the input handler
     */
    @SuppressFBWarnings(value = "EI_EXPOSE_REP2", justification = "Input is meant to be changed by the view")
    public MenuPanel(final InputHandler inputHandler) {
        super();
        try {
            this.title = ImageIO.read(this.getClass().getResourceAsStream(ASSETS_FOLDER + "logo.png"));
        } catch (IOException e) {
            new ErrorDialog("Error while loading the logo", inputHandler).showDialog();
        }

        this.addComponentListener(new ComponentListener() {
            @Override
            public void componentResized(final ComponentEvent e) {
                final int currWidth = (int) getWidth();
                final int currHeight = (int) getHeight();
                xScale = (double) currWidth / DEFAULT_WIDTH;
                yScale = (double) currHeight / DEFAULT_HEIGHT;
            }

            @Override
            public void componentMoved(final ComponentEvent e) {
            }

            @Override
            public void componentShown(final ComponentEvent e) {
            }

            @Override
            public void componentHidden(final ComponentEvent e) {
            }
        });
        this.inputHandler = Objects.requireNonNull(inputHandler);
        this.setLayout(null);
        this.setBackground(BACKGROUND_COLOR);
        this.nameField = new JTextField();
        this.nameField.setBounds(DEFAULT_WIDTH / 2, DEFAULT_HEIGHT / 2, BUTTON_WIDTH, BUTTON_HEIGHT);
        this.startButton = new JButton("Start");
        this.startButton.setBounds(DEFAULT_WIDTH / 2, DEFAULT_HEIGHT / 2 + BUTTON_PADDING, BUTTON_WIDTH, BUTTON_HEIGHT);
        this.startButton.addActionListener(e -> {
            if (this.nameField.getText().length() > 0) {
                this.inputHandler.addInput(new InputImpl(InputType.START_GAME, this.nameField.getText()));
            } else {
                // ! TO FIX
                this.inputHandler.addInput(new InputImpl(InputType.START_GAME, "Player1234"));
            }
        });
        exitButton = new JButton("Exit");
        exitButton.setBounds(DEFAULT_HEIGHT / 2, DEFAULT_HEIGHT / 2, BUTTON_WIDTH, BUTTON_HEIGHT);
        exitButton.addActionListener(e -> {
            this.inputHandler.addInput(new InputImpl(InputType.EXIT_GAME));
        });
        this.add(nameField);
        this.add(startButton);
        this.add(exitButton);
    }

    @Override
    public void paint(final Graphics g) {
        super.paint(g);
        final int width = (int) (this.getWidth() / 2 - TITLE_CENTER.getFirst());
        final int height = (int) (this.getHeight() / 2 - TITLE_CENTER.getSecond());
        final int fontSubtitle = Math.min((int) xScale, (int) yScale) == 0 ? FONT_SUBTITLE_SIZE
                : (int) ((FONT_SUBTITLE_SIZE) * Math.min((int) xScale, (int) yScale));
        g.drawImage(this.title, this.getWidth() / 4, this.getHeight() / 10,
                (int) this.getWidth() / 2, (int) this.getHeight() / 3, null);
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, fontSubtitle));
        g.drawString("Inserisci il tuo nome:", width + PADDING, height + CENTER);
        this.nameField.setBounds((int) ((DEFAULT_WIDTH / 2 - CENTER) * xScale), (int) ((DEFAULT_HEIGHT / 2) * yScale),
                (int) (BUTTON_WIDTH * xScale), (int) (BUTTON_HEIGHT * yScale));
        this.startButton.setBounds((int) ((DEFAULT_WIDTH / 2 - CENTER) * xScale),
                (int) ((DEFAULT_HEIGHT / 2 + BUTTON_PADDING) * yScale), (int) (BUTTON_WIDTH * xScale),
                (int) (BUTTON_HEIGHT * yScale));
        this.exitButton.setBounds((int) ((DEFAULT_WIDTH / 2 - CENTER) * xScale),
                (int) ((DEFAULT_HEIGHT / 2 + CENTER) * yScale),
                (int) (BUTTON_WIDTH * xScale), (int) (BUTTON_HEIGHT * yScale));
        // ! TO FIX
        if (this.nameField.getText().length() >= 0) {
            this.startButton.setEnabled(true);
        } else {
            this.startButton.setEnabled(false);
        }
    }
}
