package com.stupidfungames.pop.savegame;

import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.stupidfungames.pop.gameiconstray.GameIconsHostTrayEntity.IconId;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class SaveGame implements Serializable {

    public static SaveGame EMPTY = new SaveGame();

    public static class WallCoord implements Serializable{
        public float x1;
        public float y1;
        public float x2;
        public float y2;

        public WallCoord(float x1, float y1, float x2, float y2) {
            this.x1 = x1;
            this.y1 = y1;
            this.x2 = x2;
            this.y2 = y2;
        }
    }

    public int saveGameVersionCode;
    public int score;
    public float spawnInterval;
    public List<Pair<Float, Float>> turretPostitions;
    public List<WallCoord> wallCoords;

    public Map<IconId, Integer> gameIconInventories;

    public static SaveGame fromJson(String json) {
        if (TextUtils.isEmpty(json)) {
            return null;
        }
        SaveGame saveGame = null;
        try {
            saveGame = new Gson().fromJson(json, SaveGame.class);
        } catch (JsonSyntaxException e) {
            Log.e("SaveGame", "Error parsing SaveGame from Json " + e.getMessage());
        }
        return saveGame;
    }

    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
