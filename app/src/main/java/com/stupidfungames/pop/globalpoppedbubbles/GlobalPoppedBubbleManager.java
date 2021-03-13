package com.stupidfungames.pop.globalpoppedbubbles;

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
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class GlobalPoppedBubbleManager {

  private static GlobalPoppedBubbleManager sGlobalPoppedBubbleManager;

  public static GlobalPoppedBubbleManager getInstance() {
    if (sGlobalPoppedBubbleManager == null) {
      sGlobalPoppedBubbleManager = new GlobalPoppedBubbleManager();
    }
    return sGlobalPoppedBubbleManager;
  }

  private GlobalPoppedBubbleManager() {
  }

  public void populateTotalPoppedBubblesTextView(final TextView textView) {
    Futures.addCallback(getTotalNumberOfGlobalBubblesPopped(), new FutureCallback<Long>() {
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

  public ListenableFuture<Long> getTotalNumberOfGlobalBubblesPopped() {
    final SettableFuture<Long> globalBubblesPopped = SettableFuture.create();
    final FirebaseFirestore db = FirebaseFirestore.getInstance();
    db.collection("popped_bubbles_count").document("lDaQ4fNcBXRBszPZ2kTX").get()
        .addOnSuccessListener(
            new OnSuccessListener<DocumentSnapshot>() {
              @Override
              public void onSuccess(DocumentSnapshot documentSnapshot) {
                globalBubblesPopped.set((long) documentSnapshot.get("popped_bubbles_count"));
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

  public void incrementGlobalPoppedBubbles(long poppedAmount) {
    final FirebaseFirestore db = FirebaseFirestore.getInstance();
    db.collection("popped_bubbles_count").document("lDaQ4fNcBXRBszPZ2kTX")
        .update("popped_bubbles_count",
            FieldValue.increment(poppedAmount));
  }
}
