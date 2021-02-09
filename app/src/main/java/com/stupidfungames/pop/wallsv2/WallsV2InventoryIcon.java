package com.stupidfungames.pop.wallsv2;

import static com.stupidfungames.pop.GameConstants.WALLS_DIFFICULTY_UNLOCK_THRESHOLD;
import static com.stupidfungames.pop.eventbus.GameEvent.WALL_DOCKED;
import static com.stupidfungames.pop.gameiconstray.GameIconsHostTrayEntity.IconId.WALLS_V2_ICON;

import com.stupidfungames.pop.binder.BinderEnity;
import com.stupidfungames.pop.draggableinventory.BaseDraggableInventoryIcon;
import com.stupidfungames.pop.eventbus.GameEvent;
import com.stupidfungames.pop.gameiconstray.GameIconsHostTrayEntity.IconId;
import com.stupidfungames.pop.resources.sounds.SoundId;
import com.stupidfungames.pop.resources.textures.TextureId;
import com.stupidfungames.pop.tooltips.TooltipId;
import org.andengine.util.color.AndengineColor;

public class WallsV2InventoryIcon extends BaseDraggableInventoryIcon {

  public static final int STARTING_WALLS_INVENTORY = 1;
  public static final int WALL_INVENTORY_DELTA_PER_UPDATE = 1;
  private static final int NUM_UPGRADES = 3;

  public WallsV2InventoryIcon(BinderEnity parent) {
    super(parent);
  }

  @Override
  protected GameEvent getDockedEvent() {
    return WALL_DOCKED;
  }

  @Override
  protected SoundId getDockUnDockSoundEffect() {
    return SoundId.PUFF;
  }

  @Override
  protected int getStartingInventoryCount() {
    return STARTING_WALLS_INVENTORY;
  }

  @Override
  protected int getInventoryCountIncrementPerUpgrade() {
    return WALL_INVENTORY_DELTA_PER_UPDATE;
  }

  @Override
  protected AndengineColor getIconColor() {
    return isUnlocked() ? getUnlockedIconColor() : AndengineColor.TRANSPARENT;
  }

  @Override
  protected int getIconUpgradesQuantity() {
    return NUM_UPGRADES;
  }

  @Override
  protected TextureId getIconTextureId() {
    return TextureId.WALLS_ICON;
  }

  @Override
  protected IconId getIconId() {
    return WALLS_V2_ICON;
  }

  @Override
  protected float getGameProgressPercentageUnlockThreshold() {
    return WALLS_DIFFICULTY_UNLOCK_THRESHOLD;
  }

  @Override
  protected AndengineColor getUnlockedIconColor() {
    return AndengineColor.GREEN;
  }

  @Override
  protected TooltipId getIconTooltipId() {
    return TooltipId.WALLS_ICON_TOOLTIP;
  }
}
