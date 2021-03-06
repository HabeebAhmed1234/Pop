package com.stupidfungames.pop.walls;

import static org.andengine.input.touch.TouchEvent.ACTION_CANCEL;
import static org.andengine.input.touch.TouchEvent.ACTION_DOWN;
import static org.andengine.input.touch.TouchEvent.ACTION_MOVE;
import static org.andengine.input.touch.TouchEvent.ACTION_OUTSIDE;
import static org.andengine.input.touch.TouchEvent.ACTION_UP;

import android.util.Pair;
import com.stupidfungames.pop.BaseEntity;
import com.stupidfungames.pop.GameFixtureDefs;
import com.stupidfungames.pop.GameSceneTouchListenerEntity;
import com.stupidfungames.pop.binder.BinderEnity;
import com.stupidfungames.pop.collision.CollisionFilters;
import com.stupidfungames.pop.entitymatchers.WallsEntityMatcher;
import com.stupidfungames.pop.eventbus.EventBus;
import com.stupidfungames.pop.eventbus.GameEvent;
import com.stupidfungames.pop.fixturedefdata.WallEntityUserData;
import com.stupidfungames.pop.gameiconstray.GameIconsHostTrayEntity;
import com.stupidfungames.pop.physics.PhysicsFactory;
import com.stupidfungames.pop.physics.util.Vec2Pool;
import com.stupidfungames.pop.resources.sounds.GameSoundsManager;
import com.stupidfungames.pop.resources.sounds.SoundId;
import com.stupidfungames.pop.resources.textures.GameTexturesManager;
import com.stupidfungames.pop.savegame.SaveGame;
import com.stupidfungames.pop.savegame.SaveGame.WallCoord;
import com.stupidfungames.pop.utils.GeometryUtils;
import com.stupidfungames.pop.utils.ScreenUtils;
import java.util.ArrayList;
import java.util.List;
import org.andengine.entity.IEntity;
import org.andengine.entity.primitive.Line;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.input.touch.TouchEvent;
import org.andengine.util.color.AndengineColor;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;

/**
 * Depending on if walls are toggled on allows for the creation of walls on the screen
 */
public class WallsCreatorEntity extends BaseEntity implements
    GameSceneTouchListenerEntity.SceneTouchListener {

  private static final int WALL_HEIGHT_PX = 20;
  private static final int MAX_WALL_WIDTH = 600;
  private static final int MIN_WALL_WIDTH = 300;

  private Vec2 initialPoint;
  private Pair<Line, WallEntityUserData> pendingWallData;

  public WallsCreatorEntity(BinderEnity parent) {
    super(parent);
  }

  @Override
  public void onCreateScene() {
    get(GameSceneTouchListenerEntity.class).addSceneTouchListener(this);
  }

  @Override
  public void onSaveGame(SaveGame saveGame) {
    super.onSaveGame(saveGame);
    List<IEntity> lines = scene.query(new WallsEntityMatcher());
    if (!lines.isEmpty() && saveGame.wallCoords == null) {
      saveGame.wallCoords = new ArrayList<>();
    }
    for (IEntity lineEntity : lines) {
      Line line = (Line) lineEntity;
      saveGame.wallCoords
          .add(new WallCoord(line.getX1(), line.getY1(), line.getX2(), line.getY2()));
    }
  }

  @Override
  public void onLoadGame(SaveGame saveGame) {
    super.onLoadGame(saveGame);
    if (saveGame.wallCoords == null) {
      return;
    }

    for (WallCoord wallCoord : saveGame.wallCoords) {
      Pair<Line, WallEntityUserData> loadedWall = createWall(wallCoord.x1, wallCoord.y1,
          wallCoord.x2, wallCoord.y2);
      bakeWall(loadedWall.first, loadedWall.second, false, false);
    }
  }

  @Override
  public void onDestroy() {
    get(GameSceneTouchListenerEntity.class).removeSceneTouchListener(this);
  }

  @Override
  public boolean onSceneTouchEvent(Scene scene, TouchEvent touchEvent) {
    switch (touchEvent.getAction()) {
      case ACTION_DOWN:
        return onActionDown(touchEvent);
      case ACTION_CANCEL:
      case ACTION_OUTSIDE:
        cancelWallPlacement();
        return false;
      case ACTION_UP:
        return onActionUp();
      case ACTION_MOVE:
        return onActionMove(touchEvent);
    }
    return false;
  }

  public void cancelWallPlacement() {
    if (isWallBeingPlaced()) {
      Vec2Pool.recycle(initialPoint);
      initialPoint = null;
      removeFromScene(pendingWallData.first);
      pendingWallData = null;
      get(GameSoundsManager.class).getSound(SoundId.SCRAP).play();
    }
  }

  private boolean shouldStartPlacingWall(TouchEvent touchEvent) {
    Sprite wallsIcon = get(GameIconsHostTrayEntity.class)
        .getIcon(GameIconsHostTrayEntity.IconId.WALLS_ICON);
    return !isWallBeingPlaced() &&
        !isPointInExcludedZone(touchEvent.getX(), touchEvent.getY()) &&
        get(WallsStateMachine.class).getCurrentState() == WallsStateMachine.State.TOGGLED_ON &&
        get(WallsInventoryIconEntity.class).hasInventory() &&
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
    return pendingWallData != null && initialPoint != null;
  }

  private boolean onActionDown(TouchEvent touchEvent) {
    if (shouldStartPlacingWall(touchEvent)) {
      get(GameSoundsManager.class).getSound(SoundId.HAMMER_UP).play();
      if (initialPoint == null) {
        initialPoint = Vec2Pool.obtain();
      }
      initialPoint.set(touchEvent.getX(), touchEvent.getY());
      pendingWallData = createWall();
      spanWall(touchEvent);
      return true;
    }
    return false;
  }

  private boolean onActionMove(TouchEvent touchEvent) {
    if (isWallBeingPlaced() && !isPointInExcludedZone(touchEvent.getX(), touchEvent.getY())) {
      spanWall(touchEvent);
      return true;
    }
    return false;
  }

  private boolean onActionUp() {
    if (!isWallBeingPlaced()) {
      return false;
    }

    get(GameSoundsManager.class).getSound(SoundId.HAMMER_DOWN).play();

    // spanWall(touchEvent);
    bakeWall(pendingWallData.first, pendingWallData.second, true, true);

    pendingWallData = null;
    Vec2Pool.recycle(initialPoint);
    initialPoint = null;

    return true;
  }

  /**
   * Once the wall position and size is decided we can bake in the physics body
   */
  private void bakeWall(
      final Line wallLine,
      final WallEntityUserData wallUserData,
      final boolean isVisible,
      final boolean notifyWallPlaced) {
    final FixtureDef wallFixtureDef = GameFixtureDefs.WALL_FIXTURE_DEF;
    wallFixtureDef.setFilter(CollisionFilters.WALL_FILTER);
    wallFixtureDef.setUserData(wallUserData);
    Body wallBody = PhysicsFactory
        .createLineBody(physicsWorld, wallLine, BodyType.STATIC, wallFixtureDef);
    wallUserData.wallDeleteIcon = WallDeleteIconFactory
        .getWallDeletionSprite(wallLine, wallBody,
            get(GameTexturesManager.class), vertexBufferObjectManager);
    addToSceneWithTouch(wallUserData.wallDeleteIcon,
        get(WallsDeletionHandlerFactoryEntity.class).getWallDeletionHandler());
    wallUserData.wallDeleteIcon.setVisible(isVisible);

    if (notifyWallPlaced) {
      EventBus.get().sendEvent(GameEvent.WALL_PLACED);
    }
  }

  private void spanWall(TouchEvent touchEvent) {
    if (initialPoint != null) {
      setWallBetweenPoints(initialPoint.x, initialPoint.y, touchEvent.getX(), touchEvent.getY());
    }
  }

  private Pair<Line, WallEntityUserData> createWall() {
    return createWall(0, 0, 0, 0);
  }

  private Pair<Line, WallEntityUserData> createWall(float x1, float y1, float x2, float y2) {
    WallEntityUserData userData = new WallEntityUserData();
    Line wallLine = new Line(x1, y1, x2, y2, vertexBufferObjectManager);
    wallLine.setUserData(userData);
    wallLine.setLineWidth(WALL_HEIGHT_PX);
    wallLine.setColor(AndengineColor.WHITE);

    addToScene(wallLine);
    return new Pair<>(wallLine, userData);
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
    pendingWallData.first.setPosition(x1, y1, x1 + xComponent, y1 + yComponent);
  }

  /**
   * Returns true if the given point is in a location where a wall cannot be placed.
   */
  private boolean isPointInExcludedZone(float x, float y) {
    Rectangle rectangle = get(GameIconsHostTrayEntity.class).getTrayIconsHolderRectangle();
    return rectangle != null && rectangle.contains(x, y) || !ScreenUtils.isInScreen(x, y);
  }
}
