package com.wack.pop2.difficulty;

import static com.wack.pop2.GameConstants.MAX_SPAWN_INTERVAL;
import static com.wack.pop2.GameConstants.MIN_SPAWN_INTERVAL;
import static com.wack.pop2.GameConstants.TIME_TO_MAX_DIFFICULTY_SECONDS;

public class DifficultyConstants {

    public static final float SPAWN_INTERVAL_UPDATE_SECONDS = 2;
    public static final float SPAWN_INTERVAL_DECREASE_SPEED = (MAX_SPAWN_INTERVAL - MIN_SPAWN_INTERVAL) / (TIME_TO_MAX_DIFFICULTY_SECONDS / SPAWN_INTERVAL_UPDATE_SECONDS);
}
