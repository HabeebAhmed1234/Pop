package com.wack.pop2;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.hardware.SensorManager;
import android.opengl.GLES20;
import android.os.Bundle;
import android.view.Display;
import android.view.KeyEvent;
import android.widget.Toast;

import com.wack.pop2.physics.PhysicsConnector;
import com.wack.pop2.physics.PhysicsFactory;
import com.wack.pop2.physics.PhysicsWorld;
import com.wack.pop2.physics.util.Vec2Pool;

import org.andengine.audio.sound.Sound;
import org.andengine.audio.sound.SoundFactory;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.modifier.AlphaModifier;
import org.andengine.entity.modifier.ParallelEntityModifier;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.modifier.SequenceEntityModifier;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.IOnAreaTouchListener;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.ITouchArea;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.scene.menu.MenuScene.IOnMenuItemClickListener;
import org.andengine.entity.scene.menu.item.IMenuItem;
import org.andengine.entity.scene.menu.item.SpriteMenuItem;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.entity.util.FPSLogger;
import org.andengine.input.sensor.acceleration.AccelerationData;
import org.andengine.input.sensor.acceleration.IAccelerationListener;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.util.debug.Debug;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;

import java.io.IOException;
import java.util.LinkedList;

public class GameActivity extends SimpleBaseGameActivity implements IAccelerationListener, IOnSceneTouchListener, IOnAreaTouchListener, IOnMenuItemClickListener {
	// ===========================================================
	// Constants
	// ===========================================================



	// ===========================================================
	// Fields
	// ===========================================================

	private BitmapTextureAtlas mBitmapTextureAtlas;

	private BitmapTextureAtlas mExplosionBitmapTextureAtlas;

	private ITextureRegion mRedBallTextureRegion;
	private ITextureRegion mBlueBallTextureRegion;
	private ITextureRegion mGreenBallTextureRegion;
	private ITextureRegion mSkullBallTextureRegion;
	private ITextureRegion mBackground;
	private ITextureRegion mScorebackground;

	private TiledTextureRegion mExplosionTextureRegion;
	private ITextureRegion mGameOverFadeTextureRegion;
	private Sprite gameoverfadered;

	private boolean isgameover=false;

	private PhysicsWorld mPhysicsWorld;

	private float mGravityX;
	private float mGravityY;

	private int CAMERA_WIDTH;
	private int CAMERA_HEIGHT;
	private ShakeCamera camera;
	private Scene mScene;

	protected MenuScene mMenuScene;
	private BitmapTextureAtlas mMenuTexture;
	protected ITextureRegion mMenuQuitTextureRegion;
	protected static final int MENU_QUIT = 0;

	private int whichface=0;

	private float difficulty=1f;
	private float maxdifficulty=5;

	private Font mFont;
	private Font mScoreTickerFont;
	private Font mCountdownFont;

	private int score=0;
	private Text ScoreText;

	private int Timer=120;
	private Text TimerText;

	private LinkedList<Sprite> faces=new LinkedList<Sprite>();


	private Sound mPop1Sound;
	private Sound mPop2Sound;
	private Sound mPop3Sound;
	private Sound mPop4Sound;
	private Sound mPop5Sound;

	private Sound mExplosionSound;

	// ===========================================================
	// Constructors
	// ===========================================================

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	@Override
	public EngineOptions onCreateEngineOptions() {
		Toast.makeText(this, "! POP THE BUBBLES !", Toast.LENGTH_SHORT).show();

		if (  Integer.valueOf(android.os.Build.VERSION.SDK_INT) < 13 ) {
			Display display = getWindowManager().getDefaultDisplay();
			CAMERA_WIDTH = display.getWidth();
			CAMERA_HEIGHT = display.getHeight();
		} else {
			Display display = getWindowManager().getDefaultDisplay();
			Point size = new Point();
			display.getSize(size);
			CAMERA_WIDTH = size.x;
			CAMERA_HEIGHT = size.y;
		}
		camera = new ShakeCamera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);

		final EngineOptions engineOptions = new EngineOptions(true, ScreenOrientation.PORTRAIT_FIXED, new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), this.camera);
		engineOptions.getTouchOptions().setNeedsMultiTouch(true);
		engineOptions.getAudioOptions().setNeedsSound(true);

		return engineOptions;
	}

	@Override
	public void onCreateResources() {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");

		this.mExplosionBitmapTextureAtlas = new BitmapTextureAtlas(this.getTextureManager(), 850, 950, TextureOptions.BILINEAR);

		this.mExplosionTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.mExplosionBitmapTextureAtlas, this, "explosion.png", 0, 0, 3, 4);
		this.mExplosionBitmapTextureAtlas.load();



		this.mBitmapTextureAtlas = new BitmapTextureAtlas(this.getTextureManager(), 1000, 700, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.mRedBallTextureRegion =   BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mBitmapTextureAtlas, this, "red_ball.png", 0, 0);
		this.mBlueBallTextureRegion =   BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mBitmapTextureAtlas, this, "blue_ball.png", 100, 0);
		this.mGreenBallTextureRegion=  BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mBitmapTextureAtlas, this, "green_ball.png", 200, 0);
		this.mSkullBallTextureRegion=  BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mBitmapTextureAtlas, this, "skull_ball.png", 300, 0);
		this.mScorebackground=BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mBitmapTextureAtlas, this, "ScoreBack.png", 407, 0);
		this.mGameOverFadeTextureRegion=BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mBitmapTextureAtlas, this, "gameover_fade.png", 607, 0);

		this.mBackground=BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mBitmapTextureAtlas, this, "back_Arcade.png", 0, 100);


		this.mBitmapTextureAtlas.load();

		this.mFont = FontFactory.create(this.getFontManager(), this.getTextureManager(), 256, 256, TextureOptions.BILINEAR, Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 48,true, Color.WHITE);
		this.mFont.load();

		this.mScoreTickerFont = FontFactory.create(this.getFontManager(), this.getTextureManager(), 256, 256, TextureOptions.BILINEAR, Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 50,true, Color.WHITE);
		this.mScoreTickerFont.load();

		this.mCountdownFont = FontFactory.create(this.getFontManager(), this.getTextureManager(), 256, 256, TextureOptions.BILINEAR, Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 100,true, Color.WHITE);
		this.mCountdownFont.load();

		this.mMenuTexture = new BitmapTextureAtlas(this.getTextureManager(), 300, 100, TextureOptions.BILINEAR);
		this.mMenuQuitTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mMenuTexture, this, "Quit.png", 0, 0);
		this.mMenuTexture.load();

		SoundFactory.setAssetBasePath("mfx/");
		try {
			this.mPop1Sound = SoundFactory.createSoundFromAsset(this.mEngine.getSoundManager(), this, "pop1.wav");
			this.mPop2Sound = SoundFactory.createSoundFromAsset(this.mEngine.getSoundManager(), this, "pop2.wav");
			this.mPop3Sound = SoundFactory.createSoundFromAsset(this.mEngine.getSoundManager(), this, "pop3.wav");
			this.mPop4Sound = SoundFactory.createSoundFromAsset(this.mEngine.getSoundManager(), this, "pop4.wav");
			this.mPop5Sound = SoundFactory.createSoundFromAsset(this.mEngine.getSoundManager(), this, "pop5.wav");
			this.mExplosionSound=SoundFactory.createSoundFromAsset(this.mEngine.getSoundManager(), this, "explosion.ogg");
		} catch (final IOException e) {
			Debug.e(e);
		}
	}

	@Override
	public Scene onCreateScene() {
		this.mEngine.registerUpdateHandler(new FPSLogger());

		this.createMenuScene();

		this.mPhysicsWorld = new PhysicsWorld(new Vec2(0, SensorManager.GRAVITY_EARTH), false);

		this.mScene = new Scene();
		this.mScene.setBackground(new Background(1, 1, 1));
		//create background
		Sprite Background = new Sprite(-2, -2, mBackground, this.getVertexBufferObjectManager());
		Background.setHeight(CAMERA_HEIGHT+4);
		Background.setWidth(CAMERA_WIDTH+4);
		this.mScene.attachChild(Background);

		this.mScene.setOnSceneTouchListener(this);

		final VertexBufferObjectManager vertexBufferObjectManager = this.getVertexBufferObjectManager();
		//final Rectangle roof = new Rectangle(0, 0, CAMERA_WIDTH, 2, vertexBufferObjectManager);
		final Rectangle left = new Rectangle(0, 0, 2, CAMERA_HEIGHT, vertexBufferObjectManager);
		final Rectangle right = new Rectangle(CAMERA_WIDTH - 2, 0, 2, CAMERA_HEIGHT, vertexBufferObjectManager);
		left.setAlpha(0);
		right.setAlpha(0);
		final FixtureDef wallFixtureDef = PhysicsFactory.createFixtureDef(0, 0.5f, 0.5f);

		//PhysicsFactory.createBoxBody(this.mPhysicsWorld, roof, BodyType.StaticBody, wallFixtureDef);
		PhysicsFactory.createBoxBody(this.mPhysicsWorld, left, BodyType.STATIC, wallFixtureDef);
		PhysicsFactory.createBoxBody(this.mPhysicsWorld, right, BodyType.STATIC, wallFixtureDef);

		//this.mScene.attachChild(roof);
		this.mScene.attachChild(left);
		this.mScene.attachChild(right);
		//update handlers
		this.mScene.registerUpdateHandler(this.mPhysicsWorld);
		this.mScene.setOnAreaTouchListener(this);
		createitemstimehandler();
		checkforlossandtimertimehandler();
		//set gravity
		setGravity();
		//////////text

		ScoreText = new Text(20, 20, this.mFont, "Score: - - - - -", "Score: XXXXX".length(), this.getVertexBufferObjectManager());
		//set score background
		Sprite Scorebackground = new Sprite(0, 0, mScorebackground, this.getVertexBufferObjectManager());
		Scorebackground.setHeight((float) (ScoreText.getHeight()+40));
		Scorebackground.setWidth((float) (ScoreText.getWidth()+40));
		Scorebackground.setY(CAMERA_HEIGHT-Scorebackground.getHeight());
		ScoreText.setY(Scorebackground.getY()+20);
		this.mScene.attachChild(Scorebackground);
		mScene.attachChild(ScoreText);
		ScoreText.setColor(1,0,0);

		//timertex
		TimerText= new Text(20,20, this.mFont, "Time: 120", "Time: 000".length(), this.getVertexBufferObjectManager());
		//set score background
		Sprite TimerTextBackground = new Sprite(0, 0, mScorebackground, this.getVertexBufferObjectManager());
		TimerTextBackground.setHeight((float) (TimerText.getHeight()+40));
		TimerTextBackground.setWidth((float) (TimerText.getWidth()+40));
		TimerTextBackground.setY(0);
		TimerText.setY(TimerTextBackground.getY()+20);
		this.mScene.attachChild(TimerTextBackground);
		mScene.attachChild(TimerText);
		TimerText.setColor(1,0,0);
		//get difficulty
		Bundle b = getIntent().getExtras();
		if(b!=null)
		{
			difficulty = b.getInt("Difficulty", 1);
		}
		/////////
		//gameoverstuff
		gameoverfadered = new Sprite(0, 0, mGameOverFadeTextureRegion, this.getVertexBufferObjectManager());
		gameoverfadered.setAlpha(0);
		//return
		return this.mScene;
	}

	public boolean onKeyDown(final int pKeyCode, final KeyEvent pEvent) {
		if(pKeyCode == KeyEvent.KEYCODE_MENU && pEvent.getAction() == KeyEvent.ACTION_DOWN) {
			if(this.mScene.hasChildScene()) {
				/* Remove the menu and reset it. */
				this.mMenuScene.back();
			} else {
				/* Attach the menu. */
				this.mScene.setChildScene(this.mMenuScene, false, true, true);
			}
			return true;
		}
		if(pKeyCode == KeyEvent.KEYCODE_BACK && pEvent.getAction() == KeyEvent.ACTION_DOWN) {
			Intent intent = new Intent(GameActivity.this, MainMenuActivity.class);
			startActivity(intent);
			finish();
			return true;
		}
		return super.onKeyDown(pKeyCode, pEvent);
	}

	public boolean onMenuItemClicked(final MenuScene pMenuScene, final IMenuItem pMenuItem, final float pMenuItemLocalX, final float pMenuItemLocalY) {
		switch(pMenuItem.getID()) {
			case MENU_QUIT:
				Intent intent = new Intent(GameActivity.this, MainMenuActivity.class);
				startActivity(intent);
				finish();
				return true;
			default:
				return false;
		}
	}

	protected void createMenuScene() {
		this.mMenuScene = new MenuScene(this.camera);

		final SpriteMenuItem quitMenuItem = new SpriteMenuItem(MENU_QUIT, this.mMenuQuitTextureRegion, this.getVertexBufferObjectManager());
		quitMenuItem.setBlendFunction(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
		this.mMenuScene.addMenuItem(quitMenuItem);

		this.mMenuScene.buildAnimations();

		this.mMenuScene.setBackgroundEnabled(false);

		this.mMenuScene.setOnMenuItemClickListener(this);
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

	public void gameoversequence(float x, float y, float facewidth, float faceheight, float scale)
	{

		final AnimatedSprite explosion = new AnimatedSprite(x, y, this.mExplosionTextureRegion, this.getVertexBufferObjectManager());
		explosion.setX(x+(facewidth/2-explosion.getWidth()/2));
		explosion.setY(y+(faceheight/2-explosion.getHeight()/2));
		explosion.setScale((float) (scale*0.6));
		explosion.animate(80,0);
		mScene.attachChild(explosion);
		mExplosionSound.play();
		gameoverfadered.setWidth(CAMERA_WIDTH);
		gameoverfadered.setHeight(CAMERA_HEIGHT);
		gameoverfadered.registerEntityModifier(
				new SequenceEntityModifier(
						new ParallelEntityModifier(
								new AlphaModifier(3, 0, 1)
						)
				)
		);
		gameoverfadered.setBlendFunction(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
		mScene.attachChild(gameoverfadered);
		this.camera.shake(3, 4);
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
	public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
		//SpriteParticleSystem particles=createParticles(pSceneTouchEvent.getX(),pSceneTouchEvent.getY());
		//mScene.attachChild(particles);
		return false;
	}

	private void setGravity()
	{
		this.mGravityX = 0;
		this.mGravityY = 8;
		final Vec2 gravity = Vec2Pool.obtain(this.mGravityX, this.mGravityY);
		this.mPhysicsWorld.setGravity(gravity);
		Vec2Pool.recycle(gravity);
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

	private void addFace(final float pX, final float pY) {
		final Sprite face;

		final Body body;
		final FixtureDef objectFixtureDef = PhysicsFactory.createFixtureDef(1, 0.5f, 0.5f);

		//add object
		face = new Sprite(pX, pY, whichface(), this.getVertexBufferObjectManager());
		face.setScale((float) (Math.random()*2+1));
		body = PhysicsFactory.createCircleBody(this.mPhysicsWorld, face, BodyType.DYNAMIC, objectFixtureDef);
		this.mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(face, body, true, true));
		face.setUserData(body);
		this.mScene.registerTouchArea(face);
		faces.addLast(face);
		this.mScene.attachChild(face);
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

	private ITextureRegion whichface()
	{
		ITextureRegion whichtexture = null;

		if(whichface==0)
		{
			whichtexture=mRedBallTextureRegion;
		}
		if(whichface==1)
		{
			whichtexture=mBlueBallTextureRegion;
		}
		if(whichface==2)
		{
			whichtexture=mGreenBallTextureRegion;
		}
		if(whichface==3)
		{
			whichtexture=mSkullBallTextureRegion;
		}
		whichface++;
		if(whichface>3)
		{
			whichface=0;
		}
		return whichtexture;
	}


	private void jumpFace(final Sprite face,float yspeed, float xspeed) {
		final Body faceBody = (Body)face.getUserData();
		final Vec2 velocity = Vec2Pool.obtain(this.mGravityX+xspeed, (float) (this.mGravityY *-1*yspeed));
		faceBody.setLinearVelocity(velocity);
		Vec2Pool.recycle(velocity);
	}

	private void spawnFaceInitialDirection(final Sprite face,float speed) {
		final Body faceBody = (Body)face.getUserData();
		final Vec2 velocity = Vec2Pool.obtain((float) (this.mGravityX*(Math.random()*2-1)), (float) (this.mGravityY *0.3*speed));
		faceBody.setLinearVelocity(velocity);
		Vec2Pool.recycle(velocity);
	}

	private void createitemstimehandler() {
		TimerHandler itemTimerHandler;
		float mEffectSpawnDelay = 5f;

		itemTimerHandler = new TimerHandler(mEffectSpawnDelay, true,
				new ITimerCallback() {

					@Override
					public void onTimePassed(TimerHandler pTimerHandler) {
						if(isgameover==false)
						{
							for (int x=0;x<difficulty;x++)
							{
								addFace((int)(Math.random()*CAMERA_WIDTH),-200*(x+1));
								spawnFaceInitialDirection ((Sprite)mScene.getLastChild(), 2f);
							}
						}
					}
				});

		getEngine().registerUpdateHandler(itemTimerHandler);
	}

	private void checkforlossandtimertimehandler() {
		TimerHandler itemTimerHandler;
		float mEffectSpawnDelay = 1f;

		itemTimerHandler = new TimerHandler(mEffectSpawnDelay, true,
				new ITimerCallback() {

					@Override
					public void onTimePassed(TimerHandler pTimerHandler) {
						if(gameoverfadered.getAlpha()==1)
						{
							gameover();
						}
						for(int x=0;x<faces.size();x++)
						{
							Sprite tempface=faces.get(x);
							if(tempface.getY()>CAMERA_HEIGHT)
							{
								mScene.attachChild(createScoreLossText(tempface.getX(),CAMERA_HEIGHT-50));

								decreaseScore(5);
								faces.remove(x);
							}
						}

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
	private void gameover()
	{
		Intent intent = new Intent(GameActivity.this, GameOverScreen.class);
		Bundle b = new Bundle();
		b.putInt("Score", score);
		intent.putExtras(b);
		startActivity(intent);
		finish();
	}
	private void removeFace(Sprite face) {

		final PhysicsConnector facePhysicsConnector = this.mPhysicsWorld.getPhysicsConnectorManager().findPhysicsConnectorByShape(face);

		this.mPhysicsWorld.unregisterPhysicsConnector(facePhysicsConnector);
		this.mPhysicsWorld.destroyBody(facePhysicsConnector.getBody());

		this.mScene.unregisterTouchArea(face);
		this.mScene.detachChild(face);
		System.gc();

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

	private Text createScoreLossText(float x, float y)
	{
		final Text scoreminus5 = new Text(x, y, this.mScoreTickerFont, "-5", this.getVertexBufferObjectManager());
		scoreminus5.registerEntityModifier(
				new SequenceEntityModifier(
						new ParallelEntityModifier(
								new ScaleModifier(1.2f, 0.1f, 1.5f),
								new AlphaModifier(1.5f, 1f, 0f)
						)
				)
		);
		scoreminus5.setBlendFunction(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
		scoreminus5.setColor(1, 0, 0);
		return scoreminus5;
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
	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}