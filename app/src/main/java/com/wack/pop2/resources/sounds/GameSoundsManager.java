package com.wack.pop2.resources.sounds;

import com.wack.pop2.BaseEntity;
import com.wack.pop2.GameResources;

import org.andengine.audio.sound.SoundFactory;
import org.andengine.util.debug.Debug;

import java.io.IOException;

public class GameSoundsManager extends BaseEntity {

    public GameSoundsManager(GameResources gameResources) {
        super(gameResources);
    }

    @Override
    public void onCreateResources() {

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
}
