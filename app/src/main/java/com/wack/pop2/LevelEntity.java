package com.wack.pop2;

import com.wack.pop2.collision.CollisionFilters;
import com.wack.pop2.fixturedefdata.LevelWallEntityUserData;
import com.wack.pop2.physics.PhysicsFactory;
import com.wack.pop2.utils.ScreenUtils;

import org.andengine.entity.primitive.Line;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.background.Background;
import org.andengine.util.color.AndengineColor;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;

import static com.wack.pop2.GameFixtureDefs.WALL_FIXTURE_DEF;

/**
 * This class creates the level with all of its physics entities. This includes the walls and
 * other obstacles
 */
public class LevelEntity extends BaseEntity {

    private static final float RAMP_WIDTH = ScreenUtils.getSreenSize().width * 5;
    private static final float RAMP_HEIGHT = ScreenUtils.getSreenSize().height * 3;

    private static FixtureDef levelWallFixtureDef = createWallFixtureDef();

    public LevelEntity(GameResources gameResources) {
        super(gameResources);
    }

    @Override
    public void onCreateScene() {
        createLeftFunnelWall();
        createRightFunnelWall();
        createLeftLevelWall();
        createRightLevelWall();

        // set background color
        scene.setBackground(new Background(AndengineColor.BLACK));
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
        float screenWidth = ScreenUtils.getSreenSize().width;
        PhysicsFactory.createLineBody(
                physicsWorld,
                new Line(screenWidth, 0, screenWidth + RAMP_WIDTH, -RAMP_HEIGHT, vertexBufferObjectManager),
                BodyType.STATIC,
                levelWallFixtureDef);
    }

    private static FixtureDef createWallFixtureDef() {
        FixtureDef fixtureDef = WALL_FIXTURE_DEF;
        fixtureDef.setFilter(CollisionFilters.WALL_FILTER);
        fixtureDef.setUserData(new LevelWallEntityUserData());
        return fixtureDef;
    }
}
