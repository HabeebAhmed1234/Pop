package com.stupidfungames.pop.resources.sounds;

import android.content.Context;

import com.stupidfungames.pop.BaseEntity;
import com.stupidfungames.pop.binder.Binder;
import com.stupidfungames.pop.binder.BinderEnity;

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
            loadSound(SoundId.POP_1,"pop1.mp3");
            loadSound(SoundId.POP_2,"pop2.mp3");
            loadSound(SoundId.POP_3,"pop3.mp3");
            loadSound(SoundId.POP_4,"pop4.mp3");
            loadSound(SoundId.POP_5,"pop5.mp3");
            loadSound(SoundId.EXPOSION,"explosion.mp3", 1.2f);
            loadSound(SoundId.LAZER_BURST,"lazer_burst.mp3");
            loadSound(SoundId.OPEN,"open.mp3");
            loadSound(SoundId.CLOSE,"close.mp3");
            loadSound(SoundId.PUFF,"puff.mp3");
            loadSound(SoundId.CLICK_UP,"click_up.mp3");
            loadSound(SoundId.CLICK_DOWN,"click_down.mp3");
            loadSound(SoundId.HAMMER_UP,"hammer_up.mp3", 0.25f);
            loadSound(SoundId.HAMMER_DOWN,"hammer_down.mp3", 0.25f);
            loadSound(SoundId.SCRAP,"scrap.mp3");
            loadSound(SoundId.NUKE_START,"nuke_start.mp3");
            loadSound(SoundId.BEEP,"beep.mp3", 0.7f);
            loadSound(SoundId.PAUSE,"pause.mp3");
            loadSound(SoundId.UNPAUSE,"unpause.mp3");
            loadSound(SoundId.UPGRADE, "upgrade.mp3", 0.5f);
            loadSound(SoundId.UPGRADE_POP, "upgrade_pop.mp3", 0.5f);
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
