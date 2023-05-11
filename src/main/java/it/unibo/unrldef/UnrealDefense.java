package it.unibo.unrldef;

import it.unibo.unrldef.core.api.GameEngine;
import it.unibo.unrldef.core.impl.GameEngineImpl;
import it.unibo.unrldef.graphics.api.View;
import it.unibo.unrldef.graphics.impl.ViewImpl;
import it.unibo.unrldef.input.api.InputHandler;
import it.unibo.unrldef.input.impl.InputHandlerImpl;
import it.unibo.unrldef.model.api.Player;
import it.unibo.unrldef.model.api.World;
import it.unibo.unrldef.model.impl.LevelBuilder;
import it.unibo.unrldef.model.impl.PlayerImpl;

/**
 * Main class.
 * @author tommaso.ceredi@studio.unibo.it
 * @author tommaso.severi2@studio.unibo.it
 * @author francesco.buda3@studio.uniboit
 * @author danilo.maglia@studio.unibo.it
 */
public final class UnrealDefense {

    /**
     * Unused constructor.
     */
    private UnrealDefense() { }

    /**
     * Main method.
     * @param args unused
     */
    public static void main(final String[] args) {
        startLevelOne();
    }

    /**
     * Starts the first level.
     */
    public static void startLevelOne() {
        final Player p = new PlayerImpl();
        final LevelBuilder level = new LevelBuilder(p);
        final World world = level.fromFile("/config/levelOne.json");
        final InputHandler input = new InputHandlerImpl();
        if (world != null) {
            final View view = new ViewImpl(p, world, input);
            final GameEngine engine = new GameEngineImpl(world, p, input, view);
            engine.gameLoop();
        } else {
            System.err.println("Error loading the level"); // NOPMD it's vital for the game to stop in this case
        }
    }
}
