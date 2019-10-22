package com.wack.pop2.gamesingletons;

import org.andengine.entity.scene.Scene;

public class SceneSingleton {

    private static Scene sScene;

    public static Scene instanceOf() {
        if (sScene == null) {
            sScene = new Scene();
        }
        return sScene;
    }
}
