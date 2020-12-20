package com.stupidfungames.pop;

import com.stupidfungames.pop.settingstray.GameQuickSettingsHostTrayBaseEntity.IconId;

public class GameConstants {

  // Limits
  public static final int MAX_BUBBLES_ON_SCREEN = 30;

  // Difficulty
  public static final float MAX_BUBBLES_PER_SPAWN = 3;
  public static final float TIME_TO_MAX_DIFFICULTY_SECONDS = 3; //TimeUnit.MINUTES.toSeconds(3);
  public static final float MAX_SPAWN_INTERVAL = 5;
  public static final float MIN_SPAWN_INTERVAL = 1f;

  // Unlocks
  /**
   * The point of difficulty where all icons should be unlocked.
   */
  private static final float ALL_ICON_UNLOCK_DIFFICULTY_PERCENTAGE = 0.5f;
  /**
   * Total number of icons.
   */
  private static final int TOTAL_NUM_ICONS = IconId.values().length;

  public static final float BALL_AND_CHAIN_DIFFICULTY_UNLOCK_THRESHOLD =
      1 * ALL_ICON_UNLOCK_DIFFICULTY_PERCENTAGE / (float) TOTAL_NUM_ICONS;
  public static final float TURRETS_DIFFICULTY_UNLOCK_THRESHOLD =
      2 * ALL_ICON_UNLOCK_DIFFICULTY_PERCENTAGE / (float) TOTAL_NUM_ICONS;
  public static final float WALLS_DIFFICULTY_UNLOCK_THRESHOLD =
      3 * ALL_ICON_UNLOCK_DIFFICULTY_PERCENTAGE / (float) TOTAL_NUM_ICONS;
  public static final float NUKE_UNLOCK_THRESHOLD =
      4 * ALL_ICON_UNLOCK_DIFFICULTY_PERCENTAGE / (float) TOTAL_NUM_ICONS;
  public static final float MULTI_POP_UNLOCK_THRESHOLD =
      5 * ALL_ICON_UNLOCK_DIFFICULTY_PERCENTAGE / (float) TOTAL_NUM_ICONS;
}
