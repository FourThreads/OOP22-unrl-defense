package it.unibo.unrldef.model.impl;

/**
 * An enemy in the game Unreal Defense.
 * 
 * @author danilo.maglia@studio.unibo.it
 */
public class Goblin extends EnemyImpl {
    /**
     * The name of the enemy.
     */
    public static final String NAME = "goblin";
    /**
     * The speed of the enemy.
     */
    public static final double SPEED = 5.0;
    /**
     * The health of the enemy.
     */
    public static final double HEALTH = 40.0;
    /**
     * The amount of money that the enemy drops when it dies.
     */
    public static final double DROP = 30.0;

    public static final double RANGE_ATTACK = 5.0;

    public static final double DAMAGE_ATTACK = 5.0;

    public static final long ATTACK_RATE = 1000;
    /**
     * Create a new Goblin.
     */
    public Goblin() {
        super(Goblin.NAME, Goblin.HEALTH, Goblin.SPEED, Goblin.DROP, Goblin.RANGE_ATTACK, Goblin.DAMAGE_ATTACK, Goblin.ATTACK_RATE);
    }

    public Goblin copy() {
        final Goblin enemy = new Goblin();
        if (this.getPosition().isPresent()) {
            enemy.setPosition(this.getPosition().get().getX(), this.getPosition().get().getY());
        }

        enemy.setParentWorld(this.getParentWorld());
        return enemy;
    }
}
