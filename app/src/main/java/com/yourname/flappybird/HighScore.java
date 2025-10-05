package com.yourname.flappybird;

public class HighScore {
    private String initials;
    private int score;

    public HighScore(String initials, int score) {
        this.initials = initials;
        this.score = score;
    }

    public String getInitials() {
        return initials;
    }

    public int getScore() {
        return score;
    }

    // For SharedPreferences serialization
    public String serialize() {
        return initials + ":" + score;
    }

    public static HighScore deserialize(String data) {
        String[] parts = data.split(":");
        if (parts.length == 2) {
            return new HighScore(parts[0], Integer.parseInt(parts[1]));
        }
        return new HighScore("AAA", 0);
    }
}
