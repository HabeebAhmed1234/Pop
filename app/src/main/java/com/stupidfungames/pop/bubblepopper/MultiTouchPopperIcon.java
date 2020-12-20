package com.stupidfungames.pop.bubblepopper;

import static com.stupidfungames.pop.GameConstants.MULTI_POP_UNLOCK_THRESHOLD;

import androidx.annotation.Nullable;
import com.stupidfungames.pop.binder.BinderEnity;
import com.stupidfungames.pop.gameiconstray.GameIconsHostTrayEntity.IconId;
import com.stupidfungames.pop.icons.BaseIconEntity;
import com.stupidfungames.pop.resources.textures.TextureId;
import com.stupidfungames.pop.tooltips.TooltipId;
import org.andengine.entity.scene.IOnAreaTouchListener;
import org.andengine.util.color.AndengineColor;

/**
 * Represents the multi pop tool which is charge based and pops all similarly coloured bubbles
 * around a touch popped bubble.
 */
public class MultiTouchPopperIcon extends BaseIconEntity {

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
  protected void onIconUnlocked() {
    /** NOOP **/
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
}
