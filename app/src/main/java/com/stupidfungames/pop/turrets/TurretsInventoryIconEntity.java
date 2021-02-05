package com.stupidfungames.pop.turrets;

import static com.stupidfungames.pop.GameConstants.TURRETS_DIFFICULTY_UNLOCK_THRESHOLD;

import com.stupidfungames.pop.GameSceneTouchListenerEntity;
import com.stupidfungames.pop.binder.BinderEnity;
import com.stupidfungames.pop.draggableinventory.BaseDraggableInventoryIcon;
import com.stupidfungames.pop.eventbus.EventBus;
import com.stupidfungames.pop.eventbus.GameEvent;
import com.stupidfungames.pop.gameiconstray.GameIconsHostTrayEntity;
import com.stupidfungames.pop.resources.sounds.SoundId;
import com.stupidfungames.pop.resources.textures.TextureId;
import com.stupidfungames.pop.tooltips.TooltipId;
import org.andengine.entity.scene.IOnAreaTouchListener;
import org.andengine.util.color.AndengineColor;

/**
 * Icon used to utilized turrets. Once unlocked this icon appears. The user can then press down on
 * the icon and a turret appears under their finger. They can then drag the turret to where they
 * want it. The icon has a number on it to show the number of turrets in stock. The user can drag
 * turrets back onto the icon to store them this increasing the number turrets in stock on the
 * icon.
 */
public class TurretsInventoryIconEntity extends BaseDraggableInventoryIcon implements
    EventBus.Subscriber,
    GameSceneTouchListenerEntity.SceneTouchListener {

  public static final int STARTING_TURRET_INVENTORY = 1;
  public static final int TURRET_INVENTORY_DELTA_PER_UPDATE = 1;
  private static final int NUM_UPGRADES = 3;

  public TurretsInventoryIconEntity(BinderEnity parent) {
    super(parent);
  }

  @Override
  protected AndengineColor getIconColor() {
    return isUnlocked() ? getUnlockedIconColor() : AndengineColor.TRANSPARENT;
  }

  @Override
  protected GameEvent getDockedEvent() {
    return GameEvent.TURRET_DOCKED;
  }

  @Override
  protected SoundId getDockUnDockSoundEffect() {
    return SoundId.PUFF;
  }

  @Override
  protected int getStartingInventoryCount() {
    return STARTING_TURRET_INVENTORY;
  }

  @Override
  protected int getInventoryCountIncrementPerUpgrade() {
    return TURRET_INVENTORY_DELTA_PER_UPDATE;
  }

  @Override
  protected TextureId getIconTextureId() {
    return TextureId.TURRETS_ICON;
  }

  @Override
  protected GameIconsHostTrayEntity.IconId getIconId() {
    return GameIconsHostTrayEntity.IconId.TURRETS_ICON;
  }

  @Override
  protected float getGameProgressPercentageUnlockThreshold() {
    return TURRETS_DIFFICULTY_UNLOCK_THRESHOLD;
  }

  @Override
  protected int getIconUpgradesQuantity() {
    return NUM_UPGRADES;
  }

  @Override
  protected void onIconUnlocked() {
    super.onIconUnlocked();
  }

  @Override
  protected AndengineColor getUnlockedIconColor() {
    return AndengineColor.GREEN;
  }

  @Override
  protected IOnAreaTouchListener getTouchListener() {
    /** NOOP **/
    return null;
  }

  @Override
  protected TooltipId getIconTooltipId() {
    return TooltipId.TURRET_ICON_TOOLTIP;
  }
}