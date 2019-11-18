package com.wack.pop2;

import android.opengl.GLES20;

import com.wack.pop2.eventbus.EventBus;
import com.wack.pop2.eventbus.GameEvent;
import com.wack.pop2.eventbus.GameOverExplosionEventPayload;
import com.wack.pop2.eventbus.IncrementScoreEventPayload;
import com.wack.pop2.resources.fonts.FontId;
import com.wack.pop2.resources.fonts.GameFontsManager;
import com.wack.pop2.resources.sounds.GameSoundsManager;
import com.wack.pop2.resources.sounds.SoundId;

import org.andengine.audio.sound.Sound;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.AlphaModifier;
import org.andengine.entity.modifier.ParallelEntityModifier;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.shape.IShape;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;

public class BubblePopperEntity extends BaseEntity {

    private static final int SCORE_INCREMENT_PER_BUBBLE_POP = 10;

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

    public void triggerGameOverExplosion(Sprite skullBubble) {
        EventBus.get().sendEvent(
                GameEvent.GAME_OVER_ON_EXPLOSION_EVENT,
                new GameOverExplosionEventPayload(skullBubble.getX(), skullBubble.getY(), skullBubble.getWidth(), skullBubble.getHeight(), skullBubble.getScaleX()));
        removeFromScene(skullBubble);
    }

    public void popBubble(IShape previousBubble, BubbleSpawnerEntity.BubbleSize oldBubbleSize, Vec2 oldBubbleScenePosition, BubbleSpawnerEntity.BubbleType bubbleType) {
        // Remove the popped bubble
        removeFromScene(previousBubble);

        // Play the pop sound
        getRandomPopSound().play();

        // Spawn new bubbles if the one we popped not the smallest bubble
        if(!oldBubbleSize.isSmallestBubble()) {
            spawnPoppedBubbles(oldBubbleSize, oldBubbleScenePosition, bubbleType);
        }

        // Increment the score
        //increaseScore(oldBubbleScenePosition.x, oldBubbleScenePosition.y);
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
}
