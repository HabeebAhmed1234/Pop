package com.wack.pop2;

import android.opengl.GLES20;

import com.wack.pop2.eventbus.EventBus;
import com.wack.pop2.eventbus.GameEvent;
import com.wack.pop2.eventbus.GameOverExplosionEventPayload;
import com.wack.pop2.eventbus.IncrementScoreEventPayload;
import com.wack.pop2.fixturedefdata.BubbleEntityUserData;
import com.wack.pop2.physics.PhysicsConnector;
import com.wack.pop2.resources.fonts.FontId;
import com.wack.pop2.resources.fonts.GameFontsManager;
import com.wack.pop2.resources.sounds.GameSoundsManager;
import com.wack.pop2.resources.sounds.SoundId;

import org.andengine.audio.sound.Sound;
import org.andengine.entity.modifier.AlphaModifier;
import org.andengine.entity.modifier.ParallelEntityModifier;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.scene.IOnAreaTouchListener;
import org.andengine.entity.scene.ITouchArea;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.input.touch.TouchEvent;

public class BubblePopperEntity extends BaseEntity implements IOnAreaTouchListener {

    private static final int SCORE_INCREMENT_PER_BUBBLE_POP = 10;
    // The smallest size a bubble can be before we don't spawn new ones in its place
    private static final float MINIMUM_BUBBLE_SCALE = 1.2f;
    // The size of the new bubbles when an old bubble is popped
    private static final float NEW_BUBBLE_SCALE_MULTIPLIER = 0.5f;

    private GameFontsManager fontManager;
    private GameSoundsManager soundsManager;
    private GameAnimationManager gameAnimationManager;
    private BubbleSpawnerEntity bubbleSpawnerEntity;

    public BubblePopperEntity(
            GameFontsManager fontManager,
            GameSoundsManager soundsManager,
            GameAnimationManager gameAnimationManager,
            BubbleSpawnerEntity bubbleSpawnerEntity,
            GameResources gameResources) {
        super(gameResources);
        this.fontManager = fontManager;
        this.soundsManager = soundsManager;
        this.gameAnimationManager = gameAnimationManager;
        this.bubbleSpawnerEntity = bubbleSpawnerEntity;
    }

    @Override
    public void onCreateScene() {
        scene.setOnAreaTouchListener(this);
    }

    @Override
    public boolean onAreaTouched(TouchEvent pSceneTouchEvent, ITouchArea pTouchArea, float pTouchAreaLocalX, float pTouchAreaLocalY) {
        if(pSceneTouchEvent.isActionDown()) {
            final Sprite entity =  (Sprite) pTouchArea;
            if (entity.getUserData() == null) {
                return false;
            }
            final Object userData = entity.getUserData();

            if(userData instanceof BubbleEntityUserData) {
                BubbleEntityUserData bubbleEntityUserData = (BubbleEntityUserData) userData;
                if (bubbleEntityUserData.isGameOverWhenPopped) {
                    triggerGameOverExplosion(entity);
                    return true;
                } else {
                    popBubble(entity, bubbleEntityUserData.bubbleType);
                }
            }

            return true;
        }
        return false;
    }

    private void triggerGameOverExplosion(Sprite entity) {
        EventBus.get().sendEvent(
                GameEvent.GAME_OVER_ON_EXPLOSION_EVENT,
                new GameOverExplosionEventPayload(entity.getX(), entity.getY(), entity.getWidth(), entity.getHeight(), entity.getScaleX()));
    }

    private void popBubble(Sprite entity, BubbleSpawnerEntity.BubbleType bubbleType) {
        // Play the pop sound
        getRandomPopSound().play();

        // Spawn new bubbles if the one we popped is above the min size
        if(entity.getScaleX() > MINIMUM_BUBBLE_SCALE) {
            spawnPoppedBubbles(entity, bubbleType);
        }

        // Increment the score
        increaseScore(entity.getX(), entity.getY());

        // Remove the popped bubble
        removeFromScene(entity);
    }

    /**
     * Spawns 2 new bubbles in the place where the old bubble was
     */
    private void spawnPoppedBubbles(Sprite oldBubble, BubbleSpawnerEntity.BubbleType bubbleType) {
        PhysicsConnector leftBubble = bubbleSpawnerEntity.spawnBubble(
                bubbleType,
                oldBubble.getX() - (oldBubble.getWidth() / 2),
                oldBubble.getY(),
                oldBubble.getScaleX() * NEW_BUBBLE_SCALE_MULTIPLIER);

        BubblePhysicsUtil.applyVelocity(leftBubble,-3f, -1.2f);

        PhysicsConnector rightBubble = bubbleSpawnerEntity.spawnBubble(
                bubbleType,
                oldBubble.getX() + (oldBubble.getWidth() / 2),
                oldBubble.getY(),
                oldBubble.getScaleX() * NEW_BUBBLE_SCALE_MULTIPLIER);

        BubblePhysicsUtil.applyVelocity(rightBubble, 3f, -1.2f);
    }

    private void increaseScore(float bubbleX, float bubbleY) {
        showScoretickerText(bubbleX, bubbleY);
        EventBus.get().sendEvent(GameEvent.INCREMENT_SCORE, new IncrementScoreEventPayload(SCORE_INCREMENT_PER_BUBBLE_POP));
    }

    private void showScoretickerText(float x, float y) {
        final Text scorePlus10Text = new Text(x, y, fontManager.getFont(FontId.SCORE_TICKER_FONT), "+10!", vertexBufferObjectManager);
        scorePlus10Text.setBlendFunction(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        scorePlus10Text.setColor(0, 1, 0);
        addToScene(scorePlus10Text);

        gameAnimationManager.startModifier(
                scorePlus10Text,
                new ParallelEntityModifier(
                    new ScaleModifier(0.75f, 0.1f, 1.1f),
                    new AlphaModifier(0.75f, 1f, 0f)));
    }

    private Sound getRandomPopSound() {
        int random=(int) (Math.random()*4);
        switch (random) {
            case 0:
                return soundsManager.getSound(SoundId.POP_1);
            case 1:
                return soundsManager.getSound(SoundId.POP_2);
            case 2:
                return soundsManager.getSound(SoundId.POP_3);
            case 3:
                return soundsManager.getSound(SoundId.POP_4);
            case 4:
                return soundsManager.getSound(SoundId.POP_5);
        }
        throw new IllegalStateException("No sound for index " + random);
    }
}
