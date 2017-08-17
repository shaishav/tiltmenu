package com.siddhpuria.shaishav.tilt;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * ExperimentView.java
 * This is the actual canvas on which the interaction is drawn.
 */

public class ExperimentView extends View {

    public interface HitObserver {

        void notifyHit(int menuIndex);

    }

    private Pointer pointer;
    public MenuZone menuZone;
    private String taskLevelString = "Task:";
    private String taskString = "";
    private ExperimentConfig experimentConfig;
    private ArrayList<HitObserver> hitObservers;

    public ExperimentView(Context context, ExperimentConfig config) {
        super(context);

        hitObservers = new ArrayList<>();

        pointer = new Pointer();
        this.experimentConfig = config;

        if (config.getNumZones() == 2) {
            menuZone = new MenuZoneTwo();
            String[] options = {"a","b"};
            menuZone.setOptions(options);
        } else if (config.getNumZones() == 4) {
            menuZone = new MenuZoneFour();
            String[] options = {"a","b", "c", "d"};
            menuZone.setOptions(options);
        }

        Toast.makeText(context, "Tap anywhere to reset and recalibrate.", Toast.LENGTH_LONG).show();

    }

    public void registerForHits(HitObserver obs) {
        hitObservers.add(obs);
    }

    public void notifyHitObservers(int hitIndex) {

        for (HitObserver obs : hitObservers) {
            obs.notifyHit(hitIndex);
        }

    }

    @Override
    protected void onDraw(Canvas canvas) {

        super.onDraw(canvas);

        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);
        canvas.drawPaint(paint);
        paint.setColor(Color.BLACK);
        paint.setTextSize(36.0f);
        canvas.drawText(taskLevelString, 50.0f, 200.0f, paint);
        canvas.drawText(taskString, 50.0f, 300.0f, paint);
        menuZone.draw(canvas);
        pointer.draw(canvas);


    }

    public void updatePointerPosition(float xOffset, float yOffset) {
        pointer.drawAtOffset(xOffset, yOffset);
        this.invalidate();
    }

    public void setTaskLevelString(String text) { taskLevelString = text; }
    public void setTaskString(String text) { taskString = text; }

    /**
     * Pointer object to navigate for menu selection.
     * This will be drawn at relative x,y offsets to center of the interaction being (0,0)
     */
    public class Pointer extends Drawable {

        private int x, y;
        private int centerX, centerY;
        private Canvas canvas = null;
        private boolean reentryConfirmed = true;
        private int radius = 25;

        @Override
        public void draw(@NonNull Canvas canvas) {
            if (this.canvas == null) {
                this.canvas = canvas;
                x = canvas.getWidth()/2;
                y = canvas.getHeight()/2;
                centerX = x;
                centerY = y;
            }

            Paint paint = new Paint();
            paint.setColor(Color.RED);
            canvas.drawCircle(x, y, radius, paint);
        }

        public void drawAtOffset(float xOffset, float yOffset) {
            if (canvas == null) return;

            x = this.centerX + Math.round(xOffset);
            y = this.centerY + Math.round(yOffset);

            // using java's reflection for bounds checking
            if (menuZone.getClass() == MenuZoneTwo.class) {

                MenuZoneTwo castedMenu = (MenuZoneTwo) menuZone;

                x = this.centerX + Math.round(xOffset);
                y = this.centerY + Math.round(yOffset);

                double magnitude = Math.sqrt(xOffset*xOffset + yOffset*yOffset);
                xOffset /= magnitude;
                yOffset /= magnitude;

                boolean isInsideOuter = magnitude <= castedMenu.getBoundRadius()-radius;
                if (!isInsideOuter) {
                    xOffset *= (castedMenu.getBoundRadius()-radius);
                    yOffset *= (castedMenu.getBoundRadius()-radius);
                    x = this.centerX + Math.round(xOffset);
                    y = this.centerY + Math.round(yOffset);
                }


                float boundRadius = castedMenu.getInnerRadius() + radius;
                boolean isInsideInner = magnitude <= boundRadius;

                if (!isInsideInner) {

                    int hitIndex = 0;

                    // check if hit is top menu or bottom
                    if (y < this.centerY) {
                        hitIndex = 1;
                    } else {
                        hitIndex = 2;
                    }

                    if (reentryConfirmed) {
                        notifyHitObservers(hitIndex);
                        reentryConfirmed = false;
                    }

                } else {



                    reentryConfirmed = true;
                }

            } else if (menuZone.getClass() == MenuZoneFour.class) {

                MenuZoneFour castedMenu = (MenuZoneFour) menuZone;

                Rect limitBounds = castedMenu.getOuterRectBounds();
                x = Math.min(limitBounds.right-radius, Math.max(limitBounds.left+radius, this.centerX+Math.round(xOffset)));
                y = Math.min(limitBounds.bottom-radius, Math.max(limitBounds.top+radius, this.centerY+Math.round(yOffset)));

                Rect innerBounds = castedMenu.getInnerRectBounds();
                Rect outerBounds = castedMenu.getOuterRectBounds();

                boolean isInsideInner = innerBounds.contains(x, y);
                boolean isInsideOuter = outerBounds.contains(x, y);

                if (!isInsideInner && isInsideOuter && reentryConfirmed) {

                    int hitIndex = 0;

                    boolean hitTop = innerBounds.top > y;
                    if (hitTop) hitIndex = 1;
                    boolean hitRight = innerBounds.right < x;
                    if (hitRight) hitIndex = 2;
                    boolean hitBottom = innerBounds.bottom < y;
                    if (hitBottom) hitIndex = 3;
                    boolean hitLeft = innerBounds.left > x;
                    if (hitLeft) hitIndex = 4;

                    notifyHitObservers(hitIndex);

                    reentryConfirmed = false;
                } else if (isInsideInner && isInsideOuter && !reentryConfirmed) {
                    System.out.println("confirm reentry");
                    reentryConfirmed = true;
                }

            }
        }

        @Override
        public void setAlpha(@IntRange(from = 0, to = 255) int alpha) { throw new UnsupportedOperationException(); }

        @Override
        public void setColorFilter(@Nullable ColorFilter colorFilter) { throw new UnsupportedOperationException(); }

        @Override
        public int getOpacity() {
            throw new UnsupportedOperationException();
        }
    }

    public abstract class MenuZone extends Drawable {

        protected int lightBlueColor = Color.argb(255, 218, 237, 255);
        protected int darkBlueColor = Color.argb(255, 182, 221, 255);
        protected int centerX;
        protected int centerY;
        protected Canvas canvas;

        @Override
        public void draw(@NonNull Canvas canvas) {

            if (this.canvas == null) {
                this.canvas = canvas;
                this.centerX = Math.round(canvas.getWidth()/2);
                this.centerY = Math.round(canvas.getHeight()/2);
            }

        }

        public abstract void setOptions(String[] options);

    }

    public class MenuZoneTwo extends MenuZone {

        private float innerRadius = 250.0f;
        private float outerRadius = 350.0f;
        private String[] menuValues = new String[2];

        public MenuZoneTwo() {
            super();
        }

        @Override
        public void draw(@NonNull Canvas canvas) {
            super.draw(canvas);

            Paint paint = new Paint();
            paint.setStyle(Paint.Style.FILL_AND_STROKE);

            paint.setColor(this.darkBlueColor);
            canvas.drawCircle(this.centerX, this.centerY, outerRadius, paint);

            paint.setColor(this.lightBlueColor);
            canvas.drawCircle(this.centerX, this.centerY, innerRadius, paint);

            // draw string for menus
            paint.setColor(Color.BLACK);
            paint.setTextSize(48.0f);
            canvas.drawText(menuValues[0], this.centerX - getBoundRadius()/4, this.centerY - getBoundRadius()/2, paint);
            canvas.drawText(menuValues[1], this.centerX - getBoundRadius()/4, this.centerY + getBoundRadius()/2, paint);

            // draw middle line
            paint.setStrokeWidth(4.0f);
            canvas.drawLine(this.centerX - outerRadius, this.centerY, this.centerX + outerRadius, this.centerY, paint);
        }

        public float getInnerRadius() { return innerRadius; }

        public float getBoundRadius() {
            return outerRadius;
        }

        @Override
        public void setOptions(String[] options) {
            menuValues = options;
            invalidate();
        }

        @Override
        public void setAlpha(@IntRange(from = 0, to = 255) int alpha) { throw new UnsupportedOperationException(); }

        @Override
        public void setColorFilter(@Nullable ColorFilter colorFilter) { throw new UnsupportedOperationException(); }

        @Override
        public int getOpacity() { throw new UnsupportedOperationException(); }

    }

    public class MenuZoneFour extends MenuZone {

        private int innerRadius = 250;
        private int outerRadius = 350;
        private String[] menuValues = new String[4];
        private Rect innerRect;
        private Rect outerRect;

        public MenuZoneFour() {
            super();

            outerRect = new Rect(this.centerX-outerRadius, this.centerY-outerRadius, this.centerX+outerRadius, this.centerY+outerRadius);
            innerRect = new Rect(this.centerX-innerRadius, this.centerY-innerRadius, this.centerX+innerRadius, this.centerY+innerRadius);

        }

        @Override
        public void draw(@NonNull Canvas canvas) {
            super.draw(canvas);

            Paint paint = new Paint();
            paint.setStyle(Paint.Style.FILL_AND_STROKE);

            outerRect.offsetTo(this.centerX-outerRadius, this.centerY-outerRadius);
            innerRect.offsetTo(this.centerX-innerRadius, this.centerY-innerRadius);

            paint.setColor(this.darkBlueColor);
            canvas.drawRect(outerRect, paint);

            paint.setColor(this.lightBlueColor);
            canvas.drawRect(innerRect, paint);

            // draw string for menus
            paint.setColor(Color.BLACK);
            paint.setTextSize(48.0f);
            canvas.drawText(menuValues[0], this.centerX, this.centerY - outerRadius/2.0f, paint);
            canvas.drawText(menuValues[1], this.centerX + outerRadius/2.0f, this.centerY, paint);
            canvas.drawText(menuValues[2], this.centerX, this.centerY + outerRadius/2.0f, paint);
            canvas.drawText(menuValues[3], this.centerX - outerRadius/2.0f, this.centerY, paint);

            // draw middle line
            paint.setStrokeWidth(4.0f);
            canvas.drawLine(this.centerX - outerRadius, this.centerY - outerRadius, this.centerX + outerRadius, this.centerY + outerRadius, paint);
            canvas.drawLine(this.centerX + outerRadius, this.centerY - outerRadius, this.centerX - outerRadius, this.centerY + outerRadius, paint);
        }

        public Rect getInnerRectBounds() {
            return innerRect;
        }

        public Rect getOuterRectBounds() {
            return outerRect;
        }

        @Override
        public void setOptions(String[] options) {
            menuValues = options;
            invalidate();
        }

        @Override
        public void setAlpha(@IntRange(from = 0, to = 255) int alpha) { throw new UnsupportedOperationException(); }

        @Override
        public void setColorFilter(@Nullable ColorFilter colorFilter) { throw new UnsupportedOperationException(); }

        @Override
        public int getOpacity() { throw new UnsupportedOperationException(); }

    }
}
