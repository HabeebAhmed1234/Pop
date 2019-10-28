package com.wack.pop2;

import android.content.Intent;
import android.opengl.GLES20;
import android.view.KeyEvent;

import com.wack.pop2.eventbus.EventBus;
import com.wack.pop2.hudentities.ScoreHudEntity;
import com.wack.pop2.hudentities.TimerHudEntity;
import com.wack.pop2.physics.PhysicsConnector;
import com.wack.pop2.physics.PhysicsFactory;
import com.wack.pop2.physics.util.Vec2Pool;
import com.wack.pop2.resources.fonts.GameFontsManager;
import com.wack.pop2.resources.sounds.GameSoundsManager;
import com.wack.pop2.resources.textures.GameTexturesManager;

import org.andengine.audio.sound.Sound;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.modifier.AlphaModifier;
import org.andengine.entity.modifier.ParallelEntityModifier;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.modifier.SequenceEntityModifier;
import org.andengine.entity.scene.IOnAreaTouchListener;
import org.andengine.entity.scene.ITouchArea;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.entity.util.FPSLogger;
import org.andengine.input.sensor.acceleration.AccelerationData;
import org.andengine.input.sensor.acceleration.IAccelerationListener;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;

import java.util.LinkedList;

public class GameActivity extends SimpleBaseGameActivity implements IAccelerationListener, IOnAreaTouchListener, HostActivityInterface {

	private ShakeCamera camera;

	private LevelEntity mLevelEntity;
	private ScoreHudEntity mScoreHudEntity;
	private TimerHudEntity mTimerHudEntity;
	private GameOverSequenceEntity mGameOverSequenceEntity;
	private BubbleSpawnerEntity mBubbleSpawnerEntity;
	private BubbleLossDetectorEntity mBubbleLossDetectorEntity;

	private int whichface=0;
	private float difficulty=1f;
	private float maxdifficulty=5;
	private int score=0;
	private int Timer=120;
	private Text TimerText;
	private LinkedList<Sprite> faces=new LinkedList<Sprite>();


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

		//update handlers
		scene.setOnAreaTouchListener(this);
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
	public boolean onAreaTouched( final TouchEvent pSceneTouchEvent, final ITouchArea pTouchArea,final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
		if(pSceneTouchEvent.isActionDown()) {
			final Sprite face =  (Sprite) pTouchArea;
			//if face is skull then Game over
			if(face.getTextureRegion()==mSkullBallTextureRegion)
			{
				gameoversequence(face.getX(), face.getY(), face.getWidth(), face.getHeight(),face.getScaleX());
				removeFace(face);
				isgameover=true;
			}
			//else game continue
			else{
				this.jumpFace(face,2f,0);
				float fx=face.getX();
				float fy=face.getY();
				increaseScore(10);
				if(face.getScaleX()>1.2)
				{
					breakface(face,fx,fy);
				}else{
					removeFace(face);
				}
				whichsound().play();

				Text scoretxt=createScoretickerText(pSceneTouchEvent.getX(),pSceneTouchEvent.getY());
				mScene.attachChild(scoretxt);
			}
			return true;
		}

		return false;
	}


	public Sound whichsound()
	{
		Sound sound=mPop1Sound;
		int selector=(int) (Math.random()*4+1);
		if(selector==1)
		{
			sound=mPop1Sound;
		}
		if(selector==2)
		{
			sound=mPop2Sound;
		}
		if(selector==3)
		{
			sound=mPop3Sound;
		}
		if(selector==4)
		{
			sound=mPop4Sound;
		}
		if(selector==5)
		{
			sound=mPop5Sound;
		}
		return sound;
	}

	@Override
	public void onAccelerationAccuracyChanged(final AccelerationData pAccelerationData) {

	}

	@Override
	public void onAccelerationChanged(final AccelerationData pAccelerationData) {
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

	// ===========================================================
	// Methods
	// ===========================================================
	private void breakface(Sprite face,float fx,float fy)
	{
		Sprite newface=face;
		removeFace(face);
		addBrokenFace(fx,fy,newface, "left");
		addBrokenFace(fx+(newface.getWidth()/2),fy,newface,"right");

	}

	private void addBrokenFace(final float pX, final float pY, Sprite oldface, String jumpDirection)
	{
		final Sprite face;
		final Body body;
		final FixtureDef objectFixtureDef = PhysicsFactory.createFixtureDef(1, 0.5f, 0.5f);

		//add object
		face = new Sprite(pX, pY, oldface.getTextureRegion(), this.getVertexBufferObjectManager());
		face.setScale((float) (oldface.getScaleX()*0.6));
		body = PhysicsFactory.createBoxBody(this.mPhysicsWorld, face, BodyType.DYNAMIC, objectFixtureDef);
		this.mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(face, body, true, true));
		face.setUserData(body);
		this.mScene.registerTouchArea(face);

		faces.add(face);

		this.mScene.attachChild(face);
		if(jumpDirection=="left")
		{
			jumpFace(face,1.2f,-3f);

		}else{
			jumpFace(face,1.2f,3f);
		}
	}

	private void increaseScore(int increment)
	{
		score+=increment;
		ScoreText.setText("Score: "+score);
		if(score%100==0&&difficulty<maxdifficulty)
		{
			increaseDifficulty();
		}
	}

	private void decreaseScore(int increment)
	{
		score-=increment;
		ScoreText.setText("Score: "+score);
	}

	private void increaseDifficulty()
	{
		difficulty+=1;
	}


	private void jumpFace(final Sprite face,float yspeed, float xspeed) {
		final Body faceBody = (Body)face.getUserData();
		final Vec2 velocity = Vec2Pool.obtain(this.mGravityX+xspeed, (float) (this.mGravityY *-1*yspeed));
		faceBody.setLinearVelocity(velocity);
		Vec2Pool.recycle(velocity);
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

	private Text createScoretickerText(float x, float y)
	{
		final Text scorePlus10 = new Text(x, y, this.mScoreTickerFont, "+10!", this.getVertexBufferObjectManager());
		scorePlus10.registerEntityModifier(
				new SequenceEntityModifier(
						new ParallelEntityModifier(
								new ScaleModifier(0.75f, 0.1f, 1.1f),
								new AlphaModifier(0.75f, 1f, 0f)
						)
				)
		);
		scorePlus10.setBlendFunction(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
		scorePlus10.setColor(0, 1, 0);
		return scorePlus10;
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
}