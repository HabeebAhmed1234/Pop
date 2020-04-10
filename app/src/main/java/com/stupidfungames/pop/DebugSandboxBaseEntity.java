package com.stupidfungames.pop;

import android.util.Log;

import com.stupidfungames.pop.binder.Binder;
import com.stupidfungames.pop.binder.BinderEnity;
import com.stupidfungames.pop.resources.textures.GameTexturesManager;
import com.stupidfungames.pop.resources.textures.TextureId;
import com.stupidfungames.pop.utils.ScreenUtils;

import org.andengine.entity.scene.IOnAreaTouchListener;
import org.andengine.entity.scene.ITouchArea;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.input.touch.TouchEvent;

public class DebugSandboxBaseEntity extends BaseEntity implements GameSceneTouchListenerEntity.SceneTouchListener {

    private static final String TAG = "DebugTouchTracerEntity";

    public DebugSandboxBaseEntity(BinderEnity parent) {
        super(parent);
    }

    @Override
    protected void createBindings(Binder binder) {

    }

    @Override
    public void onCreateScene() {
        super.onCreateScene();
        scene.setTouchAreaBindingOnActionDownEnabled(true);
        //scene.setOnAreaTouchTraversalFrontToBack();

        get(GameSceneTouchListenerEntity.class).addSceneTouchListener(this);


        final Sprite bubbleSprite1 = new Sprite(
                ScreenUtils.getSreenSize().widthPx - 500,
                ScreenUtils.getSreenSize().heightPx / 2 - 300,
                get(GameTexturesManager.class).getTextureRegion(TextureId.BALL),
                vertexBufferObjectManager);

        final Sprite bubbleSprite2 = new Sprite(
                bubbleSprite1.getX() + bubbleSprite1.getWidth() /3,
                bubbleSprite1.getY() + bubbleSprite1.getHeight() /3,
                get(GameTexturesManager.class).getTextureRegion(TextureId.BALL),
                vertexBufferObjectManager);

        addToSceneWithTouch(bubbleSprite1, new IOnAreaTouchListener() {
            @Override
            public boolean onAreaTouched(TouchEvent pSceneTouchEvent, ITouchArea pTouchArea, float pTouchAreaLocalX, float pTouchAreaLocalY) {
                // Log.d("asdasd", "1 touched action = " + pSceneTouchEvent.getAction());
                if (pSceneTouchEvent.isActionDown() || pSceneTouchEvent.isActionMove() || pSceneTouchEvent.isActionUp()) {
                    return true;
                }
                return false;
            }
        });
        addToSceneWithTouch(bubbleSprite2, new IOnAreaTouchListener() {
            @Override
            public boolean onAreaTouched(TouchEvent pSceneTouchEvent, ITouchArea pTouchArea, float pTouchAreaLocalX, float pTouchAreaLocalY) {
                // Log.d("asdasd", "2 touched action = " + pSceneTouchEvent.getAction());
                if (pSceneTouchEvent.isActionDown() || pSceneTouchEvent.isActionMove() || pSceneTouchEvent.isActionUp()) {
                    return true;
                }
                return false;
            }
        });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        get(GameSceneTouchListenerEntity.class).removeSceneTouchListener(this);
    }

    @Override
    public boolean onSceneTouchEvent(Scene scene, TouchEvent touchEvent) {
        // Log.d(TAG, "TouchEvent: ("+touchEvent.getX() + "," + touchEvent.getY() + ")");
        return false;
    }
}
