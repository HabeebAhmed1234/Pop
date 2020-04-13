package com.stupidfungames.pop;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.stupidfungames.pop.androidui.GameMenuButton;
import com.stupidfungames.pop.auth.GooglePlayServicesAuthManager;
import com.stupidfungames.pop.savegame.SaveGameManager;

public class MainMenuActivity extends AppCompatActivity implements HostActivity {

    public static Intent newIntent(Context context) {
        return new Intent(context, MainMenuActivity.class);
    }

    private GooglePlayServicesAuthManager authManager;
    private SaveGameManager saveGameManager;

    private GoogleAuthPopupView popupView;
    private PlayerProfileView playerProfileView;
    private LoadGameBtnView loadGameBtnView;

    private ValueAnimator logoAnimator;

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

        authManager = GooglePlayServicesAuthManager.get( this, this);
        saveGameManager = SaveGameManager.get(this, authManager, this);

        popupView = new GoogleAuthPopupView(authManager, findViewById(R.id.root_view));
        playerProfileView = new PlayerProfileView(
            authManager, (ViewGroup) findViewById(R.id.player_profile_view));
        loadGameBtnView = new LoadGameBtnView(
            authManager,
            saveGameManager,
            (GameMenuButton) findViewById(R.id.load_game_btn),
            this);

        animateLogo();
        authManager.maybeLoginOnAppStart();
    }

    private void startNewGame() {
        startActivity(GameActivityGame.newIntent(this));
        this.finish();
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
        authManager.onActivityResult(requestCode, resultCode, data);
    }
}