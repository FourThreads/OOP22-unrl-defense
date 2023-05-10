package it.unibo.unrldef.input.api;

import java.util.Optional;

import it.unibo.unrldef.common.Position;

/**
 * Interface of an input.
 * 
 * @author tommaso.severi2@studio.unibo.it
 */
public interface Input {
    /**
     * Enumeration of the possible input types.
     */
    enum InputType {
        /**
         * The player has selected the start button.
         */
        START_GAME,
        /**
         * The player has selected the exit button.
         */
        EXIT_GAME,
        /**
         * The player has selected a tower button.
         */
        PLACE_TOWER,
        /**
         * The player has selected a spell button.
         */
        PLACE_SPELL,
        /**
         * The player has selected a hero button.
         */
        PLACE_HERO,
        /**
         * The default selection of the player.
         */
        MISS,

        RESTART
    }

    /**
     * @return the input type
     */
    InputType getInputType();

    /**
     * @return an optional of the selected position
     */
    Optional<Position> getSelectedPosition();

    /**
     * @return an optional of the selected name
     */
    Optional<String> getSelectedName();
}
