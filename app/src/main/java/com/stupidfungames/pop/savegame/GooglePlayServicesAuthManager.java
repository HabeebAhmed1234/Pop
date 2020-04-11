package com.stupidfungames.pop.savegame;

import static com.google.android.gms.common.api.CommonStatusCodes.SIGN_IN_REQUIRED;
import static com.google.android.gms.games.Games.SCOPE_GAMES_LITE;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import com.stupidfungames.pop.R;
import java.util.Arrays;

public class GooglePlayServicesAuthManager {

  private static final int RC_SIGN_IN = 1;
  private static final Scope[] REQUIRED_PERMISSIONS =
      new Scope[] {Drive.SCOPE_APPFOLDER, SCOPE_GAMES_LITE};
  GoogleSignInOptions  signInOptions =
      new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN)
          .requestScopes(
              REQUIRED_PERMISSIONS[0],
              Arrays.copyOfRange(REQUIRED_PERMISSIONS, 1, REQUIRED_PERMISSIONS.length))
          .build();

  private Activity activity;
  private SettableFuture<GoogleSignInAccount> signInAccountFuture;

  public GooglePlayServicesAuthManager(Activity activity) {
    this.activity = activity;
  }

  public ListenableFuture<GoogleSignInAccount> getAccount() {
    if (signInAccountFuture != null) {
      return signInAccountFuture;
    }

    GoogleSignInAccount account =  GoogleSignIn.getLastSignedInAccount(activity);
    signInAccountFuture = SettableFuture.create();
    if (GoogleSignIn.hasPermissions(account, signInOptions.getScopeArray())) {
      // Already signed in.
      // The signed in account is stored in the 'account' variable.
      if (account != null) {
        signInAccountFuture.set(account);
      } else {
        signInAccountFuture.setException(new IllegalStateException("Null account?"));
      }
    } else {
      // Haven't been signed-in before. Try the silent sign-in first.
      GoogleSignInClient signInClient = getSignInClient();
      signInClient
          .silentSignIn()
          .addOnCompleteListener(
              activity,
              new OnCompleteListener<GoogleSignInAccount>() {
                @Override
                public void onComplete(@NonNull Task<GoogleSignInAccount> task) {
                  GoogleSignInAccount signedInAccount = task.getResult();
                  if (task.isSuccessful() && signedInAccount != null) {
                    signInAccountFuture.set(task.getResult());
                  } else {
                    performExplicitSignIn();
                  }
                }
              });
    }

    return signInAccountFuture;
  }

  public Task<Void> logout() {
    GoogleSignInClient signInClient = getSignInClient();
    return signInClient.signOut();
  }

  public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    if (requestCode == RC_SIGN_IN) {
      GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
      if (result != null && result.isSuccess() && result.getSignInAccount() != null) {
        // The signed in account is stored in the result.
        signInAccountFuture.set(result.getSignInAccount());
      } else {
        String message = result.getStatus().getStatusMessage();
        if (message == null || message.isEmpty()) {
          message = activity.getString(R.string.signin_other_error);
        }
        // TODO make this our own UI
        new AlertDialog.Builder(activity).setMessage(message)
            .setNeutralButton(android.R.string.ok, null).show();
      }
    }
  }

  /**
   * Performs a sign in by the user via UI
   */
  private void performExplicitSignIn() {
    GoogleSignInClient signInClient = getSignInClient();
    Intent intent = signInClient.getSignInIntent();
    activity.startActivityForResult(intent, RC_SIGN_IN);
  }

  private GoogleSignInClient getSignInClient() {
    return GoogleSignIn.getClient(activity, signInOptions);
  }
}
