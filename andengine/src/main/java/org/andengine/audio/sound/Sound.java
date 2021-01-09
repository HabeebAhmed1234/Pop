package org.andengine.audio.sound;

import android.media.SoundPool;
import java.util.HashSet;
import java.util.Set;
import org.andengine.audio.BaseAudioEntity;
import org.andengine.audio.exception.AudioException;
import org.andengine.audio.sound.exception.SoundReleasedException;

/**
 * (c) 2010 Nicolas Gramlich (c) 2011 Zynga Inc.
 *
 * @author Nicolas Gramlich
 * @since 13:22:15 - 11.03.2010
 */
public class Sound extends BaseAudioEntity {
  // ===========================================================
  // Constants
  // ===========================================================

  // ===========================================================
  // Fields
  // ===========================================================

  private final int priority;

  private int mSoundID;
  private Set<Integer> mStreamIDs = new HashSet<>();

  private boolean mLoaded;

  private int mLoopCount;
  private float mRate = 1.0f;

  // ===========================================================
  // Constructors
  // ===========================================================

  Sound(final SoundManager pSoundManager, final int pSoundID) {
    super(pSoundManager);

    this.mSoundID = pSoundID;
    this.priority = 0;
  }

  Sound(final SoundManager pSoundManager, final int pSoundID, int priority) {
    super(pSoundManager);

    this.mSoundID = pSoundID;
    this.priority = priority;
  }

  // ===========================================================
  // Getter & Setter
  // ===========================================================

  public int getSoundID() {
    return this.mSoundID;
  }

  public Set<Integer> getStreamIDs() {
    return this.mStreamIDs;
  }

  public boolean isLoaded() {
    return this.mLoaded;
  }

  public void setLoaded(final boolean pLoaded) {
    this.mLoaded = pLoaded;
  }

  public void setLoopCount(final int pLoopCount) throws SoundReleasedException {
    this.assertNotReleased();

    this.mLoopCount = pLoopCount;
    for (int streamID : mStreamIDs) {
      this.getSoundPool().setLoop(streamID, pLoopCount);
    }
  }

  public float getRate() {
    return this.mRate;
  }

  public void setRate(final float pRate) throws SoundReleasedException {
    this.assertNotReleased();

    this.mRate = pRate;
    for (int streamID : mStreamIDs) {
      this.getSoundPool().setRate(streamID, pRate);
    }
  }

  private SoundPool getSoundPool() throws SoundReleasedException {
    return this.getAudioManager().getSoundPool();
  }

  // ===========================================================
  // Methods for/from SuperClass/Interfaces
  // ===========================================================

  @Override
  protected SoundManager getAudioManager() throws SoundReleasedException {
    return (SoundManager) super.getAudioManager();
  }

  @Override
  protected void throwOnReleased() throws SoundReleasedException {
    throw new SoundReleasedException();
  }

  @Override
  public int play() throws SoundReleasedException {
    super.play();

    final float masterVolume = this.getMasterVolume();
    final float leftVolume = this.mLeftVolume * masterVolume;
    final float rightVolume = this.mRightVolume * masterVolume;

    int streamId = this.getSoundPool()
        .play(this.mSoundID, leftVolume, rightVolume, priority, this.mLoopCount, this.mRate);
    mStreamIDs.add(streamId);
    return streamId;
  }

  @Override
  public void stop() throws SoundReleasedException {
    super.stop();

    for (int streamID : mStreamIDs) {
      this.getSoundPool().stop(streamID);
    }
  }

  @Override
  public void stop(int streamId) throws AudioException {
    super.stop(streamId);

    if (mStreamIDs.contains(streamId)) {
      this.getSoundPool().stop(streamId);
    }
  }

  @Override
  public void resume() throws SoundReleasedException {
    super.resume();

    for (int streamID : mStreamIDs) {
      this.getSoundPool().resume(streamID);
    }
  }

  @Override
  public void pause() throws SoundReleasedException {
    super.pause();

    for (int streamID : mStreamIDs) {
      this.getSoundPool().pause(streamID);
    }
  }

  @Override
  public void release() throws SoundReleasedException {
    this.assertNotReleased();

    this.getSoundPool().unload(this.mSoundID);
    this.mSoundID = 0;
    this.mLoaded = false;

    this.getAudioManager().remove(this);

    super.release();
  }

  @Override
  public void setLooping(final boolean pLooping) throws SoundReleasedException {
    super.setLooping(pLooping);

    this.setLoopCount((pLooping) ? -1 : 0);
  }

  @Override
  public void setVolume(final float pLeftVolume, final float pRightVolume)
      throws SoundReleasedException {
    super.setVolume(pLeftVolume, pRightVolume);

    for (int streamID : mStreamIDs) {
      final float masterVolume = this.getMasterVolume();
      final float leftVolume = this.mLeftVolume * masterVolume;
      final float rightVolume = this.mRightVolume * masterVolume;

      this.getSoundPool().setVolume(streamID, leftVolume, rightVolume);
    }
  }

  @Override
  public void onMasterVolumeChanged(final float pMasterVolume) throws SoundReleasedException {
    this.setVolume(this.mLeftVolume, this.mRightVolume);
  }

  // ===========================================================
  // Methods
  // ===========================================================

  // ===========================================================
  // Inner and Anonymous Classes
  // ===========================================================
}
