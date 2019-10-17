package com.wack.pop2;

  import org.andengine.engine.camera.Camera;
    public class ShakeCamera extends Camera {
     
            boolean         mShaking;
            float           mDuration;
            float           mIntensity;
            float           mCurrentDuration;
           
            float           mX;
            float           mY;
           
           
           
            public ShakeCamera(float pX, float pY, float pWidth, float pHeight) {
                    super(pX, pY, pWidth, pHeight);
                    mShaking = false;
                   
                    mX = this.getCenterX();
                    mY = this.getCenterY();
            }
           
            public void shake(float duration, float intensity){
                    mShaking = true;
                    mDuration = duration;
                    mIntensity = intensity;
                    mCurrentDuration = 0;
            }
     
            @Override
            public void onUpdate(float pSecondsElapsed) {
                    super.onUpdate(pSecondsElapsed);
                   
                    if(mShaking){
                            mCurrentDuration+=pSecondsElapsed;
                            if(mCurrentDuration>mDuration)
                            {
                                    mShaking = false;
                                    mCurrentDuration = 0;
                                    this.setCenter( mX, mY);
                            }
                            else{
                                    int sentitX =   1;
                                    int sentitY =   1;
                                    if(Math.random() < 0.5) sentitX = -1;
                                    if(Math.random() < 0.5) sentitY = -1;
                                    this.setCenter( (float)(mX + Math.random()*mIntensity*sentitX),
                                                                    (float)(mY + Math.random()*mIntensity*sentitY));
                            }
                    }
            }
    }