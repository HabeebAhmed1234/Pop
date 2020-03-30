package com.wack.pop2;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.view.KeyEvent;

import com.wack.pop2.backgroundmusic.BackgroundMusicBaseEntity;
import com.wack.pop2.ballandchain.BallAndChainManagerBaseEntity;
import com.wack.pop2.binder.BinderEnity;
import com.wack.pop2.binder.Binder;
import com.wack.pop2.bubblepopper.BubblePopperEntity;
import com.wack.pop2.bubblepopper.BufferedBubblePopperBaseEntity;
import com.wack.pop2.bubblespawn.BubbleSpawnerEntity;
import com.wack.pop2.bubbletimeout.BubblesLifecycleManagerEntity;
import com.wack.pop2.difficulty.GameDifficultyBaseEntity;
import com.wack.pop2.eventbus.EventBus;
import com.wack.pop2.gamesettings.GamePreferencesEntity;
import com.wack.pop2.hudentities.ScoreHudEntity;
import com.wack.pop2.hudentities.TimerHudEntity;
import com.wack.pop2.gameiconstray.GameIconsHostTrayEntity;
import com.wack.pop2.interaction.InteractionCounter;
import com.wack.pop2.nuke.NukeManagerBaseEntity;
import com.wack.pop2.resources.fonts.GameFontsManager;
import com.wack.pop2.resources.music.GameMusicResourceManagerBaseEntity;
import com.wack.pop2.resources.sounds.GameSoundsManager;
import com.wack.pop2.resources.sounds.SoundId;
import com.wack.pop2.resources.textures.GameTexturesManager;
import com.wack.pop2.settingstray.GamePauseQuickSettingsIconBaseEntity;
import com.wack.pop2.settingstray.GameQuickSettingsHostTrayBaseEntity;
import com.wack.pop2.settingstray.MusicQuickSettingIconBaseEntity;
import com.wack.pop2.tooltips.GameTooltipsEntity;
import com.wack.pop2.turret.TurretsManagerBaseEntity;
import com.wack.pop2.utils.ScreenUtils;
import com.wack.pop2.walls.WallsManagerBaseEntity;

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

public class GameActivity extends SimpleBaseGameActivity implements HostActivityInterface, IAccelerationListener, GamePauser {

	private static final int PAUSE_ACTIVITY_REQUEST_CODE = 1;

	private ShakeCamera camera;
	BinderEnity mRootBinder;

	public static Intent newIntent(Context context) {
		return new Intent(context, GameActivity.class);
	}

	@Override
	protected void onCreate(Bundle pSavedInstanceState) {
		super.onCreate(pSavedInstanceState);
		EventBus.init();
		GameLifeCycleCalllbackManager.init();

		// The root binder
		mRootBinder = new BinderEnity(null) {

			@Override
			protected void createBindings(Binder binder) {
				binder
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
						.bind(GamePreferencesEntity.class, new GamePreferencesEntity(this))
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

						.bind(GameDifficultyBaseEntity.class, new GameDifficultyBaseEntity(this))
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
						.bind(TurretsManagerBaseEntity.class, new TurretsManagerBaseEntity(this))
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
		GameLifeCycleCalllbackManager.getInstance().onCreateResources();
		onPauseGame();
	}

	@Override
	public Scene onCreateScene() {
		GameLifeCycleCalllbackManager.getInstance().onCreateScene();
		return mRootBinder.get(GameResources.class).scene;
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