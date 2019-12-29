package com.wack.pop2.turret;

import com.wack.pop2.BaseEntity;
import com.wack.pop2.GameIconsTrayEntity;
import com.wack.pop2.GameResources;
import com.wack.pop2.GameSceneTouchListenerEntity;
import com.wack.pop2.eventbus.DifficultyChangedEventPayload;
import com.wack.pop2.eventbus.EventBus;
import com.wack.pop2.eventbus.EventPayload;
import com.wack.pop2.eventbus.GameEvent;
import com.wack.pop2.fixturedefdata.TurretsIconUserData;
import com.wack.pop2.resources.textures.GameTexturesManager;
import com.wack.pop2.resources.textures.TextureId;

import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.util.color.AndengineColor;

import static org.andengine.input.touch.TouchEvent.ACTION_CANCEL;
import static org.andengine.input.touch.TouchEvent.ACTION_DOWN;
import static org.andengine.input.touch.TouchEvent.ACTION_MOVE;
import static org.andengine.input.touch.TouchEvent.ACTION_OUTSIDE;
import static org.andengine.input.touch.TouchEvent.ACTION_UP;

/**
 * Icon used to utilized turrets. Once unlocked this icon appears. The user can then press down on
 * the icon and a turret appears under their finger. They can then drag the turret to where they
 * want it. The icon has a number on it to show the number of turrets in stock. The user can drag
 * turrets back onto the icon to store them this increasing the number turrets in stock on the icon.
 */
class TurretsIconEntity extends BaseEntity implements EventBus.Subscriber, GameSceneTouchListenerEntity.SceneTouchListener {

    private GameSceneTouchListenerEntity touchListenerEntity;
    private GameTexturesManager gameTexturesManager;
    private GameIconsTrayEntity gameIconsTrayEntity;
    private TurretEntityCreator turretEntityCreator;
    private TurretsMutex mutex;

    // True if the Turrets have been unlocked in the game
    private boolean isUnlocked;
    // True if we are currently spawning
    private boolean isSpawning;
    private Sprite turretIconSprite;

    public TurretsIconEntity(
            GameSceneTouchListenerEntity touchListenerEntity,
            GameTexturesManager gameTexturesManager,
            GameIconsTrayEntity gameIconsTrayEntity,
            TurretEntityCreator turretEntityCreator,
            TurretsMutex mutex,
            GameResources gameResources) {
        super(gameResources);
        this.touchListenerEntity = touchListenerEntity;
        this.gameTexturesManager = gameTexturesManager;
        this.gameIconsTrayEntity = gameIconsTrayEntity;
        this.turretEntityCreator = turretEntityCreator;
        this.mutex = mutex;
    }

    @Override
    public void onCreateScene() {
        createIcon();

        EventBus.get().subscribe(GameEvent.DIFFICULTY_CHANGE, this);
        touchListenerEntity.addSceneTouchListener(this);

        //TODO: DEBUG
        unlock();
    }

    @Override
    public void onDestroy() {
        EventBus.get().unSubscribe(GameEvent.DIFFICULTY_CHANGE, this);
    }

    @Override
    public void onEvent(GameEvent event, EventPayload payload) {
        switch (event) {
            case DIFFICULTY_CHANGE:
                DifficultyChangedEventPayload difficultyChangedEventPayload =
                        (DifficultyChangedEventPayload) payload;
                onScoreChanged(difficultyChangedEventPayload.newDifficulty);
                break;
        }
    }

    private void createIcon() {
        ITextureRegion textureRegion =
                gameTexturesManager.getTextureRegion(TextureId.TURRETS_ICON);
        turretIconSprite = new Sprite(
                0,
                0,
                textureRegion,
                vertexBufferObjectManager);
        turretIconSprite.setUserData(new TurretsIconUserData());
        addToSceneWithTouch(turretIconSprite);
        gameIconsTrayEntity.addIcon(turretIconSprite);

        onStateChanged();
    }

    private void onScoreChanged(int newDifficulty) {
        if (newDifficulty >= TurretsConstants.TURRETS_DIFFICULTY_UNLOCK_THRESHOLD && !isUnlocked) {
            unlock();
        }
    }

    private void unlock() {
        isUnlocked = true;
        onStateChanged();
    }

    private void onStateChanged() {
        turretIconSprite.setColor(isUnlocked ? AndengineColor.GREEN : AndengineColor.TRANSPARENT);
    }

    @Override
    public boolean onSceneTouchEvent(Scene scene, TouchEvent touchEvent) {
        boolean handled = false;
        switch (touchEvent.getAction()) {
            case ACTION_DOWN:
                handled = maybeStartSpawning(touchEvent);
                break;
            case ACTION_UP:
                finishedSpawning();
                handled = true;
                break;
            case ACTION_CANCEL:
            case ACTION_OUTSIDE:
            case ACTION_MOVE:
                // NOOP
                break;

        }
        return handled;
    }

    /**
     * If the user has pressed down on the icon and they are not already dragging a turret then
     * we need to start spawning a new turret. While this new turret is being spawned we cannot
     * spawn another turret.
     *
     * @Return true if we started spawning
     */
    private boolean maybeStartSpawning(TouchEvent touchEvent) {
        if (!isSpawning && !mutex.isDragging() && turretIconSprite.contains(touchEvent.getX(), touchEvent.getY())) {
            // Create a turret and set it to be dragging
            TurretEntity turretEntity = turretEntityCreator.createTurret(touchEvent.getX(), touchEvent.getY());
            turretEntity.forceStartDragging(touchEvent.getX(), touchEvent.getY());

            isSpawning = true;
            return true;
        }
        return false;
    }

    private void finishedSpawning() {
        isSpawning = false;
    }
}