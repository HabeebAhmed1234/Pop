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
import android.widget.FrameLayout;


public class MainMenu extends SimpleBaseGameActivity implements IOnSceneTouchListener, IOnAreaTouchListener {
	// ===========================================================
	// Constants
	// ===========================================================
	
	private int CAMERA_WIDTH;
	private int CAMERA_HEIGHT;

	// ===========================================================
	// Fields
	// ===========================================================

	private BitmapTextureAtlas mBitmapTextureAtlas;
	private ITextureRegion mNewGameTextureRegion;
	private ITextureRegion mOptionsTextureRegion;
	private ITextureRegion mQuitTextureRegion;
	private ITextureRegion mTitleTextureRegion;
	private Camera camera;
	
	private Sprite newGame;
	private Sprite Options;
	private Sprite Quit;
	private Sprite Title;

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

		this.mBitmapTextureAtlas = new BitmapTextureAtlas(this.getTextureManager(),1500, 1500, TextureOptions.BILINEAR);
		this.mNewGameTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mBitmapTextureAtlas, this, "New_Game.png", 0, 0);
		this.mOptionsTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mBitmapTextureAtlas, this, "Options.png", 286, 0);
		this.mQuitTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mBitmapTextureAtlas, this, "Quit.png", 572, 0);
		this.mBackgroundTextureRegion =   BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mBitmapTextureAtlas, this, "MainMenueBack.png", 0, 80); 
		this.mTitleTextureRegion= BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mBitmapTextureAtlas, this, "Title.png", 0, 680); 
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
		
		this.newGame = new Sprite((float) (CAMERA_WIDTH*0.1), CAMERA_HEIGHT/3, this.mNewGameTextureRegion, this.getVertexBufferObjectManager());
		this.Options = new Sprite((float) (CAMERA_WIDTH*0.1), CAMERA_HEIGHT/2, this.mOptionsTextureRegion, this.getVertexBufferObjectManager());
		this.Quit = new Sprite((float) (CAMERA_WIDTH*0.1), CAMERA_HEIGHT/3+CAMERA_HEIGHT/3, this.mQuitTextureRegion, this.getVertexBufferObjectManager());
		this.Title = new Sprite((float)(CAMERA_WIDTH*0.2/2), (float)(newGame.getY()*0.2/2), this.mTitleTextureRegion, this.getVertexBufferObjectManager());

		double newwidth=CAMERA_WIDTH*0.8;
		
		newGame.setWidth((float) newwidth);
		Options.setWidth((float) newwidth);
		Quit.setWidth((float) newwidth);
		Title.setWidth((float)(CAMERA_WIDTH*0.80));
		Title.setHeight((float)(newGame.getY()*0.8));
		
		this.mScene.registerTouchArea(newGame);
		this.mScene.registerTouchArea(Options);
		this.mScene.registerTouchArea(Quit);
		
		mScene.attachChild(background);
		mScene.attachChild(this.Title);
		mScene.attachChild(this.newGame);
		mScene.attachChild(this.Options);
		mScene.attachChild(this.Quit);
		
		return mScene;
	}

	@Override
	public boolean onSceneTouchEvent(final Scene pScene, final TouchEvent pSceneTouchEvent) {
		
		return true;
	}

	@Override
	public boolean onAreaTouched(TouchEvent pSceneTouchEvent,ITouchArea pTouchArea, float pTouchAreaLocalX,float pTouchAreaLocalY) {
		if(pSceneTouchEvent.isActionDown()) {
			if(pTouchArea==newGame)
			{
				GlobalVariables appState = ((GlobalVariables)getApplicationContext());
				
				int setDifficulty=appState.getDifficulty();
				String setMode=appState.getMode();
				
				newGame.setY(newGame.getY()-20);
				Intent intent = new Intent(MainMenu.this, GameHabeebActivity_Arcade.class);
				Bundle b = new Bundle();
				b.putInt("Difficulty", setDifficulty);
				intent.putExtras(b);
				startActivity(intent);
				this.finish();
				
			}
			if(pTouchArea==Options)
			{
				Options.setY(Options.getY()-20);
				Intent myIntent = new Intent(MainMenu.this, OptionsMenu.class);
				MainMenu.this.startActivity(myIntent);
				
			}
			if(pTouchArea==Quit)
			{
				Quit.setY(Quit.getY()-20);
				this.finish();
			}
			return true;
		}
		return false;
	}
	
	

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}