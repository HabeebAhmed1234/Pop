package com.stupidfungames.pop.googleplaysave;

import android.content.Context;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.games.AnnotatedData;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.SnapshotsClient;
import com.google.android.gms.games.SnapshotsClient.DataOrConflict;
import com.google.android.gms.games.snapshot.Snapshot;
import com.google.android.gms.games.snapshot.SnapshotMetadataBuffer;
import com.google.android.gms.games.snapshot.SnapshotMetadataChange;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import com.stupidfungames.pop.HostActivity;
import com.stupidfungames.pop.savegame.NoSavesException;
import java.io.IOException;
import java.io.Serializable;
import org.apache.commons.lang3.SerializationUtils;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

/**
 * Manages the saving and loading of some object of type T in google play services with a given
 * name (Saveable.name). This name is used to uniquely identify the storage of this object instance
 * in Google play services save game api.
 *
 * @param <T> the type of the object we are saving
 */
public class GooglePlayServicesSaveManager<T extends Serializable> {

  private static final String TAG = "GooglePlayServicesSave";

  private final Context context;
  private final HostActivity hostActivity;

  public GooglePlayServicesSaveManager(HostActivity hostActivity) {
    this.hostActivity = hostActivity;
    this.context = hostActivity.getContext();
  }

  public void save(final String name, final T objectToSave) {
    final SnapshotsClient snapshotsClient =
        Games.getSnapshotsClient(context, GoogleSignIn.getLastSignedInAccount(context));

    // In the case of a conflict, the most recently modified version of this snapshot will be used.
    int conflictResolutionPolicy = SnapshotsClient.RESOLUTION_POLICY_MOST_RECENTLY_MODIFIED;

    // Open the saved game using its name.
    snapshotsClient.open(name, true, conflictResolutionPolicy)
        .addOnFailureListener(new OnFailureListener() {
          @Override
          public void onFailure(@NonNull Exception e) {
            Log.e(TAG, "Error while opening Snapshot.", e);
          }
        }).continueWith(new Continuation<SnapshotsClient.DataOrConflict<Snapshot>, byte[]>() {
          @Override
          public byte[] then(@NonNull Task<SnapshotsClient.DataOrConflict<Snapshot>> task) throws Exception {
            Snapshot snapshot = task.getResult().getData();

            // Opening the snapshot was a success and any conflicts have been resolved.
            snapshot.getSnapshotContents().writeBytes(saveToByteArray(objectToSave));
            snapshotsClient.commitAndClose(
                snapshot,
                new SnapshotMetadataChange.Builder().fromMetadata(snapshot.getMetadata()).build());

            return null;
          }
        });
  }

  public void delete(String name) {
    final SnapshotsClient snapshotsClient =
        Games.getSnapshotsClient(context, GoogleSignIn.getLastSignedInAccount(context));

    // In the case of a conflict, the most recently modified version of this snapshot will be used.
    int conflictResolutionPolicy = SnapshotsClient.RESOLUTION_POLICY_MOST_RECENTLY_MODIFIED;

    // Open the saved game using its name.
    snapshotsClient.open(name, true, conflictResolutionPolicy)
        .addOnFailureListener(new OnFailureListener() {
          @Override
          public void onFailure(@NonNull Exception e) {
            Log.e(TAG, "Error while opening Snapshot.", e);
          }
        }).continueWith(new Continuation<SnapshotsClient.DataOrConflict<Snapshot>, byte[]>() {
      @Override
      public byte[] then(@NonNull Task<SnapshotsClient.DataOrConflict<Snapshot>> task) throws Exception {
        Snapshot snapshot = task.getResult().getData();
        snapshotsClient.delete(snapshot.getMetadata());
        return null;
      }
    });

  }

  public ListenableFuture<T> load(final String name, final GoogleSignInAccount account) {
    SnapshotsClient snapshotsClient = Games.getSnapshotsClient(context, account);
    final SettableFuture<T> future = SettableFuture.create();
    snapshotsClient.load(true).addOnCompleteListener(
        new OnCompleteListener<AnnotatedData<SnapshotMetadataBuffer>>() {
          @Override
          public void onComplete(@NonNull Task<AnnotatedData<SnapshotMetadataBuffer>> task) {
            if (task.isSuccessful()) {
              AnnotatedData<SnapshotMetadataBuffer> saves = task.getResult();
              SnapshotMetadataBuffer savesBuffer = saves.get();
              if (savesBuffer.getCount() > 0) {
                // there is save data in Google
                Futures.addCallback(loadSnapshot(name, account),
                    new FutureCallback<T>() {
                      @Override
                      public void onSuccess(@NullableDecl T result) {
                        future.set(result);
                      }

                      @Override
                      public void onFailure(Throwable t) {
                        future.setException(t);
                      }
                    }, ContextCompat.getMainExecutor(context));
              } else {
                future.setException(new NoSavesException());
              }
            }
          }
        });
    return future;
  }

  private ListenableFuture<T> loadSnapshot(final String snapshotName, final GoogleSignInAccount account) {
    // In the case of a conflict, the most recently modified version of this snapshot will be used.
    int conflictResolutionPolicy = SnapshotsClient.RESOLUTION_POLICY_MOST_RECENTLY_MODIFIED;

    // Get the SnapshotsClient from the signed in account.
    SnapshotsClient snapshotsClient = Games.getSnapshotsClient(context, account);

    final SettableFuture<T> future = SettableFuture.create();
    // Open the saved game using its name.
    snapshotsClient.open(snapshotName, false, conflictResolutionPolicy)
        .addOnFailureListener(new OnFailureListener() {
          @Override
          public void onFailure(@NonNull Exception e) {
            Log.e(TAG, "Error while opening Snapshot.", e);
            future.setException(e);
          }
        }).continueWith(new Continuation<DataOrConflict<Snapshot>, byte[]>() {
          @Override
          public byte[] then(@NonNull Task<SnapshotsClient.DataOrConflict<Snapshot>> task) throws Exception {
            Snapshot snapshot = task.getResult().getData();
            // Opening the snapshot was a success and any conflicts have been resolved.
            try {
              // Extract the raw data from the snapshot.
              byte[] saveData = snapshot.getSnapshotContents().readFully();
              future.set(byteArrayToSave(saveData));
              return saveData;
            } catch (IOException e) {
              Log.e(TAG, "Error while reading Snapshot.", e);
              future.setException(e);
            }
            return null;
          }
        }).addOnCompleteListener(new OnCompleteListener<byte[]>() {
          @Override
          public void onComplete(@NonNull Task<byte[]> task) {
            if (!future.isDone()) {
              future.setException(
                  new IllegalStateException("Something went wrong while loading the save"));
            }
          }
        });
    return future;
  }

  private T byteArrayToSave(byte[] bytes) {
    return SerializationUtils.deserialize(bytes);
  }

  private byte[] saveToByteArray(T save) {
    return SerializationUtils.serialize(save);
  }
}
