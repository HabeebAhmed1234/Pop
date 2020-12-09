package com.stupidfungames.pop.icons;

import static com.stupidfungames.pop.eventbus.GameEvent.ICON_UNLOCKED;
import static com.stupidfungames.pop.eventbus.GameEvent.NO_UPGRADES_AVAILABLE;
import static com.stupidfungames.pop.eventbus.GameEvent.UPGRADES_AVAILABLE;
import static com.stupidfungames.pop.eventbus.GameEvent.UPGRADE_CONSUMED;

import android.content.Context;
import androidx.annotation.Nullable;
import com.stupidfungames.pop.binder.BinderEnity;
import com.stupidfungames.pop.eventbus.EventBus;
import com.stupidfungames.pop.eventbus.EventPayload;
import com.stupidfungames.pop.eventbus.GameEvent;
import com.stupidfungames.pop.eventbus.IconUnlockedEventPayload;
import com.stupidfungames.pop.resources.sounds.GameSoundsManager;
import com.stupidfungames.pop.resources.sounds.SoundId;
import com.stupidfungames.pop.resources.textures.GameTexturesManager;
import com.stupidfungames.pop.resources.textures.TextureId;
import com.stupidfungames.pop.touchlisteners.ButtonUpTouchListener;
import com.stupidfungames.pop.utils.ScreenUtils;
import org.andengine.entity.scene.IOnAreaTouchListener;
import org.andengine.entity.scene.ITouchArea;
import org.andengine.entity.sprite.Sprite;
import org.andengine.input.touch.TouchEvent;
import org.andengine.util.color.AndengineColor;

public abstract class BaseUpgradeableIconEntity extends BaseIconEntity {

  private static final int UPGRADE_CHEVRON_SIZE_DP = 12;
  private static final int UPGRADE_CHEVRON_RIGHT_MARGIN_DP = 16;
  private static final int UPGRADE_CHEVRON_TOP_MARGIN_DP = 0;

  /**
   * Number of upgrades this icon has consumed.
   */
  private int upgradeLevel = 0;
  private boolean isInUpgradeState = false;
  // the icon color before we entered the upgrade state
  private AndengineColor previousIconColor = new AndengineColor(getUnlockedIconColor());

  public BaseUpgradeableIconEntity(BinderEnity parent) {
    super(parent);
  }

  @Override
  public void onCreateScene() {
    super.onCreateScene();
    EventBus.get()
        .subscribe(UPGRADES_AVAILABLE, this)
        .subscribe(NO_UPGRADES_AVAILABLE, this);
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    EventBus.get()
        .unSubscribe(UPGRADES_AVAILABLE, this)
        .unSubscribe(NO_UPGRADES_AVAILABLE, this);
  }

  @Override
  protected void onIconUnlocked() {
    EventBus.get()
        .sendEvent(ICON_UNLOCKED, new IconUnlockedEventPayload(getIconUpgradesQuantity()));
  }

  @Override
  public void onEvent(GameEvent event, EventPayload payload) {
    super.onEvent(event, payload);
    switch (event) {
      case UPGRADES_AVAILABLE:
        onUpgradesAvailable();
        break;
      case NO_UPGRADES_AVAILABLE:
        onNoUpgradesAvailable();
        break;
    }
  }

  private boolean canTakeMoreUpgrades() {
    return upgradeLevel < getIconUpgradesQuantity();
  }

  private void onUpgradesAvailable() {
    if (canTakeMoreUpgrades()) {
      enterUpgradeState();
    }
  }

  private void onNoUpgradesAvailable() {
    exitUpgradeState();
  }

  private void enterUpgradeState() {
    if (!isInUpgradeState) {
      isInUpgradeState = true;
      previousIconColor.set(getCurrentIconColor());
      setIconColor(AndengineColor.CYAN);
      enableOverrideTouchListener(true);
    }

  }

  private void exitUpgradeState() {
    if (isInUpgradeState) {
      isInUpgradeState = false;
      setIconColor(previousIconColor);
      enableOverrideTouchListener(false);
    }
  }

  private void upgrade() {
    if (isInUpgradeState) {
      if (canTakeMoreUpgrades()) {
        EventBus.get().sendEvent(UPGRADE_CONSUMED);
        int previousUpgradeLevel = upgradeLevel;
        upgradeLevel++;
        addUpgradeChevron();
        onUpgraded(previousUpgradeLevel, upgradeLevel);
        get(GameSoundsManager.class).getSound(SoundId.UPGRADE).play();
      }
    }
    if (!canTakeMoreUpgrades()) {
      exitUpgradeState();
    }
  }

  private void addUpgradeChevron() {
    final Sprite chevronSprite = new Sprite(
        0,
        0,
        get(GameTexturesManager.class).getTextureRegion(TextureId.UPGRADE_CHEVRON),
        vertexBufferObjectManager);

    Context context = get(Context.class);
    chevronSprite.setScale(ScreenUtils.dpToPx(UPGRADE_CHEVRON_SIZE_DP, context) / chevronSprite.getWidth());
    chevronSprite.setColor(AndengineColor.YELLOW);
    chevronSprite.setX(-(chevronSprite.getWidthScaled() + ScreenUtils.dpToPx(UPGRADE_CHEVRON_RIGHT_MARGIN_DP, context)));
    chevronSprite.setY((upgradeLevel - 1) * chevronSprite.getHeightScaled() + ScreenUtils.dpToPx(UPGRADE_CHEVRON_TOP_MARGIN_DP, context));
    addToScene(getIconSprite(), chevronSprite);
  }

  @Override
  protected IOnAreaTouchListener getOverrideTouchListener() {
    return new ButtonUpTouchListener() {
      @Override
      protected boolean onButtonPressed(TouchEvent pSceneTouchEvent, ITouchArea pTouchArea,
          float pTouchAreaLocalX, float pTouchAreaLocalY) {
        if (isInUpgradeState) {
          upgrade();
          return true;
        } else {
          return false;
        }
      }
    };
  }

  protected abstract void onUpgraded(int previousUpgradeLevel, int newUpgradeLevel);

  /**
   * Number of upgrades this icon can take.
   */
  protected abstract int getIconUpgradesQuantity();
}
