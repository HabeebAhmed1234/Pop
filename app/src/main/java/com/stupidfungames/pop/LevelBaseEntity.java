package com.stupidfungames.pop;

import com.stupidfungames.pop.binder.BinderEnity;
import com.stupidfungames.pop.collision.CollisionFilters;
import com.stupidfungames.pop.fixturedefdata.LevelWallEntityUserData;
import com.stupidfungames.pop.physics.PhysicsFactory;
import com.stupidfungames.pop.resources.textures.GameTexturesManager;
import com.stupidfungames.pop.resources.textures.TextureId;
import com.stupidfungames.pop.utils.GeometryUtils;
import com.stupidfungames.pop.utils.ScreenUtils;

import org.andengine.entity.primitive.Line;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.background.SpriteBackground;
import org.andengine.entity.sprite.Sprite;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;

import static com.stupidfungames.pop.GameFixtureDefs.WALL_FIXTURE_DEF;

/**
 * This class creates the level with all of its physics entities. This includes the walls and
 * other obstacles
 */
public class LevelBaseEntity extends BaseEntity {

    private static final float RAMP_WIDTH = ScreenUtils.getSreenSize().widthPx * 5;
    private static final float RAMP_HEIGHT = ScreenUtils.getSreenSize().heightPx * 3;

    private static FixtureDef levelWallFixtureDef = createWallFixtureDef();

    public LevelBaseEntity(BinderEnity parent) {
        super(parent);
    }

    @Override
    public void onCreateScene() {
        createLeftFunnelWall();
        createRightFunnelWall();
        createLeftLevelWall();
        createRightLevelWall();

        createBackground();
    }

    private void createBackground() {
        final Sprite backgroundSprite = getBackgroundSprite();
        GeometryUtils.scaleToFixScreen(backgroundSprite);
        scene.setBackground(new SpriteBackground(backgroundSprite));
    }

    private void createLeftLevelWall() {
        PhysicsFactory.createBoxBody(physicsWorld, new Rectangle(0, 0, 2, levelHeight, vertexBufferObjectManager), BodyType.STATIC, levelWallFixtureDef);
    }

    private void createRightLevelWall() {
        PhysicsFactory.createBoxBody(physicsWorld, new Rectangle(levelWidth - 2, 0, 2, levelHeight, vertexBufferObjectManager), BodyType.STATIC, levelWallFixtureDef);
    }

    private void createLeftFunnelWall() {
        PhysicsFactory.createLineBody(
                physicsWorld,
                new Line(0, 0, -RAMP_WIDTH, -RAMP_HEIGHT, vertexBufferObjectManager),
                BodyType.STATIC,
                levelWallFixtureDef);
    }

    private void createRightFunnelWall() {
        float screenWidth = ScreenUtils.getSreenSize().widthPx;
        PhysicsFactory.createLineBody(
                physicsWorld,
                new Line(screenWidth, 0, screenWidth + RAMP_WIDTH, -RAMP_HEIGHT, vertexBufferObjectManager),
                BodyType.STATIC,
                levelWallFixtureDef);
    }

    private Sprite getBackgroundSprite() {
        return new Sprite(0,
                0,
                get(GameTexturesManager.class).getTextureRegion(TextureId.BACKGROUND),
                vertexBufferObjectManager);
    }

    private static FixtureDef createWallFixtureDef() {
        FixtureDef fixtureDef = WALL_FIXTURE_DEF;
        fixtureDef.setFilter(CollisionFilters.WALL_FILTER);
        fixtureDef.setUserData(new LevelWallEntityUserData());
        return fixtureDef;
    }
}
