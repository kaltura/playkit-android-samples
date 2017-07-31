package com.kaltura.playkitdemo.dragging;

import android.graphics.Point;
import android.graphics.Rect;
import android.util.Log;
import android.view.View;

/**
 * Created by Gleb on 11/25/16.
 */

public class DragViewController implements DragGesturesDetector.OnDragViewListener{

    public interface ScaleDragListener {
        void onUpdateSize(int w, int h, float scaleFactor);
        void onUpdatePosition(int x, int y);
    }

    private View mView;
    private ScaleDragListener mScaleDragListener;
    private Rect mDragArea;
    private float minScaleFactor = 0.5f;

    public DragViewController(View view) {
        mView = view;
    }

    public void setDragArea(Rect dragArea, ScaleDragListener scaleDragListener) {
        mDragArea = dragArea;
        mScaleDragListener = scaleDragListener;
    }

    @Override
    public void onMove(float transitionXPos, float transitionYPos, float touchXPos, float touchYPos) {
        Point p = getInBounds((int)transitionXPos, (int)transitionYPos);
        if (p != null) {
            onUpdatePosition(p.x, p.y);
            float y = p.y == 0 ? 1.0f : (float)p.y;
            float scaleFactor = 1.0f - (1.0f - minScaleFactor) * y / (float)(mDragArea.bottom - mView.getHeight());
            Log.v("scaleFactor", "sf = " + scaleFactor);
            onUpdateScale(scaleFactor);
            //mScaleDragListener.onUpdatePosition(p.x, p.y);
        }

    }

    @Override
    public void onMoveStart(float transitionXPos, float transitionYPos, float touchXPos, float touchYPos) {

    }

    @Override
    public void onMoveEnd(float transitionXPos, float transitionYPos, float touchXPos, float touchYPos) {

    }

    private void onUpdatePosition(int x, int y) {
        mView.setTranslationX(x);
        mView.setTranslationY(y);
    }

    private void onUpdateScale(float scaleFactor) {
        mView.setScaleX(scaleFactor);
        mView.setScaleY(scaleFactor);
        float scaledW = (float)mView.getWidth() * scaleFactor;
        float d = (mDragArea.right - scaledW) / 2;
        float offsetX = d * mView.getTranslationY() / (float)(mDragArea.bottom - mView.getHeight());

        mView.setTranslationX(offsetX);
    }

    public void clear() {
        mView = null;
        mDragArea = null;
    }

    private Point getInBounds(int newX, int newY) {
        if (mView != null && mDragArea != null) {
            return new Point(getX(newX), getY(newY));
        }
        return null;
    }

    private int getX(int x) {
        int w = mView.getWidth();
        if (x < mDragArea.left) return mDragArea.left;
        else if (x > mDragArea.right - w) return mDragArea.right - w;
        return x;
    }

    private int getY(int y) {
        int h = mView.getHeight();
        if (y < mDragArea.top) return mDragArea.top;
        else if (y > mDragArea.bottom - h) return mDragArea.bottom - h;
        return y;
    }

    private Rect getViewRect(int x, int y) {
        /*int[] l = new int[2];
        mView.getLocationOnScreen(l);*/
        return new Rect(x, y, x + mView.getWidth(), y + mView.getHeight());
    }


}
