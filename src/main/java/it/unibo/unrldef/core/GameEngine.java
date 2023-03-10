package it.unibo.unrldef.core;

import java.util.Optional;

import it.unibo.unrldef.common.Pair;
import it.unibo.unrldef.common.Position;
import it.unibo.unrldef.graphics.api.View;
import it.unibo.unrldef.graphics.impl.ViewImpl;
import it.unibo.unrldef.input.api.Input;
import it.unibo.unrldef.input.impl.PlayerInput;
import it.unibo.unrldef.model.api.*;

public class GameEngine {

    private final long period = 30;
    private Player player;
    private World currentWorld;
    private final Input input;
    private View view; 


    public GameEngine() {
        this.input = new PlayerInput();
    }

    public void initGame(final Player player, final World world) {
        this.player = player;
        this.view = new ViewImpl(player, world, this.input);
        this.setGameWorld(world);
    }

    public void setGameWorld(final World world) {
        this.currentWorld = world;
        this.player.setGameMap(world);
    }

    public void GameLoop() {
        long previousFrameStartTime = System.currentTimeMillis();
        while (!this.currentWorld.isGameOver()) {
            final long currentFrameStartTime = System.currentTimeMillis();
            final long elapsed = currentFrameStartTime-previousFrameStartTime;
            processInput();
            update(elapsed);
            render();
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
			} catch (Exception ex){}
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
                case SELECTION:
                    break;
            }
        }
    }

    private void update(final long elapsed) {
        this.currentWorld.updateState(elapsed);
    }

    private void render() {
        this.view.render();
    }
}
