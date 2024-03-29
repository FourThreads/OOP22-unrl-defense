package it.unibo.unrldef.graphics.impl;

import it.unibo.unrldef.common.Pair;
import it.unibo.unrldef.common.Position;

import java.awt.Image;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * the class that represents a sprite and its dimension.
 */
public class Sprite {
    private final Pair<Integer, Integer> dim;
    private Pair<Integer, Integer> scaledDim;
    private final Image spriteImage;
    private Image scaledSprite;

    /**
     * the constructor.
     * 
     * @param width  the real width of the sprite
     * @param height the real height of the sprite
     * @param sprite the image of the sprite
     */
    public Sprite(final int width, final int height, final Image sprite) {
        this.dim = new Pair<>(width, height);
        this.spriteImage = sprite.getScaledInstance(sprite.getWidth(null), -1, Image.SCALE_SMOOTH);
        this.scaledSprite = this.spriteImage;
        this.scaledDim = this.dim;
    }

    /**
     * Scales the sprite, given the scale factors.
     * 
     * @param xScale the scale factor for the x axis
     * @param yScale the scale factor for the y axis
     */
    public void scale(final double xScale, final double yScale) {
        this.scaledDim = new Pair<>((int) (this.dim.getFirst() * xScale),
                (int) (this.dim.getSecond() * yScale));
        this.scaledSprite = spriteImage.getScaledInstance(this.scaledDim.getFirst(), this.scaledDim.getSecond(),
                Image.SCALE_SMOOTH);
    }

    /**
     * 
     * @return the sprite in the right dimension
     */
    @SuppressFBWarnings("EI_EXPOSE_REP")
    public Image getScaledSprite() {
        return this.scaledSprite;
    }

    /**
     * 
     * @return the scaled dimension of the sprite
     */
    public Pair<Integer, Integer> getScaledDimension() {
        return new Pair<Integer, Integer>(this.scaledDim.getFirst(), this.scaledDim.getSecond());
    }

    /**
     *
     * @param pos
     * @return the point in witch to render the sprite in order to have it centered
     */
    public Position getApplicationPoint(final Position pos) {
        return new Position(pos.getX() - this.dim.getFirst().doubleValue() / 2,
                pos.getY() - this.dim.getSecond().doubleValue() / 2);
    }
}
