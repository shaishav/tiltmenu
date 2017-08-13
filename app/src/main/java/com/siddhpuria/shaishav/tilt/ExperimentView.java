package com.siddhpuria.shaishav.tilt;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
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

    Pointer pointer;

    public ExperimentView(Context context) {
        super(context);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        super.onDraw(canvas);
        pointer = new Pointer();
        pointer.draw(canvas);

    }

    public class Pointer extends Drawable {

        @Override
        public void draw(@NonNull Canvas canvas) {
            int x = canvas.getWidth();
            int y = canvas.getHeight();
            int radius = 100;
            Paint paint = new Paint();
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.BLACK);
            canvas.drawPaint(paint);
            paint.setColor(Color.RED);
            canvas.drawCircle(x/2, y/2, radius, paint);
        }

        @Override
        public void setAlpha(@IntRange(from = 0, to = 255) int alpha) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setColorFilter(@Nullable ColorFilter colorFilter) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int getOpacity() {
            throw new UnsupportedOperationException();
        }
    }
}
