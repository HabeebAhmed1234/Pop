package com.wack.pop2.gamesingletons;

import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.ui.activity.BaseGameActivity;

public class VertBuffSingleton {

    private static VertexBufferObjectManager sVertexBufferObjectManager;

    public static void init(BaseGameActivity gameActivity) {
        sVertexBufferObjectManager = gameActivity.getVertexBufferObjectManager();
    }

    public static VertexBufferObjectManager get() {
     if (sVertexBufferObjectManager == null)  {
         throw new IllegalStateException("VertBuffSingleton: Cannot get a VertexBufferObjectManager before init is called");
     }
     return sVertexBufferObjectManager;
    }
}
