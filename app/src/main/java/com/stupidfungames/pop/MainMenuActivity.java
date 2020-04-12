package com.stupidfungames.pop;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import android.view.ViewGroup;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.stupidfungames.pop.savegame.GooglePlayServicesAuthManager;
import com.stupidfungames.pop.savegame.GooglePlayServicesAuthManager.HostActivity;
import com.stupidfungames.pop.savegame.SaveGame;
import com.stupidfungames.pop.savegame.SaveGameManager;

import com.stupidfungames.pop.savegame.UpdateGameDialogActivity;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class MainMenuActivity extends AppCompatActivity implements HostActivity {

    public static Intent newIntent(Context context) {
        return new Intent(context, MainMenuActivity.class);
    }

    private ValueAnimator logoAnimator;
    private GooglePlayServicesAuthManager googlePlayServicesAuthManager;
    private PlayerProfileView playerProfileView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu_layout);

        findViewById(R.id.new_game_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNewGame();
            }
        });
        findViewById(R.id.quit_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quitGame();
            }
        });

        googlePlayServicesAuthManager =
            new GooglePlayServicesAuthManager(findViewById(R.id.root_view), this, this);
        playerProfileView = new PlayerProfileView(
            googlePlayServicesAuthManager, (ViewGroup) findViewById(R.id.player_profile_view));

        setUpLoadGameBtn();
        animateLogo();
    }

    private void startNewGame() {
        startActivity(GameActivity.newIntent(this));
        this.finish();
    }

    private void setUpLoadGameBtn() {
        ListenableFuture<SaveGame> saveGameListenableFuture = SaveGameManager.loadGame(this);
        Futures.addCallback(saveGameListenableFuture, new FutureCallback<SaveGame>() {
            @Override
            public void onSuccess(@NullableDecl final SaveGame saveGame) {
                if (saveGame != null) {
                    View btn = findViewById(R.id.load_game_btn);
                    btn.setVisibility(View.VISIBLE);
                    btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startLoadGame(saveGame);
                        }
                    });
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e("MainMenuActivity", "Error loading save game", t);
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void startLoadGame(SaveGame saveGame) {
        boolean started = SaveGameManager.startLoadedGame(saveGame, this);
        if (started) {
            this.finish();
        } else {
            showUpdateDialog();
        }
    }

    private void showUpdateDialog() {
        startActivity(UpdateGameDialogActivity.newIntent(this));
    }

    private void  quitGame() {
        this.finish();
    }

    private void animateLogo() {
        final View logo = findViewById(R.id.logo);
        logoAnimator = ObjectAnimator.ofFloat(1,0.5f, 1,1,1,1,1,1,1,1,0.7f,1);
        logoAnimator.setDuration(2000);
        logoAnimator.start();
        logoAnimator.setRepeatMode(ValueAnimator.RESTART);

        logoAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                logo.setAlpha((float) animation.getAnimatedValue());
            }
        });
        logoAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                animation.start();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        logoAnimator.end();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        googlePlayServicesAuthManager.onActivityResult(requestCode, resultCode, data);
    }
}