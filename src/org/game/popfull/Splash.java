package org.game.popfull;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;

import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.util.FPSLogger;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.ui.activity.SimpleBaseGameActivity;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;


import com.google.ads.*;

public class Splash extends SimpleBaseGameActivity   {
	// ===========================================================
		// Constants
		// ===========================================================
		
		private int CAMERA_WIDTH;
		private int CAMERA_HEIGHT;

		// ===========================================================
		// Fields
		// ===========================================================

		private BitmapTextureAtlas mBitmapTextureAtlas;
		private ITextureRegion mlogoTextureRegion;
	
		private Camera camera;
		
		private int secondspassed=0;
		
		private Scene mScene;
		
		
	 
		
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
			
			return engineOptions;
		}
		
		@Override
		public void onCreateResources() {
			BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");

			this.mBitmapTextureAtlas = new BitmapTextureAtlas(this.getTextureManager(),501, 801, TextureOptions.BILINEAR);
			this.mlogoTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mBitmapTextureAtlas, this, "Logo.png", 0, 0);
			
			this.mBitmapTextureAtlas.load();
		}

		@Override
		public Scene onCreateScene() {
			this.mEngine.registerUpdateHandler(new FPSLogger());

			mScene = new Scene();
			
			Sprite logo = new Sprite(0, 0, this.mlogoTextureRegion, this.getVertexBufferObjectManager());
			logo.setWidth(CAMERA_WIDTH);
			logo.setHeight(CAMERA_HEIGHT);
			
			mScene.attachChild(logo);
			
			tranistiontimehandler();
			
			return mScene;
		}
		
		private void tranistiontimehandler() {
		    TimerHandler itemTimerHandler;
		    float mEffectSpawnDelay = 1f;
		
		    itemTimerHandler = new TimerHandler(mEffectSpawnDelay, true,
		    new ITimerCallback() {
		
		        @Override
		        public void onTimePassed(TimerHandler pTimerHandler) {
		        	secondspassed+=1;
		        	if(secondspassed>3)
		        	{
		        		Intent intent = new Intent(Splash.this, MainMenu.class);
						startActivity(intent);
		        	}
		        	
		        }
		    });
		    
		    getEngine().registerUpdateHandler(itemTimerHandler);
		}
	
		

		// ===========================================================
		// Methods
		// ===========================================================

		// ===========================================================
		// Inner and Anonymous Classes
		// ===========================================================

}
