package it.unibo.unrldef.core;

import java.util.Optional;

import it.unibo.unrldef.common.Pair;
import it.unibo.unrldef.common.Position;
import it.unibo.unrldef.graphics.api.View;
import it.unibo.unrldef.graphics.impl.ViewImpl;
import it.unibo.unrldef.input.api.Input;
import it.unibo.unrldef.input.impl.PlayerInput;
import it.unibo.unrldef.model.api.*;
import it.unibo.unrldef.model.impl.PlayerImpl;

public class GameEngine {

    private final long period = 1000 / 30;
    private final Player player;
    private World currentWorld;
    private final Input input;
    private final View gameView; 
    private boolean started = false;

    public GameEngine(final World world) {
        this.input = new PlayerInput();
        this.player = new PlayerImpl();
        this.setGameWorld(world);
        this.gameView = new ViewImpl(player, this.currentWorld, this.input);
    }

    public void initGame(final String playerName) {
        this.player.setName(playerName);
        this.gameView.initGame();
        this.started = true;
    }

    public void setGameWorld(final World world) {
        this.currentWorld = world;
    }

    public void GameLoop() {
        while (!started) {
            this.processInput();
            this.gameView.updateMenu();
        }
        long previousFrameStartTime = System.currentTimeMillis();
        while (!this.currentWorld.isGameOver()) {
            final long currentFrameStartTime = System.currentTimeMillis();
            final long elapsed = currentFrameStartTime-previousFrameStartTime;
            this.processInput();
            this.update(elapsed);
            this.render();
            this.waitForNextFrame(currentFrameStartTime);
            previousFrameStartTime = currentFrameStartTime;
        }
        System.out.println("Hai vinto :)");
        System.exit(0);
    }

    private void waitForNextFrame(long cycleStartTime) {
        final long elapsed = System.currentTimeMillis() - cycleStartTime;
        if (elapsed < this.period) {
            try {
				Thread.sleep(period - elapsed);
			} catch (Exception ex){ }
        }
    }

    private void processInput() {
        Optional<Pair<Position, Input.HitType>> lastHit = input.getLastHit();
        if (lastHit.isPresent()) {
            final Position selectedPosition = lastHit.get().getFirst();
            final Optional<String> selectedName = this.input.getSelectedName();
            switch(lastHit.get().getSecond()) {
                case PLACE_TOWER:
                    this.player.buildNewTower(selectedPosition, selectedName.get());
                    break;
                case PLACE_SPELL:
                    this.player.throwSpell(selectedName.get(), selectedPosition);
                    break;
                case START_GAME:
                    this.initGame(selectedName.get());
                    break;
                case EXIT_GAME:
                    this.exitGame();
                    break;
                default:
                    break;
            }
        }
    }

    private void update(final long elapsed) {
        this.currentWorld.updateState(elapsed);
    }

    private void render() {
        this.gameView.render();
    }

    private void exitGame() {
        System.exit(0);
    }
}
