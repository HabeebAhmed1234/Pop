package com.wack.pop2;

import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;

/**
 * Entity that contains
 */
public class HudEntity extends BaseEntity {

    private Text scoreText;

    HudEntity(GameResources gameResources) {
        super(gameResources);
    }

    private void createScoreBoard() {
        scoreText = new Text(20, 20, this.mFont, "Score: - - - - -", "Score: XXXXX".length(), this.getVertexBufferObjectManager());

        //set score background
        Sprite scorebackground = new Sprite(0, 0, mScorebackground, this.getVertexBufferObjectManager());
        scorebackground.setHeight((float) (scoreText.getHeight()+40));
        scorebackground.setWidth((float) (scoreText.getWidth()+40));
        scorebackground.setY(CAMERA_HEIGHT-scorebackground.getHeight());
        scoreText.setY(scorebackground.getY()+20);
        scoreText.setColor(1,0,0);

        scene.attachChild(scorebackground);
        scene.attachChild(scoreText);
    }

    @Override
    public void onCreateScene() {

    }
}