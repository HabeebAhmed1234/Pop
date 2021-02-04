package com.stupidfungames.pop.icons;

import static com.stupidfungames.pop.eventbus.GameEvent.NO_UPGRADES_AVAILABLE;
import static com.stupidfungames.pop.eventbus.GameEvent.UPGRADEABLE_ICON_LOADED;
import static com.stupidfungames.pop.eventbus.GameEvent.UPGRADEABLE_ICON_UNLOCKED;
import static com.stupidfungames.pop.eventbus.GameEvent.UPGRADES_AVAILABLE;
import static com.stupidfungames.pop.eventbus.GameEvent.UPGRADE_CONSUMED;

import com.stupidfungames.pop.R;
import com.stupidfungames.pop.binder.Binder;
import com.stupidfungames.pop.binder.BinderEnity;
import com.stupidfungames.pop.eventbus.EventBus;
import com.stupidfungames.pop.eventbus.EventPayload;
import com.stupidfungames.pop.eventbus.GameEvent;
import com.stupidfungames.pop.eventbus.UpgradeableIconLoadedEventPayload;
import com.stupidfungames.pop.eventbus.UpgradeableIconUnlockedEventPayload;
import com.stupidfungames.pop.resources.sounds.GameSoundsManager;
import com.stupidfungames.pop.resources.sounds.SoundId;
import com.stupidfungames.pop.resources.textures.GameTexturesManager;
import com.stupidfungames.pop.resources.textures.TextureId;
import com.stupidfungames.pop.savegame.SaveGame;
import com.stupidfungames.pop.touchlisteners.ButtonUpTouchListener;
import com.stupidfungames.pop.upgrades.UpgradesParticleEffectEntity;
import java.util.HashMap;
import org.andengine.entity.modifier.LoopEntityModifier;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.scene.IOnAreaTouchListener;
import org.andengine.entity.scene.ITouchArea;
import org.andengine.entity.sprite.Sprite;
import org.andengine.input.touch.TouchEvent;
import org.andengine.util.color.AndengineColor;

public abstract class BaseUpgradeableIconEntity extends BaseIconEntity {

  /**
   * Number of upgrades this icon has consumed.
   */
  private int upgradeLevel = 0;
  private boolean isInUpgradeState = false;
  // the icon color before we entered the upgrade state
  // private AndengineColor previousIconColor = new AndengineColor(getUnlockedIconColor());

  public BaseUpgradeableIconEntity(BinderEnity parent) {
    super(parent);
  }

  @Override
  protected void createBindings(Binder binder) {
    super.createBindings(binder);
    binder.bind(UpgradesParticleEffectEntity.class, new UpgradesParticleEffectEntity(this));
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
        .sendEvent(UPGRADEABLE_ICON_UNLOCKED,
            new UpgradeableIconUnlockedEventPayload(getIconUpgradesQuantity()));
  }

  @Override
  public void onEvent(GameEvent event, EventPayload payload) {
    super.onEvent(event, payload);
    switch (event) {
      case UPGRADES_AVAILABLE:
        onUpgradesAvailable();
        break;
      case NO_UPGRADES_AVAILABLE:
        exitUpgradeState();
        break;
    }
  }

  @Override
  public void onSaveGame(SaveGame saveGame) {
    super.onSaveGame(saveGame);
    if (saveGame.iconUpgradeLevels == null) {
      saveGame.iconUpgradeLevels = new HashMap<>();
    }
    saveGame.iconUpgradeLevels.put(getIconId(), upgradeLevel);
  }

  @Override
  public void onLoadGame(SaveGame saveGame) {
    super.onLoadGame(saveGame);
    if (saveGame.iconUpgradeLevels != null && saveGame.iconUpgradeLevels.containsKey(getIconId())) {
      int upgradeToLevel = saveGame.iconUpgradeLevels.get(getIconId());
      if (upgradeToLevel == upgradeLevel) {
        return;
      }
      int numLevelsToUpgrade = upgradeToLevel - upgradeLevel;
      for (int i = 0; i < numLevelsToUpgrade; i++) {
        int previousUpgradeLevel = upgradeLevel;
        upgradeLevel++;
        addUpgradeChevron();
        onUpgraded(previousUpgradeLevel, upgradeLevel);
      }
      // Send the new loaded state of this icon to the UpgradeSpawner
      EventBus.get()
          .sendEvent(UPGRADEABLE_ICON_LOADED, new UpgradeableIconLoadedEventPayload(upgradeLevel));
    }
  }

  protected boolean isInUpgradeState() {
    return isInUpgradeState;
  }

  protected abstract AndengineColor getIconColor();

  private boolean canTakeMoreUpgrades() {
    return upgradeLevel < getIconUpgradesQuantity();
  }

  private void onUpgradesAvailable() {
    if (canTakeMoreUpgrades()) {
      enterUpgradeState();
    }
  }

  private void enterUpgradeState() {
    if (!isInUpgradeState) {
      isInUpgradeState = true;
      // previousIconColor.set(getCurrentIconSpriteColor());
      setIconColor(AndengineColor.CYAN);
      enableOverrideTouchListener(true);
      startBouncingEffect();
    }
  }

  private void exitUpgradeState() {
    if (isInUpgradeState) {
      isInUpgradeState = false;
      setIconColor(getIconColor());
      enableOverrideTouchListener(false);
      stopBouncingEffect();
    }
  }

  private void startBouncingEffect() {
    stopBouncingEffect();
    getIconSprite()
        .registerEntityModifier(
            new LoopEntityModifier(new ScaleModifier(0.5f, 1.0f, 0.9f)));
  }

  private void stopBouncingEffect() {
    getIconSprite().clearEntityModifiers();
  }

  private void upgrade() {
    if (isInUpgradeState) {
      if (canTakeMoreUpgrades()) {
        EventBus.get().sendEvent(UPGRADE_CONSUMED);
        int previousUpgradeLevel = upgradeLevel;
        upgradeLevel++;
        addUpgradeChevron();
        onUpgraded(previousUpgradeLevel, upgradeLevel);
        triggerEffects();
      }
      if (!canTakeMoreUpgrades()) {
        exitUpgradeState();
      }
    }
  }

  private void addUpgradeChevron() {
    final Sprite chevronSprite = new Sprite(
        0,
        0,
        get(GameTexturesManager.class).getTextureRegion(TextureId.UPGRADE_CHEVRON),
        vertexBufferObjectManager);

    chevronSprite
        .setScale(getDimenPx(R.dimen.upgrade_chevron_size) / chevronSprite.getWidth());
    chevronSprite.setColor(AndengineColor.YELLOW);
    chevronSprite
        .setX(-(chevronSprite.getWidthScaled() + getDimenPx(R.dimen.upgrade_chevron_right_margin)));
    chevronSprite
        .setY((upgradeLevel - 1) * chevronSprite.getHeightScaled() + getDimenPx(
            R.dimen.upgrade_chevron_top_margin));
    addToScene(getIconSprite(), chevronSprite);
  }

  private void triggerEffects() {
    // Play the upgrade sound
    get(GameSoundsManager.class).getSound(SoundId.UPGRADE).play();
    // Spawn upgrade particles
    float[] position = getIconSprite().getSceneCenterCoordinates();
    get(UpgradesParticleEffectEntity.class).emit(position[0], position[1]);
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
