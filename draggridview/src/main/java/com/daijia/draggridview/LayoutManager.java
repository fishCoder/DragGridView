package com.daijia.draggridview;

/**
 * Created by flb on 16/5/24.
 */
public class LayoutManager {


    int mScreenTop;
    int mScreenHeight;
    int mScreenWidth;

    int mItemHeight;
    int mItemWidth;

    int mColumn;
    int mTotalCount;

    int mFirstVisibleIndex;
    int mLastVisibleIndex;

    int mFirstVisibleRowIndex;
    int mLastVisibleRowIndex;

    public int getColumn(){
        return mColumn;
    }

    public void setGridViewInfo(int column,int count){
        mColumn = column;
        mTotalCount = count;
    }

    public void setScreenInfo(int height,int width){
        mScreenHeight = height;
        mScreenWidth = width;
    }

    public void setItemInfo(int width,int height){
        mItemHeight = height;
        mItemWidth = width;
        onScroll(0,0);
    }

    public void onScroll(int dx,int dy){
        mScreenTop = mScreenTop + dy;

        int firstVisibleRowIndex = mScreenTop%mItemHeight==0?mScreenTop/mItemHeight:(mScreenTop/mItemHeight+1);


        mFirstVisibleRowIndex = firstVisibleRowIndex * mColumn;

        int exceptFirstHeight = mScreenTop + mScreenHeight - mFirstVisibleIndex * mItemHeight;
        mLastVisibleRowIndex = exceptFirstHeight % mItemHeight == 0 ? exceptFirstHeight / mItemHeight : (exceptFirstHeight / mItemHeight + 1);

        mLastVisibleIndex = (mLastVisibleRowIndex+1) * mColumn > mTotalCount-1  ? mTotalCount-1 : (mLastVisibleRowIndex+1) * mColumn -1 ;

    }

    public void scrolled(){

    }

    public int getTopByIndex(int index){
        int offsetIndex = index - mFirstVisibleIndex;
        int offsetRowIndex = offsetIndex/mColumn;
        return mScreenTop + offsetRowIndex * mItemHeight;
    }

    public int start(){
        return mFirstVisibleIndex;
    }

    public int end(){
        return mLastVisibleIndex;
    }
}
