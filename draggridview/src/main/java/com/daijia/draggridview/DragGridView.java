package com.daijia.draggridview;

import android.annotation.TargetApi;
import android.content.Context;
import android.database.DataSetObserver;
import android.util.AttributeSet;
import android.view.ViewGroup;

/**
 * Created by flb on 16/5/24.
 */
public class DragGridView extends ViewGroup {

    BaseAdapter mAdapter;

    LayoutManager mLayoutManager = new LayoutManager();

    ViewRecycler mRecycler = new ViewRecycler();

    DataSetObserver mDataSetObserver = new DataSetObserver() {
        @Override
        public void onChanged() {
            super.onChanged();
        }

        @Override
        public void onInvalidated() {
            super.onInvalidated();
        }
    };

    public DragGridView(Context context) {
        super(context);
    }

    public DragGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DragGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(21)
    public DragGridView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }



    public void setAdapter(BaseAdapter baseAdapter){

        if (mAdapter != null && mDataSetObserver != null) {
            mAdapter.unregisterDataSetObserver(mDataSetObserver);
        }

        mAdapter = baseAdapter;
        mRecycler.setAdapter(mAdapter);
        mLayoutManager.setGridViewInfo(3,mAdapter.getCount());

        requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if(mAdapter == null || mAdapter.getCount()==0){
            return;
        }

        int itemHeight = 0;
        int itemWidth = getMeasuredWidth()/mLayoutManager.getColumn();

        BaseAdapter.ViewHolder viewHolder0 = mRecycler.get(0,this);
        measureChildWithMargins(viewHolder0.mView,
                MeasureSpec.makeMeasureSpec(itemWidth,MeasureSpec.EXACTLY),0,
                MeasureSpec.makeMeasureSpec(itemHeight,MeasureSpec.UNSPECIFIED),0
        );
        itemHeight = viewHolder0.mView.getMeasuredHeight();

        mLayoutManager.setItemInfo(itemWidth,itemHeight);
        mLayoutManager.setScreenInfo(getMeasuredWidth(),getMeasuredHeight());
        for (int i=mLayoutManager.start();i<mLayoutManager.end();i++){
            BaseAdapter.ViewHolder viewHolder = mRecycler.get(i,this);

            measureChildWithMargins(viewHolder.mView,
                    MeasureSpec.makeMeasureSpec(itemWidth,MeasureSpec.EXACTLY),0,
                    MeasureSpec.makeMeasureSpec(itemHeight,MeasureSpec.UNSPECIFIED),0
            );

            itemHeight = viewHolder.mView.getMeasuredHeight();
            LayoutParams layoutParams = (LayoutParams) viewHolder.mView.getLayoutParams();
            layoutParams.height = itemHeight;
            layoutParams.width = itemWidth;
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        for (int i=mLayoutManager.start();i<mLayoutManager.end();i++){
            BaseAdapter.ViewHolder viewHolder = mRecycler.get(i,this);
            LayoutParams layoutParams = (LayoutParams) viewHolder.mView.getLayoutParams();
            layoutParams.leftMargin = i%mLayoutManager.mColumn * mLayoutManager.mItemWidth;
            layoutParams.topMargin = mLayoutManager.getTopByIndex(i);

            int left = layoutParams.leftMargin;
            int top  = layoutParams.topMargin;
            int right= layoutParams.leftMargin + mLayoutManager.mItemWidth;
            int bottom = layoutParams.topMargin + mLayoutManager.mItemHeight;

            viewHolder.mView.layout(left,top,right,bottom);
        }

        mRecycler.recyclerView(this);
    }



    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    }

    @Override
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new LayoutParams(p);
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams;
    }

    public static class LayoutParams extends MarginLayoutParams{

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }
    }
}
