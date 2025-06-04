package com.yourname.flappybird;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class Pipe {
    private float x;
    private int topHeight;
    private int bottomY;
    private static final int WIDTH = 80;
    private static final int SPEED = 5;
    private boolean scored;

    public Pipe(float x, int topHeight, int gap) {
        this.x = x;
        this.topHeight = topHeight;
        this.bottomY = topHeight + gap;
        this.scored = false;
    }

    public void update() {
        x -= SPEED;
    }

    public void draw(Canvas canvas, Paint paint) {
        // Draw top pipe
        paint.setColor(Color.GREEN);
        canvas.drawRect(x, 0, x + WIDTH, topHeight, paint);
        
        // Draw bottom pipe
        canvas.drawRect(x, bottomY, x + WIDTH, canvas.getHeight(), paint);
        
        // Draw pipe borders
        paint.setColor(Color.DKGRAY);
        paint.setStrokeWidth(3);
        canvas.drawLine(x, 0, x, topHeight, paint);
        canvas.drawLine(x + WIDTH, 0, x + WIDTH, topHeight, paint);
        canvas.drawLine(x, bottomY, x, canvas.getHeight(), paint);
        canvas.drawLine(x + WIDTH, bottomY, x + WIDTH, canvas.getHeight(), paint);
        
        // Draw pipe caps
        paint.setColor(Color.DKGRAY);
        canvas.drawRect(x - 10, topHeight - 30, x + WIDTH + 10, topHeight, paint);
        canvas.drawRect(x - 10, bottomY, x + WIDTH + 10, bottomY + 30, paint);
    }

    // Getters and setters
    public float getX() { return x; }
    public int getWidth() { return WIDTH; }
    public int getTopHeight() { return topHeight; }
    public int getBottomY() { return bottomY; }
    public boolean isScored() { return scored; }
    public void setScored(boolean scored) { this.scored = scored; }
}
