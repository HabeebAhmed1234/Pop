package com.stupidfungames.pop.difficulty;

import static com.stupidfungames.pop.GameConstants.MAX_SPAWN_INTERVAL;
import static com.stupidfungames.pop.GameConstants.MIN_SPAWN_INTERVAL;
import static com.stupidfungames.pop.GameConstants.TIME_TO_MAX_DIFFICULTY_SECONDS;

public class DifficultyConstants {

    public static final float SPAWN_INTERVAL_UPDATE_SECONDS = 2;
    public static final float SPAWN_INTERVAL_DECREASE_SPEED = (MAX_SPAWN_INTERVAL - MIN_SPAWN_INTERVAL) / (TIME_TO_MAX_DIFFICULTY_SECONDS / SPAWN_INTERVAL_UPDATE_SECONDS);
}
