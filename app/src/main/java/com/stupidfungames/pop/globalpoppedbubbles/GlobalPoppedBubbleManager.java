package com.stupidfungames.pop.globalpoppedbubbles;

import android.content.Context;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.stupidfungames.pop.gamesettings.GamePreferencesManager;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class GlobalPoppedBubbleManager {

  private static final String GLOBAL_POPPED_BUBBLES_CACHED_KEY = "global_popped_bubbles";
  private static GlobalPoppedBubbleManager sGlobalPoppedBubbleManager;

  private long globalBubblesPoppedCached = -1;

  public static GlobalPoppedBubbleManager getInstance() {
    if (sGlobalPoppedBubbleManager == null) {
      sGlobalPoppedBubbleManager = new GlobalPoppedBubbleManager();
    }
    return sGlobalPoppedBubbleManager;
  }

  private GlobalPoppedBubbleManager() {
  }

  public void populateTotalPoppedBubblesTextView(final TextView textView) {
    Futures.addCallback(getTotalNumberOfGlobalBubblesPopped(textView.getContext()),
        new FutureCallback<Long>() {
          @Override
          public void onSuccess(@NullableDecl Long result) {
            if (result != null) {
              textView.setText(Long.toString(result));
            } else {
              textView.setText("");
            }
          }

          @Override
          public void onFailure(Throwable t) {
            textView.setText("");
          }
        }, ContextCompat.getMainExecutor(textView.getContext()));
  }

  public long getGlobalBubblesPoppedCached(Context context) {
    if (globalBubblesPoppedCached < 0) {
      globalBubblesPoppedCached = GamePreferencesManager
          .getLong(context, GLOBAL_POPPED_BUBBLES_CACHED_KEY);
    }
    return globalBubblesPoppedCached;
  }

  public ListenableFuture<Long> getTotalNumberOfGlobalBubblesPopped(final Context context) {
    final SettableFuture<Long> globalBubblesPopped = SettableFuture.create();
    final FirebaseFirestore db = FirebaseFirestore.getInstance();
    db.collection("popped_bubbles_count").document("lDaQ4fNcBXRBszPZ2kTX").get()
        .addOnSuccessListener(
            new OnSuccessListener<DocumentSnapshot>() {
              @Override
              public void onSuccess(DocumentSnapshot documentSnapshot) {
                long globalPoppedBubbles = (long) documentSnapshot.get("popped_bubbles_count");
                setGlobalPoppedBubblesCached(context, globalPoppedBubbles);
                globalBubblesPopped.set(globalPoppedBubbles);
              }
            })
        .addOnFailureListener(new OnFailureListener() {
          @Override
          public void onFailure(@NonNull Exception e) {
            globalBubblesPopped.setException(e);
          }
        });
    return globalBubblesPopped;
  }

  public void incrementGlobalPoppedBubbles(final Context context, final long poppedAmount) {
    // Update the remote value
    final FirebaseFirestore db = FirebaseFirestore.getInstance();
    db.collection("popped_bubbles_count").document("lDaQ4fNcBXRBszPZ2kTX")
        .update("popped_bubbles_count",
            FieldValue.increment(poppedAmount));

    // Read the new value and update local cache
    getTotalNumberOfGlobalBubblesPopped(context);
  }

  private void setGlobalPoppedBubblesCached(Context context, long globalBubblesPopped) {
    globalBubblesPoppedCached = globalBubblesPopped;
    GamePreferencesManager.set(context, GLOBAL_POPPED_BUBBLES_CACHED_KEY, globalBubblesPopped);
  }
}
