package it.unibo.unrldef.graphics.impl;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.swing.JPanel;
import javax.swing.event.MouseInputListener;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import it.unibo.unrldef.common.Position;
import it.unibo.unrldef.input.api.InputHandler;
import it.unibo.unrldef.input.api.Input.InputType;
import it.unibo.unrldef.input.impl.InputImpl;
import it.unibo.unrldef.model.api.Enemy;
import it.unibo.unrldef.model.api.Entity;
import it.unibo.unrldef.model.api.Spell;
import it.unibo.unrldef.model.api.Tower;
import it.unibo.unrldef.model.api.World;
import it.unibo.unrldef.model.impl.Cannon;
import it.unibo.unrldef.model.impl.Cesare;
import it.unibo.unrldef.model.impl.FireBall;
import it.unibo.unrldef.model.impl.Goblin;
import it.unibo.unrldef.model.impl.Hunter;
import it.unibo.unrldef.model.impl.Orc;
import it.unibo.unrldef.model.impl.SnowStorm;
import it.unibo.unrldef.model.api.Hero;

/**
 * 
 * This is the game panel where game gets rendered on the screen.
 * 
 * @author danilo.maglia@studio.unibo.it
 * @author tommaso.ceredi@studio.unibo.it
 * @author tommaso.severi2@studio.unibo.it
 * @author francesco.buda3@studio.unibo.it
 */
public final class GamePanel extends JPanel {

    private static final long serialVersionUID = 1L;

    /**
     * Each state in which the game can be.
     * 
     * @author danilo.maglia@studio.unibo.it
     */
    public enum ViewState {
        /**
         * The game is idle.
         */
        IDLE,
        /**
         * The user is placing a tower.
         */
        TOWER_SELECTED,
        /**
         * The user is placing a spell.
         */
        SPELL_SELECTED,
        /**
         * The user is selecting a hero.
         */
        HERO_SELECTED
    }

    private static final int MAP_WIDTH_IN_UNITS = 80;
    private static final int MAP_HEIGHT_IN_UNITS = 80;
    private static final int DEFAULT_WIDTH = 600;
    private static final int DEFAULT_HEIGHT = 600;
    private static final long TOWER_ANIMATION_LENGTH = 500;

    private String selectedEntity;
    private final Set<Position> towerAvailablePositions;
    private final World gameWorld;
    private ViewState viewState;
    private final transient SpriteLoader spriteLoader = new SpriteLoader();
    private final transient Sprite orc;
    private final transient Sprite goblin;
    private final transient Sprite fireball;
    private final transient Sprite snowStorm;
    private final transient Sprite cesare;
    private final transient Sprite map;
    private final transient Sprite cannon;
    private final transient Sprite hunter;
    private final transient Sprite shootingCannon;
    private final transient Sprite explosion;
    private final transient Sprite shootingHunter;
    private final transient Sprite towerPlace;
    private final transient Sprite heart;
    private final transient Sprite money;
    private final transient Sprite missingAsset;
    private final Set<Sprite> sprites = new HashSet<>();
    private double xScale;
    private double yScale;

    private int xMapPosition;
    private int yMapPosition;

    private transient Position mousePosition;

    private final transient Map<Entity, SpriteAnimation> animationMap;

    /**
     * 
     * @param gameWorld    the game world
     * @param inputHandler the input handler
     */
    public GamePanel(final World gameWorld, final InputHandler inputHandler) {
        this.viewState = ViewState.IDLE;
        this.mousePosition = new Position(0, 0);
        this.spriteLoader
                .loadSpritesFromFile("/config/spritesInfo.json",
                        inputHandler);
        this.map = this.spriteLoader.getSprite(SpriteLoader.FIRST_MAP);
        this.orc = this.spriteLoader.getSprite(SpriteLoader.ORC);
        this.sprites.add(this.orc);
        this.goblin = this.spriteLoader.getSprite(SpriteLoader.GOBLIN);
        this.sprites.add(this.goblin);
        this.fireball = this.spriteLoader.getSprite(SpriteLoader.FIRE_BALL);
        this.sprites.add(this.fireball);
        this.snowStorm = this.spriteLoader.getSprite(SpriteLoader.SNOW_STORM);
        this.sprites.add(this.snowStorm);
        this.cesare = this.spriteLoader.getSprite(SpriteLoader.CESARE);
        this.sprites.add(this.cesare);
        this.cannon = this.spriteLoader.getSprite(SpriteLoader.CANNON);
        this.sprites.add(this.cannon);
        this.hunter = this.spriteLoader.getSprite(SpriteLoader.HUNTER);
        this.sprites.add(this.hunter);
        this.shootingCannon = this.spriteLoader.getSprite(SpriteLoader.SHOOTING_CANNON);
        this.sprites.add(this.shootingCannon);
        this.explosion = this.spriteLoader.getSprite(SpriteLoader.EXPLOSION);
        this.sprites.add(this.explosion);
        this.shootingHunter = this.spriteLoader.getSprite(SpriteLoader.SHOOTING_HUNTER);
        this.sprites.add(this.shootingHunter);
        this.towerPlace = this.spriteLoader.getSprite(SpriteLoader.TOWER_PLACE);
        this.sprites.add(this.towerPlace);
        this.heart = this.spriteLoader.getSprite(SpriteLoader.HEART);
        this.sprites.add(this.heart);
        this.money = this.spriteLoader.getSprite(SpriteLoader.MONEY);
        this.sprites.add(this.money);
        this.missingAsset = this.spriteLoader.getSprite(SpriteLoader.MISSING_ASSET);
        this.sprites.add(this.missingAsset);

        this.animationMap = new HashMap<>();
        this.gameWorld = gameWorld;
        this.towerAvailablePositions = new HashSet<>(this.gameWorld.getAvailablePositions());
        this.setSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
        this.scaleAll(DEFAULT_WIDTH, DEFAULT_HEIGHT);

        this.addComponentListener(new ComponentListener() {
            @Override
            public void componentResized(final ComponentEvent e) {
                scaleAll(getWidth(), getHeight());
            }

            @Override
            public void componentMoved(final ComponentEvent e) {
            }

            @Override
            public void componentShown(final ComponentEvent e) {
            }

            @Override
            public void componentHidden(final ComponentEvent e) {
            }
        });

        final MouseInputListener mouse = new MouseInputListener() {

            @Override
            public void mouseClicked(final MouseEvent e) {
                final Position pView = new Position(e.getX(), e.getY());
                final Position pModel = fromRealPositionToPosition(pView);
                switch (viewState) {
                    case TOWER_SELECTED:
                        final int towerWidth = towerPlace.getScaledDimension().getFirst();
                        final int towerHeight = towerPlace.getScaledDimension().getSecond();
                        towerAvailablePositions.stream()
                                .filter(pos -> {
                                    final Position realPos = fromPositionToRealPosition(pos);
                                    return isInSquare(pView,
                                            new Position(realPos.getX() - towerWidth / 2.0,
                                                    realPos.getY() - towerHeight / 2.0),
                                            new Position(realPos.getX() + towerWidth / 2.0,
                                                    realPos.getY() + towerHeight / 2.0));
                                })
                                .findFirst().ifPresent(modelP -> {
                                    towerAvailablePositions.remove(modelP);
                                    inputHandler.addInput(new InputImpl(
                                            InputType.PLACE_TOWER, modelP, selectedEntity));
                                });
                        break;
                    case SPELL_SELECTED:
                        inputHandler.addInput(new InputImpl(InputType.PLACE_SPELL, pModel,
                                selectedEntity));
                        break;
                    case HERO_SELECTED:
                        inputHandler.addInput(new InputImpl(InputType.PLACE_HERO, pModel,
                                selectedEntity));
                        break;
                    default:
                        break;
                }
                viewState = ViewState.IDLE; // reset the view state every time the mouse is clicked
            }

            @Override
            public void mousePressed(final MouseEvent e) {
            }

            @Override
            public void mouseReleased(final MouseEvent e) {
            }

            @Override
            public void mouseEntered(final MouseEvent e) {
            }

            @Override
            public void mouseExited(final MouseEvent e) {
            }

            @Override
            public void mouseDragged(final MouseEvent e) {
            }

            @Override
            public void mouseMoved(final MouseEvent e) {
                mousePosition = new Position(e.getX(), e.getY());
            }
        };

        this.addMouseListener(mouse);
        this.addMouseMotionListener(mouse);
    }

    /**
     * Set the state of the panel.
     * 
     * @param state the state to set
     */
    public void setState(final ViewState state) {
        this.viewState = state;
    }

    /**
     * Set the selected entity.
     * 
     * @param entity the entity to set
     */
    public void setSelectedEntity(final String entity) {
        this.selectedEntity = entity;
    }

    @SuppressFBWarnings(value = "BC_UNCONFIRMED_CAST", justification = "It's a Graphics2D object")
    @Override
    public void paint(final Graphics g) {
        super.paint(g);
        final Graphics2D graphic = (Graphics2D) g;
        graphic.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphic.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        graphic.clearRect(0, 0, this.getWidth(), this.getHeight());

        this.renderBackground(graphic);
        this.renderMap(graphic);
        this.renderHearts(graphic);
        this.renderMoney(graphic);

        for (final Entity entity : gameWorld.getSceneEntities()) {
            this.renderEntity(graphic, entity);
        }

        switch (viewState) {
            case TOWER_SELECTED:
                this.renderTowersSquare(graphic);
                break;
            case SPELL_SELECTED:
                this.renderSpellMouseRange(graphic);
                break;
            case HERO_SELECTED:
                this.renderHeroMouseRange(graphic);
                break;
            default:
                break;
        }
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    private void renderBackground(final Graphics2D graphic) {
        final Color firstColor = new Color(23, 79, 120);
        final Color secondColor = new Color(21, 95, 110);
        final GradientPaint cp = new GradientPaint(0, this.getHeight(), firstColor, this.getWidth(), 0, secondColor);
        graphic.setPaint(cp);
        graphic.fillRect(0, 0, this.getWidth(), this.getHeight());
        graphic.setColor(Color.BLACK);
    }

    private void renderTowersSquare(final Graphics2D graphic) {
        final int towerWidth = towerPlace.getScaledDimension().getFirst();
        final int towerHeight = towerPlace.getScaledDimension().getSecond();
        towerAvailablePositions.stream()
                .map(x -> this.fromPositionToRealPosition(x))
                .filter(pos -> this.isInSquare(mousePosition,
                        new Position(pos.getX() - towerPlace.getScaledDimension().getFirst() / 2.0,
                                pos.getY() - towerWidth / 2.0),
                        new Position(pos.getX() + towerPlace.getScaledDimension().getFirst() / 2.0,
                                pos.getY() + towerHeight / 2.0)))
                .findFirst()
                .ifPresent(towerSquare -> {
                    int radius = 0;
                    final Position modelP = fromRealPositionToPosition(towerSquare);
                    if (Hunter.NAME.equals(selectedEntity)) {
                        radius = (int) (Hunter.RADIOUS);
                    } else if (Cannon.NAME.equals(selectedEntity)) {
                        radius = (int) (Cannon.RADIOUS);
                    }
                    if (radius != 0) {
                        graphic.setColor(Color.GRAY);
                        final Position realPL = fromPositionToRealPosition(
                                new Position(modelP.getX() - radius, modelP.getY() - radius));
                        final Position realPR = fromPositionToRealPosition(
                                new Position(modelP.getX() + radius, modelP.getY() + radius));
                        graphic.drawOval((int) realPL.getX(), (int) realPL.getY(),
                                (int) (realPR.getX() - realPL.getX()), (int) (realPR.getY() - realPL.getY()));
                    }
                });
        for (final Position p : this.towerAvailablePositions) {
            final Position pos = this.towerPlace.getApplicationPoint(p);
            final Position realPos = fromPositionToRealPosition(pos);
            graphic.drawImage(this.towerPlace.getScaledSprite(), (int) realPos.getX(), (int) realPos.getY(), null);
        }
        graphic.setColor(Color.BLACK);
    }

    private void renderSpellMouseRange(final Graphics2D graphic) {
        if (this.mousePosition.getY() < this.getHeight() - 2 && this.mousePosition.getX() < this.getWidth() - 2
                && this.mousePosition.getY() > 0 && this.mousePosition.getX() > 0) {
            Sprite asset = null;
            switch (selectedEntity) {
                case FireBall.NAME:
                    asset = this.fireball;
                    break;
                case SnowStorm.NAME:
                    asset = this.snowStorm;
                    break;
                default:
                    asset = this.missingAsset;
                    break;
            }
            final Position mPos = this.fromRealPositionToPosition(this.mousePosition);
            final Position realPos = this.fromPositionToRealPosition(asset.getApplicationPoint(mPos));
            graphic.drawImage(asset.getScaledSprite(), (int) realPos.getX(), (int) realPos.getY(), null);
        }
    }

    private void renderHeroMouseRange(final Graphics2D graphic) {
        if (this.mousePosition.getY() < this.getHeight() - 2 && this.mousePosition.getX() < this.getWidth() - 2
                && this.mousePosition.getY() > 0 && this.mousePosition.getX() > 0) {
            final double radius = Cesare.MOVEMENT_RANGE;
            final Position mPos = this.fromRealPositionToPosition(this.mousePosition);
            final Position realPos = this.fromPositionToRealPosition(
                    this.cesare.getApplicationPoint(mPos));
            graphic.drawImage(this.cesare.getScaledSprite(), (int) realPos.getX(),
                    (int) realPos.getY(), null);
            graphic.setColor(Color.GRAY);
            final Position realPL = fromPositionToRealPosition(
                    new Position(mPos.getX() - radius, mPos.getY() - radius));
            final Position realPR = fromPositionToRealPosition(
                    new Position(mPos.getX() + radius, mPos.getY() + radius));
            graphic.drawOval((int) realPL.getX(), (int) realPL.getY(),
                    (int) (realPR.getX() - realPL.getX()), (int) (realPR.getY() - realPL.getY()));
            graphic.setColor(Color.BLACK);
        }
    }

    private void renderEntity(final Graphics2D graphic, final Entity entity) {
        //System.out.println("Rendering entity: " + entity);
        if (entity instanceof Enemy) {
            this.renderEnemy(graphic, entity);
        } else if (entity instanceof Spell) {
            this.renderSpell(graphic, entity);
        } else if (entity instanceof Tower) {
            this.renderTower(graphic, entity);
        } else if (entity instanceof Hero) {
            this.renderHero(graphic, entity);
        }
    }

    private void renderHero(final Graphics2D graphic, final Entity hero) {
        Sprite heroAsset = null;
        double startingHealth = 0;
        switch (hero.getName()) {
            case Cesare.NAME:
                heroAsset = this.cesare;
                startingHealth = Cesare.HEALTH;
                break;
            default:
                heroAsset = this.missingAsset;
                break;
        }

        // barra della vita sopra l'eroe, deve essere di colore verde e quando la vita diminuisce diventa rossa
        final double healthPercentage = ((Hero) hero).getHealth() / startingHealth;
        final int width = heroAsset.getScaledDimension().getFirst();
        final Position realPos = this.fromPositionToRealPosition(heroAsset.getApplicationPoint(hero.getPosition().get()));
        final int x = (int) realPos.getX();
        final int y = (int) realPos.getY();
        final int healthBarY = (int) (y - 1 * yScale);
        final int barHeight = 5;
        graphic.setColor(Color.RED);
        graphic.fillRect(x, healthBarY, width, barHeight);
        graphic.setColor(Color.GREEN);
        graphic.fillRect(x, healthBarY, (int) (width * healthPercentage), barHeight);

        graphic.drawImage(heroAsset.getScaledSprite(), x, y, null);
    }

    private void renderTower(final Graphics2D graphic, final Entity tower) {
        Sprite towerAsset = null;
        final int electrodeHeight = 4;
        final Optional<Enemy> target = ((Tower) tower).getTarget();
        final Position rayStartPos = this.fromPositionToRealPosition(new Position(
                tower.getPosition().get().getX(), tower.getPosition().get().getY() - electrodeHeight));
        this.animationMap.putIfAbsent(tower, new SpriteAnimation(TOWER_ANIMATION_LENGTH));
        final SpriteAnimation animation = this.animationMap.get(tower);
        if (target.isPresent()) {
            animation.startAnimation(System.currentTimeMillis(), target.get());
        }
        // switch for the tower asset
        switch (tower.getName()) {
            case Cannon.NAME:
                towerAsset = animation.isAnimationRunning() ? this.shootingCannon : this.cannon;
                break;
            case Hunter.NAME:
                towerAsset = animation.isAnimationRunning() ? this.shootingHunter : this.hunter;
                break;
            default:
                towerAsset = this.missingAsset;
                break;
        }

        if (animation.isAnimationRunning() && animation.getTarget().isDead()) {
            renderEnemy(graphic, animation.getTarget());
        }
        final Position towerPos = tower.getPosition().get();
        final Position realTowerPos = fromPositionToRealPosition(towerAsset.getApplicationPoint(towerPos));
        graphic.drawImage(towerAsset.getScaledSprite(), (int) realTowerPos.getX(), (int) realTowerPos.getY(), null);
        // switch for the tower attack animation
        switch (tower.getName()) {
            case Hunter.NAME:
                final int strokeWidth = 5;
                if (animation.isAnimationRunning()) {
                    final Position realTargetPosition = this
                            .fromPositionToRealPosition(animation.getTarget().getPosition().get());
                    graphic.setColor(Color.BLUE);
                    graphic.setStroke(new BasicStroke(strokeWidth));
                    graphic.drawLine((int) rayStartPos.getX(), (int) rayStartPos.getY(),
                            (int) realTargetPosition.getX(), (int) realTargetPosition.getY());
                    graphic.setStroke(new BasicStroke(1));
                    graphic.setColor(Color.BLACK);
                    animation.updateTimePassed();
                } else {
                    animation.resetAnimation();
                }
                break;
            case Cannon.NAME:
                if (animation.isAnimationRunning()) {
                    final Position explosionPos = this.fromPositionToRealPosition(
                            this.explosion.getApplicationPoint(animation.getStartTargetPosition()));
                    graphic.drawImage(this.explosion.getScaledSprite(), (int) explosionPos.getX(),
                            (int) explosionPos.getY(), null);
                    animation.updateTimePassed();
                } else {
                    animation.resetAnimation();
                }
                break;
            default:
                break;
        }
    }

    private void renderEnemy(final Graphics2D graphic, final Entity enemy) {
        final Enemy e = (Enemy) enemy;
        double startingHealth = 0;
        Sprite asset = this.missingAsset;
        switch (e.getName()) {
            case Orc.NAME:
                asset = orc;
                startingHealth = Orc.HEALTH;
                break;
            case Goblin.NAME:
                asset = goblin;
                startingHealth = Goblin.HEALTH;
                break;
            default:
                break;
        }
        final double healthPercentage = e.getHealth() / startingHealth;
        final int width = asset.getScaledDimension().getFirst();
        final Position pos = enemy.getPosition().get();
        final Position realPos = this.fromPositionToRealPosition(asset.getApplicationPoint(pos));
        final int x = (int) realPos.getX();
        final int y = (int) realPos.getY();
        final int healthBarY = (int) (y - 1 * yScale);
        final int barHeight = 5;
        graphic.drawImage(asset.getScaledSprite(), (int) x, y, null);
        graphic.setColor(Color.RED);

        graphic.fillRect(x, healthBarY, width, barHeight);
        graphic.setColor(Color.GREEN);

        graphic.fillRect(x, healthBarY, (int) (width * healthPercentage), barHeight);
    }

    private void renderSpell(final Graphics2D graphic, final Entity spell) {
        Sprite asset = null;
        switch (spell.getName()) {
            case FireBall.NAME:
                asset = this.fireball;
                break;
            case SnowStorm.NAME:
                asset = this.snowStorm;
                break;
            default:
                asset = this.missingAsset;
                break;
        }
        final Position pos = spell.getPosition().get();
        final Position realPos = this.fromPositionToRealPosition(asset.getApplicationPoint(pos));
        graphic.drawImage(asset.getScaledSprite(), (int) realPos.getX(), (int) realPos.getY(), null);
    }

    private void renderHearts(final Graphics2D graphic) {
        final int n = this.gameWorld.getHearts();
        final int denominator = 4;
        final int startX = this.xMapPosition + this.heart.getScaledDimension().getFirst() / denominator;
        final int startY = this.yMapPosition + this.heart.getScaledDimension().getSecond() / denominator;
        for (int i = 0; i < n; i++) {
            graphic.drawImage(this.heart.getScaledSprite(),
                    startX + i * this.heart.getScaledDimension().getFirst(), startY, null);
        }
    }

    private void renderMoney(final Graphics2D graphic) {
        final int denominator = 6;
        final int startX = this.xMapPosition + this.money.getScaledDimension().getFirst() / 4;
        final int startY = this.yMapPosition + this.money.getScaledDimension().getSecond() * 4 / 3;
        graphic.drawImage(this.money.getScaledSprite(), startX, startY, null);
        graphic.setFont(new Font("", Font.PLAIN, this.money.getScaledDimension().getSecond() / 2));
        graphic.drawString(" " + this.gameWorld.getMoney() + " ",
                startX + this.money.getScaledDimension().getFirst(),
                startY + this.money.getScaledDimension().getSecond() * 4 / denominator);
    }

    private void renderMap(final Graphics2D graphic) {
        graphic.drawImage(this.map.getScaledSprite(), this.xMapPosition, this.yMapPosition, null);
    }

    private Position fromPositionToRealPosition(final Position pos) {
        final double newX = pos.getX() * this.xScale + this.xMapPosition;
        final double newY = pos.getY() * this.yScale + this.yMapPosition;
        return new Position(newX, newY);
    }

    private Position fromRealPositionToPosition(final Position pos) {
        final double newX = ((pos.getX() - this.xMapPosition)) / xScale;
        final double newY = ((pos.getY() - this.yMapPosition)) / yScale;
        return new Position(newX, newY);
    }

    private void scaleAll(final int realWidth, final int realHeight) {
        double width;
        double height;

        width = realWidth;
        height = width * MAP_HEIGHT_IN_UNITS / MAP_WIDTH_IN_UNITS;
        xMapPosition = 0;
        yMapPosition = (int) Math.floor((realHeight - height) / 2);
        if (height > realHeight) {
            height = realHeight;
            width = height * MAP_WIDTH_IN_UNITS / MAP_HEIGHT_IN_UNITS;
            yMapPosition = 0;
            xMapPosition = (int) Math.floor((realWidth - width) / 2);
        }

        xScale = width / MAP_WIDTH_IN_UNITS;
        yScale = height / MAP_HEIGHT_IN_UNITS;

        map.scale(xScale, yScale);
        sprites.forEach(x -> x.scale(xScale, yScale));
    }

    private boolean isInSquare(final Position pos, final Position upLeft, final Position downRight) {
        return pos.getX() >= upLeft.getX()
                && pos.getX() <= downRight.getX()
                && pos.getY() <= downRight.getY()
                && pos.getY() >= upLeft.getY();
    }

    private void writeObject(final ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
    }

    private void readObject(final ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
    }
}
