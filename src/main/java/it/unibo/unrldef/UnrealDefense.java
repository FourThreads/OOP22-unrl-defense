package it.unibo.unrldef;

import it.unibo.unrldef.core.api.GameEngine;
import it.unibo.unrldef.core.impl.GameEngineImpl;
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
        final Player p = new PlayerImpl();
        final LevelBuilder level = new LevelBuilder(p);
        final World world = level.fromFile("resources/config/levelOne.json");
        if (world != null) {
            final GameEngine engine = new GameEngineImpl(world, p);
            engine.menuLoop();
        } else {
            System.err.println("Error loading the level"); // NOPMD it's vital for the game to stop in this case
        }
    }
}
