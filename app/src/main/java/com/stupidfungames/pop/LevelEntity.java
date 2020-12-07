package com.stupidfungames.pop;

import static com.stupidfungames.pop.GameFixtureDefs.FLOOR_SENSOR_FIXTURE_DEF;
import static com.stupidfungames.pop.GameFixtureDefs.WALL_FIXTURE_DEF;

import com.stupidfungames.pop.binder.BinderEnity;
import com.stupidfungames.pop.collision.CollisionFilters;
import com.stupidfungames.pop.fixturedefdata.FloorEntityUserData;
import com.stupidfungames.pop.fixturedefdata.LevelWallEntityUserData;
import com.stupidfungames.pop.physics.PhysicsFactory;
import com.stupidfungames.pop.resources.textures.GameTexturesManager;
import com.stupidfungames.pop.resources.textures.TextureId;
import com.stupidfungames.pop.utils.GeometryUtils;
import com.stupidfungames.pop.utils.ScreenUtils;
import javax.microedition.khronos.opengles.GL10;
import org.andengine.entity.primitive.Line;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.background.SpriteBackground;
import org.andengine.entity.sprite.Sprite;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;

/**
 * This class creates the level with all of its physics entities. This includes the walls and other
 * obstacles
 */
public class LevelEntity extends BaseEntity {

  private static final float RAMP_WIDTH = ScreenUtils.getSreenSize().widthPx * 5;
  private static final float RAMP_HEIGHT = ScreenUtils.getSreenSize().heightPx * 3;

  private static FixtureDef levelWallFixtureDef = createWallFixtureDef();

  public LevelEntity(BinderEnity parent) {
    super(parent);
  }

  @Override
  public void onCreateScene() {
    createLeftFunnelWall();
    createRightFunnelWall();
    createLeftLevelWall();
    createRightLevelWall();
    createFloorDetectorWall();

    createBackground();
  }

  private void createBackground() {
    final Sprite backgroundSprite = getBackgroundSprite();
    GeometryUtils.scaleToFixScreen(backgroundSprite);
    backgroundSprite.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
    backgroundSprite.setAlpha(0.4f);
    scene.setBackground(new SpriteBackground(backgroundSprite));
  }

  private void createLeftLevelWall() {
    PhysicsFactory.createBoxBody(physicsWorld,
        new Rectangle(0, 0, 2, levelHeight, vertexBufferObjectManager),
        BodyType.STATIC,
        levelWallFixtureDef);
  }

  private void createRightLevelWall() {
    PhysicsFactory.createBoxBody(physicsWorld,
        new Rectangle(levelWidth - 2, 0, 2, levelHeight, vertexBufferObjectManager),
        BodyType.STATIC,
        levelWallFixtureDef);
  }

  private void createLeftFunnelWall() {
    PhysicsFactory.createLineBody(
        physicsWorld,
        new Line(0, 0, -RAMP_WIDTH, -RAMP_HEIGHT, vertexBufferObjectManager),
        BodyType.STATIC,
        levelWallFixtureDef);
  }

  private void createRightFunnelWall() {
    final float screenWidth = ScreenUtils.getSreenSize().widthPx;
    PhysicsFactory.createLineBody(
        physicsWorld,
        new Line(screenWidth, 0, screenWidth + RAMP_WIDTH, -RAMP_HEIGHT,
            vertexBufferObjectManager),
        BodyType.STATIC,
        levelWallFixtureDef);
  }

  private void createFloorDetectorWall() {
    final Rectangle floorDetector = new Rectangle(0, levelHeight, levelWidth, 10,
        vertexBufferObjectManager);
    floorDetector.setAlpha(0);
    final FixtureDef floorFixtureDef = FLOOR_SENSOR_FIXTURE_DEF;
    floorFixtureDef.setUserData(new FloorEntityUserData());
    floorFixtureDef.setFilter(CollisionFilters.WALL_FILTER);
    PhysicsFactory.createBoxBody(physicsWorld, floorDetector, BodyType.STATIC, floorFixtureDef);
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
