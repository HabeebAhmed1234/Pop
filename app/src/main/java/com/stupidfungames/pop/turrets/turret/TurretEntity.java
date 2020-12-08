package com.stupidfungames.pop.turrets.turret;

import com.stupidfungames.pop.BaseEntity;
import com.stupidfungames.pop.binder.Binder;
import com.stupidfungames.pop.binder.BinderEnity;
import com.stupidfungames.pop.savegame.SaveGame;
import com.stupidfungames.pop.statemachine.BaseStateMachine;
import com.stupidfungames.pop.turrets.turret.TurretStateMachine.State;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.andengine.entity.sprite.Sprite;

/**
 * Contains the components of the TurretEntity. Can be used to set the position and rotation of the
 * turret. Each turret contains a state machine
 */
public class TurretEntity extends BaseEntity implements
    HostTurretCallback, BaseStateMachine.Listener<State> {

  public static final float TURRET_DRAGGING_SCALE_MULTIPLIER = 1.3f;

  private Sprite turretBodySprite;
  private Sprite turretCannonSprite;

  public TurretEntity(Sprite turretBodySprite,
      Sprite turretCannonSprite,
      BinderEnity parent) {
    super(parent);
    this.turretBodySprite = turretBodySprite;
    this.turretCannonSprite = turretCannonSprite;
    //get(TurretColoringEntity.class).setSprites(turretBodySprite, turretCannonSprite);
    init();
  }

  @Override
  protected void createBindings(Binder binder) {
    binder.bind(HostTurretCallback.class, this)
        .bind(TurretStateMachine.class, new TurretStateMachine())
        .bind(TurretFiringEntity.class, new TurretFiringEntity(this))
        .bind(TurretTargetingEntity.class, new TurretTargetingEntity(this))
        // .bind(TurretColoringEntity.class, new TurretColoringEntity(this))
        .bind(TurretDraggingManager.class, new TurretDraggingManager(this))
        .bind(TurretCannonRotationManagerEntity.class, new TurretCannonRotationManagerEntity(this));
  }

  @Override
  public boolean setTurretAngle(float angle) {
    return get(TurretCannonRotationManagerEntity.class).setRotation(angle);
  }

  @Override
  public Sprite getTurretBodySprite() {
    return turretBodySprite;
  }

  @Override
  public Sprite getTurretCannonSprite() {
    return turretCannonSprite;
  }

  @Override
  public void setTurretPosition(float x, float y) {
    turretBodySprite.setX(x - turretBodySprite.getWidthScaled() / 2);
    turretBodySprite.setY(y - turretBodySprite.getHeightScaled() / 2);
  }

  public void forceStartDragging(float pointerX, float pointerY) {
    get(TurretDraggingManager.class).forceStartDragging(pointerX, pointerY);
  }

  public void forceDrop() {
    get(TurretStateMachine.class).transitionState(TurretStateMachine.State.TARGETING);
  }

  private void init() {
    get(TurretStateMachine.class).addAllStateTransitionListener(this);
  }

  @Override
  public void onEnterState(TurretStateMachine.State newState) {
    if (newState == TurretStateMachine.State.DRAGGING) {
      turretBodySprite.setScale(TURRET_DRAGGING_SCALE_MULTIPLIER);
    } else {
      turretBodySprite.setScale(1);
    }
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
        .asList(turretBodySprite.getX() + turretBodySprite.getWidthScaled() / 2,
            turretBodySprite.getY() + turretBodySprite.getHeightScaled() / 2);
    saveGame.turretPostitions.add(position);
  }
}
