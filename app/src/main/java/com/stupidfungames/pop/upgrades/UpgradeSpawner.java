package com.stupidfungames.pop.upgrades;

import static com.stupidfungames.pop.bubblespawn.BubbleSpawnerEntity.BUBBLE_GRAVITY_SCALE;
import static com.stupidfungames.pop.eventbus.GameEvent.BUBBLE_POPPED;
import static com.stupidfungames.pop.eventbus.GameEvent.GAME_PROGRESS_CHANGED;
import static com.stupidfungames.pop.eventbus.GameEvent.UPGRADEABLE_ICON_LOADED;
import static com.stupidfungames.pop.eventbus.GameEvent.UPGRADEABLE_ICON_UNLOCKED;

import android.content.Context;
import com.stupidfungames.pop.BaseEntity;
import com.stupidfungames.pop.GameConstants;
import com.stupidfungames.pop.GameFixtureDefs;
import com.stupidfungames.pop.binder.Binder;
import com.stupidfungames.pop.binder.BinderEnity;
import com.stupidfungames.pop.collision.CollisionFilters;
import com.stupidfungames.pop.eventbus.BubblePoppedEventPayload;
import com.stupidfungames.pop.eventbus.EventBus;
import com.stupidfungames.pop.eventbus.EventBus.Subscriber;
import com.stupidfungames.pop.eventbus.EventPayload;
import com.stupidfungames.pop.eventbus.GameEvent;
import com.stupidfungames.pop.eventbus.GameProgressEventPayload;
import com.stupidfungames.pop.eventbus.UpgradeableIconLoadedEventPayload;
import com.stupidfungames.pop.eventbus.UpgradeableIconUnlockedEventPayload;
import com.stupidfungames.pop.fixturedefdata.UpgradeUserData;
import com.stupidfungames.pop.physics.PhysicsFactory;
import com.stupidfungames.pop.resources.textures.GameTexturesManager;
import com.stupidfungames.pop.resources.textures.TextureId;
import com.stupidfungames.pop.tooltips.GameTooltipsEntity;
import com.stupidfungames.pop.tooltips.TooltipId;
import com.stupidfungames.pop.utils.ScreenUtils;
import java.util.concurrent.TimeUnit;
import org.andengine.entity.sprite.Sprite;
import org.andengine.util.color.AndengineColor;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;

/**
 * Maybe spawns an upgrade for the tools. After each tool spawns its upgrade quantity gets added to
 * the total number of spawnable upgrades. Upgrades then spawn at regular intervals = Remaining
 * difficulty / Remaining upgrades.
 */
public class UpgradeSpawner extends BaseEntity implements Subscriber {

  private static final AndengineColor UPGRADE_COLOUR = new AndengineColor(241f / 255f, 10f / 255f,
      245f / 255f);
  private static final int UPGRADE_SIZE_DP = 80;

  private int numUpgradesRemaining = 0;
  private long lastTimeUpgradeWasSpawned = -1;
  private float currentGameDifficultyPercentProgress = -1;

  public UpgradeSpawner(BinderEnity parent) {
    super(parent);
  }

  @Override
  protected void createBindings(Binder binder) {
    super.createBindings(binder);
    binder.bind(UpgradeTouchFactoryEntity.class, new UpgradeTouchFactoryEntity(this));
  }

  @Override
  public void onCreateScene() {
    super.onCreateScene();
    EventBus.get()
        .subscribe(BUBBLE_POPPED, this)
        .subscribe(UPGRADEABLE_ICON_UNLOCKED, this)
        .subscribe(GAME_PROGRESS_CHANGED, this)
        .subscribe(UPGRADEABLE_ICON_LOADED, this);
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    EventBus.get()
        .unSubscribe(BUBBLE_POPPED, this)
        .unSubscribe(UPGRADEABLE_ICON_UNLOCKED, this)
        .unSubscribe(GAME_PROGRESS_CHANGED, this)
        .unSubscribe(UPGRADEABLE_ICON_LOADED, this);
  }

  @Override
  public void onEvent(GameEvent event, EventPayload payload) {
    switch (event) {
      case BUBBLE_POPPED:
        maybeSpawnUpgrade((BubblePoppedEventPayload) payload);
        break;
      case UPGRADEABLE_ICON_UNLOCKED:
        onIconUnlocked((UpgradeableIconUnlockedEventPayload) payload);
        break;
      case GAME_PROGRESS_CHANGED:
        currentGameDifficultyPercentProgress = ((GameProgressEventPayload) payload).percentProgress;
        break;
      case UPGRADEABLE_ICON_LOADED:
        onIconLoadedAndUpgradesAdjusted((UpgradeableIconLoadedEventPayload) payload);
        break;
    }
  }

  /**
   * Called when an upgrade falls below the screen. We want it get added back into the available
   * upgrades.
   */
  public void onUpgradeLost() {
    numUpgradesRemaining++;
  }

  private void maybeSpawnUpgrade(BubblePoppedEventPayload bubblePoppedEventPayload) {
    if (currentGameDifficultyPercentProgress == -1
        || currentGameDifficultyPercentProgress
        <= GameConstants.MIN_UPGRADE_SPAWN_START_THRESHOLD_DIFFICULTY
        || numUpgradesRemaining == 0) {
      // Only start spawning upgrades if all these values are set.
      return;
    }

    long now = System.currentTimeMillis();

    if (lastTimeUpgradeWasSpawned == -1) {
      // Set the last time upgrade was spawned to now so that when the first interval passes we will
      // spawn an upgrade.
      lastTimeUpgradeWasSpawned = now;
    }

    // First get the amount of time remaining until game reaches max difficulty. Once the game hits
    // max difficulty the user should have all the upgrades.
    long timeRemainingToMaxDifficultyMillis = (long) (
        (1.0f - currentGameDifficultyPercentProgress)
            * TimeUnit.SECONDS.toMillis((long) GameConstants.TIME_TO_MAX_DIFFICULTY_SECONDS));
    long upgradeSpawnInterval = timeRemainingToMaxDifficultyMillis / numUpgradesRemaining;

    // If the current time has passed the first interval required to spawn an upgrade then spawn
    // an upgrade.
    if (now >= lastTimeUpgradeWasSpawned + upgradeSpawnInterval) {
      // spawn an upgrade.
      spawnUpgrade(bubblePoppedEventPayload);
    }
  }

  private void spawnUpgrade(BubblePoppedEventPayload bubblePoppedEventPayload) {
    numUpgradesRemaining--;
    lastTimeUpgradeWasSpawned = System.currentTimeMillis();
    final Sprite upgradeSprite = new Sprite(
        bubblePoppedEventPayload.poppedBubbleX,
        bubblePoppedEventPayload.poppedBubbleY,
        get(GameTexturesManager.class).getTextureRegion(TextureId.UPGRADE),
        vertexBufferObjectManager);
    upgradeSprite.setUserData(new UpgradeUserData(upgradeSprite));

    float upgradeSizePx = ScreenUtils.dpToPx(UPGRADE_SIZE_DP, get(Context.class));
    upgradeSprite.setScale(upgradeSizePx / upgradeSprite.getWidth());
    upgradeSprite.setColor(UPGRADE_COLOUR);

    final FixtureDef bubbleFixtureDef = GameFixtureDefs.UPGRADE_FIXTURE_DEF;
    bubbleFixtureDef.setFilter(CollisionFilters.UPGRADES_FILTER);
    bubbleFixtureDef.setUserData(upgradeSprite.getUserData());
    final Body body = PhysicsFactory
        .createBoxBody(physicsWorld, upgradeSprite, BodyType.DYNAMIC, bubbleFixtureDef);
    body.setGravityScale(BUBBLE_GRAVITY_SCALE);
    addToSceneWithTouch(upgradeSprite, body,
        get(UpgradeTouchFactoryEntity.class).getNewTouchListener(), false);

    if (ScreenUtils.isInScreen(upgradeSprite)) {
      get(GameTooltipsEntity.class)
          .maybeShowTooltip(TooltipId.UPGRADE_TOOLTIP);
    }
  }

  private void onIconUnlocked(
      UpgradeableIconUnlockedEventPayload upgradeableIconUnlockedEventPayload) {
    numUpgradesRemaining += upgradeableIconUnlockedEventPayload.iconUpgradesQuantity;
  }

  /**
   * This is needed so we don't spawn too many upgrades (more than what can be applied to the tools
   */
  private void onIconLoadedAndUpgradesAdjusted(UpgradeableIconLoadedEventPayload payload) {
    numUpgradesRemaining -= payload.upgradeLevel;
  }
}
