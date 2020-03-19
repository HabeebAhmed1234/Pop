package com.wack.pop2;

import android.util.Log;

import com.wack.pop2.resources.textures.GameTexturesManager;
import com.wack.pop2.resources.textures.TextureId;
import com.wack.pop2.utils.ScreenUtils;

import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.input.touch.TouchEvent;

public class DebugSandboxEntity extends BaseEntity implements GameSceneTouchListenerEntity.SceneTouchListener {

    private static final String TAG = "DebugTouchTracerEntity";
    private GameSceneTouchListenerEntity touchListenerEntity;
    private GameTexturesManager gameTexturesManager;

    public DebugSandboxEntity(GameTexturesManager gameTexturesManager, GameSceneTouchListenerEntity touchListenerEntity, GameResources gameResources) {
        super(gameResources);
        this.gameTexturesManager = gameTexturesManager;
        this.touchListenerEntity = touchListenerEntity;
    }

    @Override
    public void onCreateScene() {
        super.onCreateScene();
        scene.setTouchAreaBindingOnActionDownEnabled(true);
        scene.setOnAreaTouchTraversalFrontToBack();

        touchListenerEntity.addSceneTouchListener(this);


        final Sprite bubbleSprite1 = new Sprite(
                ScreenUtils.getSreenSize().widthPx/2 - 300,
                ScreenUtils.getSreenSize().heightPx/2 - 300,
                gameTexturesManager.getTextureRegion(TextureId.BALL),
                vertexBufferObjectManager) {
            @Override
            public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
                Log.d("asdasd", "1 touched action = " + pSceneTouchEvent.getAction());
                if (pSceneTouchEvent.isActionDown() || pSceneTouchEvent.isActionMove() || pSceneTouchEvent.isActionUp()) {
                    return true;
                }
                return false;
            }
        };

        final Sprite bubbleSprite2 = new Sprite(
                bubbleSprite1.getX() + bubbleSprite1.getWidth() /3,
                bubbleSprite1.getY() + bubbleSprite1.getHeight() /3,
                gameTexturesManager.getTextureRegion(TextureId.BALL),
                vertexBufferObjectManager) {
            @Override
            public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
                Log.d("asdasd", "2 touched action = " + pSceneTouchEvent.getAction());
                if (pSceneTouchEvent.isActionDown() || pSceneTouchEvent.isActionMove() || pSceneTouchEvent.isActionUp()) {
                    return true;
                }
                return false;
            }
        };

        addToSceneWithTouch(bubbleSprite1);
        addToSceneWithTouch(bubbleSprite2);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        touchListenerEntity.removeSceneTouchListener(this);
    }

    @Override
    public boolean onSceneTouchEvent(Scene scene, TouchEvent touchEvent) {
        Log.d(TAG, "TouchEvent: ("+touchEvent.getX() + "," + touchEvent.getY() + ")");
        return false;
    }
}
