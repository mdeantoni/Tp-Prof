package com.example.matias.tprof.custom;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;

/**
 * Created by Mati on 7/31/2016.
 */
public class CustomSwipeRefreshLayout extends SwipeRefreshLayout{

    public CustomSwipeRefreshLayout(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public CustomSwipeRefreshLayout(Context context) {
        super(context);
    }

    private boolean mMeasured = false;
    private boolean mPreMeasureRefreshing = false;

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (!mMeasured) {
            mMeasured = true;
            setRefreshing(mPreMeasureRefreshing);
        }
    }


    @Override
    public void setRefreshing(boolean refreshing) {
        if (mMeasured) {
            super.setRefreshing(refreshing);
        } else {
            mPreMeasureRefreshing = refreshing;
        }
    }
}
