package com.stupidfungames.pop.difficulty;

import static com.stupidfungames.pop.GameConstants.MAX_SPAWN_INTERVAL_SECONDS;
import static com.stupidfungames.pop.GameConstants.MIN_SPAWN_INTERVAL_SECONDS;
import static com.stupidfungames.pop.GameConstants.TIME_TO_MAX_DIFFICULTY_SECONDS;

public class DifficultyConstants {

    public static final float SPAWN_INTERVAL_UPDATE_SECONDS = 1;
    public static final float SPAWN_INTERVAL_DECREASE_SPEED_SECONDS = (MAX_SPAWN_INTERVAL_SECONDS - MIN_SPAWN_INTERVAL_SECONDS) / (TIME_TO_MAX_DIFFICULTY_SECONDS / SPAWN_INTERVAL_UPDATE_SECONDS);
}
