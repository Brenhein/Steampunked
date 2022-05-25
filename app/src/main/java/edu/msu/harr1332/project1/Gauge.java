package edu.msu.harr1332.project1;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

public class Gauge extends Pipe {

    public Gauge(boolean north, boolean east, boolean south, boolean west) {
        super(north, east, south, west);
        gauge = true;

    }

    @Override
    public Bitmap getBitmap(Context context, int pixelSpacing) {
        if (pipe != null) {
            return pipe;
        }
        else {
            pipe = BitmapFactory.decodeResource(context.getResources(), R.drawable.gauge);
            Matrix matrix = new Matrix();
            matrix.postRotate(-90);
            pipe = Bitmap.createBitmap(pipe, 0, 0, pipe.getWidth(), pipe.getHeight(), matrix, true);
            pipe = Bitmap.createScaledBitmap(pipe, pixelSpacing, pixelSpacing, true);
        }
        return pipe;
    }
}
