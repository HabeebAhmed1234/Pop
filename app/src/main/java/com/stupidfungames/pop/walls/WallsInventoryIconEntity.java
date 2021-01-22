package com.stupidfungames.pop.walls;

import static com.stupidfungames.pop.GameConstants.WALLS_DIFFICULTY_UNLOCK_THRESHOLD;

import androidx.annotation.Nullable;
import com.stupidfungames.pop.binder.BinderEnity;
import com.stupidfungames.pop.eventbus.EventBus;
import com.stupidfungames.pop.eventbus.EventPayload;
import com.stupidfungames.pop.eventbus.GameEvent;
import com.stupidfungames.pop.gameiconstray.GameIconsHostTrayEntity;
import com.stupidfungames.pop.icons.BaseInventoryIconEntity;
import com.stupidfungames.pop.resources.sounds.GameSoundsManager;
import com.stupidfungames.pop.resources.sounds.SoundId;
import com.stupidfungames.pop.resources.textures.TextureId;
import com.stupidfungames.pop.statemachine.BaseStateMachine;
import com.stupidfungames.pop.tooltips.TooltipId;
import com.stupidfungames.pop.touchlisteners.ButtonUpTouchListener;
import org.andengine.entity.scene.IOnAreaTouchListener;
import org.andengine.entity.scene.ITouchArea;
import org.andengine.input.touch.TouchEvent;
import org.andengine.util.color.AndengineColor;

public class WallsInventoryIconEntity extends BaseInventoryIconEntity implements
    BaseStateMachine.Listener<WallsStateMachine.State> {

  public static final int STARTING_WALLS_INVENTORY = 1;
  public static final int WALLS_INVENTORY_DELTA_PER_UPDATE = 1;
  private static final int NUM_UPGRADES = 3;

  public WallsInventoryIconEntity(BinderEnity parent) {
    super(parent);
  }

  @Override
  public void onEnterState(WallsStateMachine.State newState) {
    GameSoundsManager soundsManager = get(GameSoundsManager.class);
    switch (newState) {
      case UNLOCKED_TOGGLED_OFF:
        soundsManager.getSound(SoundId.CLICK_DOWN).play();
        break;
      case TOGGLED_ON:
        soundsManager.getSound(SoundId.CLICK_UP).play();
        break;
    }
    if (!isInUpgradeState()) {
      setIconColor(getColorFromState(newState));
    }
  }

  @Override
  protected int getStartingInventoryCount() {
    return STARTING_WALLS_INVENTORY;
  }

  @Override
  protected int getInventoryCountIncrementPerUpgrade() {
    return WALLS_INVENTORY_DELTA_PER_UPDATE;
  }

  @Override
  public void onCreateScene() {
    super.onCreateScene();

    get(WallsStateMachine.class).addAllStateTransitionListener(this);

    EventBus.get()
        .subscribe(GameEvent.WALL_PLACED, this)
        .subscribe(GameEvent.WALL_DELETED, this)
        .subscribe(GameEvent.GAME_ICONS_TRAY_CLOSED, this);
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    get(WallsStateMachine.class).removeAllStateTransitionListener(this);

    EventBus.get()
        .unSubscribe(GameEvent.WALL_PLACED, this)
        .unSubscribe(GameEvent.WALL_DELETED, this)
        .unSubscribe(GameEvent.GAME_ICONS_TRAY_CLOSED, this);
  }

  @Override
  public void onEvent(GameEvent event, EventPayload payload) {
    super.onEvent(event, payload);

    switch (event) {
      case WALL_PLACED:
        decreaseInventory();
        break;
      case WALL_DELETED:
        increaseInventory();
        break;
      case GAME_ICONS_TRAY_CLOSED:
        toggleOff();
        break;
    }
  }

  @Override
  protected AndengineColor getIconColor() {
    return getColorFromCurrentState();
  }

  @Override
  protected TextureId getIconTextureId() {
    return TextureId.WALLS_ICON;
  }

  @Override
  protected GameIconsHostTrayEntity.IconId getIconId() {
    return GameIconsHostTrayEntity.IconId.WALLS_ICON;
  }

  @Override
  protected float getGameProgressPercentageUnlockThreshold() {
    return WALLS_DIFFICULTY_UNLOCK_THRESHOLD;
  }

  @Override
  protected int getIconUpgradesQuantity() {
    return NUM_UPGRADES;
  }

  @Override
  protected void onIconUnlocked() {
    super.onIconUnlocked();
    get(WallsStateMachine.class).transitionState(WallsStateMachine.State.UNLOCKED_TOGGLED_OFF);
  }

  @Override
  protected AndengineColor getUnlockedIconColor() {
    return AndengineColor.GREEN;
  }

  @Nullable
  @Override
  protected IOnAreaTouchListener getTouchListener() {
    return new ButtonUpTouchListener() {
      @Override
      protected boolean onButtonPressed(TouchEvent pSceneTouchEvent, ITouchArea pTouchArea,
          float pTouchAreaLocalX, float pTouchAreaLocalY) {
        WallsStateMachine stateMachine = get(WallsStateMachine.class);
        if (stateMachine.getCurrentState() == WallsStateMachine.State.UNLOCKED_TOGGLED_OFF) {
          toggleOn();
          return true;
        } else if (stateMachine.getCurrentState() == WallsStateMachine.State.TOGGLED_ON) {
          toggleOff();
          return true;
        }
        return false;
      }
    };
  }

  @Override
  protected TooltipId getIconTooltipId() {
    return TooltipId.WALLS_ICON_TOOLTIP;
  }

  private void toggleOn() {
    WallsStateMachine stateMachine = get(WallsStateMachine.class);
    if (stateMachine.getCurrentState() == WallsStateMachine.State.UNLOCKED_TOGGLED_OFF) {
      stateMachine.transitionState(WallsStateMachine.State.TOGGLED_ON);
    }
  }

  public void toggleOff() {
    WallsStateMachine stateMachine = get(WallsStateMachine.class);
    if (stateMachine.getCurrentState() == WallsStateMachine.State.TOGGLED_ON) {
      stateMachine.transitionState(WallsStateMachine.State.UNLOCKED_TOGGLED_OFF);
    }
  }

  private AndengineColor getColorFromCurrentState() {
    return getColorFromState(get(WallsStateMachine.class).getCurrentState());
  }

  private AndengineColor getColorFromState(WallsStateMachine.State state) {
    AndengineColor color = AndengineColor.GREY;
    switch (state) {
      case LOCKED:
        color = AndengineColor.TRANSPARENT;
        break;
      case UNLOCKED_TOGGLED_OFF:
        color = AndengineColor.GREY;
        break;
      case TOGGLED_ON:
        color = AndengineColor.GREEN;
        break;
    }
    return color;
  }
}
