package com.wack.pop2.walls;

import com.wack.pop2.BaseEntity;
import com.wack.pop2.GameAreaTouchListenerEntity;
import com.wack.pop2.GameIconsTrayEntity;
import com.wack.pop2.GameResources;
import com.wack.pop2.eventbus.DifficultyChangedEventPayload;
import com.wack.pop2.eventbus.EventBus;
import com.wack.pop2.eventbus.EventPayload;
import com.wack.pop2.eventbus.GameEvent;
import com.wack.pop2.fixturedefdata.WallsIconUserData;
import com.wack.pop2.resources.fonts.FontId;
import com.wack.pop2.resources.fonts.GameFontsManager;
import com.wack.pop2.resources.textures.GameTexturesManager;
import com.wack.pop2.resources.textures.TextureId;
import com.wack.pop2.statemachine.BaseStateMachine;

import org.andengine.entity.scene.ITouchArea;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.util.color.AndengineColor;

import static com.wack.pop2.turret.TurretsConstants.MAX_TURRET_INVENTORY;
import static com.wack.pop2.walls.WallsConstants.MAX_WALLS_INVENTORY;

public class WallsIconEntity extends BaseEntity implements GameAreaTouchListenerEntity.AreaTouchListener, EventBus.Subscriber, BaseStateMachine.Listener<WallsStateMachine.State> {

    private static final float WALLS_INVENTORY_TEXT_MAX_WIDTH_PX = 20;
    private static final float WALLS_INVENTORY_TEXT_MAX_HEIGHT_PX = 80;

    private GameFontsManager fontsManager;

    private WallsStateMachine stateMachine;

    private GameIconsTrayEntity gameIconsTrayEntity;
    private GameAreaTouchListenerEntity touchListenerEntity;
    private GameTexturesManager gameTexturesManager;

    private Sprite wallsIconSprite;
    private Text wallsInventoryText;
    // Number of turrets currently DOCKED in the icon
    private int numWallsInventory = MAX_WALLS_INVENTORY;

    public WallsIconEntity(
            WallsStateMachine stateMachine,
            GameIconsTrayEntity gameIconsTrayEntity,
            GameAreaTouchListenerEntity touchListenerEntity,
            GameTexturesManager gameTexturesManager,
            GameFontsManager fontsManager,
            GameResources gameResources) {
        super(gameResources);
        this.stateMachine = stateMachine;
        this.gameIconsTrayEntity = gameIconsTrayEntity;
        this.touchListenerEntity = touchListenerEntity;
        this.fontsManager = fontsManager;
        this.gameTexturesManager = gameTexturesManager;
    }

    @Override
    public void onCreateScene() {
        createIconAndText();

        EventBus.get().subscribe(GameEvent.DIFFICULTY_CHANGE, this, true);
        touchListenerEntity.addAreaTouchListener(WallsIconUserData.class, this);
        stateMachine.addAllStateTransitionListener(this);
    }

    @Override
    public void onDestroy() {
        EventBus.get().unSubscribe(GameEvent.DIFFICULTY_CHANGE, this);
        touchListenerEntity.removeAreaTouchListener(WallsIconUserData.class, this);
    }

    @Override
    public void onEvent(GameEvent event, EventPayload payload) {
        switch (event) {
            case DIFFICULTY_CHANGE:
                DifficultyChangedEventPayload difficultyChangedEventPayload =
                        (DifficultyChangedEventPayload) payload;
                onDifficultyChanged(difficultyChangedEventPayload.newDifficulty);
                break;
        }
    }

    private void createIconAndText() {
        // Create the icon sprite
        ITextureRegion textureRegion =
                gameTexturesManager.getTextureRegion(TextureId.WALLS_ICON);
        wallsIconSprite = new Sprite(
                0,
                0,
                textureRegion,
                vertexBufferObjectManager);
        wallsIconSprite.setUserData(new WallsIconUserData());
        addToSceneWithTouch(wallsIconSprite);
        gameIconsTrayEntity.addIcon(GameIconsTrayEntity.ICON_ID.WALLS_ICON, wallsIconSprite);

        // Create text
        wallsInventoryText = new Text(
                wallsIconSprite.getX() + wallsIconSprite.getWidthScaled() / 2 - WALLS_INVENTORY_TEXT_MAX_WIDTH_PX,
                wallsIconSprite.getY() - WALLS_INVENTORY_TEXT_MAX_HEIGHT_PX,
                fontsManager.getFont(FontId.TURRET_ICON_FONT),
                Integer.toString(numWallsInventory),
                (Integer.toString(MAX_TURRET_INVENTORY)).length(),
                vertexBufferObjectManager);
        scene.attachChild(wallsInventoryText);

    }

    private void onDifficultyChanged(int newDifficulty) {
        if (newDifficulty >= WallsConstants.WALLS_DIFFICULTY_UNLOCK_THRESHOLD && stateMachine.getCurrentState() == WallsStateMachine.State.LOCKED) {
            stateMachine.transitionState(WallsStateMachine.State.UNLOCKED_TOGGLED_OFF);
        }
    }

    @Override
    public boolean onTouch(
            TouchEvent touchEvent,
            ITouchArea pTouchArea,
            float pTouchAreaLocalX,
            float pTouchAreaLocalY) {
        if (touchEvent.isActionUp()) {
            if (stateMachine.getCurrentState() == WallsStateMachine.State.UNLOCKED_TOGGLED_OFF) {
                stateMachine.transitionState(WallsStateMachine.State.TOGGLED_ON);
                return true;
            } else if (stateMachine.getCurrentState() == WallsStateMachine.State.TOGGLED_ON) {
                stateMachine.transitionState(WallsStateMachine.State.UNLOCKED_TOGGLED_OFF);
                return true;
            }
        }
        return false;
    }

    @Override
    public void onEnterState(WallsStateMachine.State newState) {
        setIconColor(newState);
    }

    private void setIconColor(WallsStateMachine.State newState) {
        AndengineColor color = AndengineColor.WHITE;
        switch (newState) {
            case LOCKED:
                color = AndengineColor.TRANSPARENT;
                break;
            case UNLOCKED_TOGGLED_OFF:
                color = AndengineColor.WHITE;
                break;
            case TOGGLED_ON:
                color = AndengineColor.GREEN;
                break;
        }
        wallsIconSprite.setColor(color);
    }

}
