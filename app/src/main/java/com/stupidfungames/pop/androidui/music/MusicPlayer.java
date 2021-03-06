package com.stupidfungames.pop.androidui.music;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import androidx.core.util.Preconditions;
import com.stupidfungames.pop.R;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Can be used from a non Game activity to play background music
 */
public class MusicPlayer implements OnCompletionListener, OnPreparedListener {

  private static final int MAX_VOLUME = 100;
  private static final int IN_GAME_VOLUME = 80;

  private final Random rand = new Random();

  private Context context;
  private MediaPlayer mediaPlayer;
  private boolean isPaused = false;

  private static final Integer[] musicResId = new Integer[]{
      R.raw.track_0,
      R.raw.track_1,
      R.raw.track_2,
      R.raw.track_3,
      R.raw.track_4,
      R.raw.track_5,
      R.raw.track_6,
      R.raw.track_7,
      R.raw.track_8,
      R.raw.track_9};

  // the list of tracks remaining to be played in our pseudorandom play order
  private static List<Integer> playableMusicList = new ArrayList<>(Arrays.asList(musicResId));
  // the list of tracks already played
  private static List<Integer> playedMusicList = new ArrayList<>();
  private static MusicPlayer sMusicPlayer;

  public static void init(Context context) {
    sMusicPlayer = new MusicPlayer(context);
  }

  public static MusicPlayer get() {
    return sMusicPlayer;
  }

  private MusicPlayer(Context context) {
    this.context = context;
  }

  @Override
  public void onCompletion(MediaPlayer mp) {
    playNextTrack();
  }

  @Override
  public void onPrepared(MediaPlayer mp) {
    if (!isPaused) {
      mediaPlayer.start();
    }
  }

  public void resumePlaying() {
    isPaused = false;
    if (mediaPlayer == null) {
      initializeMediaPlayer();
    } else {
      try {
        mediaPlayer.start();
      } catch (IllegalStateException e) {
        playNextTrack();
      }
    }
  }

  public void pausePlaying() {
    isPaused = true;
    if (mediaPlayer != null) {
      mediaPlayer.pause();
    }
  }

  public void onAppForegrounded() {
    isPaused = false;
    playNextTrack();
  }

  public void onGameActivityResumed(boolean isPaused) {
    boolean isPlaying = mediaPlayer != null && mediaPlayer.isPlaying();
    if (isPaused) {
      pausePlaying();
    } else if (!isPlaying || mediaPlayer == null) {
      playNextTrack();
    }
    setVolume(IN_GAME_VOLUME);
  }

  /**
   * Called when we leave the game activity to go to the main menu or to the game over flow.
   */
  public void onLeaveGameActivity() {
    resumePlaying();
    playNextTrack();
    setVolume(MAX_VOLUME);
  }

  public void stop() {
    if (mediaPlayer != null) {
      mediaPlayer.stop();
      mediaPlayer.setOnPreparedListener(null);
      mediaPlayer.setOnCompletionListener(null);
      mediaPlayer.release();
    }
    mediaPlayer = null;
    isPaused = false;
  }

  public void playNextTrack() {
    if (isPaused) {
      return;
    }
    if (mediaPlayer == null) {
      initializeMediaPlayer();
    } else {
      AssetFileDescriptor assetFileDescriptor = context.getResources()
          .openRawResourceFd(getNextTrackToPlayResId());
      if (assetFileDescriptor == null) {
        return;
      }
      try {
        mediaPlayer.reset();
        mediaPlayer.setDataSource(assetFileDescriptor.getFileDescriptor(),
            assetFileDescriptor.getStartOffset(), assetFileDescriptor.getLength());
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.prepareAsync();
        assetFileDescriptor.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  private void initializeMediaPlayer() {
    if (playableMusicList.isEmpty()) {
      swapLists();
    }
    mediaPlayer = MediaPlayer.create(context, playableMusicList.get(0));
    mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
    mediaPlayer.setOnPreparedListener(this);
    mediaPlayer.setOnCompletionListener(this);
    mediaPlayer.start();

  }

  private int getNextTrackToPlayResId() {
    if (playableMusicList.isEmpty()) {
      swapLists();
    }
    int nextTrackIndex = rand.nextInt(playableMusicList.size());
    int nextTrackResId = playableMusicList.get(nextTrackIndex);
    playedMusicList.add(playableMusicList.remove(nextTrackIndex));
    return nextTrackResId;
  }

  /**
   * Swaps the playableMusicList and playedMusicList since we are done playing all the tracks.
   */
  private void swapLists() {
    List<Integer> temp = playableMusicList;
    playableMusicList = playedMusicList;
    playedMusicList = temp;
  }

  private void setVolume(int volume) {
    if (mediaPlayer == null) {
      return;
    }
    Preconditions.checkArgument(volume >= 0 && volume <= MAX_VOLUME);
    float log1 = 1.0f - (float) (Math.log(MAX_VOLUME - volume) / Math.log(MAX_VOLUME));
    if (log1 < 0) {
      log1 = 0;
    }
    if (log1 > 1) {
      log1 = 1;
    }
    mediaPlayer.setVolume(log1, log1);
  }
}
