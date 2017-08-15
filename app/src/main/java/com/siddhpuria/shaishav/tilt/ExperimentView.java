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

/**
 * ExperimentView.java
 * This is the actual canvas on which the interaction is drawn.
 */

public class ExperimentView extends View {

    private Pointer pointer;
    private MenuZone menuZone;

    public ExperimentView(Context context) {
        super(context);
        pointer = new Pointer();
        menuZone = new MenuZoneFour();
        String menu[] = {"A", "B", "C", "D"};
        menuZone.setOptions(menu);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        super.onDraw(canvas);

        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);
        canvas.drawPaint(paint);

        menuZone.draw(canvas);
        pointer.draw(canvas);


    }

    public void updatePointerPosition(float xOffset, float yOffset) {
        pointer.drawAtOffset(xOffset, yOffset);
        this.invalidate();
    }

    /**
     * Pointer object to navigate for menu selection.
     * This will be drawn at relative x,y offsets to center of the interaction being (0,0)
     */
    public class Pointer extends Drawable {

        private float x, y;
        private float centerX, centerY;
        private Canvas canvas = null;

        @Override
        public void draw(@NonNull Canvas canvas) {
            if (this.canvas == null) {
                this.canvas = canvas;
                x = canvas.getWidth()/2;
                y = canvas.getHeight()/2;
                centerX = x;
                centerY = y;
            }

            int radius = 50;
            Paint paint = new Paint();
            paint.setColor(Color.RED);
            canvas.drawCircle(x, y, radius, paint);
        }

        public void drawAtOffset(float xOffset, float yOffset) {
            if (canvas == null) return;

            x = this.centerX + xOffset;
            y = this.centerY + yOffset;

            // using java's reflection for bounds checking
            if (menuZone.getClass() == MenuZoneTwo.class) {

                double magnitude = Math.sqrt(xOffset*xOffset + yOffset*yOffset);
                xOffset /= magnitude;
                yOffset /= magnitude;

                float boundRadius = ((MenuZoneTwo) menuZone).getBoundRadius();

                if (magnitude > boundRadius) {
                    xOffset *= boundRadius;
                    yOffset *= boundRadius;

                    x = this.centerX + xOffset;
                    y = this.centerY + yOffset;

                    if (y < this.centerY) {
                        String[] newMenu = {"AB", "CD"};
                        menuZone.setOptions(newMenu);
                    } else {
                        String[] newMenu = {"CD", "AB"};
                        menuZone.setOptions(newMenu);
                    }
                }
            } else if (menuZone.getClass() == MenuZoneFour.class) {

                double magnitude = Math.sqrt(xOffset*xOffset + yOffset*yOffset);
                Rect bounds = ((MenuZoneFour) menuZone).getSquareBounds();

                xOffset = Math.min(bounds.right, Math.max(bounds.left, this.centerX+xOffset));
                yOffset = Math.min(bounds.bottom, Math.max(bounds.top, this.centerY+yOffset));

                x = xOffset;
                y = yOffset;

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
        protected float centerX;
        protected float centerY;
        protected Canvas canvas;
        protected  int numVisibleItems = 0;

        @Override
        public void draw(@NonNull Canvas canvas) {

            if (this.canvas == null) {
                this.canvas = canvas;
                this.centerX = canvas.getWidth()/2;
                this.centerY = canvas.getHeight()/2;
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
            numVisibleItems = 2;
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

        public float getBoundRadius() {
            return (innerRadius+outerRadius)/2.0f;
        }

        @Override
        public void setOptions(String[] options) {
            menuValues = options;
            invalidate();
        }

        public int getNumVisibleItems() { return numVisibleItems; }

        @Override
        public void setAlpha(@IntRange(from = 0, to = 255) int alpha) { throw new UnsupportedOperationException(); }

        @Override
        public void setColorFilter(@Nullable ColorFilter colorFilter) { throw new UnsupportedOperationException(); }

        @Override
        public int getOpacity() { throw new UnsupportedOperationException(); }

    }

    public class MenuZoneFour extends MenuZone {

        private float innerRadius = 250.0f;
        private float outerRadius = 350.0f;
        private String[] menuValues = new String[4];

        public MenuZoneFour() {
            super();
            numVisibleItems = 4;
        }

        @Override
        public void draw(@NonNull Canvas canvas) {
            super.draw(canvas);

            Paint paint = new Paint();
            paint.setStyle(Paint.Style.FILL_AND_STROKE);

            paint.setColor(this.darkBlueColor);
            canvas.drawRect(this.centerX-outerRadius, this.centerY-outerRadius, this.centerX+outerRadius, this.centerY+outerRadius, paint);

            paint.setColor(this.lightBlueColor);
            canvas.drawRect(this.centerX-innerRadius, this.centerY-innerRadius, this.centerX+innerRadius, this.centerY+innerRadius, paint);

            // draw string for menus
            paint.setColor(Color.BLACK);
            paint.setTextSize(48.0f);
            canvas.drawText(menuValues[0], this.centerX, this.centerY - outerRadius/1.5f, paint);
            canvas.drawText(menuValues[1], this.centerX + outerRadius/1.5f, this.centerY, paint);
            canvas.drawText(menuValues[2], this.centerX, this.centerY + outerRadius/1.5f, paint);
            canvas.drawText(menuValues[3], this.centerX - outerRadius/1.5f, this.centerY, paint);

            // draw middle line
            paint.setStrokeWidth(4.0f);
            canvas.drawLine(this.centerX - outerRadius, this.centerY - outerRadius, this.centerX + outerRadius, this.centerY + outerRadius, paint);
            canvas.drawLine(this.centerX + outerRadius, this.centerY - outerRadius, this.centerX - outerRadius, this.centerY + outerRadius, paint);
        }

        public Rect getSquareBounds() {
            float midPoint = (innerRadius+outerRadius)/2.0f;
            return new Rect((int) (this.centerX-midPoint), (int) (this.centerY-midPoint), (int) (this.centerX+midPoint), (int) (this.centerY+midPoint));
        }

        @Override
        public void setOptions(String[] options) {
            menuValues = options;
            invalidate();
        }

        public int getNumVisibleItems() { return numVisibleItems; }

        @Override
        public void setAlpha(@IntRange(from = 0, to = 255) int alpha) { throw new UnsupportedOperationException(); }

        @Override
        public void setColorFilter(@Nullable ColorFilter colorFilter) { throw new UnsupportedOperationException(); }

        @Override
        public int getOpacity() { throw new UnsupportedOperationException(); }

    }
}
