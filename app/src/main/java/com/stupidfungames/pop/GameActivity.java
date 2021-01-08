package com.stupidfungames.pop;

import static com.stupidfungames.pop.analytics.Events.GAME_STOP_PROGRESS;
import static com.stupidfungames.pop.analytics.Events.GAME_STOP_SCORE;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.Toast;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import com.stupidfungames.pop.analytics.Logger;
import com.stupidfungames.pop.androidui.music.MusicPlayer;
import com.stupidfungames.pop.auth.GooglePlayServicesAuthManager;
import com.stupidfungames.pop.backgroundmusic.BackgroundMusicEntity;
import com.stupidfungames.pop.ballandchain.BallAndChainManagerEntity;
import com.stupidfungames.pop.binder.Binder;
import com.stupidfungames.pop.binder.BinderEnity;
import com.stupidfungames.pop.bubblepopper.BubblePopperEntity;
import com.stupidfungames.pop.bubblepopper.MultiTouchPopperIcon;
import com.stupidfungames.pop.bubblepopper.TouchBubblePopper;
import com.stupidfungames.pop.bubblespawn.BubbleSpawnerEntity;
import com.stupidfungames.pop.bubblespawn.BubbleSpritePool;
import com.stupidfungames.pop.bubbletimeout.BubblesLifecycleManagerEntity;
import com.stupidfungames.pop.difficulty.GameDifficultyEntity;
import com.stupidfungames.pop.eventbus.EventBus;
import com.stupidfungames.pop.gameiconstray.GameIconsHostTrayEntity;
import com.stupidfungames.pop.gamesettings.GamePreferencesManager;
import com.stupidfungames.pop.gamesettings.Setting;
import com.stupidfungames.pop.hudentities.ScoreHudEntity;
import com.stupidfungames.pop.hudentities.StreakHudEntity;
import com.stupidfungames.pop.hudentities.TimerHudEntity;
import com.stupidfungames.pop.nuke.NukeManagerEntity;
import com.stupidfungames.pop.resources.fonts.GameFontsManager;
import com.stupidfungames.pop.resources.sounds.GameSoundsManager;
import com.stupidfungames.pop.resources.sounds.SoundId;
import com.stupidfungames.pop.resources.textures.GameTexturesManager;
import com.stupidfungames.pop.savegame.SaveGame;
import com.stupidfungames.pop.savegame.SaveGameFlowDialog;
import com.stupidfungames.pop.savegame.SaveGameManager;
import com.stupidfungames.pop.settingstray.GamePauseQuickSettingsIconEntity;
import com.stupidfungames.pop.settingstray.GameQuickSettingsHostTrayBaseEntity;
import com.stupidfungames.pop.settingstray.MusicQuickSettingIconEntity;
import com.stupidfungames.pop.settingstray.NextSongQuickSettingsIconEntity;
import com.stupidfungames.pop.settingstray.SaveGameQuickSettingsIconEntity;
import com.stupidfungames.pop.settingstray.SaveGameQuickSettingsIconEntity.SaveGameButtonCallback;
import com.stupidfungames.pop.tooltips.GameTooltipsEntity;
import com.stupidfungames.pop.turrets.TurretsManagerEntity;
import com.stupidfungames.pop.upgrades.UpgradeManager;
import com.stupidfungames.pop.utils.ScreenUtils;
import com.stupidfungames.pop.walls.WallsManagerBaseEntity;
import org.andengine.audio.music.MusicManager;
import org.andengine.audio.sound.SoundManager;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.util.FPSLogger;
import org.andengine.input.sensor.acceleration.AccelerationData;
import org.andengine.input.sensor.acceleration.IAccelerationListener;
import org.andengine.opengl.font.FontManager;
import org.andengine.opengl.texture.TextureManager;
import org.andengine.ui.activity.SimpleBaseGameActivity;

public class GameActivity extends SimpleBaseGameActivity implements HostActivity,
    IAccelerationListener, GamePauser, GameSaver,
    SaveGameButtonCallback {

  public static final String SAVE_GAME_EXTRA = "save_game";
  private static final int PAUSE_ACTIVITY_REQUEST_CODE = 1;

  private ShakeCamera camera;

  private GameLifeCycleCalllbackManager gameLifeCycleCalllbackManager = new GameLifeCycleCalllbackManager();
  private GooglePlayServicesAuthManager authManager;
  private SaveGameManager saveGameManager;

  BinderEnity mRootBinder;

  public static Intent newIntent(SaveGame saveGame, Context context) {
    Intent intent = new Intent(context, GameActivity.class);
    intent.putExtra(SAVE_GAME_EXTRA, saveGame.toJson());
    return intent;
  }

  public static Intent newIntent(Context context) {
    return new Intent(context, GameActivity.class);
  }

  @Override
  protected void onCreate(Bundle pSavedInstanceState) {
    super.onCreate(pSavedInstanceState);
    authManager = new GooglePlayServicesAuthManager(this);
    saveGameManager = new SaveGameManager(this, this);

    // The root binder
    mRootBinder = new BinderEnity(null) {

      @Override
      protected void createBindings(Binder binder) {
        binder
            .bind(GameSaver.class, GameActivity.this)
            .bind(GameLifeCycleCalllbackManager.class, gameLifeCycleCalllbackManager)
            .bind(GameResources.class,
                GameResources.createNew(GameActivity.this, GameActivity.this))
            .bind(FontManager.class, getFontManager())
            .bind(TextureManager.class, getTextureManager())
            .bind(AssetManager.class, getAssets())
            .bind(Context.class, GameActivity.this)
            .bind(SoundManager.class, getSoundManager())
            .bind(MusicManager.class, getMusicManager())
            .bind(ShakeCamera.class, camera)
            .bind(GamePauser.class, GameActivity.this)
            .bind(SaveGameButtonCallback.class, GameActivity.this)

            .bind(GameTexturesManager.class, new GameTexturesManager(this))
            .bind(GameSoundsManager.class, new GameSoundsManager(this))
            .bind(MusicPlayer.class, MusicPlayer.get())
            .bind(BackgroundMusicEntity.class, new BackgroundMusicEntity(this))
            .bind(GameFontsManager.class, new GameFontsManager(this))
            .bind(GameAnimationManager.class, new GameAnimationManager(this))
            .bind(LevelEntity.class, new LevelEntity(this))

            .bind(GamePhysicsContactsEntity.class, new GamePhysicsContactsEntity(this))
            .bind(GameSceneTouchListenerEntity.class, new GameSceneTouchListenerEntity(this))

            .bind(GameTooltipsEntity.class, new GameTooltipsEntity(this))
            .bind(GameStartTooltipEntity.class, new GameStartTooltipEntity(this))

            .bind(GameQuickSettingsHostTrayBaseEntity.class,
                new GameQuickSettingsHostTrayBaseEntity(this))
            .bind(MusicQuickSettingIconEntity.class, new MusicQuickSettingIconEntity(this))
            .bind(NextSongQuickSettingsIconEntity.class, new NextSongQuickSettingsIconEntity(this))
            .bind(GamePauseQuickSettingsIconEntity.class,
                new GamePauseQuickSettingsIconEntity(this))
            .bind(SaveGameQuickSettingsIconEntity.class, new SaveGameQuickSettingsIconEntity(this))

            .bind(ScoreHudEntity.class, new ScoreHudEntity(this))
            .bind(StreakHudEntity.class, new StreakHudEntity(this))
            .bind(TimerHudEntity.class, new TimerHudEntity(this))

            .bind(BubbleSpritePool.class, new BubbleSpritePool(this))
            .bind(GameDifficultyEntity.class, new GameDifficultyEntity(this))
            .bind(GameOverSequenceEntity.class, new GameOverSequenceEntity(this))
            .bind(BubblesLifecycleManagerEntity.class, new BubblesLifecycleManagerEntity(this))

            .bind(BubbleSpawnerEntity.class, new BubbleSpawnerEntity(this))
            .bind(BubbleCleanerEntity.class, new BubbleCleanerEntity(this))
            .bind(BubbleLossDetectorEntity.class, new BubbleLossDetectorEntity(this))
            .bind(BubbleTouchFactoryEntity.class, new BubbleTouchFactoryEntity(this))
            .bind(BubblePopperEntity.class, new BubblePopperEntity(this))

            .bind(GameIconsHostTrayEntity.class, new GameIconsHostTrayEntity(this))
            .bind(BallAndChainManagerEntity.class, new BallAndChainManagerEntity(this))
            .bind(TurretsManagerEntity.class, new TurretsManagerEntity(this))
            .bind(WallsManagerBaseEntity.class, new WallsManagerBaseEntity(this))
            .bind(NukeManagerEntity.class, new NukeManagerEntity(this))

            .bind(MultiTouchPopperIcon.class, new MultiTouchPopperIcon(this))
            .bind(TouchBubblePopper.class, new TouchBubblePopper(this))

            .bind(UpgradeManager.class, new UpgradeManager(this));
      }
    };
  }

  @Override
  public void pauseGameWithPauseScreen() {
    mRootBinder.get(GameSoundsManager.class).getSound(SoundId.PAUSE).play();
    startActivityForResult(GamePauseActivity.newIntent(this), PAUSE_ACTIVITY_REQUEST_CODE);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == PAUSE_ACTIVITY_REQUEST_CODE) {
      mRootBinder.get(GameSoundsManager.class).getSound(SoundId.UNPAUSE).play();
      if (resultCode == GamePauseActivity.RESULT_QUIT) {
        startActivity(MainMenuActivity.newIntent(this));
        finish();
      }
    }
  }

  @Override
  public EngineOptions onCreateEngineOptions() {
    ScreenUtils.initialize(this);
    ScreenUtils.ScreenSize screenSize = ScreenUtils.getSreenSize();
    camera = new ShakeCamera(0, 0, screenSize.widthPx, screenSize.heightPx);
    final EngineOptions engineOptions = new EngineOptions(
        true,
        ScreenOrientation.PORTRAIT_FIXED,
        new RatioResolutionPolicy(screenSize.widthPx, screenSize.heightPx),
        this.camera);
    engineOptions.getTouchOptions().setNeedsMultiTouch(true);
    engineOptions.getAudioOptions().setNeedsSound(true);
    engineOptions.getAudioOptions().getMusicOptions().setNeedsMusic(true);
    engineOptions.getAudioOptions().getSoundOptions().setMaxSimultaneousStreams(50);
    return engineOptions;
  }

  @Override
  public void onCreateResources() {
    ScreenUtils.onCreateResources(getVertexBufferObjectManager());
    gameLifeCycleCalllbackManager.onCreateResources();
  }

  @Override
  public Scene onCreateScene() {
    gameLifeCycleCalllbackManager.onCreateScene();
    SaveGame saveGame = getSaveGameFromIntent();
    if (saveGame != null) {
      gameLifeCycleCalllbackManager.onLoadGame(saveGame);
    }

    getEngine().registerUpdateHandler(new FPSLogger());
    return mRootBinder.get(GameResources.class).scene;
  }

  @Override
  protected void onResume() {
    super.onResume();
    MusicPlayer.get().onGameActivityResumed(GamePreferencesManager.getBoolean(
        this,
        Setting.IS_MUSIC_DISABLED_SETTING_BOOLEAN));
  }

  @Override
  public void onResumeGame() {
    super.onResumeGame();
    this.enableAccelerationSensor(this);
  }

  @Override
  public void onPauseGame() {
    super.onPauseGame();
    this.disableAccelerationSensor();

    // if we are already logged in then just save the game
    if (authManager.isLoggedIn()) {
      saveGameManager.saveGame(this, fabricateSaveGame());
    }
  }

  @Override
  public void saveGamePressed() {
    if (authManager.isLoggedIn()) {
      ContextCompat.getMainExecutor(this).execute(new Runnable() {
        @Override
        public void run() {
          saveGameManager.saveGame(GameActivity.this, fabricateSaveGame());
          showGameSavedToast();
        }
      });
    } else {
      startSaveGameFlow(false, true, false);
    }
  }

  @Override
  public boolean onKeyDown(int keyCode, KeyEvent event) {
    if (keyCode == KeyEvent.KEYCODE_BACK) {
      // If we are not logged in then we must launch a prompt for the user to logSelect in to save their
      // game
      if (!authManager.isLoggedIn()) {
        if (startSaveGameFlow(true, false, true)) {
          return true;
        }
      }
      // The save game flow cannot be not launched. proceed to exit the game
      goBackToMainMenu();
      return true;
    }
    return false;
  }

  private boolean startSaveGameFlow(final boolean allowForPermanentDismiss, final boolean forceShow,
      final boolean goToMainMenuAfter) {
    @Nullable Intent intent =
        SaveGameFlowDialog
            .getIntent(fabricateSaveGame(), allowForPermanentDismiss, forceShow, this);
    if (intent == null) {
      return false;
    }
    prepareCall(new StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
      @Override
      public void onActivityResult(ActivityResult result) {
        if (goToMainMenuAfter && result.getResultCode() != SaveGameFlowDialog.RESULT_DISMISSED) {
          goBackToMainMenu();
        }
        if (result.getResultCode() == SaveGameFlowDialog.RESULT_SUCCESS) {
          showGameSavedToast();
        }
      }
    }).launch(intent);
    return true;
  }

  private void showGameSavedToast() {
    Toast.makeText(GameActivity.this, R.string.game_saved, Toast.LENGTH_SHORT).show();
  }

  private void goBackToMainMenu() {
    startActivity(MainMenuActivity.newIntent(this));
    MusicPlayer.get().onLeaveGameActivity();
    finish();
  }

  @Override
  public SaveGame fabricateSaveGame() {
    SaveGame newSaveGame = new SaveGame();
    gameLifeCycleCalllbackManager.onSaveGame(newSaveGame);
    return newSaveGame;
  }

  @Override
  protected void onDestroy() {
    logGameExitData();
    // Destroy the game
    gameLifeCycleCalllbackManager.onDestroy();
    gameLifeCycleCalllbackManager = null;

    EventBus.get().onDestroy();
    super.onDestroy();
  }

  @Override
  public void onAccelerationAccuracyChanged(AccelerationData pAccelerationData) {
  }

  @Override
  public void onAccelerationChanged(AccelerationData pAccelerationData) {
  }

  private SaveGame getSaveGameFromIntent() {
    String saveGameJson = getIntent().getStringExtra(SAVE_GAME_EXTRA);
    SaveGame saveGame = SaveGame.fromJson(saveGameJson);
    return saveGame;
  }

  @Override
  public Context getContext() {
    return this;
  }

  @Override
  public GooglePlayServicesAuthManager getAuthManager() {
    return authManager;
  }

  private void logGameExitData() {
    Logger.logSelect(
        this,
        GAME_STOP_SCORE,
        mRootBinder.get(ScoreHudEntity.class).getScore());
    Logger.logSelect(
        this,
        GAME_STOP_PROGRESS,
        mRootBinder.get(GameDifficultyEntity.class).getGameProgress());
  }
}