package com.wack.pop2;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;

import com.wack.pop2.ballandchain.BallAndChainManagerEntity;
import com.wack.pop2.eventbus.EventBus;
import com.wack.pop2.hudentities.ScoreHudEntity;
import com.wack.pop2.hudentities.TimerHudEntity;
import com.wack.pop2.resources.fonts.GameFontsManager;
import com.wack.pop2.resources.sounds.GameSoundsManager;
import com.wack.pop2.resources.textures.GameTexturesManager;
import com.wack.pop2.turret.TurretsManagerEntity;
import com.wack.pop2.utils.ScreenUtils;
import com.wack.pop2.walls.WallsManager;

import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.scene.Scene;
import org.andengine.input.sensor.acceleration.AccelerationData;
import org.andengine.input.sensor.acceleration.IAccelerationListener;
import org.andengine.ui.activity.SimpleBaseGameActivity;

public class GameActivity extends SimpleBaseGameActivity implements HostActivityInterface, IAccelerationListener {

	private ShakeCamera camera;

	private GameResources mGameResources;
	private LevelEntity mLevelEntity;
	private GameDifficultyEntity mGameDifficultyEntity;
	private ScoreHudEntity mScoreHudEntity;
	private TimerHudEntity mTimerHudEntity;
	private GameOverSequenceEntity mGameOverSequenceEntity;
	private BubbleSpawnerEntity mBubbleSpawnerEntity;
	private BubbleLossDetectorEntity mBubbleLossDetectorEntity;
	private BubbleCleanerEntity mBubbleCleanerEntity;
	private BubblePopperEntity mBubblePopperEntity;
	private TouchPopperEntity mTouchPopperEntity;
	private BallAndChainManagerEntity mBallAndChainManagerEntity;
	private TurretsManagerEntity mTurrentsManagerEntity;
	private WallsManager mWallsManager;

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
		GameTexturesManager gameTexturesManager = new GameTexturesManager(this, getTextureManager(), mGameResources);
		GameSoundsManager gameSoundsManager = new GameSoundsManager(this, getSoundManager(), mGameResources);
		GameAnimationManager gameAnimationManager = new GameAnimationManager(mGameResources);
		GamePhysicsContactsEntity gamePhysicsContactsEntity = new GamePhysicsContactsEntity(mGameResources);
		GameAreaTouchListenerEntity gameAreaTouchListenerEntity = new GameAreaTouchListenerEntity(mGameResources);
		GameSceneTouchListenerEntity gameSceneTouchListenerEntity = new GameSceneTouchListenerEntity(mGameResources);
		GameIconsTrayEntity gameIconsTrayEntity = new GameIconsTrayEntity(mGameResources);

		// Create game entities
		mLevelEntity = new LevelEntity(mGameResources);
		mGameDifficultyEntity = new GameDifficultyEntity(mGameResources);
		mScoreHudEntity = new ScoreHudEntity(gameFontsManager, mGameResources);
		mTimerHudEntity = new TimerHudEntity(gameFontsManager, gameAnimationManager, mGameResources);
		mGameOverSequenceEntity = new GameOverSequenceEntity(
				this,
				mScoreHudEntity,
				gameTexturesManager,
				gameSoundsManager,
				gameAnimationManager,
				camera,
				mGameResources);
		mBubbleSpawnerEntity = new BubbleSpawnerEntity(gameTexturesManager, mGameResources);
		mBubbleLossDetectorEntity = new BubbleLossDetectorEntity(gameFontsManager, gameAnimationManager, gamePhysicsContactsEntity, mGameResources);
		mBubbleCleanerEntity = new BubbleCleanerEntity(mGameResources);
		mBubblePopperEntity = new BubblePopperEntity(gameFontsManager, gameSoundsManager, gameAnimationManager, mBubbleSpawnerEntity, mGameResources);
		mTouchPopperEntity = new TouchPopperEntity(gameAreaTouchListenerEntity, mBubblePopperEntity, mGameResources);
		mBallAndChainManagerEntity = new BallAndChainManagerEntity(gameTexturesManager, gameSceneTouchListenerEntity, gameIconsTrayEntity, gameAreaTouchListenerEntity, gamePhysicsContactsEntity, mBubblePopperEntity, mGameResources);
		mTurrentsManagerEntity = new TurretsManagerEntity(mBubblePopperEntity, gameTexturesManager, gameIconsTrayEntity, gamePhysicsContactsEntity, gameFontsManager, gameSceneTouchListenerEntity, gameTexturesManager, mGameResources);
		mWallsManager = new WallsManager(gameSceneTouchListenerEntity, gameTexturesManager, mGameResources);
	}

	@Override
	public EngineOptions onCreateEngineOptions() {
		ScreenUtils.init(this);
		ScreenUtils.ScreenSize screenSize = ScreenUtils.getSreenSize();
		camera = new ShakeCamera(0, 0, screenSize.width, screenSize.height);
		final EngineOptions engineOptions = new EngineOptions(
				true,
				ScreenOrientation.PORTRAIT_FIXED,
				new RatioResolutionPolicy(screenSize.width, screenSize.height),
				this.camera);
		engineOptions.getTouchOptions().setNeedsMultiTouch(true);
		engineOptions.getAudioOptions().setNeedsSound(true);
		return engineOptions;
	}

	@Override
	public void onCreateResources() {
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
}