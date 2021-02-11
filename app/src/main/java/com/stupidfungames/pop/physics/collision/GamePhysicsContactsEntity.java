package com.stupidfungames.pop.physics.collision;

import android.util.Log;
import androidx.annotation.Nullable;
import com.stupidfungames.pop.BaseEntity;
import com.stupidfungames.pop.binder.BinderEnity;
import com.stupidfungames.pop.fixturedefdata.BaseEntityUserData;
import com.stupidfungames.pop.physics.util.Vec2Pool;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.collision.Manifold;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.contacts.Contact;

/**
 * Enables other entities to listen to contacts that happen in the physics world involving one or
 * two bodies with extensions of {@link BaseEntityUserData}s.
 *
 * Ensures that callbacks on contact get called after a physics step has been completed
 */
public class GamePhysicsContactsEntity extends BaseEntity implements ContactListener {

  public interface GameContactListener {

    void onBeginContact(Fixture fixture1, Fixture fixture2);

    void onEndContact(Fixture fixture1, Fixture fixture2);
  }

  private Map<Vec2, Set<GameContactListener>> gameContactListenerMap = new HashMap<>();

  public GamePhysicsContactsEntity(BinderEnity parent) {
    super(parent);
  }

  @Override
  public void onCreateScene() {
    physicsWorld.setContactListener(this);
  }

  @Override
  public void onDestroy() {
    physicsWorld.setContactListener(null);
  }

  @Override
  public void beginContact(final Contact contact) {
    if (isContactListenedTo(contact)) {
      @Nullable final Set<GameContactListener> listeners = getListenersFromContact(contact);
      if (listeners == null) {
        return;
      }
      physicsWorld.postRunnable(new Runnable() {
        @Override
        public void run() {
          notifyBeginContact(listeners, contact.m_fixtureA, contact.m_fixtureB);
        }
      });
    }
  }

  @Override
  public void endContact(final Contact contact) {
    if (isContactListenedTo(contact)) {
      @Nullable final Set<GameContactListener> listeners = getListenersFromContact(contact);
      if (listeners == null) {
        return;
      }
      physicsWorld.postRunnable(new Runnable() {
        @Override
        public void run() {
          notifyEndContact(listeners, contact.m_fixtureA, contact.m_fixtureB);
        }
      });
    }
  }

  @Override
  public void preSolve(Contact contact, Manifold oldManifold) {
  }

  @Override
  public void postSolve(Contact contact, ContactImpulse impulse) {
  }


  public void addContactListener(
      int collisionIdA,
      int collisionIdB,
      GameContactListener contactListener) {
    Vec2 key = createKeyCollisionIds(collisionIdA, collisionIdB);
    if (!gameContactListenerMap.containsKey(key)) {
      gameContactListenerMap.put(key, new HashSet<GameContactListener>());
    }

    gameContactListenerMap.get(key).add(contactListener);
  }

  public void removeContactListener(
      int collisionIdA,
      int collisionIdB,
      GameContactListener contactListener) {
    Vec2 key = createKeyCollisionIds(collisionIdA, collisionIdB);
    if (gameContactListenerMap.containsKey(key)) {
      gameContactListenerMap.get(key).remove(contactListener);
    } else {
      throw new IllegalStateException(
          "gameContactListenerMap does not contain a listener for these collisionIds");
    }
    Vec2Pool.recycle(key);
  }

  public boolean containsContactListener(
      int collisionIdA,
      int collisionIdB,
      GameContactListener contactListener) {
    Vec2 key = createKeyCollisionIds(collisionIdA, collisionIdB);
    boolean contains = gameContactListenerMap.containsKey(key) && gameContactListenerMap.get(key)
        .contains(contactListener);
    Vec2Pool.recycle(key);
    return contains;
  }

  /**
   * Returns true if the given contact is one that we are listening to. If not then discard it.
   */
  private boolean isContactListenedTo(Contact contact) {
    Vec2 key = createKeyFromContact(contact);
    if (key == null) {
      return false;
    }
    boolean isListenedTo = gameContactListenerMap.containsKey(key);
    Vec2Pool.recycle(key);
    return isListenedTo;
  }

  private void notifyBeginContact(Set<GameContactListener> listeners, Fixture a, Fixture b) {
    if (physicsWorld.isLocked()) {
      throw new IllegalStateException("Physics world is locked during custom contact callback!");
    }
    for (GameContactListener listener : listeners) {
      listener.onBeginContact(a, b);
    }
  }

  private void notifyEndContact(Set<GameContactListener> listeners, Fixture a, Fixture b) {
    if (physicsWorld.isLocked()) {
      throw new IllegalStateException("Physics world is locked during custom contact callback!");
    }
    for (GameContactListener listener : listeners) {
      listener.onEndContact(a, b);
    }
  }

  /**
   * Returns the listeners (if any) for the supplied contact in the physics world
   */
  @Nullable
  private Set<GameContactListener> getListenersFromContact(Contact contact) {
    Vec2 key = createKeyFromContact(contact);
    Set<GameContactListener> listeners = gameContactListenerMap.get(createKeyFromContact(contact));
    Vec2Pool.recycle(key);
    return listeners;
  }

  private Vec2 createKeyFromContact(Contact contact) {
    if (contact.m_fixtureA.m_userData == null || contact.m_fixtureB.m_userData == null) {
      Log.w("GamePhysicsContacts",
          "Collision detected between one or more null BaseUserData fixtures");
      return null;
    }
    return createKeyCollisionIds(
        ((BaseEntityUserData) contact.m_fixtureA.m_userData).collisionType(),
        ((BaseEntityUserData) contact.m_fixtureB.m_userData).collisionType());
  }

  private Vec2 createKeyCollisionIds(int a, int b) {
    if (a <= 0 || b <= 0) {
      throw new IllegalArgumentException(
          "You cannot create a key with one or more unset collisionIds");
    }
    return Vec2Pool.obtain(Math.min(a, b), Math.max(a, b));
  }
}
