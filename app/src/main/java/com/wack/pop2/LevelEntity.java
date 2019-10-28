package com.wack.pop2;

import com.wack.pop2.physics.PhysicsFactory;

import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.background.Background;
import org.andengine.util.color.AndengineColor;
import org.jbox2d.dynamics.BodyType;

import static com.wack.pop2.GameFixtureDefs.WALL_FIXTURE_DEF;

/**
 * This class creates the level with all of its physics entities. This includes the walls and
 * other obstacles
 */
public class LevelEntity extends BaseEntity {

    private final int levelWidthPx;
    private final int levelHeightPx;

    public LevelEntity(GameResources gameResources) {
        super(gameResources);
        ScreenUtils.ScreenSize size = ScreenUtils.getSreenSize();
        this.levelWidthPx = size.width;
        this.levelHeightPx = size.height;
    }

    @Override
    public void onCreateScene() {
        final Rectangle left = new Rectangle(0, 0, 2, levelHeightPx, vertexBufferObjectManager);
        final Rectangle right = new Rectangle(levelWidthPx - 2, 0, 2, levelHeightPx, vertexBufferObjectManager);
        left.setAlpha(0);
        right.setAlpha(0);

        PhysicsFactory.createBoxBody(physicsWorld, left, BodyType.STATIC, WALL_FIXTURE_DEF);
        PhysicsFactory.createBoxBody(physicsWorld, right, BodyType.STATIC, WALL_FIXTURE_DEF);

        scene.attachChild(left);
        scene.attachChild(right);

        // set background color
        scene.setBackground(new Background(AndengineColor.WHITE));
    }
}
