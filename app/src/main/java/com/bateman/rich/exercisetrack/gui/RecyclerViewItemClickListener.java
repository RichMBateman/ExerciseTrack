package com.bateman.rich.exercisetrack.gui;

import android.content.Context;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * https://developer.android.com/reference/android/support/v7/widget/RecyclerView.SimpleOnItemTouchListener
 * The RecyclerView.SimpleOnItemTouchListener is an implementation of RecyclerView.OnItemTouchListener (an interface)
 * that has empty method bodies and default return values.
 *
 * You may prefer to extend this class if you don't need to override all method
 * (So in my words... if you're just going to go ahead and override everything... you might as well
 * just implement RecyclerView.OnItemTouchListener rather than use this class)
 */
class RecyclerViewItemClickListener extends RecyclerView.SimpleOnItemTouchListener {
    private static final String TAG = "RecyclerItemClickListen";
    /**
     * The distance a user has to swipe in a direction for a swipe to be triggered.
     */
    private static final int SWIPE_THRESHOLD = 150;

    interface OnRecyclerClickListener {
        void onItemClick(View view, int position);
        void onItemLongClick(View view, int position);
        void onItemSwipeRight(View view);
        void onItemSwipeLeft(View view);
    }

    private final OnRecyclerClickListener m_listener;
    private final GestureDetectorCompat m_gestureDetector;

    public RecyclerViewItemClickListener(Context context, final RecyclerView recyclerView, OnRecyclerClickListener listener) {
        Log.d(TAG, "RecyclerItemClickListener: start");
        m_listener = listener;
        m_gestureDetector = new GestureDetectorCompat(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                Log.d(TAG, "onSingleTapUp: starts");
                View childView = recyclerView.findChildViewUnder(e.getX(), e.getY());
                if(childView != null && m_listener != null) {
                    Log.d(TAG, "onSingleTapUp: calling listener.onItemClick");
                    m_listener.onItemClick(childView, recyclerView.getChildAdapterPosition(childView));
                }
                return true;
            }

            @Override
            public void onLongPress(MotionEvent e) {
                Log.d(TAG, "onLongPress: starts");
                View childView = recyclerView.findChildViewUnder(e.getX(), e.getY());
                if(childView != null && m_listener != null) {
                    Log.d(TAG, "onLongPress: calling listener.onItemLongClick");
                    m_listener.onItemLongClick(childView, recyclerView.getChildAdapterPosition(childView));
                }
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                Log.d(TAG, "onFling/swipe: starts.  velX, velY: " + velocityX + "," + velocityY);
                View childView = recyclerView.findChildViewUnder(e1.getX(), e1.getY());
                if(childView != null && m_listener != null) {
                    if(velocityX > SWIPE_THRESHOLD) {
                        Log.d(TAG, "onFling: calling listener.onItemSwipeRight");
                        m_listener.onItemSwipeRight(childView);
                    } else if (velocityX < -SWIPE_THRESHOLD) {
                        Log.d(TAG, "onFling: calling listener.onItemSwipeLeft");
                        m_listener.onItemSwipeLeft(childView);
                    }
                }
                return super.onFling(e1, e2, velocityX, velocityY);
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                return super.onScroll(e1, e2, distanceX, distanceY);
            }

            @Override
            public void onShowPress(MotionEvent e) {
                super.onShowPress(e);
            }

            @Override
            public boolean onDown(MotionEvent e) {
                return super.onDown(e);
            }

            @Override
            public boolean onDoubleTap(MotionEvent e) {
                return super.onDoubleTap(e);
            }

            @Override
            public boolean onDoubleTapEvent(MotionEvent e) {
                return super.onDoubleTapEvent(e);
            }

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                return super.onSingleTapConfirmed(e);
            }

            @Override
            public boolean onContextClick(MotionEvent e) {
                return super.onContextClick(e);
            }
        });
        Log.d(TAG, "RecyclerItemClickListener: end");
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        Log.d(TAG, "onInterceptTouchEvent: starts");
        if(m_gestureDetector != null) {
            boolean result = m_gestureDetector.onTouchEvent(e);
            Log.d(TAG, "onInterceptTouchEvent(): returned: " + result);
            return result;
        } else {
            Log.d(TAG, "onInterceptTouchEvent(): returned: false (should never see this)");
            return false;
        }
    }
}
