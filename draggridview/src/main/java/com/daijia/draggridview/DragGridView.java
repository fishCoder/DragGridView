package com.daijia.draggridview;

import android.annotation.TargetApi;
import android.content.ClipData;
import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.DragEvent;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;

/**
 * Created by flb on 16/5/24.
 */
public class DragGridView extends ViewGroup {

    private static final int TOUCH_SLOP = 6;

    BaseAdapter mAdapter;

    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            requestLayout();
        }
    };

    LayoutManager mLayoutManager = new LayoutManager(mHandler);

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

    boolean mIsDraggingView = false;

    public DragGridView(Context context) {
        super(context);
    }

    public DragGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs);
    }

    public DragGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context,attrs);
    }

    @TargetApi(21)
    public DragGridView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context,attrs);
    }

    private void init(Context context,AttributeSet attrs){
        TypedArray ta = context.obtainStyledAttributes(attrs,R.styleable.DragGirdView);
        mLayoutManager.mColumn = ta.getInteger(R.styleable.DragGirdView_column_num, 3);
        ta.recycle();

        mGestureDetectorCompat = new GestureDetectorCompat(context,mGestureListener);
    }

    @Override
    public boolean shouldDelayChildPressedState() {
        return false;
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
        defaultOnMeasure(widthMeasureSpec, heightMeasureSpec);

        if(mAdapter == null || mAdapter.getCount()==0 || mLayoutManager.isPlayingAnimation()){
            return;
        }


        int itemHeight = 0;
        int itemWidth = getMeasuredWidth()/mLayoutManager.getColumn();

        BaseAdapter.ViewHolder viewHolder0 = mRecycler.get(0,this);
        measureChildWithMargins(viewHolder0.itemView,
                MeasureSpec.makeMeasureSpec(itemWidth,MeasureSpec.EXACTLY),0,
                MeasureSpec.makeMeasureSpec(itemHeight,MeasureSpec.UNSPECIFIED),0
        );
        itemHeight = viewHolder0.itemView.getMeasuredHeight();

        mLayoutManager.setItemInfo(itemWidth,itemHeight);
        mLayoutManager.setScreenInfo(getMeasuredWidth(),getMeasuredHeight());

        for (int i=mLayoutManager.start();i<=mLayoutManager.end();i++){
            BaseAdapter.ViewHolder viewHolder = mRecycler.get(i,this);


            if(viewHolder.itemView.getVisibility()==GONE){
                continue;
            }


            measureChildWithMargins(viewHolder.itemView,
                    MeasureSpec.makeMeasureSpec(itemWidth,MeasureSpec.EXACTLY),0,
                    MeasureSpec.makeMeasureSpec(itemHeight,MeasureSpec.UNSPECIFIED),0
            );

            itemHeight = viewHolder.itemView.getMeasuredHeight();
            LayoutParams layoutParams = new LayoutParams(itemWidth,itemHeight);
            layoutParams.leftMargin = i % mLayoutManager.mColumn * layoutParams.width;
            layoutParams.topMargin = (int) mLayoutManager.getTopByIndex(i);

            layoutParams.left    = layoutParams.leftMargin;
            layoutParams.top     = layoutParams.topMargin;
            layoutParams.right   = layoutParams.leftMargin + layoutParams.width;
            layoutParams.bottom  = layoutParams.topMargin + layoutParams.height;

            viewHolder.itemView.setLayoutParams(layoutParams);
        }
    }

    private void defaultOnMeasure(int widthSpec, int heightSpec) {
        final int widthMode = MeasureSpec.getMode(widthSpec);
        final int heightMode = MeasureSpec.getMode(heightSpec);
        final int widthSize = MeasureSpec.getSize(widthSpec);
        final int heightSize = MeasureSpec.getSize(heightSpec);

        int width = 0;
        int height = 0;

        switch (widthMode) {
            case MeasureSpec.EXACTLY:
            case MeasureSpec.AT_MOST:
                width = widthSize;
                break;
            case MeasureSpec.UNSPECIFIED:
            default:
                width = ViewCompat.getMinimumWidth(this);
                break;
        }

        switch (heightMode) {
            case MeasureSpec.EXACTLY:
            case MeasureSpec.AT_MOST:
                height = heightSize;
                break;
            case MeasureSpec.UNSPECIFIED:
            default:
                height = ViewCompat.getMinimumHeight(this);
                break;
        }

        setMeasuredDimension(width, height);
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        if(mAdapter == null || mAdapter.getCount()==0 || mLayoutManager.isPlayingAnimation()){
            return;
        }

        int start = mLayoutManager.start();
        int end   = mLayoutManager.end();
        for (int i = start ; i <= end ; i++){
            BaseAdapter.ViewHolder viewHolder = mRecycler.get(i,this);

            LayoutParams layoutParams = (LayoutParams) viewHolder.itemView.getLayoutParams();

            ViewCompat.setTranslationX(viewHolder.itemView,0);
            ViewCompat.setTranslationY(viewHolder.itemView,0);

            viewHolder.itemView.layout(layoutParams.left,layoutParams.top,layoutParams.right,layoutParams.bottom);

            viewHolder.itemView.setOnLongClickListener(mOnLongClickListener);
            setOnDragListener(mOnDragListener);
        }

    }


    float mInitX,mInitY;
    float mLastX,mLastY;
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return true;
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {


        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                mInitX = mLastX = event.getX();
                mInitY = mLastY = event.getY();

                break;
            case MotionEvent.ACTION_MOVE:
//                mLayoutManager.onScroll((int)(nowX-mLastX),(int)(nowY-mLastY),mRecycler,this);

                break;
            case MotionEvent.ACTION_UP:
                mLayoutManager.scrolled(this);
                break;
            case MotionEvent.ACTION_CANCEL:
                break;
        }

        requestLayout();
        mGestureDetectorCompat.onTouchEvent(event);
        return true;
    }

    GestureDetectorCompat mGestureDetectorCompat;
    GestureDetector.OnGestureListener mGestureListener = new GestureDetector.SimpleOnGestureListener(){
        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            for (int i=0 ; i<getChildCount();i++){
                View child = getChildAt(i);
                LayoutParams layoutParams = (LayoutParams) child.getLayoutParams();
                if(mInitX>layoutParams.left&&mInitX<layoutParams.right
                        &&mInitY>layoutParams.top&&mInitY<layoutParams.bottom){
                    child.performClick();
                }
            }
            return super.onSingleTapUp(e);
        }

        @Override
        public void onLongPress(MotionEvent e) {
            super.onLongPress(e);

            for (int i=0 ; i<getChildCount();i++){
                View child = getChildAt(i);
                LayoutParams layoutParams = (LayoutParams) child.getLayoutParams();
                if(mInitX>layoutParams.left&&mInitX<layoutParams.right
                        &&mInitY>layoutParams.top&&mInitY<layoutParams.bottom){
                    child.performLongClick();
                    mLayoutManager.scrolled(DragGridView.this);
                }
            }
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            mLayoutManager.onScroll(e2.getX()-mLastX,e2.getY()-mLastY,mRecycler,DragGridView.this);
            mLastX = e2.getX();
            mLastY = e2.getY();
            return super.onScroll(e1, e2, distanceX, distanceY);
        }
    };


    View.OnLongClickListener mOnLongClickListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            // 创建DragShadowBuilder，我把控件本身传进去
            View.DragShadowBuilder builder = new View.DragShadowBuilder(v);
            // 剪切板数据，可以在DragEvent.ACTION_DROP方法的时候获取。
            ClipData data = ClipData.newPlainText("dot", "Dot : " + v.toString());

            // 开始拖拽
            v.startDrag(data, builder, v, 0);
            v.setVisibility(INVISIBLE);
            return true;

        }
    };




    View.OnDragListener mOnDragListener = new View.OnDragListener() {

        float mInitDragX,mInitDragY;
        float mLastDragX,mLastDragY;

        @Override
        public boolean onDrag(View v, DragEvent event) {
            final int action = event.getAction();

            int[] loc = new int[]{0,0};
            getLocationInWindow(loc);
            View view = (View) event.getLocalState();


            switch (action) {

                case DragEvent.ACTION_DRAG_STARTED:
                    mInitDragX = mLastDragX = event.getX();
                    mInitDragY = mLastDragY = event.getY();

                    mLayoutManager.startDragging(mRecycler,mInitDragX-loc[0],mLastDragY-loc[1]);

                    mIsDraggingView = true;
                    break;

                case DragEvent.ACTION_DRAG_ENDED:
                    view.setVisibility(VISIBLE);
                    mIsDraggingView = false;
                    mLayoutManager.onDragEnd();
                    break;
                case DragEvent.ACTION_DRAG_EXITED:

                    break;
                // 拖拽进某个控件后，保持
                case DragEvent.ACTION_DRAG_LOCATION:
                    float nowDragX = event.getX();
                    float nowDragY = event.getY();

                    mLayoutManager.onDragging(mRecycler,nowDragX,nowDragY);

                    mLastDragX = nowDragX;
                    mLastDragY = nowDragY;
                    break;
                // 推拽进入某个控件
                case DragEvent.ACTION_DRAG_ENTERED:
//                    view.setVisibility(VISIBLE);
                    break;
                // 推拽进入某个控件，后在该控件内，释放。即把推拽控件放入另一个控件
                case DragEvent.ACTION_DROP:

                    break;
                default:
                    view.setVisibility(VISIBLE);
            }

            return true;
        }
    };

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

        int left;
        int top;
        int right;
        int bottom;

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
