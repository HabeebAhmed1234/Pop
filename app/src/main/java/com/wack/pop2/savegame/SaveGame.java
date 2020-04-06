package com.wack.pop2.savegame;

import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.util.List;

public class SaveGame {

    public int score;
    public float difficulty;
    public List<Pair<Float, Float>> turretPostitions;
    public List<Pair<Pair<Float, Float>, Pair<Float, Float>>> wallPositions;

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
