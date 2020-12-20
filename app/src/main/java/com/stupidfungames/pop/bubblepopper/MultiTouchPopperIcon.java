package com.stupidfungames.pop.bubblepopper;

import static com.stupidfungames.pop.GameConstants.MULTI_POP_UNLOCK_THRESHOLD;

import androidx.annotation.Nullable;
import com.stupidfungames.pop.binder.BinderEnity;
import com.stupidfungames.pop.gameiconstray.GameIconsHostTrayEntity.IconId;
import com.stupidfungames.pop.icons.BaseInventoryIconEntity;
import com.stupidfungames.pop.resources.textures.TextureId;
import com.stupidfungames.pop.tooltips.TooltipId;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.scene.IOnAreaTouchListener;
import org.andengine.util.color.AndengineColor;

/**
 * Represents the multi pop tool which is charge based and pops all similarly coloured bubbles
 * around a touch popped bubble.
 */
public class MultiTouchPopperIcon extends BaseInventoryIconEntity {

  public static final int STARTING_MULTI_TOUCH_INVENTORY = 1;
  public static final int MULTI_TOUCH_INVENTORY_DELTA_PER_UPDATE = 1;
  public static final float TIME_TO_REPLENISH_CHARGE_SECONDS = 10;
  private static final int NUM_UPGRADES = 3;


  public MultiTouchPopperIcon(BinderEnity parent) {
    super(parent);
  }

  @Override
  protected TextureId getIconTextureId() {
    return TextureId.MULTI_POP_ICON;
  }

  @Override
  protected IconId getIconId() {
    return IconId.MULTI_POP_ICON;
  }

  @Override
  protected float getGameProgressPercentageUnlockThreshold() {
    return MULTI_POP_UNLOCK_THRESHOLD;
  }

  @Override
  protected int getStartingInventoryCount() {
    return STARTING_MULTI_TOUCH_INVENTORY;
  }

  @Override
  protected int getInventoryCountIncrementPerUpgrade() {
    return MULTI_TOUCH_INVENTORY_DELTA_PER_UPDATE;
  }

  @Override
  protected int getIconUpgradesQuantity() {
    return NUM_UPGRADES;
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
    return TooltipId.MULTI_TOUCH_ICON_TOOLTIP;
  }

  @Override
  public void decreaseInventory() {
    super.decreaseInventory();
    // Start a delayed runnable to replenish this consumed inventory.
    engine.registerUpdateHandler(new TimerHandler(TIME_TO_REPLENISH_CHARGE_SECONDS,
        new ITimerCallback() {
          @Override
          public void onTimePassed(TimerHandler pTimerHandler) {
            increaseInventory();
          }
        }));
  }

  @Override
  protected void onInventoryChanged() {
    super.onInventoryChanged();
    if (isUnlocked()) {
      setIconColor(hasInventory() ? AndengineColor.GREEN : AndengineColor.RED);
    }
  }
}
