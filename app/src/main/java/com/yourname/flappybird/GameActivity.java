package com.yourname.flappybird;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

public class GameActivity extends AppCompatActivity {

    private GameView gameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create the game view
        gameView = new GameView(this);
        setContentView(gameView);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (gameView != null) {
            gameView.pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (gameView != null) {
            gameView.resume();
        }
    }
}
