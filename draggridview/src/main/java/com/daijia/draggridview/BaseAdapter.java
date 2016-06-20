package com.daijia.draggridview;

import android.database.DataSetObservable;
import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by flb on 16/5/24.
 */
public abstract class BaseAdapter {

    private final DataSetObservable mDataSetObservable = new DataSetObservable();

    public boolean hasStableIds() {
        return false;
    }

    public void registerDataSetObserver(DataSetObserver observer) {
        mDataSetObservable.registerObserver(observer);
    }

    public void unregisterDataSetObserver(DataSetObserver observer) {
        mDataSetObservable.unregisterObserver(observer);
    }

    /**
     * Notifies the attached observers that the underlying data has been changed
     * and any View reflecting the data set should refresh itself.
     */
    public void notifyDataSetChanged() {
        mDataSetObservable.notifyChanged();
    }

    /**
     * Notifies the attached observers that the underlying data is no longer valid
     * or available. Once invoked this adapter is no longer valid and should
     * not report further data set changes.
     */
    public void notifyDataSetInvalidated() {
        mDataSetObservable.notifyInvalidated();
    }

    public abstract int getCount();
    public abstract Object getItem();
    public abstract long getItemId();
    public abstract void onBindView(int position,ViewHolder viewHolder);
    public abstract ViewHolder onCreateView(ViewGroup parent);
    public abstract void onExchange(int index0,int index1);
    public abstract void onExchangeEnd(int index0,int index1);

    public static class ViewHolder{

        View itemView;
        public ViewHolder(View view){
            itemView = view;

        }
    }
}
