package com.daijia.draggridview;

import android.animation.ValueAnimator;
import android.os.Handler;
import android.support.v4.util.ArrayMap;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorListener;
import android.view.View;
import android.view.ViewGroup;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by flb on 16/5/24.
 */
public class LayoutManager {

    public static int DRAG_SCROLL_UP = 1;
    public static int DRAG_SCROLL_DOWN = -1;
    public static int DRAG_SCROLL_NONE = 0;


    int mDragScrollAreaHeight;

    float mScreenTop;
    int mScreenHeight;
    int mScreenWidth;

    int mItemHeight;
    int mItemWidth;

    int mColumn;
    int mTotalCount;
    int mTotalRow;

    int mFirstVisibleIndex;
    int mLastVisibleIndex;

    int mFirstVisibleRowIndex;
    int mLastVisibleRowIndex;

    Handler mHandler;

    public LayoutManager(Handler handler){
        mHandler  = handler;
    }

    public int getColumn(){
        return mColumn;
    }

    public void setGridViewInfo(int column,int count){
        mColumn = column;
        mTotalCount = count;
        mTotalRow = count%column==0?count/column:count/column+1;
    }

    public void setScreenInfo(int width,int height){
        mScreenHeight = height;
        mScreenWidth = width;


    }

    public void setItemInfo(int width,int height){
        mItemHeight = height;
        mItemWidth = width;

        mDragScrollAreaHeight = mItemHeight/2;
        onScroll(0,0,null,null);
    }

    public void onScroll(float dx, float dy, ViewRecycler recycler, ViewGroup parent){

        float pendScrollTop = mScreenTop + dy;

        if(pendScrollTop>0){
            dy=dy/(float) Math.sqrt(pendScrollTop/8+1f);
        }
        else
        if( mScreenHeight - pendScrollTop> mTotalRow * mItemHeight ) {
            dy=dy/(float) Math.sqrt((mScreenHeight - pendScrollTop - mTotalRow * mItemHeight)/8+1f);
        }


        mFirstVisibleIndex = 0;
        mLastVisibleIndex = mTotalCount-1;

        mScreenTop = mScreenTop + dy;


        if(recycler != null){
            for (int i=start();i<=end();i++){
                BaseAdapter.ViewHolder viewHolder = recycler.get(i,parent);
                DragGridView.LayoutParams layoutParams = (DragGridView.LayoutParams) viewHolder.itemView.getLayoutParams();
                layoutParams.top += dy;
                layoutParams.bottom += dy;

                if(layoutParams.bottom < 0 || layoutParams.top > mScreenHeight){
//                    Log.d("recyclerView",""+i);
//                    recycler.recyclerView(i,viewHolder,parent);
                }
            }

        }

//        int iScreenTop = (int) mScreenTop;
//
//
//        if(iScreenTop > 0){
//            mFirstVisibleIndex = 0;
//        }else {
//            int firstVisibleRow = -iScreenTop/mItemHeight;
//            mFirstVisibleIndex = firstVisibleRow * mColumn;
//        }
//
//        if(mScreenHeight - iScreenTop> mTotalRow * mItemHeight){
//            mLastVisibleIndex = mTotalCount-1;
//        }else {
//            int h = mScreenHeight - iScreenTop;
//            int lastVisibleRow = h/mItemHeight;
//            mLastVisibleIndex = lastVisibleRow * mColumn + mColumn - 1;
//            mLastVisibleIndex = mLastVisibleIndex > mTotalCount-1 ? mTotalCount-1 : mLastVisibleIndex;
//        }

    }

    public void scrolled(final View view){

        float end=0;
        if(mScreenTop>0){

        }
        else
        if( mScreenHeight - mScreenTop > mTotalRow * mItemHeight ) {

            if(mScreenHeight > mTotalRow * mItemHeight){

            }else {
                end = mScreenHeight - mTotalRow * mItemHeight;
            }

        }else {
            return;
        }

        ValueAnimator animation = ValueAnimator.ofFloat(mScreenTop, end);
        animation.setDuration(300);
        animation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mScreenTop = (Float) animation.getAnimatedValue();
                view.requestLayout();
            }
        });
        animation.start();

    }

    int mLastDragPassPosition = 0;
    int mDragViewStartPosition = 0;


    public void startDragging(ViewRecycler recycler,float x,float y){
        for (int i=start();i<=end();i++){
            BaseAdapter.ViewHolder viewHolder = recycler.get(i,null);
            DragGridView.LayoutParams layoutParams = (DragGridView.LayoutParams) viewHolder.itemView.getLayoutParams();

            if(x>layoutParams.left && x<layoutParams.right &&
                    y>layoutParams.top && y<layoutParams.bottom){
                mDragViewStartPosition = mLastDragPassPosition = i;
                break;
            }

        }
    }




    private int mIsAnimationTranslate = 0;

    public void onDragging(ViewRecycler recycler, float x, float y){
        if(mIsAnimationTranslate != 0)return;

        for (int i=start();i<=end();i++){
            BaseAdapter.ViewHolder viewHolder = recycler.get(i,null);
            DragGridView.LayoutParams layoutParams = (DragGridView.LayoutParams) viewHolder.itemView.getLayoutParams();
            if(x> layoutParams.left && x< layoutParams.right &&
                    y> layoutParams.top && y<layoutParams.bottom &&
                    mLastDragPassPosition != i){

                int deta = mLastDragPassPosition < i ? -1 : 1;

                DragGridView.LayoutParams tempLayout = layoutParams;

                ArrayMap<Integer,BaseAdapter.ViewHolder> rePositionMap = new ArrayMap<>();

                for (int k=i ; mLastDragPassPosition != k ; k+=deta ){

                    BaseAdapter.ViewHolder vh = recycler.get(k,null);
                    DragGridView.LayoutParams lp = (DragGridView.LayoutParams) recycler.get(k+deta,null).itemView.getLayoutParams();
                    exchangePosition(vh.itemView,lp);
                    rePositionMap.put(k+deta,vh);

                    recycler.exchange(k,k+deta);
                }

                recycler.onExchangeEnd(i,mLastDragPassPosition);
                BaseAdapter.ViewHolder viewHolderLast = recycler.get(mLastDragPassPosition,null);
                rePositionMap.put(i,viewHolderLast);
                exchangePosition(viewHolderLast.itemView,tempLayout);



                for (Integer key : rePositionMap.keySet()){
                    recycler.put(key,rePositionMap.get(key));
                }


                mLastDragPassPosition = i;

                break;
            }

        }

        if(mTotalRow*mItemHeight>mScreenHeight-mScreenTop){
            if (y>0&&y<mDragScrollAreaHeight){
                mScrollDirct = SCROLL_DOWN;

                if(mScrollTimer == null){
                    mScrollTimer = new Timer();
                    mScrollTimer.schedule(new Task(),0,20);
                }else {

                }


            }else if(y<mScreenHeight&&y>mScreenHeight-mDragScrollAreaHeight){
                mScrollDirct = SCROLL_UP;

                if(mScrollTimer == null){
                    mScrollTimer = new Timer();
                    mScrollTimer.schedule(new Task(),0,16);
                }
            }else {
                if(mScrollTimer != null)
                    mScrollTimer.cancel();
            }


        }


    }

    public void onDragEnd(){
        if(mScrollTimer!=null){
            mScrollTimer.cancel();
            mScrollTimer = null;
        }
    }

    Timer mScrollTimer = null;

    static int  SCROLL_UP = 0;
    static int  SCROLL_DOWN = 1;

    int mScrollDirct = -1;

    int SCROLL_SLOP = 8;

    class Task extends  TimerTask {
        @Override
        public void run() {

                if(SCROLL_DOWN == mScrollDirct){
                    if(mScreenTop + SCROLL_SLOP >= 0){
                        onScroll(0,mScreenTop,null,null);
                        mScrollTimer.cancel();
                    }else {
                        onScroll(0,SCROLL_SLOP,null,null);
                    }
                }else {
                    if(mScreenHeight-mScreenTop+SCROLL_SLOP>=mTotalRow*mItemHeight){
                        onScroll(0,(mScreenHeight-mScreenTop)-mTotalRow*mItemHeight,null,null);
                        mScrollTimer.cancel();
                    }else {
                        onScroll(0,-SCROLL_SLOP,null,null);
                    }
                }

                mHandler.sendEmptyMessageAtTime(0,0);
        }


    };



    public boolean isPlayingAnimation(){
        return mIsAnimationTranslate != 0;
    }


    void exchangePosition(final View animationView,final DragGridView.LayoutParams layoutParams){

        DragGridView.LayoutParams lp = (DragGridView.LayoutParams) animationView.getLayoutParams();
        final int detaX = layoutParams.left - lp.left;
        final int detaY = layoutParams.top - lp.top;

        ViewCompat.animate(animationView)
                .translationX(detaX)
                .translationY(detaY)
                .setDuration(300)
                .setListener(mAnimatorListener)
                .start();

        animationView.setLayoutParams(layoutParams);

    }


    ViewPropertyAnimatorListener mAnimatorListener = new ViewPropertyAnimatorListener() {
        @Override
        public void onAnimationStart(View view) {
            synchronized (this){
                mIsAnimationTranslate ++;
            }
        }

        @Override
        public void onAnimationEnd(View view) {
            synchronized (this){
                mIsAnimationTranslate --;
            }
            mHandler.sendEmptyMessageAtTime(0,0);
        }

        @Override
        public void onAnimationCancel(View view) {

        }
    };

    public float getTopByIndex(int index){
        int rowIndex = index/mColumn;
        return mScreenTop + rowIndex * mItemHeight;
    }

    public int start(){
        return mFirstVisibleIndex;
    }

    public int end(){
        return mLastVisibleIndex;
    }
}
