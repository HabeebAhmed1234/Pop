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
import org.andengine.entity.util.FPSLogger;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.view.RenderSurfaceView;
import org.andengine.ui.activity.SimpleBaseGameActivity;

import com.google.ads.*;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.widget.FrameLayout;


public class OptionsMenu extends SimpleBaseGameActivity implements IOnSceneTouchListener, IOnAreaTouchListener {
	// ===========================================================
	// Constants
	// ===========================================================

	private int CAMERA_WIDTH;
	private int CAMERA_HEIGHT;

	// ===========================================================
	// Fields
	// ===========================================================

	private BitmapTextureAtlas mBitmapTextureAtlas;
	private ITextureRegion mDifficultyRegion;
	private ITextureRegion mModeTextureRegion;
	private ITextureRegion mHiscoresTextureRegion;
	private ITextureRegion mBackTextureRegion;
	private Camera camera;
	
	private Sprite Difficulty_Setting;
	private Sprite Mode;
	private Sprite Hiscores;
	private Sprite Back;
	
	private Scene mScene;
	private ITextureRegion mBackgroundTextureRegion;

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
		engineOptions.getAudioOptions().setNeedsSound(true);
		
		return engineOptions;
	}

	@Override
	public void onCreateResources() {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");

		this.mBitmapTextureAtlas = new BitmapTextureAtlas(this.getTextureManager(),900, 900, TextureOptions.BILINEAR);
		this.mDifficultyRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mBitmapTextureAtlas, this, "Difficulty.png", 0, 0);
		this.mModeTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mBitmapTextureAtlas, this, "Mode.png", 286, 0);//set
		this.mHiscoresTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mBitmapTextureAtlas, this, "Highscore.png", 572, 0);//set
		this.mBackgroundTextureRegion =   BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mBitmapTextureAtlas, this, "MainMenueBack.png", 0, 80); 
		this.mBackTextureRegion =   BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mBitmapTextureAtlas, this, "back_arrow.png", 600, 80); 

		this.mBitmapTextureAtlas.load();
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

		this.Back = new Sprite((float) (CAMERA_WIDTH*0.25), CAMERA_HEIGHT/3-CAMERA_HEIGHT/6, this.mBackTextureRegion, this.getVertexBufferObjectManager());
		this.Difficulty_Setting = new Sprite((float) (CAMERA_WIDTH*0.1), CAMERA_HEIGHT/3, this.mDifficultyRegion, this.getVertexBufferObjectManager());
		this.Mode = new Sprite((float) (CAMERA_WIDTH*0.1), CAMERA_HEIGHT/2, this.mModeTextureRegion, this.getVertexBufferObjectManager());
		this.Hiscores = new Sprite((float) (CAMERA_WIDTH*0.1), CAMERA_HEIGHT/3+CAMERA_HEIGHT/3, this.mHiscoresTextureRegion, this.getVertexBufferObjectManager());
		
		double newwidth=CAMERA_WIDTH*0.8;
		
		Back.setWidth(CAMERA_WIDTH/2);
		Difficulty_Setting.setWidth((float) newwidth);
		Mode.setWidth((float) newwidth);
		Hiscores.setWidth((float) newwidth);
		
		this.mScene.registerTouchArea(Back);
		this.mScene.registerTouchArea(Difficulty_Setting);
		this.mScene.registerTouchArea(Mode);
		this.mScene.registerTouchArea(Hiscores);
		
		mScene.attachChild(background);
		mScene.attachChild(this.Back);
		mScene.attachChild(this.Difficulty_Setting);
		mScene.attachChild(this.Mode);
		mScene.attachChild(this.Hiscores);
		
		return mScene;
	}

	@Override
	public boolean onSceneTouchEvent(final Scene pScene, final TouchEvent pSceneTouchEvent) {
		
		return true;
	}

	@Override
	public boolean onAreaTouched(TouchEvent pSceneTouchEvent,ITouchArea pTouchArea, float pTouchAreaLocalX,float pTouchAreaLocalY) {
		if(pSceneTouchEvent.isActionDown()) {
			if(pTouchArea==Back)
			{
				Intent intent = new Intent(OptionsMenu.this, MainMenuActivity.class);
				startActivity(intent);
				finish();
			}
			if(pTouchArea==Difficulty_Setting)
			{
				Intent intent = new Intent(OptionsMenu.this, DifficultyMenu.class);
				startActivity(intent);
				finish();
			}
			if(pTouchArea==Mode)
			{
				Intent intent = new Intent(OptionsMenu.this, ModeMenu.class);
				startActivity(intent);
				finish();
			}
			if(pTouchArea==Hiscores)
			{
				Intent intent = new Intent(OptionsMenu.this, HighscoresMenu.class);
				startActivity(intent);
				finish();
			}
			return true;
		}
		return false;
	}

	public void returntomainmenu()
	{
		Intent intent = new Intent(OptionsMenu.this, MainMenuActivity.class);
		Bundle b = new Bundle();
		intent.putExtras(b);
		startActivity(intent);
		finish();
	}
	
	public boolean onKeyDown(final int pKeyCode, final KeyEvent pEvent) {
		if(pKeyCode == KeyEvent.KEYCODE_BACK && pEvent.getAction() == KeyEvent.ACTION_DOWN) {
			returntomainmenu();
			return true;
		} else {
			return super.onKeyDown(pKeyCode, pEvent);
		}
	}
	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}