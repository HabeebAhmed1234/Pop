package com.wack.pop2.turret;

import com.wack.pop2.BaseEntity;
import com.wack.pop2.GameResources;
import com.wack.pop2.GameSceneTouchListenerEntity;
import com.wack.pop2.fixturedefdata.BubbleEntityUserData;
import com.wack.pop2.resources.textures.GameTexturesManager;

import org.andengine.entity.IEntity;
import org.andengine.entity.IEntityMatcher;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.input.touch.TouchEvent;

import java.util.List;

import androidx.annotation.Nullable;

/**
 * Holds all the mixins for turret management.
 */
public class TurretsManagerEntity extends BaseEntity implements IOnSceneTouchListener, GameSceneTouchListenerEntity.SceneTouchListener {

    private GameTexturesManager texturesManager;
    private TurretCreatorEntity turretCreatorEntity;
    private GameSceneTouchListenerEntity sceneTouchListenerEntity;

    private Turret turret;

    public TurretsManagerEntity(
            GameSceneTouchListenerEntity sceneTouchListenerEntity,
            GameTexturesManager texturesManager,
            GameResources gameResources) {
        super(gameResources);
        this.sceneTouchListenerEntity = sceneTouchListenerEntity;
        this.texturesManager = texturesManager;
        this.turretCreatorEntity = new TurretCreatorEntity(texturesManager, gameResources);
    }

    @Override
    public void onCreateScene() {
        super.onCreateScene();
        turret = turretCreatorEntity.createTurret();
        sceneTouchListenerEntity.addSceneTouchListener(this);
    }

    @Override
    public void onDestroy() {
        sceneTouchListenerEntity.removeSceneTouchListener(this);
    }

    /**
     * Returns all the bubbles present in the scene
     */
    private List<IEntity> getAllBubbles() {
        return scene.query(new IEntityMatcher() {
            @Override
            public boolean matches(IEntity pEntity) {
                @Nullable Object userdata = pEntity.getUserData();
                if (userdata != null) {
                    return userdata instanceof BubbleEntityUserData;
                }
                return false;
            }
        });
    }

    @Override
    public boolean onSceneTouchEvent(Scene pScene, TouchEvent touchEvent) {
        switch(touchEvent.getAction()) {
            case TouchEvent.ACTION_UP:
            case TouchEvent.ACTION_DOWN:
            case TouchEvent.ACTION_MOVE:
                turret.setAimAt(touchEvent.getX(), touchEvent.getY());
                return true;
        }
        return false;
    }
}
