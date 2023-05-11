package it.unibo.unrldef.model.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import it.unibo.unrldef.common.Pair;
import it.unibo.unrldef.common.Position;
import it.unibo.unrldef.model.api.Bank;
import it.unibo.unrldef.model.api.Enemy;
import it.unibo.unrldef.model.api.Entity;
import it.unibo.unrldef.model.api.Hero;
import it.unibo.unrldef.model.api.Horde;
import it.unibo.unrldef.model.api.Integrity;
import it.unibo.unrldef.model.api.Path;
import it.unibo.unrldef.model.api.Player;
import it.unibo.unrldef.model.api.Tower;
import it.unibo.unrldef.model.api.Wave;
import it.unibo.unrldef.model.api.World;
import it.unibo.unrldef.model.api.Path.Direction;

/**
 * The world of the game Unreal Defense.
 * 
 * @author tommaso.ceredi@studio.unibo.it
 * @author francesco.buda3@studio.unibo.it
 */
public final class WorldImpl implements World {

    private static final long SPAWNING_TIME = 1500;
    private static final int ENEMY_POWER = 1;
    private static final int PATH_DEPHT = 3;
    private static final double SAFETY_MARGIN = 0.2;

    private final String name;
    private final Player player;
    private final Integrity castleIntegrity;
    private final Bank bank;
    private final Path path;
    private final List<Wave> waves;
    private int waveCounter;
    private final List<Tower> placedTowers;
    private final Map<String, Tower> availableTowers;
    private final Set<Position> availablePositions;
    private final List<Enemy> livingEnemies;
    private final Queue<Enemy> spawningQueue;
    private long timeToNextHorde;
    private long timeToNextSpawn;
    private final Random rand = new Random();

    private WorldImpl(final String name, final Player player, final Integrity castleIntegrity, final Path path,
            final List<Wave> waves,
            final Map<String, Tower> availableTowers, final Set<Position> validPositions, final Bank bank) {
        this.name = name;
        this.player = player;
        this.castleIntegrity = castleIntegrity;
        this.path = path;
        this.waves = waves;
        this.availableTowers = availableTowers;
        this.placedTowers = new ArrayList<>();
        this.livingEnemies = new ArrayList<>();
        this.spawningQueue = new LinkedList<>();
        this.availablePositions = validPositions;
        this.timeToNextHorde = 0;
        this.timeToNextSpawn = 0;
        this.waveCounter = 0;
        this.bank = bank;

    }

    @Override
    public void updateState(final long time) {
        // updateing time
        this.timeToNextHorde = this.timeToNextHorde - time < 0 ? 0 : this.timeToNextHorde - time;
        this.timeToNextSpawn = this.timeToNextSpawn - time < 0 ? 0 : this.timeToNextSpawn - time;
        // getting money from dead enemies and removing them
        this.livingEnemies.stream()
                .filter(Enemy::isDead)
                .forEach(x -> this.bank.addMoney(x.getDropAmount()));
        this.livingEnemies.removeAll(this.livingEnemies.stream().filter(Enemy::isDead).toList());
        // damaging castle and removing enemies that reached the end of the path
        this.livingEnemies.stream()
                .filter(Enemy::hasReachedEndOfPath)
                .forEach(x -> this.castleIntegrity.damage(ENEMY_POWER));
        this.livingEnemies.removeAll(this.livingEnemies.stream().filter(Enemy::hasReachedEndOfPath).toList());
        // updating living enemies and towers
        this.livingEnemies.forEach(x -> x.updateState(time));
        this.placedTowers.forEach(x -> x.updateState(time));
        // updating player's spells
        this.player.updateSpellState(time);
        this.player.updateHeroState(time);
        if (timeToNextHorde == 0 && !this.areWavesEnded()) {
            if (this.waves.get(this.waveCounter).isWaveOver()) {
                this.waveCounter++;
            }
            final Pair<Horde, Long> nextHorde = this.waves.get(this.waveCounter).getNextHorde().get();
            this.timeToNextHorde = nextHorde.getSecond();
            this.addToQueue(nextHorde.getFirst().getEnemies());
        }
        if (timeToNextSpawn == 0 && !this.spawningQueue.isEmpty()) {
            timeToNextSpawn = SPAWNING_TIME;
            final Enemy newEnemy = this.spawningQueue.poll();
            final Position spawningPoint = this.path.getSpawningPoint();
            final Position pos = new Position(spawningPoint.getX() + rand.nextInt(-PATH_DEPHT / 2, PATH_DEPHT / 2),
                    spawningPoint.getY() + rand.nextInt(-PATH_DEPHT / 2, PATH_DEPHT / 2));
            this.spawnEnemy(newEnemy, pos);
        }
    }

    private Boolean areWavesEnded() {
        return this.waveCounter == this.waves.size() - 1
                && this.waves.get(this.waveCounter).isWaveOver();
    }

    private List<Enemy> enemiesInArea(final Position upLeft, final Position downRight) {
        return this.livingEnemies.stream()
                .filter(x -> x.getPosition().get().getX() >= upLeft.getX())
                .filter(x -> x.getPosition().get().getX() <= downRight.getX())
                .filter(x -> x.getPosition().get().getY() >= upLeft.getY())
                .filter(x -> x.getPosition().get().getY() <= downRight.getY())
                .toList();
    }

    @Override
    public List<Enemy> sorroundingEnemies(final Position center, final double radius) {
        Position pathCur = this.path.getSpawningPoint().copy();
        boolean end = false;
        Optional<Enemy> firstInLine = Optional.empty();
        int i = 0;
        final List<Enemy> sorroundingEnemies = this.livingEnemies.stream()
                .filter(x -> distance(center, x.getPosition().get()) <= radius).collect(Collectors.toList());
        while (!end) {
            final Pair<Direction, Double> dir = this.path.getDirection(i);
            /*
             * this while loop cycles through the path segment by segment,
             * in order to find the enemy who is the most advanced in the path.
             * a and b are the ends of the current segment
             */
            Position a;
            Position b = new Position(0, 0);
            /*
             * ul and dr are respectively the up-left and down-right vertices of the
             * rectangle
             * witch describes the path segment thickness
             */
            Position ul;
            Position dr;
            Optional<Enemy> tmp = Optional.empty();
            switch (dir.getFirst()) {
                case UP:
                    a = pathCur;
                    b = new Position(a.getX(), a.getY() - dir.getSecond());
                    ul = new Position(b.getX() - (PATH_DEPHT + SAFETY_MARGIN) / 2, b.getY());
                    dr = new Position(a.getX() + (PATH_DEPHT + SAFETY_MARGIN) / 2, a.getY());
                    tmp = sorroundingEnemies.stream()
                            .filter(x -> this.enemiesInArea(ul, dr).contains(x))
                            .reduce((c, d) -> c.getPosition().get().getY() < d.getPosition().get().getY() ? c : d);
                    break;
                case DOWN:
                    a = pathCur;
                    b = new Position(a.getX(), a.getY() + dir.getSecond());
                    ul = new Position(a.getX() - (PATH_DEPHT + SAFETY_MARGIN) / 2, a.getY());
                    dr = new Position(b.getX() + (PATH_DEPHT + SAFETY_MARGIN) / 2, b.getY());
                    tmp = sorroundingEnemies.stream()
                            .filter(x -> this.enemiesInArea(ul, dr).contains(x))
                            .reduce((c, d) -> c.getPosition().get().getY() > d.getPosition().get().getY() ? c : d);
                    break;
                case RIGHT:
                    a = pathCur;
                    b = new Position(a.getX() + dir.getSecond(), a.getY());
                    ul = new Position(a.getX(), a.getY() - (PATH_DEPHT + SAFETY_MARGIN) / 2);
                    dr = new Position(b.getX(), b.getY() + (PATH_DEPHT + SAFETY_MARGIN) / 2);
                    tmp = sorroundingEnemies.stream()
                            .filter(x -> this.enemiesInArea(ul, dr).contains(x))
                            .reduce((c, d) -> c.getPosition().get().getX() > d.getPosition().get().getX() ? c : d);
                    break;
                case LEFT:
                    a = pathCur;
                    b = new Position(a.getX() - dir.getSecond(), a.getY());
                    ul = new Position(b.getX(), b.getY() - (PATH_DEPHT + SAFETY_MARGIN) / 2);
                    dr = new Position(a.getX(), a.getY() + (PATH_DEPHT + SAFETY_MARGIN) / 2);
                    tmp = sorroundingEnemies.stream()
                            .filter(x -> this.enemiesInArea(ul, dr).contains(x))
                            .reduce((c, d) -> c.getPosition().get().getX() < d.getPosition().get().getX() ? c : d);
                    break;
                default:
                    end = true;
                    break;
            }
            if (!end) {
                if (tmp.isPresent()) {
                    firstInLine = tmp;
                }
                pathCur = b;
                i++;
            }
        }
        if (firstInLine.isPresent()) {
            sorroundingEnemies.remove(firstInLine.get());
            sorroundingEnemies.add(0, firstInLine.get());
        }
        return sorroundingEnemies;
    }

    private void addToQueue(final List<Enemy> enemies) {
        this.spawningQueue.addAll(enemies);
    }

    @Override
    public Boolean tryBuildTower(final Position pos, final String towerName) {
        System.out.println("Available towers: " + this.availableTowers);
        if (this.availablePositions.contains(pos)) {
            final Tower newTower = this.availableTowers.get(towerName).copy();
            if (this.bank.trySpend(newTower.getCost())) {
                this.availablePositions.remove(pos);
                this.placedTowers.add(newTower);
                newTower.setParentWorld(this);
                newTower.setPosition(pos.getX(), pos.getY());
                System.out.println("Tower list: " + this.placedTowers);
                return true;
            }
        }
        return false;
    }

    @Override
    public void spawnEnemy(final Enemy enemy, final Position pos) {
        this.livingEnemies.add(enemy);
        enemy.setParentWorld(this);
        enemy.setPosition(pos.getX(), pos.getY());
    }

    @Override
    public List<Entity> getSceneEntities() {
        final List<Entity> ret = new ArrayList<>(this.livingEnemies);
        ret.sort((a, b) -> {
            if (a.getPosition().get().getY() > b.getPosition().get().getY()) {
                return 1;
            } else if (a.getPosition().get().getY() < b.getPosition().get().getY()) {
                return -1;
            } else {
                if (a.getPosition().get().getX() > b.getPosition().get().getX()) {
                    return 1;
                } else if (a.getPosition().get().getX() < b.getPosition().get().getX()) {
                    return -1;
                } else {
                    return 0;
                }
            }
        });
        ret.addAll(this.placedTowers);
        ret.addAll(((PlayerImpl) this.player).getActiveSpells());
        ret.addAll(((PlayerImpl) this.player).getActiveHeros());
        return ret;
    }

    @Override
    public int getHearts() {
        return this.castleIntegrity.getHearts();
    }

    @Override
    public double getMoney() {
        return this.bank.getMoney();
    }

    @Override
    public Set<Tower> getAvailableTowers() {
        return this.availableTowers.entrySet().stream()
                .map(x -> x.getValue())
                .collect(Collectors.toSet());
    }

    @Override
    public Path getPath() {
        return new PathImpl(this.path);
    }

    private double distance(final Position a, final Position b) {
        return Math.sqrt(Math.pow(a.getX() - b.getX(), 2) + Math.pow(a.getY() - b.getY(), 2));
    }

    @Override
    public GameState gameState() {
        if (this.areWavesEnded() && this.livingEnemies.isEmpty()) {
            return GameState.VICTORY;
        } else if (this.castleIntegrity.isCompromised()) {
            return GameState.DEFEAT;
        } else {
            return GameState.PLAYING;
        }
    }

    /**
     * 
     * @return the name of the world
     */
    public String getName() {
        return this.name;
    }

    @Override
    public Set<Position> getAvailablePositions() {
        return new HashSet<>(this.availablePositions);
    }

    /**
     * the world's builder.
     * 
     * @author francesco.buda3@studio.unibo.it
     */
    public static class Builder {
        private final String name;
        private final Player player;
        private Integrity castleIntegrity;
        private final Path path;
        private Bank bank;
        private final List<List<Pair<Horde, Long>>> wavesTemp;
        private final Map<String, Tower> availableTowers;
        private final Set<Position> validTowersPositions;

        /**
         * the builder's constructor.
         * 
         * @param worldName     the name of the world
         * @param player        the player
         * @param spawnPoint    the spawn point of the enemies
         * @param castleHearts  castle's number of hearts
         * @param startingMoney the starting money of the player
         */
        @SuppressFBWarnings(value = "EI_EXPOSE_REP2", justification = "Player is meant to be the same as the other objects")
        public Builder(final String worldName, final Player player, final Position spawnPoint, final int castleHearts,
                final double startingMoney) {
            this.name = worldName;
            this.player = player;
            this.path = new PathImpl(spawnPoint);
            this.wavesTemp = new ArrayList<>();
            this.castleIntegrity = new IntegrityImpl(castleHearts);
            this.bank = new BankImpl(startingMoney);
            this.availableTowers = new HashMap<>();
            this.validTowersPositions = new HashSet<>();
        }

        /**
         * adds a path segment to the path.
         * 
         * @param direction the direction of the segment
         * @param lenght    the lenght of the segment
         * @return the builder
         */
        public Builder addPathSegment(final Direction direction, final double lenght) {
            this.path.addDirection(direction, lenght);
            return this;
        }

        /**
         * adds a wave to the world.
         * 
         * @return the builder
         */
        public Builder addWave() {
            this.wavesTemp.add(new ArrayList<>());
            return this;
        }

        /**
         * adds an horde to a wave.
         * 
         * @param waveIndex     the index of the wave in which to add the horde
         * @param hordeDuration the duration of the horde in milliseconds
         * @return the builder
         */
        public Builder addHordeToWave(final int waveIndex, final long hordeDuration) {
            this.wavesTemp.get(waveIndex).add(new Pair<Horde, Long>(new HordeImpl(), hordeDuration));
            return this;
        }

        /**
         * adds an enemy to a horde.
         * 
         * @param waveIndex  the wave index where the horde to add the enemy to is
         *                   located
         * @param hordeIndex the index of the horde to add the enemy to
         * @param enemy      the enemy to add
         * @return the builder
         */
        public Builder addEnemyToHorde(final int waveIndex, final int hordeIndex, final Enemy enemy) {
            this.wavesTemp.get(waveIndex).get(hordeIndex).getFirst().addEnemy(enemy);
            return this;
        }

        /**
         * adds multiple enemies to a horde.
         * 
         * @param waveIndex       the wave index where the horde to add enemies to is
         *                        located
         * @param hordeIndex      the index of the horde to add the enemies to
         * @param enemy           the enemy to add
         * @param numberOfEnemies the number of enemies to add
         * @return the builder
         */
        public Builder addMultipleEnemiesToHorde(final int waveIndex, final int hordeIndex, final Enemy enemy,
                final short numberOfEnemies) {
            this.wavesTemp.get(waveIndex).get(hordeIndex).getFirst().addMultipleEnemies(enemy, numberOfEnemies);
            return this;
        }

        /**
         * adds a new type of tower to the list of available towers.
         * 
         * @param name  the name of the tower
         * @param tower the tower
         * @return the builder
         */
        public Builder addAvailableTower(final String name, final Tower tower) {
            this.availableTowers.put(name, tower);
            return this;
        }

        /**
         * adds a position where a tower can be built.
         * 
         * @param x the x coordinate
         * @param y the y coordinate
         * @return the builder
         */
        public Builder addTowerBuildingSpace(final double x, final double y) {
            this.validTowersPositions.add(new Position(x, y));
            return this;
        }

        /**
         * changes the default implementation of bank with a personal one.
         * 
         * @param bank
         * @return the builder
         */
        public Builder changeBank(final Bank bank) {
            this.bank = bank.copy();
            return this;
        }

        /**
         * changes the default implementation of castle integrity with a personal one.
         * 
         * @param castleIntegrity
         * @return the builder
         */
        public Builder changeCastleIntegrity(final Integrity castleIntegrity) {
            this.castleIntegrity = castleIntegrity.copy();
            return this;
        }

        /**
         * builds the world and returns it.
         * 
         * @return the world
         */
        public WorldImpl build() {

            Objects.requireNonNull(this.name);
            Objects.requireNonNull(this.player);
            Objects.requireNonNull(this.castleIntegrity);
            Objects.requireNonNull(this.path);

            final List<Wave> waves = new ArrayList<>();

            this.wavesTemp.forEach(x -> waves.add(new WaveImpl()));
            for (int i = 0; i < this.wavesTemp.size(); i++) {
                for (final Pair<Horde, Long> horde : this.wavesTemp.get(i)) {
                    waves.get(i).addHorde(horde.getFirst(), horde.getSecond());
                }
            }

            final WorldImpl ret = new WorldImpl(this.name, this.player, this.castleIntegrity, this.path, waves,
                    this.availableTowers, this.validTowersPositions, this.bank);
            for (int i = 0; i < this.wavesTemp.size(); i++) {
                for (final Pair<Horde, Long> horde : this.wavesTemp.get(i)) {
                    horde.getFirst().getEnemies().forEach(x -> x.setParentWorld(ret));
                }
            }

            this.player.setGameWorld(ret);

            return ret;
        }
    }

}
