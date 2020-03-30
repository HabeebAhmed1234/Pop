package com.wack.pop2.resources.sounds;

import android.content.Context;

import com.wack.pop2.BaseEntity;
import com.wack.pop2.binder.Binder;
import com.wack.pop2.binder.BinderEnity;

import org.andengine.audio.sound.Sound;
import org.andengine.audio.sound.SoundFactory;
import org.andengine.audio.sound.SoundManager;
import org.andengine.util.debug.Debug;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class GameSoundsManager extends BaseEntity {

    private final Map<SoundId, Sound> mSounds = new HashMap();

    public GameSoundsManager(BinderEnity parent) {
        super(parent);
    }

    @Override
    public void onCreateResources() {
        SoundFactory.setAssetBasePath("mfx/");
        try {
            loadSound(SoundId.POP_1,"pop1.wav");
            loadSound(SoundId.POP_2,"pop2.wav");
            loadSound(SoundId.POP_3,"pop3.wav");
            loadSound(SoundId.POP_4,"pop4.wav");
            loadSound(SoundId.POP_5,"pop5.wav");
            loadSound(SoundId.EXPOSION,"explosion.ogg");
            loadSound(SoundId.NEON_BUZZ,"neon_buzz.wav");
            loadSound(SoundId.LAZER_BURST,"lazer_burst.mp3");
            loadSound(SoundId.OPEN,"open.wav");
            loadSound(SoundId.CLOSE,"close.wav");
            loadSound(SoundId.PUFF,"puff.wav");
            loadSound(SoundId.CLICK_UP,"click_up.wav");
            loadSound(SoundId.CLICK_DOWN,"click_down.wav");
            loadSound(SoundId.HAMMER_UP,"hammer_up.wav", 0.5f);
            loadSound(SoundId.HAMMER_DOWN,"hammer_down.wav", 0.5f);
            loadSound(SoundId.SCRAP,"scrap.wav");
            loadSound(SoundId.NUKE_START,"nuke_start.wav");
            loadSound(SoundId.BEEP,"beep.wav");
            loadSound(SoundId.PAUSE,"pause.wav");
            loadSound(SoundId.UNPAUSE,"unpause.wav");
        } catch (final IOException e) {
            Debug.e(e);
        }
    }

    private void loadSound(SoundId soundId, String path) throws IOException {
        loadSound(soundId, path, 1.0f);
    }

    private void loadSound(SoundId soundId, String path, float volume) throws IOException {
        Sound sound = SoundFactory.createSoundFromAsset(get(SoundManager.class), get(Context.class), path);
        sound.setVolume(volume);
        mSounds.put(soundId, sound);
    }

    public Sound getSound(SoundId soundId) {
        return mSounds.get(soundId);
    }

    @Override
    protected void createBindings(Binder binder) {

    }
}
