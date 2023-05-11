package it.unibo.unrldef.graphics.api;

import it.unibo.unrldef.model.api.World.GameState;

/**
 * Models the view of the game.
 * 
 * @author danilo.maglia@studio.unibo.it
 * @author tommaso.severi2@studio.unibo.it
 */
public interface View {
    /**
     * Render the game playing.
     */
    void renderGame();

    /**
     * Updates the menu of the game.
     */
    void renderMenu();

    /**
     * Starts the rendering of the game.
     */
    void initGame();

    /**
     * Renders the end of the game.
     * 
     * @param state the state of the game
     */
    void renderEndGame(GameState state);

    /**
     * Exits the game.
     */
    void exitGame();

    /**
     * Restarts the game.
     */
    void restartGame();
}
