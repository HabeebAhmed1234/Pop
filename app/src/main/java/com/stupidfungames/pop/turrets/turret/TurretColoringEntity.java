package com.stupidfungames.pop.turrets.turret;

import com.stupidfungames.pop.BaseEntity;
import com.stupidfungames.pop.binder.BinderEnity;
import com.stupidfungames.pop.statemachine.BaseStateMachine.Listener;
import com.stupidfungames.pop.turrets.turret.TurretStateMachine.State;
import org.andengine.entity.sprite.Sprite;
import org.andengine.util.color.AndengineColor;

public class TurretColoringEntity extends BaseEntity implements Listener<State> {

  private Sprite turretBodySprite;
  private Sprite turretCannonSprite;

  public TurretColoringEntity(BinderEnity parent) {
    super(parent);
    get(TurretStateMachine.class).addAllStateTransitionListener(this);
  }

  public void setSprites(Sprite turretBodySprite, Sprite turretCannonSprite) {
    this.turretBodySprite = turretBodySprite;
    this.turretCannonSprite = turretCannonSprite;
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    turretBodySprite = null;
    turretCannonSprite = null;
    get(TurretStateMachine.class).removeAllStateTransitionListener(this);
  }

  @Override
  public void onEnterState(State newState) {
    AndengineColor color = AndengineColor.WHITE;
    switch (newState) {
      case DRAGGING:
        color = AndengineColor.YELLOW;
        break;
      case FIRING:
        color = AndengineColor.PURPLE;
        break;
      case DOCKED:
        color = AndengineColor.TRANSPARENT;
        break;
      case TARGETING:
        color = AndengineColor.PURPLE;
        break;
    }

    if (turretBodySprite != null && turretCannonSprite != null) {
      turretBodySprite.setColor(color);
      turretCannonSprite.setColor(color);
    }
  }
}
