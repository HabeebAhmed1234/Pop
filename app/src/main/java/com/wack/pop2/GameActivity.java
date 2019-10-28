package com.wack.pop2;

import android.content.Intent;
import android.opengl.GLES20;
import android.view.KeyEvent;

import com.wack.pop2.eventbus.EventBus;
import com.wack.pop2.hudentities.ScoreHudEntity;
import com.wack.pop2.hudentities.TimerHudEntity;
import com.wack.pop2.resources.fonts.GameFontsManager;
import com.wack.pop2.resources.sounds.GameSoundsManager;
import com.wack.pop2.resources.textures.GameTexturesManager;

import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.modifier.AlphaModifier;
import org.andengine.entity.modifier.ParallelEntityModifier;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.modifier.SequenceEntityModifier;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.text.Text;
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

		// Create game entities
		mLevelEntity = new LevelEntity(gameResources);
		mGameDifficultyEntity = new GameDifficultyEntity(gameResources);
		mScoreHudEntity = new ScoreHudEntity(gameFontsManager, gameTexturesManager, gameResources);
		mTimerHudEntity = new TimerHudEntity(gameFontsManager, gameTexturesManager, gameResources);
		mGameOverSequenceEntity = new GameOverSequenceEntity(
				this,
				mScoreHudEntity,
				gameTexturesManager,
				gameSoundsManager,
				camera,
				gameResources);
		mBubbleSpawnerEntity = new BubbleSpawnerEntity(gameTexturesManager, gameResources);
		mBubbleLossDetectorEntity = new BubbleLossDetectorEntity(gameFontsManager, gameResources);
		mBubblePopperEntity = new BubblePopperEntity(gameFontsManager, gameSoundsManager, gameResources);

		//update handlers
		checkforlossandtimertimehandler();

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

	private void checkforlossandtimertimehandler() {
		TimerHandler itemTimerHandler;
		float mEffectSpawnDelay = 1f;

		itemTimerHandler = new TimerHandler(mEffectSpawnDelay, true,
				new ITimerCallback() {

					@Override
					public void onTimePassed(TimerHandler pTimerHandler) {

						Timer-=1;
						TimerText.setText("Time: "+Timer);
						if(Timer<=10)
						{
							mScene.attachChild(createCountDownText((int)Timer));
						}
						if(Timer<1&&isgameover==false)
						{
							TimerText.setText("TIMES UP!");
							isgameover=true;
							gameover();
						}

					}
				});

		getEngine().registerUpdateHandler(itemTimerHandler);
	}

	private Text createCountDownText(int time)
	{

		final Text Countdown = new Text(0, CAMERA_HEIGHT/3, this.mCountdownFont, ""+time , this.getVertexBufferObjectManager());
		float newscale=CAMERA_WIDTH/Countdown.getWidth();
		Countdown.registerEntityModifier(
				new SequenceEntityModifier(
						new ParallelEntityModifier(
								new ScaleModifier(1.0f, newscale, 0f),
								new AlphaModifier(1.0f, 0.0f, 1f)
						)
				)
		);
		Countdown.setX(CAMERA_WIDTH/2-Countdown.getWidth()/2);
		Countdown.setBlendFunction(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
		Countdown.setColor(1, 0, 0);

		return Countdown;
	}

	@Override
	public void onAccelerationAccuracyChanged(AccelerationData pAccelerationData) {}

	@Override
	public void onAccelerationChanged(AccelerationData pAccelerationData) {}
}