package com.stupidfungames.pop;

import java.util.concurrent.TimeUnit;

public class GameConstants {

  // Limits
  public static final int MAX_BUBBLES_ON_SCREEN = 30;

  // Difficulty
  public static final float MAX_BUBBLES_PER_SPAWN = 3;
  public static final float TIME_TO_MAX_DIFFICULTY_SECONDS = TimeUnit.MINUTES.toSeconds(4);
  public static final float MAX_SPAWN_INTERVAL = 5;
  public static final float MIN_SPAWN_INTERVAL = 1f;

  // Unlocks
  public static final float BALL_AND_CHAIN_DIFFICULTY_UNLOCK_THRESHOLD = 0.01f;
  public static final float TURRETS_DIFFICULTY_UNLOCK_THRESHOLD = 0.01f;
  public static final float WALLS_DIFFICULTY_UNLOCK_THRESHOLD = 0.01f;
  public static final float NUKE_UNLOCK_THRESHOLD = 0.01f;
}
