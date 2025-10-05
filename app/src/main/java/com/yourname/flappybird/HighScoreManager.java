package com.yourname.flappybird;

import android.content.Context;
import android.content.SharedPreferences;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class HighScoreManager {
    private static final String PREFS_NAME = "FlappyBirdHighScores";
    private static final String HIGH_SCORES_KEY = "high_scores";
    private static final int MAX_HIGH_SCORES = 5;
    
    private SharedPreferences sharedPreferences;
    private List<HighScore> highScores;

    public HighScoreManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        loadHighScores();
    }

    private void loadHighScores() {
        highScores = new ArrayList<>();
        
        // Load saved high scores
        for (int i = 0; i < MAX_HIGH_SCORES; i++) {
            String scoreData = sharedPreferences.getString(HIGH_SCORES_KEY + "_" + i, null);
            if (scoreData != null) {
                highScores.add(HighScore.deserialize(scoreData));
            }
        }
        
        // Fill with default scores if empty
        while (highScores.size() < MAX_HIGH_SCORES) {
            highScores.add(new HighScore("AAA", 0));
        }
    }

    public boolean isHighScore(int score) {
        return score > highScores.get(MAX_HIGH_SCORES - 1).getScore();
    }

    public int getHighScoreRank(int score) {
        for (int i = 0; i < highScores.size(); i++) {
            if (score > highScores.get(i).getScore()) {
                return i + 1; // Return 1-based rank
            }
        }
        return -1; // Not a high score
    }

    public void addHighScore(String initials, int score) {
        highScores.add(new HighScore(initials.toUpperCase(), score));
        
        // Sort by score (descending)
        Collections.sort(highScores, new Comparator<HighScore>() {
            @Override
            public int compare(HighScore a, HighScore b) {
                return Integer.compare(b.getScore(), a.getScore());
            }
        });
        
        // Keep only top 5
        if (highScores.size() > MAX_HIGH_SCORES) {
            highScores = highScores.subList(0, MAX_HIGH_SCORES);
        }
        
        saveHighScores();
    }

    private void saveHighScores() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        
        for (int i = 0; i < highScores.size(); i++) {
            editor.putString(HIGH_SCORES_KEY + "_" + i, highScores.get(i).serialize());
        }
        
        editor.apply();
    }

    public List<HighScore> getHighScores() {
        return new ArrayList<>(highScores);
    }

    public int getTopScore() {
        return highScores.isEmpty() ? 0 : highScores.get(0).getScore();
    }
}
