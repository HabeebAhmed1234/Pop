package com.stupidfungames.pop;

import android.util.Log;
import com.stupidfungames.pop.binder.Binder;
import com.stupidfungames.pop.binder.BinderEnity;
import java.util.ArrayList;
import java.util.List;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.input.touch.TouchEvent;

public class GameSceneTouchListenerEntity extends BaseEntity implements IOnSceneTouchListener {

  public interface SceneTouchListener {

    /**
     * Returns true if the touch event was handled
     */
    boolean onSceneTouchEvent(Scene scene, TouchEvent touchEvent);
  }

  private static final String TAG = "TouchListener";

  private List<SceneTouchListener> sceneTouchlisteners = new ArrayList<>();

  public GameSceneTouchListenerEntity(BinderEnity parent) {
    super(parent);
    scene.setOnSceneTouchListener(this);
  }

  @Override
  protected void createBindings(Binder binder) {

  }

  public boolean hasSceneTouchListener(SceneTouchListener listener) {
    boolean hasSceneTouchListener = false;
    hasSceneTouchListener = sceneTouchlisteners.contains(listener);
    return hasSceneTouchListener;
  }

  public GameSceneTouchListenerEntity addSceneTouchListener(SceneTouchListener listener) {
    if (sceneTouchlisteners.contains(listener)) {
      Log.w(TAG, "SceneTouchListener has already been added to the GameSceneTouchListenerEntity");
    }
    sceneTouchlisteners.add(listener);
    return this;
  }

  public void removeSceneTouchListener(SceneTouchListener listener) {
    if (!sceneTouchlisteners.contains(listener)) {
      Log.w(TAG, "No listeners for " + listener);
    }
    sceneTouchlisteners.remove(listener);
  }

  @Override
  public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
    boolean handled = false;
    for (int i = 0; i < sceneTouchlisteners.size(); i++) {
      if (i < sceneTouchlisteners.size()) {
        if (sceneTouchlisteners.get(i).onSceneTouchEvent(pScene, pSceneTouchEvent)) {
          handled = true;
        }
      }
    }
    return handled;
  }
}
