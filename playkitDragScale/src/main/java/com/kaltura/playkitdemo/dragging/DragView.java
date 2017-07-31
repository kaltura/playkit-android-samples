package com.kaltura.playkitdemo.dragging;

/**
 * Created by Gleb on 11/21/16.
 */

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.Display;
import android.view.MotionEvent;
import android.view.ViewParent;
import android.widget.FrameLayout;

/**
 * Sample of TextView with added gesture controls.
 */
public class DragView extends FrameLayout {

    private DragGesturesDetector mDetector;
    private DragViewController mViewController;

    public DragView(Context context) {
        super(context);
        init();
    }

    public DragView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DragView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    protected void init() {
        mViewController = new DragViewController(this);
        mViewController.setDragArea(getDragArea(), null);
        mDetector = new DragGesturesDetector(getContext(), mViewController);
    }

    private Rect getDragArea() {
        Display display = ((Activity)getContext()).getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return new Rect(0, 0, size.x, size.y);
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        if (event.getActionMasked() == MotionEvent.ACTION_POINTER_DOWN) {
            ViewParent parent = getParent();
            if (parent != null) {
                parent.requestDisallowInterceptTouchEvent(true);
            }
        }
        return mDetector.onTouch(this, event);
    }

    @Override
    protected void onDetachedFromWindow() {
        mViewController.clear();
        super.onDetachedFromWindow();
    }
}

