package com.kaltura.playkitdemo.dragging;

/**
 * Created by Gleb on 11/21/16.
 */

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.Display;
import android.view.MotionEvent;
import android.view.ViewParent;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;

/**
 * Sample of TextView with added gesture controls.
 */
public class DragView extends RelativeLayout {

    private DragGesturesDetector mDetector;
    private DragViewController mViewController;
    private OnClickListener mOnClickListener;

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
        mDetector = new DragGesturesDetector(getContext(), mViewController);
    }

    public void setEventListener(final DragViewController.EventListener eventListener) {
        post(new Runnable() {
            @Override
            public void run() {
                    mViewController.setDragArea(getDragArea(), eventListener);
                }
        });
    }

    private Rect getDragArea() {
        Display display = ((Activity)getContext()).getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return new Rect(0, 0, size.x, size.y);
    }

    @Override
    public void setOnClickListener(OnClickListener onClickListener) {
        mOnClickListener = onClickListener;
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

