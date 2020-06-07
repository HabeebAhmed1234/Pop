package com.stupidfungames.pop.utils;

import java9.util.concurrent.CompletableFuture;
import java9.util.function.BiFunction;
import java9.util.function.Function;

public class CompletableFutureUtil {

  static <T> CompletableFuture<T> withFallback(
      CompletableFuture<T> future,
      Function<Throwable, ? extends CompletableFuture<T>> fallback) {
    return future.handle(new BiFunction<T, Throwable, T>() {
      @Override
      public T apply(T t, Throwable throwable) {
        return throwable;
      }
    }).thenCompose(error -> error!=null? fallback.apply(error): future);
  }
}
