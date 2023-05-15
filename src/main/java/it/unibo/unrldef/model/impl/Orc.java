package it.unibo.unrldef.model.impl;

/**
 * An enemy in the game Unreal Defense.
 * 
 * @author danilo.maglia@studio.unibo.it
 * @author tommaso.ceredi@studio.unibo.it
 */
public class Orc extends EnemyImpl {
    /**
     * The name of the enemy.
     */
    public static final String NAME = "orc";
    /**
     * The speed of the enemy.
     */
    public static final double SPEED = 3.0;
    /**
     * The health of the enemy.
     */
    public static final double HEALTH = 80.0;
    /**
     * The amount of money that the enemy drops when it dies.
     */
    public static final double DROP = 50.0;

    private static final double DAMAGE_ATTACK = 10.0;

    private static final double RANGE_ATTACK = 5.0;

    private static final long ATTACK_RATE = 1000;
    /**
     * Create a new Orc.
     */
    public Orc() {
        super(Orc.NAME, Orc.HEALTH, Orc.SPEED, Orc.DROP, Orc.RANGE_ATTACK, Orc.DAMAGE_ATTACK, Orc.ATTACK_RATE);
    }

    @Override
    public final Orc copy() {
        final Orc enemy = new Orc();
        this.getPosition().ifPresent(p -> enemy.setPosition(p.getX(), p.getY()));
        enemy.setParentWorld(this.getParentWorld());
        return enemy;
    }


}
