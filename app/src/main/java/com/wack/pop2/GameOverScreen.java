package com.wack.pop2;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.scene.IOnAreaTouchListener;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.ITouchArea;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.entity.util.FPSLogger;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.view.RenderSurfaceView;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.util.HorizontalAlign;

import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.widget.FrameLayout;


public class GameOverScreen extends SimpleBaseGameActivity implements IOnSceneTouchListener, IOnAreaTouchListener {
	// ===========================================================
	// Constants
	// ===========================================================

	private int CAMERA_WIDTH;
	private int CAMERA_HEIGHT;

	// ===========================================================
	// Fields
	// ===========================================================

	private BitmapTextureAtlas mBitmapTextureAtlas;
	private ITextureRegion mGameOverTextureRegion;
	private Camera camera;
	
	private Sprite GameOver;

	private Font mFont;

	private Scene mScene;
	private int score;
	private ITextureRegion mBackgroundTextureRegion;
	private ITextureRegion mScorebackground;
	private String publisherID="a14fc7f884d1063";

	// ===========================================================
	// Constructors
	// ===========================================================

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================
	protected void onSetContentView() {
       
        
        final FrameLayout frameLayout = new FrameLayout(this);
        final FrameLayout.LayoutParams frameLayoutLayoutParams =
                new FrameLayout.LayoutParams(FrameLayout.LayoutParams.FILL_PARENT,
                                             FrameLayout.LayoutParams.FILL_PARENT);
 
        final AdView adView = new AdView(this, AdSize.BANNER, publisherID);
        
        adView.refreshDrawableState();
        adView.setVisibility(AdView.VISIBLE);
        final FrameLayout.LayoutParams adViewLayoutParams =
                new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,
                                             FrameLayout.LayoutParams.WRAP_CONTENT,
                                             Gravity.CENTER_HORIZONTAL|Gravity.TOP);
       
 
        AdRequest adRequest = new AdRequest();

        adView.loadAd(adRequest);
 
        this.mRenderSurfaceView = new RenderSurfaceView(this);
        mRenderSurfaceView.setRenderer(this.mEngine,this);
 
        final android.widget.FrameLayout.LayoutParams surfaceViewLayoutParams =
                new FrameLayout.LayoutParams(super.createSurfaceViewLayoutParams());
 
        frameLayout.addView(this.mRenderSurfaceView, surfaceViewLayoutParams);
        frameLayout.addView(adView, adViewLayoutParams);
 
        this.setContentView(frameLayout, frameLayoutLayoutParams);
    }

	@Override
	public EngineOptions onCreateEngineOptions() {
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
		camera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
		
		final EngineOptions engineOptions = new EngineOptions(true, ScreenOrientation.PORTRAIT_FIXED, new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), this.camera);
		engineOptions.getTouchOptions().setNeedsMultiTouch(true);
		engineOptions.getAudioOptions().setNeedsSound(true);
		
		return engineOptions; 
	}

	@Override
	public void onCreateResources() {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");

		this.mBitmapTextureAtlas = new BitmapTextureAtlas(this.getTextureManager(),800, 950, TextureOptions.BILINEAR);
		this.mGameOverTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mBitmapTextureAtlas, this, "game_over.png", 0, 0);
		this.mBackgroundTextureRegion =   BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mBitmapTextureAtlas, this, "MainMenueBack.png", 193, 0); 
		this.mScorebackground=BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mBitmapTextureAtlas, this, "ScoreBack.png", 0, 600);

		this.mBitmapTextureAtlas.load();
		
		this.mFont = FontFactory.create(this.getFontManager(), this.getTextureManager(), 256, 256, Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 32, true, Color.WHITE);
		this.mFont.load();
	}

	@Override
	public Scene onCreateScene() {
		this.mEngine.registerUpdateHandler(new FPSLogger());

		mScene = new Scene();
		this.mScene.setOnSceneTouchListener(this);
		this.mScene.setBackground(new Background(1f, 1f, 1f));
		this.mScene.setOnAreaTouchListener(this);
		
		Sprite background = new Sprite(0, 0, this.mBackgroundTextureRegion, this.getVertexBufferObjectManager());
		background.setWidth(CAMERA_WIDTH);
		background.setHeight(CAMERA_HEIGHT); 
		
		this.GameOver = new Sprite(10, CAMERA_HEIGHT/3, this.mGameOverTextureRegion, this.getVertexBufferObjectManager());
		double GOwidthoverheightratio=GameOver.getWidth()/GameOver.getHeight();
		GameOver.setWidth(CAMERA_WIDTH-20);
		GameOver.setHeight((float) (GameOver.getWidth()/GOwidthoverheightratio));
		mScene.attachChild(background);
		
		mScene.attachChild(this.GameOver);
		Bundle b = getIntent().getExtras();
		if(b!=null)
		{
			score = b.getInt("Score", 1);
		}
		final Text ScoreText = new Text(GameOver.getX(), GameOver.getY()+GameOver.getHeight()+10, this.mFont, "Your Score: "+score, new TextOptions(HorizontalAlign.CENTER), this.getVertexBufferObjectManager());
		ScoreText.setX(CAMERA_WIDTH/2-ScoreText.getWidth()/2);
		final Text Instructiontext = new Text(ScoreText.getX(), ScoreText.getY()+ScoreText.getHeight()+10, this.mFont, "Tap This To Continue", new TextOptions(HorizontalAlign.CENTER), this.getVertexBufferObjectManager());
		Instructiontext.setX(CAMERA_WIDTH/2-Instructiontext.getWidth()/2);
		mScene.registerTouchArea(Instructiontext);
		mScene.attachChild(ScoreText);
		mScene.attachChild(Instructiontext);

		return mScene;
	}

	@Override
	public boolean onSceneTouchEvent(final Scene pScene, final TouchEvent pSceneTouchEvent) {
		
		return true;
	}

	@Override
	public boolean onAreaTouched(TouchEvent pSceneTouchEvent,ITouchArea pTouchArea, float pTouchAreaLocalX,float pTouchAreaLocalY) {
		Intent intent = new Intent(GameOverScreen.this, MainMenu.class);
		startActivity(intent);
		finish();
		return true;
	}

}