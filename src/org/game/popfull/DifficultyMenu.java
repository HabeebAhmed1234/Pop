package org.game.popfull;

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

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.widget.FrameLayout;


public class DifficultyMenu extends SimpleBaseGameActivity implements IOnSceneTouchListener, IOnAreaTouchListener {
	// ===========================================================
	// Constants
	// ===========================================================

	private int CAMERA_WIDTH;
	private int CAMERA_HEIGHT;

	// ===========================================================
	// Fields
	// ===========================================================

	private BitmapTextureAtlas mBitmapTextureAtlas;
	private ITextureRegion mEasyRegion;
	private ITextureRegion mToughTextureRegion;
	private ITextureRegion mImpossibleTextureRegion;
	private ITextureRegion mBackTextureRegion;
	private ITextureRegion mShadowTextureRegion;
	private Camera camera;
	
	private Sprite Easy;
	private Sprite Tough;
	private Sprite Impossible;
	private Sprite Back;
	private Sprite shadow;
	
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
		engineOptions.getTouchOptions().setNeedsMultiTouch(true);
		engineOptions.getAudioOptions().setNeedsSound(true);
		
		return engineOptions;
	}

	@Override
	public void onCreateResources() {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");

		this.mBitmapTextureAtlas = new BitmapTextureAtlas(this.getTextureManager(),900, 900, TextureOptions.BILINEAR);
		this.mEasyRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mBitmapTextureAtlas, this, "Easy.png", 0, 0);
		this.mToughTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mBitmapTextureAtlas, this, "Tough.png", 286, 0);
		this.mImpossibleTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mBitmapTextureAtlas, this, "Impossible.png", 572, 0);
		this.mBackgroundTextureRegion =   BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mBitmapTextureAtlas, this, "MainMenueBack.png", 0, 80); 
		this.mBackTextureRegion =   BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mBitmapTextureAtlas, this, "back_arrow.png", 600, 80); 
		this.mShadowTextureRegion =   BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mBitmapTextureAtlas, this, "Shadow.png", 600, 187); 

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
		this.Easy = new Sprite((float) (CAMERA_WIDTH*0.1), CAMERA_HEIGHT/3, this.mEasyRegion, this.getVertexBufferObjectManager());
		this.Tough = new Sprite((float) (CAMERA_WIDTH*0.1), CAMERA_HEIGHT/2, this.mToughTextureRegion, this.getVertexBufferObjectManager());
		this.Impossible = new Sprite((float) (CAMERA_WIDTH*0.1), CAMERA_HEIGHT/3+CAMERA_HEIGHT/3, this.mImpossibleTextureRegion, this.getVertexBufferObjectManager());
		
		double newwidth=CAMERA_WIDTH*0.8;
		
		Back.setWidth(CAMERA_WIDTH/2);
		Easy.setWidth((float) newwidth);
		Tough.setWidth((float) newwidth);
		Impossible.setWidth((float) newwidth);
		
		addshadow();
		
		this.mScene.registerTouchArea(Back);
		this.mScene.registerTouchArea(Easy);
		this.mScene.registerTouchArea(Tough);
		this.mScene.registerTouchArea(Impossible);
		
		mScene.attachChild(background);
		mScene.attachChild(this.Back);
		mScene.attachChild(this.Easy);
		mScene.attachChild(this.Tough);
		mScene.attachChild(this.Impossible);
		
		return mScene;
	}
	
	public void addshadow()
	{
		this.shadow = new Sprite(0, 0, this.mShadowTextureRegion, this.getVertexBufferObjectManager());
		if(getDiff()==1)
		{
			shadow.setX(Easy.getX()-1);
			shadow.setY(Easy.getY()-1);
		}
		if(getDiff()==2)
		{
			shadow.setX(Tough.getX()-1);
			shadow.setY(Tough.getY()-1);
		}
		if(getDiff()==3)
		{
			shadow.setX(Impossible.getX()-1);
			shadow.setY(Impossible.getY()-1);
		}
		mScene.attachChild(this.shadow);
	}

	@Override
	public boolean onSceneTouchEvent(final Scene pScene, final TouchEvent pSceneTouchEvent) {
		
		return true;
	}

	@Override
	public boolean onAreaTouched(TouchEvent pSceneTouchEvent,ITouchArea pTouchArea, float pTouchAreaLocalX,float pTouchAreaLocalY) {
		if(pSceneTouchEvent.isActionDown()) {
			addshadow();
			if(pTouchArea==Back)
			{
				Intent intent = new Intent(DifficultyMenu.this, OptionsMenu.class);
				startActivity(intent);
				finish();
			}
			if(pTouchArea==Easy)
			{
				setDiff(1);
				returntooptionsmenu();
			}
			if(pTouchArea==Tough)
			{
				setDiff(2);
				returntooptionsmenu();
			}
			if(pTouchArea==Impossible)
			{
				setDiff(3);
				returntooptionsmenu();
			}
			return true;
		}
		return false;
	}
	
	public void setDiff(int d)
	{
		GlobalVariables appState = ((GlobalVariables)getApplicationContext());
	    appState.setDifficulty(d);
	}
	
	public int getDiff()
	{
		GlobalVariables appState = ((GlobalVariables)getApplicationContext());
		int difficulty=appState.getDifficulty();
		return difficulty;
	}
	
	public void returntooptionsmenu()
	{
		Intent intent = new Intent(DifficultyMenu.this, OptionsMenu.class);
		Bundle b = new Bundle();

		startActivity(intent);

		finish();
	}
	
	public boolean onKeyDown(final int pKeyCode, final KeyEvent pEvent) {
		if(pKeyCode == KeyEvent.KEYCODE_BACK && pEvent.getAction() == KeyEvent.ACTION_DOWN) {
			
			setDiff(1);
			returntooptionsmenu();
			
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