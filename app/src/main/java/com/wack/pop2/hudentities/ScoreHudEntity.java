package com.wack.pop2.hudentities;

import com.wack.pop2.BaseEntity;
import com.wack.pop2.EventPayload;
import com.wack.pop2.GameResources;
import com.wack.pop2.ScreenUtils;
import com.wack.pop2.eventbus.DecrementScoreEventPayload;
import com.wack.pop2.eventbus.EventBus;
import com.wack.pop2.eventbus.GameEvent;
import com.wack.pop2.eventbus.IncrementScoreEventPayload;
import com.wack.pop2.eventbus.ScoreChangeEventPayload;
import com.wack.pop2.resources.fonts.FontId;
import com.wack.pop2.resources.fonts.GameFontsManager;
import com.wack.pop2.resources.textures.GameTexturesManager;
import com.wack.pop2.resources.textures.TextureId;

import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;

/**
 * Entity that contains score hud panel
 */
public class ScoreHudEntity extends BaseEntity implements EventBus.Subscriber {

    private GameFontsManager fontsManager;
    private GameTexturesManager texturesManager;

    private int scoreValue = 0;

    private Text scoreText;

    public ScoreHudEntity(GameFontsManager fontsManager, GameTexturesManager texturesManager, GameResources gameResources) {
        super(gameResources);
        this.fontsManager = fontsManager;
        this.texturesManager = texturesManager;
    }

    @Override
    public void onCreateScene() {
        EventBus.get().subscribe(GameEvent.INCREMENT_SCORE, this).subscribe(GameEvent.DECREMENT_SCORE, this);

        scoreText = new Text(20, 20, fontsManager.getFont(FontId.SCORE_TICKER_FONT), "Score: - - - - -", "Score: XXXXX".length(), vertexBufferObjectManager);

        //set score background
        Sprite scorebackground = new Sprite(0, 0, texturesManager.getTextureRegion(TextureId.SCORE_BACKGROUND), vertexBufferObjectManager);
        scorebackground.setHeight(scoreText.getHeight() + 40f);
        scorebackground.setWidth(scoreText.getWidth() + 40f);
        scorebackground.setY(ScreenUtils.getSreenSize().height-scorebackground.getHeight());
        scoreText.setY(scorebackground.getY()+20);
        scoreText.setColor(1,0,0);

        scene.attachChild(scorebackground);
        scene.attachChild(scoreText);
    }

    @Override
    public void onDestroy() {
        EventBus.get().unSubscribe(GameEvent.INCREMENT_SCORE, this).unSubscribe(GameEvent.DECREMENT_SCORE, this);
    }

    public int getScore() {
        return scoreValue;
    }

    @Override
    public void onEvent(GameEvent event, EventPayload payload) {
        switch (event) {
            case DECREMENT_SCORE:
                decrementScore((DecrementScoreEventPayload) payload);
                break;
            case INCREMENT_SCORE:
                incrementScore((IncrementScoreEventPayload) payload);
                break;
        }
    }

    private void incrementScore(IncrementScoreEventPayload payload) {
        scoreValue += payload.incrementAmmount;
        updateScoreText();

        EventBus.get().sendEvent(GameEvent.SCORE_CHANGED, new ScoreChangeEventPayload(scoreValue));
    }

    private void decrementScore(DecrementScoreEventPayload payload) {
        scoreValue -= payload.decrementAmmount;
        updateScoreText();
    }

    private void updateScoreText() {
        scoreText.setText("Score: " + scoreValue);
    }
}