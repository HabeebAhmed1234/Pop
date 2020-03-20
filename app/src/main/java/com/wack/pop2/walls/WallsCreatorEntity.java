package com.wack.pop2.walls;

import com.wack.pop2.BaseEntity;
import com.wack.pop2.GameFixtureDefs;
import com.wack.pop2.gameiconstray.GameIconsHostTrayEntity;
import com.wack.pop2.GameResources;
import com.wack.pop2.GameSceneTouchListenerEntity;
import com.wack.pop2.collision.CollisionFilters;
import com.wack.pop2.entitymatchers.WallsEntityMatcher;
import com.wack.pop2.eventbus.EventBus;
import com.wack.pop2.eventbus.GameEvent;
import com.wack.pop2.fixturedefdata.WallEntityUserData;
import com.wack.pop2.physics.PhysicsFactory;
import com.wack.pop2.physics.util.Vec2Pool;
import com.wack.pop2.resources.sounds.GameSoundsManager;
import com.wack.pop2.resources.sounds.SoundId;
import com.wack.pop2.resources.textures.GameTexturesManager;
import com.wack.pop2.utils.GeometryUtils;

import org.andengine.entity.IEntity;
import org.andengine.entity.primitive.Line;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.input.touch.TouchEvent;
import org.andengine.util.color.AndengineColor;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;

import java.util.List;

import static org.andengine.input.touch.TouchEvent.ACTION_CANCEL;
import static org.andengine.input.touch.TouchEvent.ACTION_DOWN;
import static org.andengine.input.touch.TouchEvent.ACTION_MOVE;
import static org.andengine.input.touch.TouchEvent.ACTION_OUTSIDE;
import static org.andengine.input.touch.TouchEvent.ACTION_UP;

/**
 * Depending on if walls are toggled on allows for the creation of walls on the screen
 */
public class WallsCreatorEntity extends BaseEntity implements GameSceneTouchListenerEntity.SceneTouchListener {

    private static final float WALL_HEIGHT_PX = 30;
    private static final float MAX_WALL_WIDTH = 500;
    private static final float MIN_WALL_WIDTH = 200;

    private WallsStateMachine stateMachine;
    private WallsIconEntity wallsIconEntity;
    private WallsDeletionHandlerFactoryEntity wallsDeletionHandlerFactory;
    private GameSceneTouchListenerEntity touchListenerEntity;
    private GameTexturesManager gameTexturesManager;
    private GameIconsHostTrayEntity gameIconsTrayEntity;
    private GameSoundsManager soundManager;

    private Vec2 initialPoint;

    private WallEntityUserData userData;
    private Line wallSprite;

    public WallsCreatorEntity(
            WallsStateMachine stateMachine,
            WallsIconEntity wallsIconEntity,
            WallsDeletionHandlerFactoryEntity wallsDeletionHandlerFactory,
            GameSceneTouchListenerEntity touchListenerEntity,
            GameTexturesManager gameTexturesManager,
            GameIconsHostTrayEntity gameIconsTrayEntity,
            GameSoundsManager soundManager,
            GameResources gameResources) {
        super(gameResources);
        this.stateMachine = stateMachine;
        this.wallsIconEntity = wallsIconEntity;
        this.wallsDeletionHandlerFactory = wallsDeletionHandlerFactory;
        this.touchListenerEntity = touchListenerEntity;
        this.gameTexturesManager = gameTexturesManager;
        this.gameIconsTrayEntity = gameIconsTrayEntity;
        this.soundManager = soundManager;
    }

    @Override
    public void onCreateScene() {
        touchListenerEntity.addSceneTouchListener(this);
    }

    @Override
    public void onDestroy() {
        touchListenerEntity.removeSceneTouchListener(this);
    }

    @Override
    public boolean onSceneTouchEvent(Scene scene, TouchEvent touchEvent) {
        switch (touchEvent.getAction()) {
            case ACTION_DOWN:
                onActionDown(touchEvent);
                break;
            case ACTION_UP:
            case ACTION_CANCEL:
            case ACTION_OUTSIDE:
                onActionUp(touchEvent);
                break;
            case ACTION_MOVE:
                onActionMove(touchEvent);
                break;

        }
        return true;
    }

    private boolean shouldStartPlacingWall(TouchEvent touchEvent) {
        Sprite wallsIcon = gameIconsTrayEntity.getIcon(GameIconsHostTrayEntity.IconId.WALLS_ICON);
        return !isWallBeingPlaced() &&
                stateMachine.getCurrentState() == WallsStateMachine.State.TOGGLED_ON &&
                wallsIconEntity.hasInventory() &&
                !wallsIcon.contains(touchEvent.getX(), touchEvent.getY()) &&
                !isTouchingWallDeleteIcon(touchEvent);
    }

    private boolean isTouchingWallDeleteIcon(TouchEvent touchEvent) {
        for (IEntity wall : getAllWalls()) {
            WallEntityUserData userData = (WallEntityUserData) wall.getUserData();
            if (userData.wallDeleteIcon.contains(touchEvent.getX(), touchEvent.getY())) {
                return true;
            }
        }
        return false;
    }

    private List<IEntity> getAllWalls() {
        return scene.query(new WallsEntityMatcher());
    }

    private boolean isWallBeingPlaced() {
        return userData != null && wallSprite != null && initialPoint != null;
    }

    private void onActionDown(TouchEvent touchEvent) {
        if (shouldStartPlacingWall(touchEvent)) {
            soundManager.getSound(SoundId.HAMMER_UP).play();
            initialPoint = Vec2Pool.obtain(touchEvent.getX(), touchEvent.getY());
            createWall();
            spanWall(touchEvent);
        }
    }

    private void onActionMove(TouchEvent touchEvent) {
        if (isWallBeingPlaced()) {
            spanWall(touchEvent);
        }
    }

    private void onActionUp(TouchEvent touchEvent) {
        if (!isWallBeingPlaced()) {
            return;
        }

        soundManager.getSound(SoundId.HAMMER_DOWN).play();

        spanWall(touchEvent);
        bakeWall();

        userData = null;
        wallSprite = null;
        initialPoint = null;
    }

    /**
     * Once the wall position and size is decided we can bake in the physics body
     */
    private void bakeWall() {
        final FixtureDef wallFixtureDef = GameFixtureDefs.WALL_FIXTURE_DEF;
        wallFixtureDef.setFilter(CollisionFilters.WALL_FILTER);
        wallFixtureDef.setUserData(userData);
        Body wallBody = PhysicsFactory.createLineBody( physicsWorld, wallSprite, BodyType.STATIC, wallFixtureDef);
        userData.wallDeleteIcon = WallDeleteIconUtil.getWallDeletionSprite(hostActivity.getActivityContext(), wallSprite, wallBody, gameTexturesManager, vertexBufferObjectManager);
        addToSceneWithTouch(userData.wallDeleteIcon, wallsDeletionHandlerFactory.getWallDeletionHandler());

        EventBus.get().sendEvent(GameEvent.WALL_PLACED);
    }

    private void spanWall(TouchEvent touchEvent) {
        if (initialPoint != null) {
            setWallBetweenPoints(initialPoint.x, initialPoint.y, touchEvent.getX(), touchEvent.getY());
        }
    }

    private void createWall() {
        userData = new WallEntityUserData();
        wallSprite = new Line(0, 0, 0, 0, vertexBufferObjectManager);
        wallSprite.setUserData(userData);
        wallSprite.setLineWidth(WALL_HEIGHT_PX);
        wallSprite.setColor(AndengineColor.WHITE);

        addToScene(wallSprite);
    }

    private float clipWallLength(float wallLength) {
        if (wallLength < MIN_WALL_WIDTH) {
            return MIN_WALL_WIDTH;
        } else if (wallLength > MAX_WALL_WIDTH) {
            return MAX_WALL_WIDTH;
        }
        return wallLength;
    }

    private void setWallBetweenPoints(float x1, float y1, float x2, float y2) {
        float wallLength = GeometryUtils.distanceBetween(x1, y1, x2, y2);
        wallLength = clipWallLength(wallLength);
        float angle = (float) Math.toRadians(GeometryUtils.getAngle(x1, y1, x2, y2));
        float xComponent = (float) Math.cos(angle) * wallLength;
        float yComponent = (float) Math.sin(angle) * wallLength;
        wallSprite.setPosition(x1, y1, x1 + xComponent, y1 + yComponent);
    }
}
