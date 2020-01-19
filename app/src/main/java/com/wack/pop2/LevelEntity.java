package com.wack.pop2;

import com.wack.pop2.collision.CollisionFilters;
import com.wack.pop2.fixturedefdata.LevelWallEntityUserData;
import com.wack.pop2.physics.PhysicsFactory;

import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.background.Background;
import org.andengine.util.color.AndengineColor;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;

import static com.wack.pop2.GameFixtureDefs.WALL_FIXTURE_DEF;

/**
 * This class creates the level with all of its physics entities. This includes the walls and
 * other obstacles
 */
public class LevelEntity extends BaseEntity {

    public LevelEntity(GameResources gameResources) {
        super(gameResources);
    }

    @Override
    public void onCreateScene() {
        final Rectangle left = new Rectangle(0, 0, 2, levelHeight, vertexBufferObjectManager);
        final Rectangle right = new Rectangle(levelWidth - 2, 0, 2, levelHeight, vertexBufferObjectManager);
        //final Rectangle bottom = new Rectangle(0, levelHeight - 2, levelWidth, 2, vertexBufferObjectManager);
        left.setAlpha(0);
        right.setAlpha(0);

        FixtureDef fixtureDef = WALL_FIXTURE_DEF;
        fixtureDef.setFilter(CollisionFilters.WALL_FILTER);
        fixtureDef.setUserData(new LevelWallEntityUserData());

        PhysicsFactory.createBoxBody(physicsWorld, left, BodyType.STATIC, fixtureDef);
        PhysicsFactory.createBoxBody(physicsWorld, right, BodyType.STATIC, fixtureDef);
        //PhysicsFactory.createBoxBody(physicsWorld, bottom, BodyType.STATIC, fixtureDef);

        scene.attachChild(left);
        scene.attachChild(right);
        //scene.attachChild(bottom);

        // set background color
        scene.setBackground(new Background(AndengineColor.BLACK));
    }
}
