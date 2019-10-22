package com.wack.pop2.resources.sounds;

import android.content.Context;

import com.wack.pop2.BaseEntity;
import com.wack.pop2.GameResources;

import org.andengine.audio.sound.Sound;
import org.andengine.audio.sound.SoundFactory;
import org.andengine.audio.sound.SoundManager;
import org.andengine.util.debug.Debug;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class GameSoundsManager extends BaseEntity {

    private final Context context;
    private final SoundManager soundManager;
    private final Map<SoundId, Sound> mSounds = new HashMap();

    public GameSoundsManager(Context context, SoundManager soundManager, GameResources gameResources) {
        super(gameResources);
        this.context = context;
        this.soundManager = soundManager;
    }

    @Override
    public void onCreateResources() {

        SoundFactory.setAssetBasePath("mfx/");
        try {
            mSounds.put(
                    SoundId.POP_1,
                    SoundFactory.createSoundFromAsset(soundManager, context, "pop1.wav"));
            mSounds.put(
                    SoundId.POP_2,
                    SoundFactory.createSoundFromAsset(soundManager, context, "pop2.wav"));
            mSounds.put(
                    SoundId.POP_3,
                    SoundFactory.createSoundFromAsset(soundManager, context, "pop3.wav"));
            mSounds.put(
                    SoundId.POP_4,
                    SoundFactory.createSoundFromAsset(soundManager, context, "pop4.wav"));
            mSounds.put(
                    SoundId.POP_5,
                    SoundFactory.createSoundFromAsset(soundManager, context, "pop5.wav"));
            mSounds.put(
                    SoundId.EXPOSION,
                    SoundFactory.createSoundFromAsset(soundManager, context, "explosion.ogg"));
        } catch (final IOException e) {
            Debug.e(e);
        }
    }
}
