package com.wack.pop2.walls;

import android.util.Log;

import com.wack.pop2.BaseEntity;
import com.wack.pop2.GameFixtureDefs;
import com.wack.pop2.GameResources;
import com.wack.pop2.GameSceneTouchListenerEntity;
import com.wack.pop2.fixturedefdata.WallEntityUserData;
import com.wack.pop2.physics.PhysicsFactory;
import com.wack.pop2.physics.util.Vec2Pool;
import com.wack.pop2.resources.textures.GameTexturesManager;
import com.wack.pop2.resources.textures.TextureId;
import com.wack.pop2.utils.GeometryUtils;

import org.andengine.entity.primitive.Line;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.input.touch.TouchEvent;
import org.andengine.util.adt.transformation.Transformation;
import org.andengine.util.color.AndengineColor;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;

import static com.wack.pop2.utils.GeometryUtils.distanceBetween;
import static com.wack.pop2.utils.GeometryUtils.getAngle;
import static org.andengine.input.touch.TouchEvent.ACTION_CANCEL;
import static org.andengine.input.touch.TouchEvent.ACTION_DOWN;
import static org.andengine.input.touch.TouchEvent.ACTION_MOVE;
import static org.andengine.input.touch.TouchEvent.ACTION_OUTSIDE;
import static org.andengine.input.touch.TouchEvent.ACTION_UP;

/**
 * Manages the creation of walls that can catch or redirect bubbles
 */
public class WallsManager extends BaseEntity implements GameSceneTouchListenerEntity.SceneTouchListener {

    private static final float WALL_HEIGHT_PX = 30;

    GameSceneTouchListenerEntity touchListenerEntity;
    GameTexturesManager gameTexturesManager;

    private Vec2 initialPoint;

    private WallEntityUserData userData;
    private Line wallSprite;

    public WallsManager(GameSceneTouchListenerEntity touchListenerEntity, GameTexturesManager gameTexturesManager, GameResources gameResources) {
        super(gameResources);
        this.touchListenerEntity = touchListenerEntity;
        this.gameTexturesManager = gameTexturesManager;
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

    private boolean isWallBeingPlaced() {
        return userData != null && wallSprite != null && initialPoint != null;
    }

    private void onActionDown(TouchEvent touchEvent) {
        if (!isWallBeingPlaced()) {
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

        spanWall(touchEvent);
        bakeWall(touchEvent);

        userData = null;
        wallSprite = null;
        initialPoint = null;
    }

    /**
     * Once the wall position and size is decided we can bake in the physics body
     */
    private void bakeWall(TouchEvent touchEvent) {
        final FixtureDef wallFixtureDef = GameFixtureDefs.WALL_FIXTURE_DEF;
        wallFixtureDef.setUserData(userData);
        float[] center = GeometryUtils.getCenterPoint(wallSprite.getX1(), wallSprite.getY1(), wallSprite.getX2(), wallSprite.getY2());
        PhysicsFactory.createBoxBody(
                physicsWorld,
                center[0],
                center[1],
                GeometryUtils.distanceBetween(wallSprite.getX1(), wallSprite.getY1(), wallSprite.getX2(), wallSprite.getY2()),
                wallSprite.getLineWidth(),
                GeometryUtils.getAngle(wallSprite.getX1(), wallSprite.getY1(), wallSprite.getX2(), wallSprite.getY2()),
                BodyType.STATIC,
                wallFixtureDef);
    }

    private float[] getSpriteCenter(TouchEvent touchEvent) {
        float centerX = initialPoint.x + (touchEvent.getX() - initialPoint.x) / 2;
        float centerY = initialPoint.y + (touchEvent.getY() - initialPoint.y) / 2;
        return new float[] {centerX, centerY};
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

    private void setWallBetweenPoints(float x1, float y1, float x2, float y2) {
        wallSprite.setPosition(x1, y1, x2, y2);
    }
}
