package com.wack.pop2;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;

import com.wack.pop2.backgroundmusic.BackgroundMusicEntity;
import com.wack.pop2.ballandchain.BallAndChainManagerEntity;
import com.wack.pop2.bubblepopper.BubblePopperEntity;
import com.wack.pop2.bubblepopper.BufferedBubblePopperEntity;
import com.wack.pop2.bubbletimeout.BubblesLifecycleManagerEntity;
import com.wack.pop2.difficulty.GameDifficultyEntity;
import com.wack.pop2.eventbus.EventBus;
import com.wack.pop2.gamesettings.GamePreferencesEntity;
import com.wack.pop2.hudentities.InteractionScoreHudEntity;
import com.wack.pop2.hudentities.ScoreHudEntity;
import com.wack.pop2.hudentities.TimerHudEntity;
import com.wack.pop2.gameiconstray.GameIconsHostTrayEntity;
import com.wack.pop2.interaction.InteractionCounter;
import com.wack.pop2.nuke.NukeManagerEntity;
import com.wack.pop2.resources.fonts.GameFontsManager;
import com.wack.pop2.resources.music.GameMusicResourceManagerEntity;
import com.wack.pop2.resources.sounds.GameSoundsManager;
import com.wack.pop2.resources.textures.GameTexturesManager;
import com.wack.pop2.turret.TurretsManagerEntity;
import com.wack.pop2.utils.ScreenUtils;
import com.wack.pop2.walls.WallsManagerEntity;

import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.scene.Scene;
import org.andengine.input.sensor.acceleration.AccelerationData;
import org.andengine.input.sensor.acceleration.IAccelerationListener;
import org.andengine.ui.activity.SimpleBaseGameActivity;

public class GameActivity extends SimpleBaseGameActivity implements HostActivityInterface, IAccelerationListener {

	private ShakeCamera camera;

	//Debug
	// private DebugTouchTracerEntity mDebugTouchTracerEntity;

	private GameResources mGameResources;
	private LevelEntity mLevelEntity;
	private BackgroundMusicEntity mBackgroundMusicEntity;
	private GameDifficultyEntity mGameDifficultyEntity;
	private InteractionScoreHudEntity mInteractionScoreHudEntity;
	private ScoreHudEntity mScoreHudEntity;
	private TimerHudEntity mTimerHudEntity;
	private GameOverSequenceEntity mGameOverSequenceEntity;
	private BubblesLifecycleManagerEntity mBubblesLifecycleManagerEntity;
	private BubbleSpawnerEntity mBubbleSpawnerEntity;
	private BubbleLossDetectorEntity mBubbleLossDetectorEntity;
	private BubbleCleanerEntity mBubbleCleanerEntity;
	private BubblePopperEntity mBubblePopperEntity;
	private BufferedBubblePopperEntity mBufferedBubblePopperEntity;
	private TouchPopperEntity mTouchPopperEntity;
	private BallAndChainManagerEntity mBallAndChainManagerEntity;
	private TurretsManagerEntity mTurrentsManagerEntity;
	private WallsManagerEntity mWallsManagerEntity;
	private NukeManagerEntity mNukeManagerEntityEntity;

	public static Intent newIntent(Context context) {
		return new Intent(context, GameActivity.class);
	}

	@Override
	protected void onCreate(Bundle pSavedInstanceState) {
		super.onCreate(pSavedInstanceState);
		EventBus.init();
		GameLifeCycleCalllbackManager.init();

		// Initialize game resources
		mGameResources = GameResources.createNew(this, this);

		// Initialize game resource managers
		GameFontsManager gameFontsManager = new GameFontsManager(getFontManager(), getTextureManager(), getAssets(), mGameResources);
		GamePreferencesEntity preferencesEntity = new GamePreferencesEntity(this, mGameResources);
		GameTexturesManager gameTexturesManager = new GameTexturesManager(this, getTextureManager(), mGameResources);
		GameSoundsManager gameSoundsManager = new GameSoundsManager(this, getSoundManager(), mGameResources);
		GameMusicResourceManagerEntity gameMusicResourceManagerEntity = new GameMusicResourceManagerEntity(this, getMusicManager(), mGameResources);
		GameAnimationManager gameAnimationManager = new GameAnimationManager(mGameResources);
		GamePhysicsContactsEntity gamePhysicsContactsEntity = new GamePhysicsContactsEntity(mGameResources);
		InteractionCounter interactionCounter = new InteractionCounter(mGameResources);
		GameAreaTouchListenerEntity gameAreaTouchListenerEntity = new GameAreaTouchListenerEntity(interactionCounter, mGameResources);
		GameSceneTouchListenerEntity gameSceneTouchListenerEntity = new GameSceneTouchListenerEntity(interactionCounter, mGameResources);
		GameIconsHostTrayEntity gameIconsTrayEntity = new GameIconsHostTrayEntity(gameTexturesManager, gameAreaTouchListenerEntity, mGameResources);

		// Create game entities
		mLevelEntity = new LevelEntity(gameTexturesManager, mGameResources);
		mBackgroundMusicEntity = new BackgroundMusicEntity(gameMusicResourceManagerEntity, preferencesEntity, mGameResources);
		mInteractionScoreHudEntity = new InteractionScoreHudEntity(gameFontsManager, mGameResources);
		mScoreHudEntity = new ScoreHudEntity(gameFontsManager, mGameResources);
		mScoreHudEntity = new ScoreHudEntity(gameFontsManager, mGameResources);
		mGameDifficultyEntity = new GameDifficultyEntity(mGameResources);
		mTimerHudEntity = new TimerHudEntity(gameFontsManager, mGameResources);
		mGameOverSequenceEntity = new GameOverSequenceEntity(
				this,
				mScoreHudEntity,
				gameTexturesManager,
				gameSoundsManager,
				gameAnimationManager,
				camera,
				mGameResources);
		mBubblesLifecycleManagerEntity = new BubblesLifecycleManagerEntity(mGameResources);
		mBubbleSpawnerEntity = new BubbleSpawnerEntity(gameTexturesManager, mGameResources);
		mBubbleLossDetectorEntity = new BubbleLossDetectorEntity(gameFontsManager, gameAnimationManager, gamePhysicsContactsEntity, mGameResources);
		mBubbleCleanerEntity = new BubbleCleanerEntity(mGameResources);
		mBubblePopperEntity = new BubblePopperEntity(gameFontsManager, gameSoundsManager, gameAnimationManager, mBubbleSpawnerEntity, mGameResources);
		mBufferedBubblePopperEntity = new BufferedBubblePopperEntity(mBubblePopperEntity, mGameResources);
		mTouchPopperEntity = new TouchPopperEntity(gameAreaTouchListenerEntity, mBubblePopperEntity, mGameResources);
		mBallAndChainManagerEntity = new BallAndChainManagerEntity(gameTexturesManager, gameSceneTouchListenerEntity, gameIconsTrayEntity, gameAreaTouchListenerEntity, gamePhysicsContactsEntity, mBubblePopperEntity, mGameResources);
		mTurrentsManagerEntity = new TurretsManagerEntity(mBubblePopperEntity, gameTexturesManager, gameIconsTrayEntity, gamePhysicsContactsEntity, gameFontsManager, gameAreaTouchListenerEntity, gameSceneTouchListenerEntity, gameTexturesManager, gameSoundsManager, mGameResources);
		mWallsManagerEntity = new WallsManagerEntity(gameIconsTrayEntity, gameAreaTouchListenerEntity, gameSceneTouchListenerEntity, gameTexturesManager, gameFontsManager, mGameResources);
		mNukeManagerEntityEntity = new NukeManagerEntity(mBufferedBubblePopperEntity, gameIconsTrayEntity, gameTexturesManager, gameAreaTouchListenerEntity, mGameResources);

		// Debug
		// mDebugTouchTracerEntity = new DebugTouchTracerEntity(gameSceneTouchListenerEntity, mGameResources);
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
		return engineOptions;
	}

	@Override
	public void onCreateResources() {
		ScreenUtils.onCreateResources(getVertexBufferObjectManager());
		GameLifeCycleCalllbackManager.getInstance().onCreateResources();
	}

	@Override
	public Scene onCreateScene() {
		GameLifeCycleCalllbackManager.getInstance().onCreateScene();
		return mGameResources.scene;
	}

	@Override
	public boolean onKeyDown(final int pKeyCode, final KeyEvent pEvent) {
		if(pKeyCode == KeyEvent.KEYCODE_BACK && pEvent.getAction() == KeyEvent.ACTION_DOWN) {
			startActivity(MainMenuActivity.newIntent(this));
			finish();
			return true;
		}
		return super.onKeyDown(pKeyCode, pEvent);
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
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		GameLifeCycleCalllbackManager.getInstance().onDestroy();
		EventBus.destroy();
		GameLifeCycleCalllbackManager.destroy();
	}

	@Override
	public void onAccelerationAccuracyChanged(AccelerationData pAccelerationData) {}

	@Override
	public void onAccelerationChanged(AccelerationData pAccelerationData) {}

	@Override
	public Context getActivityContext() {
		return this;
	}
}