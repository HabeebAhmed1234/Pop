package com.stupidfungames.pop.sword;

import static com.stupidfungames.pop.GameConstants.SWORD_DIFFICULTY_UNLOCK_THRESHOLD;
import static com.stupidfungames.pop.gameiconstray.GameIconsHostTrayEntity.IconId.SWORD_ICON;

import androidx.annotation.Nullable;
import com.stupidfungames.pop.binder.BinderEnity;
import com.stupidfungames.pop.gameiconstray.GameIconsHostTrayEntity.IconId;
import com.stupidfungames.pop.icons.BaseUpgradeableIconEntity;
import com.stupidfungames.pop.resources.textures.TextureId;
import com.stupidfungames.pop.statemachine.BaseStateMachine.Listener;
import com.stupidfungames.pop.sword.SwordStateMachine.State;
import com.stupidfungames.pop.tooltips.TooltipId;
import org.andengine.entity.scene.IOnAreaTouchListener;
import org.andengine.util.color.AndengineColor;

public class SwordIcon extends BaseUpgradeableIconEntity implements Listener<State> {

  private static final int NUM_UPGRADES = 3;

  public SwordIcon(BinderEnity parent) {
    super(parent);
  }

  @Override
  public void onCreateScene() {
    super.onCreateScene();
    get(SwordStateMachine.class).addAllStateTransitionListener(this);
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    get(SwordStateMachine.class).removeAllStateTransitionListener(this);
  }

  @Override
  public void onEnterState(State newState) {
    if (!isInUpgradeState()) {
      setIconColor(getIconColorFromState(newState));
    }
  }

  @Override
  protected AndengineColor getIconColor() {
    return getIconColorFromCurrentState();
  }

  @Override
  protected void onUpgraded(int previousUpgradeLevel, int newUpgradeLevel) {
    get(SwordChargeManager.class).onUpgrade(newUpgradeLevel);
  }

  @Override
  protected void onIconUnlocked() {
    super.onIconUnlocked();
    get(SwordStateMachine.class).transitionState(State.CHARGED);
  }

  @Override
  protected int getIconUpgradesQuantity() {
    return NUM_UPGRADES;
  }

  @Override
  protected TextureId getIconTextureId() {
    return TextureId.BALL_AND_CHAIN_ICON;
  }

  @Override
  protected IconId getIconId() {
    return SWORD_ICON;
  }

  @Override
  protected float getGameProgressPercentageUnlockThreshold() {
    return SWORD_DIFFICULTY_UNLOCK_THRESHOLD;
  }

  @Override
  protected AndengineColor getUnlockedIconColor() {
    return AndengineColor.GREEN;
  }

  @Nullable
  @Override
  protected IOnAreaTouchListener getTouchListener() {
    /** NOOP **/
    return null;
  }

  @Override
  protected TooltipId getIconTooltipId() {
    return TooltipId.SWORD_ICON_TOOLTIP;
  }

  private AndengineColor getIconColorFromCurrentState() {
    return getIconColorFromState(get(SwordStateMachine.class).getCurrentState());
  }

  private AndengineColor getIconColorFromState(State state) {
    AndengineColor color = AndengineColor.WHITE;
    switch (state) {
      case LOCKED:
        color = AndengineColor.TRANSPARENT;
        break;
      case CHARGED:
        color = AndengineColor.GREEN;
        break;
      case UNCHARGED:
        color = AndengineColor.RED;
        break;
    }
    return color;
  }
}
