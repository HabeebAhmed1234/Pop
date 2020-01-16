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
import com.wack.pop2.resources.fonts.FontId;
import com.wack.pop2.resources.fonts.GameFontsManager;
import com.wack.pop2.resources.textures.GameTexturesManager;
import com.wack.pop2.resources.textures.TextureId;

import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.util.color.AndengineColor;

import static com.wack.pop2.turret.TurretsConstants.MAX_TURRET_INVENTORY;
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

    private static final float TURRET_INVENTORY_TEXT_MAX_WIDTH_PX = 20;
    private static final float TURRET_INVENTORY_TEXT_MAX_HEIGHT_PX = 80;

    private GameFontsManager fontsManager;
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
    private Text dockedTurretsText;
    // Number of turrets currently DOCKED in the icon
    private int numDockedTurets = MAX_TURRET_INVENTORY;

    public TurretsIconEntity(
            GameFontsManager fontsManager,
            GameSceneTouchListenerEntity touchListenerEntity,
            GameTexturesManager gameTexturesManager,
            GameIconsTrayEntity gameIconsTrayEntity,
            TurretEntityCreator turretEntityCreator,
            TurretsMutex mutex,
            GameResources gameResources) {
        super(gameResources);
        this.fontsManager = fontsManager;
        this.touchListenerEntity = touchListenerEntity;
        this.gameTexturesManager = gameTexturesManager;
        this.gameIconsTrayEntity = gameIconsTrayEntity;
        this.turretEntityCreator = turretEntityCreator;
        this.mutex = mutex;
    }

    @Override
    public void onCreateScene() {
        createIconAndText();

        EventBus.get().subscribe(GameEvent.DIFFICULTY_CHANGE, this, true);
        EventBus.get().subscribe(GameEvent.TURRET_DOCKED, this);
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
            case TURRET_DOCKED:
                onDockTurret();
                break;
        }
    }

    @Override
    public boolean onSceneTouchEvent(Scene scene, TouchEvent touchEvent) {
        boolean handled = false;
        switch (touchEvent.getAction()) {
            case ACTION_DOWN:
                handled = maybeStartUndocking(touchEvent);
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

    public void onUnDockTurret() {
        numDockedTurets--;
        onStateChanged();
    }

    public void onDockTurret() {
        numDockedTurets++;
        onStateChanged();
    }

    private void createIconAndText() {
        // Create the icon sprite
        ITextureRegion textureRegion =
                gameTexturesManager.getTextureRegion(TextureId.TURRETS_ICON);
        turretIconSprite = new Sprite(
                0,
                0,
                textureRegion,
                vertexBufferObjectManager);
        turretIconSprite.setUserData(new TurretsIconUserData());
        addToSceneWithTouch(turretIconSprite);
        gameIconsTrayEntity.addIcon(GameIconsTrayEntity.ICON_ID.TURRETS_ICON, turretIconSprite);

        // Create text
        dockedTurretsText = new Text(
                turretIconSprite.getX() + turretIconSprite.getWidthScaled() / 2 - TURRET_INVENTORY_TEXT_MAX_WIDTH_PX,
                turretIconSprite.getY() - TURRET_INVENTORY_TEXT_MAX_HEIGHT_PX,
                fontsManager.getFont(FontId.TURRET_ICON_FONT),
                Integer.toString(numDockedTurets),
                (Integer.toString(MAX_TURRET_INVENTORY)).length(),
                vertexBufferObjectManager);
        scene.attachChild(dockedTurretsText);

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
        AndengineColor iconColor = isUnlocked ? AndengineColor.GREEN : AndengineColor.TRANSPARENT;
        turretIconSprite.setColor(iconColor);
        dockedTurretsText.setText(Integer.toString(numDockedTurets));
        dockedTurretsText.setColor(iconColor);
    }

    /**
     * If the user has pressed down on the icon and they are not already dragging a turret then
     * we need to start undocking a new turret. While this new turret is being undocking we cannot
     * undocking another turret.
     *
     * @Return true if we started spawning
     */
    private boolean maybeStartUndocking(TouchEvent touchEvent) {
        if (canUndockTurret(touchEvent)) {
            // Create a turret and set it to be dragging
            TurretEntity turretEntity = turretEntityCreator.createTurret(touchEvent.getX(), touchEvent.getY());
            turretEntity.forceStartDragging(touchEvent.getX(), touchEvent.getY());

            onUnDockTurret();
            isSpawning = true;
            return true;
        }
        return false;
    }

    /**
     * Returns true if it is permissible to undock a new turret
     * @return
     */
    private boolean canUndockTurret(TouchEvent touchEvent) {
        return !isSpawning && !mutex.isDragging() && turretIconSprite.contains(touchEvent.getX(), touchEvent.getY()) && numDockedTurets > 0;
    }

    private void finishedSpawning() {
        isSpawning = false;
    }
}