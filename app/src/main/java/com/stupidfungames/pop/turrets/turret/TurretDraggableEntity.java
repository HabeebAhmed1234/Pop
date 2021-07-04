package com.stupidfungames.pop.turrets.turret;

import static com.stupidfungames.pop.eventbus.GameEvent.CANCEL_WALL_PLACEMENT;
import static com.stupidfungames.pop.eventbus.GameEvent.TURRET_DOCKED;

import com.stupidfungames.pop.binder.Binder;
import com.stupidfungames.pop.binder.BinderEnity;
import com.stupidfungames.pop.draggableinventory.BaseDraggableEntity;
import com.stupidfungames.pop.eventbus.EventBus;
import com.stupidfungames.pop.eventbus.GameEvent;
import com.stupidfungames.pop.gameiconstray.GameIconsHostTrayEntity.IconId;
import com.stupidfungames.pop.savegame.SaveGame;
import com.stupidfungames.pop.turrets.turret.TurretStateMachine.State;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.andengine.entity.sprite.Sprite;

/**
 * Contains the components of the TurretEntity. Can be used to set the position and rotation of the
 * turret. Each turret contains a state machine
 */
public class TurretDraggableEntity extends BaseDraggableEntity implements HostTurretCallback {

  private static final float TURRET_DRAGGING_OFFSET = 60;
  public static final float TURRET_DRAGGING_SCALE_MULTIPLIER = 1.3f;

  private Sprite turretCannonSprite;

  public TurretDraggableEntity(Sprite turretBodySprite,
      Sprite turretCannonSprite,
      BinderEnity parent) {
    super(turretBodySprite, parent);
    this.turretCannonSprite = turretCannonSprite;
    get(TurretColoringEntity.class).setSprites(turretBodySprite, turretCannonSprite);
  }

  @Override
  protected void createBindings(Binder binder) {
    super.createBindings(binder);
    binder.bind(HostTurretCallback.class, this)
        .bind(TurretStateMachine.class, new TurretStateMachine())
        .bind(TurretFiringEntity.class, new TurretFiringEntity(this))
        .bind(TurretTargetingEntity.class, new TurretTargetingEntity(this))
        .bind(TurretColoringEntity.class, new TurretColoringEntity(this))
        .bind(TurretCannonRotationManagerEntity.class, new TurretCannonRotationManagerEntity(this));
  }

  @Override
  protected float getDraggingOffsetPx() {
    return TURRET_DRAGGING_OFFSET;
  }

  @Override
  protected float getDraggingScaleMultiplier() {
    return TURRET_DRAGGING_SCALE_MULTIPLIER;
  }

  @Override
  protected IconId getHomeIconId() {
    return IconId.TURRETS_ICON;
  }

  @Override
  public boolean setTurretAngle(float angle) {
    return get(TurretCannonRotationManagerEntity.class).setRotation(angle);
  }

  @Override
  public Sprite getTurretBodySprite() {
    return draggableSprite;
  }

  @Override
  public Sprite getTurretCannonSprite() {
    return turretCannonSprite;
  }

  @Override
  public void onDropped() {
    get(TurretStateMachine.class).transitionState(TurretStateMachine.State.TARGETING);
  }

  @Override
  public void onDocked() {
    get(TurretStateMachine.class).transitionState(TurretStateMachine.State.DOCKED);
  }

  @Override
  public GameEvent getDockedEvent() {
    return TURRET_DOCKED;
  }

  @Override
  public void onSaveGame(SaveGame saveGame) {
    super.onSaveGame(saveGame);
    if (get(TurretStateMachine.class).getCurrentState() == State.DOCKED) {
      return;
    }
    if (saveGame.turretPostitions == null) {
      saveGame.turretPostitions = new ArrayList<>();
    }
    List<Float> position = Arrays
        .asList(draggableSprite.getX() + draggableSprite.getWidthScaled() / 2,
            draggableSprite.getY() + draggableSprite.getHeightScaled() / 2);
    saveGame.turretPostitions.add(position);
  }

  @Override
  public boolean canDrag() {
    return get(TurretStateMachine.class).getCurrentState() != TurretStateMachine.State.DRAGGING;
  }

  @Override
  public void onDraggingStarted() {
    get(TurretStateMachine.class).transitionState(State.DRAGGING);
    EventBus.get().sendEvent(CANCEL_WALL_PLACEMENT);
  }
}
