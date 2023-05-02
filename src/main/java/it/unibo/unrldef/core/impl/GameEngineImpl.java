package it.unibo.unrldef.core.impl;

import java.util.Optional;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import it.unibo.unrldef.common.Position;
import it.unibo.unrldef.core.api.GameEngine;
import it.unibo.unrldef.graphics.api.View;
import it.unibo.unrldef.input.api.InputHandler;
import it.unibo.unrldef.input.api.Input.InputType;
import it.unibo.unrldef.model.api.Player;
import it.unibo.unrldef.model.api.World;
import it.unibo.unrldef.model.api.World.GameState;

/**
 * This class modules the engine that updates the game.
 * 
 * @author tommaso.severi2@studio.unibo.it
 */
public final class GameEngineImpl implements GameEngine {

    /**
     * This is used to manage the various states that 
     * the loop of the game can be on.
     */
    private enum LoopState {
        /**
         * The game is currently in the menu.
         */
        MENU, 
        /**
         * The game is currently int it's playing state.
         */
        PLAYING, 
        /**
         * The game is displaying the end screen.
         */
        ENDING, 
        /**
         * The game needs to be closed.
         */
        EXIT
    }

    private static final long PERIOD = 1000 / 30;
    private Player player;
    private World currentWorld;
    private InputHandler input;
    private View gameView;
    private LoopState status;

    /**
     * Builds a new GameEngine.
     * 
     * @param world  the world of the game
     * @param player the player of the game
     * @param input  the input of the game
     * @param view   the view of the game
     */
    public GameEngineImpl(final World world, final Player player, final InputHandler input, final View view) {
        this.setInput(input);
        this.setView(view);
        this.setPlayer(player);
        this.setGameWorld(world);
        this.status = LoopState.MENU;
    }

    @Override
    public void initGame(final String playerName) {
        this.player.setName(playerName);
        this.gameView.initGame();
        this.status = LoopState.PLAYING;
    }

    @Override
    public void setGameWorld(final World world) {
        this.currentWorld = world;
    }

    @SuppressFBWarnings(value = "EI_EXPOSE_REP2", justification = "Player is meant to be the same as the other objects")
    @Override
    public void setPlayer(final Player player) {
        this.player = player;
    }

    @Override
    public void setView(final View view) {
        this.gameView = view;
    }

    @SuppressFBWarnings(value = "EI_EXPOSE_REP2", justification = "Input is meant to be the same as the other objects")
    @Override
    public void setInput(final InputHandler input) {
        this.input = input;
    }

    @Override
    public void gameLoop() {
        long previousFrameStartTime = System.currentTimeMillis();
        while (this.isGameRunning()) {
            final long currentFrameStartTime = System.currentTimeMillis();
            final long elapsed = currentFrameStartTime - previousFrameStartTime;
            this.processInput();
            this.update(elapsed);
            this.render();
            this.waitForNextFrame(currentFrameStartTime);
            previousFrameStartTime = currentFrameStartTime;
        }
    }

    /**
     * Waits for the next frame.
     * 
     * @param cycleStartTime the start time of the cycle
     */
    private void waitForNextFrame(final long cycleStartTime) {
        final long elapsed = System.currentTimeMillis() - cycleStartTime;
        if (elapsed < PERIOD) {
            try {
                Thread.sleep(PERIOD - elapsed);
            } catch (InterruptedException e) {
                e.printStackTrace(); // NOPMD if this fails the game has to stop
            }
        }
    }

    /**
     * Checks if the game is running.
     * 
     * @return true if the game is running, false otherwise
     */
    private boolean isGameRunning() {
        return this.status != LoopState.EXIT;
    }

    /**
     * @return the current state of the game
     */
    private GameState gameState() {
        return this.currentWorld.gameState();
    }

    /**
     * Processes the input.
     */
    private void processInput() {
        this.input.getInputs().forEach(in -> {
            final InputType type = in.getInputType();
            final Optional<Position> position = in.getSelectedPosition();
            final Optional<String> name = in.getSelectedName();
            switch (type) {
                case PLACE_TOWER:
                    this.player.buildTower(position.get(), name.get());
                    break;
                case PLACE_SPELL:
                    this.player.throwSpell(position.get(), name.get());
                    break;
                case PLACE_HERO:
                    this.player.spawnHero(position.get(), name.get());
                    break;
                case START_GAME:
                    this.initGame(name.get());
                    break;
                case EXIT_GAME:
                    this.exitGame();
                    break;
                default:
                    break;
            }
        });
    }

    /**
     * Updates the game.
     * 
     * @param elapsed the elapsed time since last frame
     */
    private void update(final long elapsed) {
        switch (this.status) {
            case PLAYING:
                if (this.gameState() == GameState.PLAYING) {
                    this.currentWorld.updateState(elapsed);
                } else {
                    this.status = LoopState.ENDING;
                }
                break;
            default:
                break;
        }
    }

    /**
     * Renders the game.
     */
    private void render() {
        switch (this.status) {
            case MENU:
                this.gameView.renderMenu();
                break;
            case PLAYING:
                this.gameView.renderGame();
                break;
            case ENDING:
                this.gameView.renderEndGame(this.gameState());
                break;
            default:
                break;
        }
    }

    /**
     * Exits the game.
     */
    private void exitGame() {
        this.status = LoopState.EXIT;
        this.gameView.exitGame();
    }
}
