package com.yourname.flappybird;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class InitialEntryActivity extends AppCompatActivity {
    private TextView initialDisplay;
    private String currentInitials = "A";
    private int currentPosition = 0;
    private int score;
    private int rank;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Get score and rank from intent
        score = getIntent().getIntExtra("score", 0);
        rank = getIntent().getIntExtra("rank", 1);
        
        // Create layout programmatically
        createLayout();
    }

    private void createLayout() {
        setContentView(R.layout.activity_main); // We'll modify this
        
        // For now, let's create a simple programmatic layout
        // We'll improve this in the next step
        
        setTitle("High Score #" + rank + "!");
        
        // This is a simplified version - we'll create a proper layout next
        finish();
        
        // Return to game with initials
        Intent intent = new Intent();
        intent.putExtra("initials", "AAA"); // Default for now
        setResult(RESULT_OK, intent);
        finish();
    }
}
