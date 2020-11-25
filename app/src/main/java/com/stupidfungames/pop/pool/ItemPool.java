package com.stupidfungames.pop.pool;

import com.stupidfungames.pop.BaseEntity;
import com.stupidfungames.pop.binder.BinderEnity;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * A pool of items.
 */
public abstract class ItemPool<T, P> extends BaseEntity {

  public interface ItemInitializer<I, X> {

    /**
     * Called to create a new instance of the item
     */
    I createNew(X params);

    /**
     * Called to update either a new instance or a recycled instance
     */
    void update(I item, X params);

    /**
     * Called to recycle the given item
     * @param item
     */
    void onRecycle(I item);

    /**
     * Called to destroy the item instance. Use this to clean up references
     */
    void destroy(I item);
  }

  private final Queue<T> items = new ConcurrentLinkedQueue<>();
  private ItemInitializer initializer;

  public ItemPool(BinderEnity parent) {
    super(parent);
  }

  public T get(P params) {
    ItemInitializer initializer = getInitializerInternal();
    T item = items.poll();
    if (item == null) {
      item = (T) initializer.createNew(params);
    }
    initializer.update(item, params);
    return item;
  }


  public void recycle(T item) {
    getInitializerInternal().onRecycle(item);
    items.add(item);
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    for (T item : items) {
      getInitializerInternal().destroy(item);
    }
    items.clear();
  }

  private ItemInitializer getInitializerInternal() {
    if (initializer == null) {
      initializer = getInitializer();
    }
    return initializer;
  }


  protected abstract ItemInitializer getInitializer();
}
