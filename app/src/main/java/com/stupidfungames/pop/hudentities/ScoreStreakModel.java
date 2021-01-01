package com.stupidfungames.pop.hudentities;

import static com.stupidfungames.pop.GameConstants.STREAK_2X_POPPED_BUBBLES_THRESHOLD;
import static com.stupidfungames.pop.GameConstants.STREAK_4X_POPPED_BUBBLES_THRESHOLD;
import static com.stupidfungames.pop.GameConstants.STREAK_8X_POPPED_BUBBLES_THRESHOLD;

import com.stupidfungames.pop.bubblespawn.BubbleSpawnerEntity.BubbleType;
import org.andengine.util.color.AndengineColor;

public class ScoreStreakModel {

  private BubbleType currentStreakBubbleType = null;
  private int numBubblesPoppedOfStreak = 0;

  public int getScoreMultiplier() {
    if (numBubblesPoppedOfStreak >= STREAK_8X_POPPED_BUBBLES_THRESHOLD) {
      return 8;
    } else if (numBubblesPoppedOfStreak >= STREAK_4X_POPPED_BUBBLES_THRESHOLD) {
      return 4;
    } else if (numBubblesPoppedOfStreak >= STREAK_2X_POPPED_BUBBLES_THRESHOLD) {
      return 2;
    }
    return 1;
  }

  public String getScoreMultiplierString() {
    int mult = getScoreMultiplier();
    if (mult == 1) {
      return "";
    }
    switch (getScoreMultiplier()) {
      case 2:
        return "  X2!";
      case 4:
        return "  X4!!";
      case 8:
        return "  X8!!";
    }
    return "";
  }

  public AndengineColor getStreakColor() {
    return currentStreakBubbleType != null ? currentStreakBubbleType.color
        : AndengineColor.TRANSPARENT;
  }

  public boolean shouldStartNewStreak(BubbleType poppedBubbleType) {
    return currentStreakBubbleType == null || currentStreakBubbleType != poppedBubbleType;
  }

  public boolean shouldContinueStreak(BubbleType poppedBubbleType) {
    return currentStreakBubbleType == poppedBubbleType;
  }

  public void startNewStreak(BubbleType poppedBubbleType) {
    currentStreakBubbleType = poppedBubbleType;
    numBubblesPoppedOfStreak = 1;
  }

  public void continueStreak() {
    numBubblesPoppedOfStreak += 1;
  }

  public void cancelCurrentStreak() {
    currentStreakBubbleType = null;
    numBubblesPoppedOfStreak = 0;
  }
}
