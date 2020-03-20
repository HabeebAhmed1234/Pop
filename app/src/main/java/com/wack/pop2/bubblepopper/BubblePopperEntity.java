package com.wack.pop2.bubblepopper;

import android.opengl.GLES20;

import com.wack.pop2.BaseEntity;
import com.wack.pop2.BubbleSpawnerEntity;
import com.wack.pop2.GameAnimationManager;
import com.wack.pop2.GameResources;
import com.wack.pop2.eventbus.BubbleTouchedEventPayload;
import com.wack.pop2.eventbus.EventBus;
import com.wack.pop2.eventbus.EventPayload;
import com.wack.pop2.eventbus.GameEvent;
import com.wack.pop2.eventbus.IncrementScoreEventPayload;
import com.wack.pop2.physics.util.Vec2Pool;
import com.wack.pop2.resources.fonts.FontId;
import com.wack.pop2.resources.fonts.GameFontsManager;
import com.wack.pop2.resources.sounds.GameSoundsManager;
import com.wack.pop2.resources.sounds.SoundId;
import com.wack.pop2.utils.BubblePhysicsUtil;

import org.andengine.audio.sound.Sound;
import org.andengine.entity.modifier.AlphaModifier;
import org.andengine.entity.modifier.ParallelEntityModifier;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.shape.IShape;
import org.andengine.entity.text.Text;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;

public class BubblePopperEntity extends BaseEntity implements BubblePopper, EventBus.Subscriber {

    public static final int SCORE_INCREMENT_PER_BUBBLE_POP = 10;
    public static final int MAX_SCORE_INCREASE_PER_NEW_SPAWNED_BUBBLE = 70; // Since there are three bubble sizes a total of 7 bubbles can be popped from one spawned bubble

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
        super.onCreateScene();
        EventBus.get().subscribe(GameEvent.BUBBLE_TOUCHED, this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.get().unSubscribe(GameEvent.BUBBLE_TOUCHED, this);
    }

    @Override
    public void popBubble(IShape previousBubble, BubbleSpawnerEntity.BubbleSize oldBubbleSize, BubbleSpawnerEntity.BubbleType bubbleType) {
        // Remove the popped bubble
        removeFromScene(previousBubble);

        // Play the pop sound
        getRandomPopSound().play();

        Vec2 oldBubbleScenePosition = Vec2Pool.obtain(previousBubble.getX(), previousBubble.getY());
        // Spawn new bubbles if the one we popped not the smallest bubble
        if(!oldBubbleSize.isSmallestBubble()) {
            spawnPoppedBubbles(oldBubbleSize, oldBubbleScenePosition, bubbleType);
        }

        // Increment the score
        increaseScore(oldBubbleScenePosition.x, oldBubbleScenePosition.y);
    }

    /**
     * Spawns 2 new bubbles in the place where the old bubble was
     */
    private void spawnPoppedBubbles(BubbleSpawnerEntity.BubbleSize oldBubbleSize, Vec2 oldBubbleScenePosition, BubbleSpawnerEntity.BubbleType bubbleType) {
        Body leftBubble = bubbleSpawnerEntity.spawnBubble(
                bubbleType,
                oldBubbleScenePosition.x,
                oldBubbleScenePosition.y,
                oldBubbleSize.nextPoppedSize());

        BubblePhysicsUtil.applyVelocity(leftBubble,-3f, -1.2f);

        Body rightBubble = bubbleSpawnerEntity.spawnBubble(
                bubbleType,
                oldBubbleScenePosition.x,
                oldBubbleScenePosition.y,
                oldBubbleSize.nextPoppedSize());

        BubblePhysicsUtil.applyVelocity(rightBubble, 3f, -1.2f);
    }

    private void increaseScore(float sceneX, float sceneY) {
        showScoretickerText(sceneX, sceneY);
        EventBus.get().sendEvent(GameEvent.INCREMENT_SCORE, new IncrementScoreEventPayload(SCORE_INCREMENT_PER_BUBBLE_POP));
    }

    private void showScoretickerText(float sceneX, float sceneY) {
        final Text scorePlus10Text = new Text(sceneX, sceneY, fontManager.getFont(FontId.SCORE_TICKER_FONT), "+10!", vertexBufferObjectManager);
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

    @Override
    public void onEvent(GameEvent event, EventPayload payload) {
        if (event == GameEvent.BUBBLE_TOUCHED) {
            BubbleTouchedEventPayload touchedEventPayload = (BubbleTouchedEventPayload) payload;
            popBubble(touchedEventPayload.sprite, touchedEventPayload.size, touchedEventPayload.type);
        }
    }
}
