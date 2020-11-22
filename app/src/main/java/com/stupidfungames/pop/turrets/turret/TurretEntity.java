package com.stupidfungames.pop.turrets.turret;

import static com.stupidfungames.pop.turrets.TurretsConstants.TURRET_DRAGGING_SCALE_MULTIPLIER;

import android.util.Pair;
import com.stupidfungames.pop.BaseEntity;
import com.stupidfungames.pop.binder.Binder;
import com.stupidfungames.pop.binder.BinderEnity;
import com.stupidfungames.pop.savegame.SaveGame;
import com.stupidfungames.pop.statemachine.BaseStateMachine;
import com.stupidfungames.pop.turrets.turret.TurretStateMachine.State;
import java.util.ArrayList;
import org.andengine.entity.sprite.Sprite;
import org.andengine.util.color.AndengineColor;

/**
 * Contains the components of the TurretEntity. Can be used to set the position and rotation of the
 * turret. Each turret contains a state machine
 */
public class TurretEntity extends BaseEntity implements
    HostTurretCallback, BaseStateMachine.Listener<State> {

  private Sprite turretBodySprite;
  private Sprite turretCannonSprite;

  public TurretEntity(Sprite turretBodySprite,
      Sprite turretCannonSprite,
      BinderEnity parent) {
    super(parent);
    this.turretBodySprite = turretBodySprite;
    this.turretCannonSprite = turretCannonSprite;
    init();
  }

  @Override
  protected void createBindings(Binder binder) {
    binder.bind(HostTurretCallback.class, this);
    binder.bind(TurretStateMachine.class, new TurretStateMachine());
    binder.bind(TurretFiringEntity.class, new TurretFiringEntity(this));
    binder.bind(TurretTargetingBaseEntity.class, new TurretTargetingBaseEntity(this));
    binder.bind(TurretDraggingManager.class, new TurretDraggingManager(this));
    binder
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
    AndengineColor color = AndengineColor.WHITE;
    switch (newState) {
      case DRAGGING:
        color = AndengineColor.YELLOW;
        break;
      case FIRING:
        color = AndengineColor.RED;
        break;
      case DOCKED:
        color = AndengineColor.TRANSPARENT;
        break;
      case TARGETING:
        color = AndengineColor.GREEN;
        break;
    }

    turretBodySprite.setColor(color);
    turretCannonSprite.setColor(color);

    updateScale(newState);
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
    Pair position = new Pair<>(turretBodySprite.getX() + turretBodySprite.getWidthScaled() / 2,
        turretBodySprite.getY() + turretBodySprite.getHeightScaled() / 2);
    saveGame.turretPostitions.add(position);
  }

  private void updateScale(TurretStateMachine.State state) {
    if (state == TurretStateMachine.State.DRAGGING) {
      turretBodySprite.setScale(TURRET_DRAGGING_SCALE_MULTIPLIER);
    } else {
      turretBodySprite.setScale(1);
    }
  }
}
