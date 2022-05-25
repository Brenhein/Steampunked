package edu.msu.harr1332.project1;

import android.content.Context;
import android.view.Display;
import android.view.ScaleGestureDetector;
import android.widget.RelativeLayout;

public abstract class ZoomLayout extends RelativeLayout implements ScaleGestureDetector.OnScaleGestureListener {

    private static final float MIN_ZOOM = 1.0f;
    private static final float MAX_ZOOM = 4.0f;
//    private Display.Mode mode = Display.Mode.;
    private float scale = 1.0f;
    private float lastScaleFactor = 0f;
    private float startX = 0f;
    private float startY = 0f;
    private float dx = 0f;
    private float dy = 0f;
    private float prevDx = 0f;
    private float prevDy = 0f;

    public ZoomLayout(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
    }


}
