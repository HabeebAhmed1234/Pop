package com.wack.pop2;

import android.util.Log;

import com.wack.pop2.fixturedefdata.BaseEntityUserData;

import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.collision.Manifold;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.contacts.Contact;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import androidx.annotation.Nullable;

/**
 * Enables other entities to listen to contacts that happen in the physics world involving one or two
 * bodies with extensions of {@link BaseEntityUserData}s
 */
public class GamePhysicsContactsEntity extends BaseEntity implements ContactListener {

    public interface GameContactListener {
        void onBeginContact(Fixture fixture1, Fixture fixture2);
        void onEndContact(Fixture fixture1, Fixture fixture2);
    }

    private Map<Set<Class<? extends BaseEntityUserData>>, Set<GameContactListener>> gameContactListenerMap = new HashMap<>();

    public GamePhysicsContactsEntity(GameResources gameResources) {
        super(gameResources);
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
    public void beginContact(Contact contact) {
        @Nullable Set<GameContactListener> listeners = getListenersFromContact(contact);
        if (listeners == null) {
            return;
        }
        notifyBeginContact(listeners, contact.m_fixtureA, contact.m_fixtureB);
    }

    @Override
    public void endContact(Contact contact) {
        @Nullable Set<GameContactListener> listeners = getListenersFromContact(contact);
        if (listeners == null) {
            return;
        }
        notifyEndContact(listeners, contact.m_fixtureA, contact.m_fixtureB);
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {}

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {}

    public void addContactListener(
            Class<? extends BaseEntityUserData> entityType1,
            Class<? extends BaseEntityUserData> entityType2,
            GameContactListener contactListener) {
        Set<Class<? extends BaseEntityUserData>> key = createKeyFromTypes(entityType1, entityType2);

        if (!gameContactListenerMap.containsKey(key)) {
            gameContactListenerMap.put(key, new HashSet<GameContactListener>());
        }

        gameContactListenerMap.get(key).add(contactListener);
    }

    public void removeContactListener(
            Class<? extends BaseEntityUserData> entityType1,
            Class<? extends BaseEntityUserData> entityType2,
            GameContactListener contactListener) {
        Set<Class<? extends BaseEntityUserData>> key = createKeyFromTypes(entityType1, entityType2);
        if (gameContactListenerMap.containsKey(key)) {
            gameContactListenerMap.get(key).remove(contactListener);
        } else {
            throw new IllegalStateException("gameContactListenerMap does not contain a listener for these types");
        }
    }

    private void notifyBeginContact(Set<GameContactListener> listeners, Fixture a, Fixture b) {
        for (GameContactListener listener : listeners) {
            listener.onBeginContact(a, b);
        }
    }

    private void notifyEndContact(Set<GameContactListener> listeners, Fixture a, Fixture b) {
        for (GameContactListener listener : listeners) {
            listener.onEndContact(a, b);
        }
    }

    /**
     * Returns the listeners (if any) for the supplied contact in the physics world
     * @param contact
     * @return
     */
    @Nullable
    private Set<GameContactListener> getListenersFromContact(Contact contact) {
        if (contact.m_fixtureA.m_userData == null || contact.m_fixtureB.m_userData == null) {
            Log.w("GamePhysicsContacts", "Collision detected between one or more null BaseUserData fixtures");
            return null;
        }
        Set<Class<? extends BaseEntityUserData>> key = createKeyFromTypes(
                (Class<BaseEntityUserData>) contact.m_fixtureA.m_userData.getClass(),
                (Class<BaseEntityUserData>) contact.m_fixtureB.m_userData.getClass());

        return gameContactListenerMap.get(key);
    }

    private Set<Class<? extends BaseEntityUserData>> createKeyFromTypes(
            Class<? extends BaseEntityUserData> type1,
            Class<? extends BaseEntityUserData> type2) {
        if (type1 == null || type2 == null) {
            throw new IllegalArgumentException(
                    "You cannot create a key with one or more null BaseEntityUserData types");
        }
        Set<Class<? extends BaseEntityUserData>> key = new HashSet<>();
        key.add(type1);
        key.add(type2);
        return key;
    }
}
