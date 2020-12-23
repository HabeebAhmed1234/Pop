package com.stupidfungames.pop;

import com.stupidfungames.pop.gameiconstray.GameIconsHostTrayEntity.IconId;
import java.util.concurrent.TimeUnit;

public class GameConstants {

  // Unlock all icons by the time we are halfway through to our max difficulty
  private static final float ALL_ICON_UNLOCK_DIFFICULTY_PERCENTAGE = 0.5f;

  public static final float BALL_AND_CHAIN_DIFFICULTY_UNLOCK_THRESHOLD =
      1 * (ALL_ICON_UNLOCK_DIFFICULTY_PERCENTAGE / (float) IconId.values().length);
  public static final float TURRETS_DIFFICULTY_UNLOCK_THRESHOLD =
      2 * (ALL_ICON_UNLOCK_DIFFICULTY_PERCENTAGE / (float) IconId.values().length);
  public static final float WALLS_DIFFICULTY_UNLOCK_THRESHOLD =
      3 * (ALL_ICON_UNLOCK_DIFFICULTY_PERCENTAGE / (float) IconId.values().length);
  public static final float NUKE_UNLOCK_THRESHOLD =
      4 * (ALL_ICON_UNLOCK_DIFFICULTY_PERCENTAGE / (float) IconId.values().length);
  public static final float MULTI_POP_UNLOCK_THRESHOLD =
      5 * (ALL_ICON_UNLOCK_DIFFICULTY_PERCENTAGE / (float) IconId.values().length);

  // Limits
  public static final int MAX_BUBBLES_ON_SCREEN = 60;

  // Difficulty
  public static final float MAX_BUBBLES_PER_SPAWN = 3;
  public static final float TIME_TO_MAX_DIFFICULTY_SECONDS = TimeUnit.MINUTES.toSeconds(8);
  public static final float MAX_SPAWN_INTERVAL_SECONDS = 4;
  public static final float MIN_SPAWN_INTERVAL_SECONDS = 0.5f;
}
