package com.yourname.flappybird;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

public class Pipe {
    private float x;
    private int topHeight;
    private int gapSize;
    private int width;
    private int speed;
    private boolean scored;
    private int screenHeight;
    
    // Pipe variation flags
    private boolean hasTopPipe;
    private boolean hasBottomPipe;
    private int customBottomHeight; // For bottom-only pipes

    public Pipe(int screenWidth, int topHeight, int gapSize) {
        this(screenWidth, topHeight, gapSize, true, true);
    }
    
    // Constructor for pipe variations
    public Pipe(int screenWidth, int topHeight, int gapSize, boolean hasTop, boolean hasBottom) {
        this.x = screenWidth;
        this.topHeight = topHeight;
        this.gapSize = gapSize;
        this.width = 320; // 4 times larger than original (was ~80)
        this.speed = 8;
        this.scored = false;
        this.screenHeight = 1200; // Will be updated when drawn
        this.hasTopPipe = hasTop;
        this.hasBottomPipe = hasBottom;
        this.customBottomHeight = -1;
    }
    
    // Constructor for bottom-only pipes with custom height
    public Pipe(int screenWidth, int unusedTopHeight, int customBottomHeight, boolean bottomOnly) {
        this.x = screenWidth;
        this.topHeight = unusedTopHeight;
        this.gapSize = 0;
        this.width = 320;
        this.speed = 8;
        this.scored = false;
        this.screenHeight = 1200;
        this.hasTopPipe = false;
        this.hasBottomPipe = true;
        this.customBottomHeight = customBottomHeight;
    }

    public void update() {
        x -= speed;
    }

    public void draw(Canvas canvas, Paint paint) {
        screenHeight = canvas.getHeight();
        
        // Set pipe color (green like classic Flappy Bird)
        paint.setColor(Color.rgb(0, 180, 0));
        
        if (hasTopPipe) {
            // Draw top pipe
            canvas.drawRect(x, 0, x + width, topHeight, paint);
            
            // Draw pipe cap (slightly wider)
            paint.setColor(Color.rgb(0, 150, 0));
            canvas.drawRect(x - 10, topHeight - 30, x + width + 10, topHeight, paint);
        }
        
        if (hasBottomPipe) {
            int bottomPipeTop;
            if (customBottomHeight > 0) {
                // Bottom-only pipe with custom height
                bottomPipeTop = screenHeight - customBottomHeight;
            } else {
                // Standard bottom pipe
                bottomPipeTop = topHeight + gapSize;
            }
            
            // Draw bottom pipe
            paint.setColor(Color.rgb(0, 180, 0));
            canvas.drawRect(x, bottomPipeTop, x + width, screenHeight, paint);
            
            // Draw pipe cap (slightly wider)
            paint.setColor(Color.rgb(0, 150, 0));
            canvas.drawRect(x - 10, bottomPipeTop, x + width + 10, bottomPipeTop + 30, paint);
        }
        
        // Reset paint color
        paint.setColor(Color.WHITE);
    }

    public boolean collidesWith(Bird bird) {
        // Get bird bounds
        float birdLeft = bird.getX() - bird.getRadius();
        float birdRight = bird.getX() + bird.getRadius();
        float birdTop = bird.getY() - bird.getRadius();
        float birdBottom = bird.getY() + bird.getRadius();
        
        // Check if bird is within pipe's x range
        if (birdRight > x && birdLeft < x + width) {
            
            if (hasTopPipe) {
                // Check collision with top pipe
                if (birdTop < topHeight) {
                    return true;
                }
            }
            
            if (hasBottomPipe) {
                int bottomPipeTop;
                if (customBottomHeight > 0) {
                    bottomPipeTop = screenHeight - customBottomHeight;
                } else {
                    bottomPipeTop = topHeight + gapSize;
                }
                
                // Check collision with bottom pipe
                if (birdBottom > bottomPipeTop) {
                    return true;
                }
            }
        }
        
        return false;
    }

    // Getters
    public float getX() {
        return x;
    }

    public int getWidth() {
        return width;
    }

    public boolean isScored() {
        return scored;
    }

    public void setScored(boolean scored) {
        this.scored = scored;
    }
    
    public int getTopHeight() {
        return topHeight;
    }
    
    public int getGapSize() {
        return gapSize;
    }
}