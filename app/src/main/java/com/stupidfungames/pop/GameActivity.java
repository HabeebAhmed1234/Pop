package com.stupidfungames.pop;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.view.KeyEvent;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.ActivityResultRegistry;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.stupidfungames.pop.auth.GooglePlayServicesAuthManager;
import com.stupidfungames.pop.backgroundmusic.BackgroundMusicBaseEntity;
import com.stupidfungames.pop.ballandchain.BallAndChainManagerBaseEntity;
import com.stupidfungames.pop.binder.Binder;
import com.stupidfungames.pop.binder.BinderEnity;
import com.stupidfungames.pop.bubblepopper.BubblePopperEntity;
import com.stupidfungames.pop.bubblepopper.BufferedBubblePopperBaseEntity;
import com.stupidfungames.pop.bubblespawn.BubbleSpawnerEntity;
import com.stupidfungames.pop.bubbletimeout.BubblesLifecycleManagerEntity;
import com.stupidfungames.pop.difficulty.GameDifficultyEntity;
import com.stupidfungames.pop.eventbus.EventBus;
import com.stupidfungames.pop.gameiconstray.GameIconsHostTrayEntity;
import com.stupidfungames.pop.hudentities.ScoreHudEntity;
import com.stupidfungames.pop.hudentities.TimerHudEntity;
import com.stupidfungames.pop.interaction.InteractionCounter;
import com.stupidfungames.pop.nuke.NukeManagerBaseEntity;
import com.stupidfungames.pop.resources.fonts.GameFontsManager;
import com.stupidfungames.pop.resources.music.GameMusicResourceManagerBaseEntity;
import com.stupidfungames.pop.resources.sounds.GameSoundsManager;
import com.stupidfungames.pop.resources.sounds.SoundId;
import com.stupidfungames.pop.resources.textures.GameTexturesManager;
import com.stupidfungames.pop.savegame.SaveGame;
import com.stupidfungames.pop.savegame.SaveGameFlowDialog;
import com.stupidfungames.pop.savegame.SaveGameManager;
import com.stupidfungames.pop.settingstray.GamePauseQuickSettingsIconBaseEntity;
import com.stupidfungames.pop.settingstray.GameQuickSettingsHostTrayBaseEntity;
import com.stupidfungames.pop.settingstray.MusicQuickSettingIconBaseEntity;
import com.stupidfungames.pop.tooltips.GameTooltipsEntity;
import com.stupidfungames.pop.turret.TurretsManagerEntity;
import com.stupidfungames.pop.utils.ScreenUtils;
import com.stupidfungames.pop.walls.WallsManagerBaseEntity;
import org.andengine.audio.music.MusicManager;
import org.andengine.audio.sound.SoundManager;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.scene.Scene;
import org.andengine.input.sensor.acceleration.AccelerationData;
import org.andengine.input.sensor.acceleration.IAccelerationListener;
import org.andengine.opengl.font.FontManager;
import org.andengine.opengl.texture.TextureManager;
import org.andengine.ui.activity.SimpleBaseGameActivity;

public class GameActivity extends SimpleBaseGameActivity implements HostActivity, IAccelerationListener, GamePauser {

	public static final String SAVE_GAME_EXTRA = "save_game";
	private static final int PAUSE_ACTIVITY_REQUEST_CODE = 1;
	private static final int SAVE_GAME_FLOW_REQUEST_CODE = 2;

	private ShakeCamera camera;
	private GameLifeCycleCalllbackManager gameLifeCycleCalllbackManager;

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
		gameLifeCycleCalllbackManager = new GameLifeCycleCalllbackManager();

		// The root binder
		mRootBinder = new BinderEnity(null) {

			@Override
			protected void createBindings(Binder binder) {
				binder
						.bind(GameLifeCycleCalllbackManager.class, gameLifeCycleCalllbackManager)
						.bind(GameResources.class, GameResources.createNew(GameActivity.this, GameActivity.this))
						.bind(FontManager.class, getFontManager())
						.bind(TextureManager.class, getTextureManager())
						.bind(AssetManager.class, getAssets())
						.bind(Context.class, GameActivity.this)
						.bind(SoundManager.class, getSoundManager())
						.bind(MusicManager.class, getMusicManager())
						.bind(ShakeCamera.class, camera)
						.bind(GamePauser.class, GameActivity.this)

						.bind(GameTexturesManager.class, new GameTexturesManager(this))
						.bind(GameSoundsManager.class, new GameSoundsManager(this))
						.bind(GameMusicResourceManagerBaseEntity.class, new GameMusicResourceManagerBaseEntity(this))
						.bind(BackgroundMusicBaseEntity.class, new BackgroundMusicBaseEntity(this))
						.bind(GameFontsManager.class, new GameFontsManager(this))
						.bind(GameAnimationManager.class, new GameAnimationManager(this))
						.bind(LevelBaseEntity.class, new LevelBaseEntity(this))

						.bind(GamePhysicsContactsEntity.class, new GamePhysicsContactsEntity(this))
						.bind(InteractionCounter.class, new InteractionCounter(this))
						.bind(GameSceneTouchListenerEntity.class, new GameSceneTouchListenerEntity(this))

						.bind(GameTooltipsEntity.class, new GameTooltipsEntity(this))
						.bind(GameStartTooltipEntity.class, new GameStartTooltipEntity(this))

						.bind(GameQuickSettingsHostTrayBaseEntity.class, new GameQuickSettingsHostTrayBaseEntity(this))
						.bind(MusicQuickSettingIconBaseEntity.class, new MusicQuickSettingIconBaseEntity(this))
						.bind(GamePauseQuickSettingsIconBaseEntity.class, new GamePauseQuickSettingsIconBaseEntity(this))

						.bind(ScoreHudEntity.class, new ScoreHudEntity(this))
						.bind(TimerHudEntity.class, new TimerHudEntity(this))

						.bind(GameDifficultyEntity.class, new GameDifficultyEntity(this))
						.bind(GameOverSequenceEntity.class, new GameOverSequenceEntity(this))
						.bind(BubblesLifecycleManagerEntity.class, new BubblesLifecycleManagerEntity(this))

						.bind(BubbleSpawnerEntity.class, new BubbleSpawnerEntity(this))
						.bind(BubbleCleanerBaseEntity.class, new BubbleCleanerBaseEntity(this))
						.bind(BubbleLossDetectorBaseEntity.class, new BubbleLossDetectorBaseEntity(this))
						.bind(BubblePopperEntity.class, new BubblePopperEntity(this))
						.bind(TouchPopperFactoryEntity.class, new TouchPopperFactoryEntity(this))
						.bind(BufferedBubblePopperBaseEntity.class, new BufferedBubblePopperBaseEntity(this))

						.bind(GameIconsHostTrayEntity.class, new GameIconsHostTrayEntity(this))
						.bind(BallAndChainManagerBaseEntity.class, new BallAndChainManagerBaseEntity(this))
						.bind(TurretsManagerEntity.class, new TurretsManagerEntity(this))
						.bind(WallsManagerBaseEntity.class, new WallsManagerBaseEntity(this))
						.bind(NukeManagerBaseEntity.class, new NukeManagerBaseEntity(this))

				;
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
			if(resultCode == GamePauseActivity.RESULT_QUIT) {
				startActivity(MainMenuActivity.newIntent(this));
				finish();
			}
		} else if (requestCode == SAVE_GAME_FLOW_REQUEST_CODE) {
			if (resultCode == SaveGameFlowDialog.RESULT_SUCCESS
					|| resultCode == SaveGameFlowDialog.RESULT_DECLINED
					|| resultCode == SaveGameFlowDialog.RESULT_DECLINED_PERMANENT) {
				goBackToMainMenu();
			}
		}
	}

	@Override
	public EngineOptions onCreateEngineOptions() {
		ScreenUtils.onCreateEngineOptions(this);
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
		return mRootBinder.get(GameResources.class).scene;
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
		if (GooglePlayServicesAuthManager.get(this, this).isLoggedIn()) {
			SaveGameManager.get(this, this).saveGame(this, fabricateSaveGame());
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)  {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			// If we are not logged in then we must launch a prompt for the user to log in to save their
			// game
			if (!GooglePlayServicesAuthManager.get(this, this).isLoggedIn()) {
				// try launching the save game flow
				@Nullable Intent saveGameFlowIntent = SaveGameFlowDialog.getIntent(fabricateSaveGame(), this);
				if (saveGameFlowIntent != null) {
					startActivityForResult(saveGameFlowIntent, SAVE_GAME_FLOW_REQUEST_CODE);
					return true;
				}
			}
			// The save game flow cannot be not launched. proceed to exit the game
			goBackToMainMenu();
			return true;
		}
		return false;
	}

	private void goBackToMainMenu() {
		startActivity(MainMenuActivity.newIntent(this));
		finish();
	}

	private SaveGame fabricateSaveGame() {
		SaveGame newSaveGame = new SaveGame();
		gameLifeCycleCalllbackManager.onSaveGame(newSaveGame);
		return newSaveGame;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		// Destroy the game
		gameLifeCycleCalllbackManager.onDestroy();
		EventBus.destroy();
		gameLifeCycleCalllbackManager = null;
	}

	@Override
	public void onAccelerationAccuracyChanged(AccelerationData pAccelerationData) {}

	@Override
	public void onAccelerationChanged(AccelerationData pAccelerationData) {}

	private SaveGame getSaveGameFromIntent() {
		String saveGameJson = getIntent().getStringExtra(SAVE_GAME_EXTRA);
		SaveGame saveGame = SaveGame.fromJson(saveGameJson);
		return saveGame;
	}
}