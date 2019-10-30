package com.wack.pop2;

import android.content.Intent;
import android.view.KeyEvent;

import com.wack.pop2.eventbus.EventBus;
import com.wack.pop2.hudentities.ScoreHudEntity;
import com.wack.pop2.hudentities.TimerHudEntity;
import com.wack.pop2.resources.fonts.GameFontsManager;
import com.wack.pop2.resources.sounds.GameSoundsManager;
import com.wack.pop2.resources.textures.GameTexturesManager;

import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.util.FPSLogger;
import org.andengine.input.sensor.acceleration.AccelerationData;
import org.andengine.input.sensor.acceleration.IAccelerationListener;
import org.andengine.ui.activity.SimpleBaseGameActivity;

public class GameActivity extends SimpleBaseGameActivity implements HostActivityInterface, IAccelerationListener {

	private ShakeCamera camera;

	private LevelEntity mLevelEntity;
	private GameDifficultyEntity mGameDifficultyEntity;
	private ScoreHudEntity mScoreHudEntity;
	private TimerHudEntity mTimerHudEntity;
	private GameOverSequenceEntity mGameOverSequenceEntity;
	private BubbleSpawnerEntity mBubbleSpawnerEntity;
	private BubbleLossDetectorEntity mBubbleLossDetectorEntity;
	private BubblePopperEntity mBubblePopperEntity;

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
		GameLifeCycleCalllbackManager.init();
		GameLifeCycleCalllbackManager.getInstance().onCreateResources();
	}

	@Override
	public Scene onCreateScene() {
		// Andengine setup code
		this.mEngine.registerUpdateHandler(new FPSLogger());
		EventBus.init();

		// Initialize game resources
		GameResources gameResources = GameResources.createNew(this, this);

		// Initialize game resource managers
		GameFontsManager gameFontsManager = new GameFontsManager(getFontManager(), getTextureManager(), gameResources);
		GameTexturesManager gameTexturesManager = new GameTexturesManager(this, getTextureManager(), gameResources);
		GameSoundsManager gameSoundsManager = new GameSoundsManager(this, getSoundManager(), gameResources);
		GameAnimationManager gameAnimationManager = new GameAnimationManager(gameResources);

		// Create game entities
		mLevelEntity = new LevelEntity(gameResources);
		mGameDifficultyEntity = new GameDifficultyEntity(gameResources);
		mScoreHudEntity = new ScoreHudEntity(gameFontsManager, gameTexturesManager, gameResources);
		mTimerHudEntity = new TimerHudEntity(gameFontsManager, gameTexturesManager, gameAnimationManager, gameResources);
		mGameOverSequenceEntity = new GameOverSequenceEntity(
				this,
				mScoreHudEntity,
				gameTexturesManager,
				gameSoundsManager,
				gameAnimationManager,
				camera,
				gameResources);
		mBubbleSpawnerEntity = new BubbleSpawnerEntity(gameTexturesManager, gameResources);
		mBubbleLossDetectorEntity = new BubbleLossDetectorEntity(gameFontsManager, gameAnimationManager, gameResources);
		mBubblePopperEntity = new BubblePopperEntity(gameFontsManager, gameSoundsManager, gameAnimationManager, gameResources);

		GameLifeCycleCalllbackManager.getInstance().onCreateScene();
		return gameResources.scene;
	}

	@Override
	public boolean onKeyDown(final int pKeyCode, final KeyEvent pEvent) {
		if(pKeyCode == KeyEvent.KEYCODE_BACK && pEvent.getAction() == KeyEvent.ACTION_DOWN) {
			Intent intent = new Intent(GameActivity.this, MainMenuActivity.class);
			startActivity(intent);
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
	public void onAccelerationAccuracyChanged(AccelerationData pAccelerationData) {}

	@Override
	public void onAccelerationChanged(AccelerationData pAccelerationData) {}
}