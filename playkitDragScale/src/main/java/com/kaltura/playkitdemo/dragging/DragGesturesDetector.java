package com.kaltura.playkitdemo.dragging;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

/**
 * Created by Gleb on 11/21/16.
 */

@TargetApi(Build.VERSION_CODES.FROYO)
public class DragGesturesDetector implements View.OnTouchListener {

    public interface OnDragViewListener {
        void onMove(float transitionXPos, float transitionYPos, float touchXPos, float touchYPos);
        void onMoveStart(float transitionXPos, float transitionYPos, float touchXPos, float touchYPos);
        void onMoveEnd(float transitionXPos, float transitionYPos, float touchXPos, float touchYPos);
        void onClick();
    }

    private OnDragViewListener mOnEditorViewListener;
    private float mXPos, mYPos;
    private float mPrevXPos, mPrevYPos;
    private boolean mIsTwoFingers;
    private float mPrevX1, mPrevX2, mPrevY1, mPrevY2;
    private boolean mIsMoving;

    private final static float DETECT_2_FINGERS = 0.0f;


    public DragGesturesDetector(Context context, OnDragViewListener onEditorViewListener) {
        mOnEditorViewListener = onEditorViewListener;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        //Log.v("motion", "event = " + event.getActionMasked() + " pointers count = " + event.getPointerCount());
        boolean result = false;
        switch(event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN:
                if(!mIsTwoFingers && event.getPointerCount() == 1) {
                    mPrevXPos = event.getRawX();
                    mPrevYPos = event.getRawY();
                    result = true;
                }
                break;
            case MotionEvent.ACTION_MOVE:
               if(detect1FingerMove(event)) {
                    result = true;
                    if (!mIsMoving) {
                        mIsMoving = true;
                        mOnEditorViewListener.onMoveStart(mXPos, mYPos, event.getRawX(), event.getRawY());
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
            case MotionEvent.ACTION_CANCEL:
                if(event.getPointerCount() == 1) {
                    result = detect1FingerMove(event);
                    mIsTwoFingers = false;
                }
                if (result) {
                    mIsMoving = false;
                    mOnEditorViewListener.onMoveEnd(mXPos, mYPos, event.getRawX(), event.getRawY());
                }
                break;
        }
        return result;
    }

    private boolean detect2FingersMove(MotionEvent event) {
        if (event.getPointerCount() == 2) {
            mIsTwoFingers = true;
            float X1 = event.getX(0);
            float X2 = event.getX(1);
            float Y1 = event.getY(0);
            float Y2 = event.getY(1);
            Log.v("scale", "x1 = " + X1 + " y1 = " + Y1 + " x2 = " + X2 + " y2 = " + Y2);
            return true;
        }
        return false;
    }

    private boolean detect1FingerMove(MotionEvent event) {
        if (!mIsTwoFingers && event.getPointerCount() == 1) {
            float X1 = event.getRawX();
            float Y1 = event.getRawY();
            mXPos += (X1 - mPrevXPos);
            mYPos += (Y1 - mPrevYPos);
            mPrevXPos = X1;
            mPrevYPos = Y1;
            mOnEditorViewListener.onMove(mXPos, mYPos, event.getRawX(), event.getRawY());
            if (!mIsMoving) {
                mOnEditorViewListener.onClick();
            }
            return true;
        }
        return false;
    }

    public void updatePosition(float x, float y) {
        mXPos = x;
        mYPos = y;
        mPrevXPos = x;
        mPrevYPos = y;
    }
}
