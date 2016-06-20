package com.daijia.draggridview;

import android.support.v4.util.ArrayMap;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by flb on 16/5/24.
 */
public class ViewRecycler {

    BaseAdapter mAdapter;

    public void setAdapter(BaseAdapter adapter){
        mAdapter = adapter;
    }


    ArrayMap<Integer,BaseAdapter.ViewHolder> viewArrayMap = new ArrayMap<>();

    ArrayMap<Integer,BaseAdapter.ViewHolder> scrapViewMap = new ArrayMap<>();



    public void put(int index, BaseAdapter.ViewHolder viewHolder){
        viewArrayMap.put(index,viewHolder);
    }

    public BaseAdapter.ViewHolder get(int index, ViewGroup parent){

        BaseAdapter.ViewHolder viewHolder = viewArrayMap.get(index);
        if (viewHolder == null){

            if(scrapViewMap.size()!=0){
                viewHolder = getViewHolderFromScrapView(index);
            }else {
                viewHolder = mAdapter.onCreateView(parent);
                viewArrayMap.put(index,viewHolder);
            }
            parent.addView(viewHolder.itemView);
            mAdapter.onBindView(index,viewHolder);
        }else {
//            viewArrayMap.removeAt(index);
        }


        return viewHolder;
    }


    BaseAdapter.ViewHolder getViewHolderFromScrapView(int index){
        BaseAdapter.ViewHolder viewHolder = scrapViewMap.get(index);
        if(viewHolder != null){
            scrapViewMap.remove(index);
        }else {
            for (Integer i : scrapViewMap.keySet()){
                viewHolder = scrapViewMap.get(i);
                scrapViewMap.remove(i);
                break;
            }
        }
        viewHolder.itemView.setVisibility(View.VISIBLE);
        return viewHolder;
    }

    public void exchange(int index0,int index1){
        mAdapter.onExchange(index0,index1);
    }

    public void onExchangeEnd(int index0,int index1){
        mAdapter.onExchangeEnd(index0,index1);
    }

    public void recyclerView(int index, BaseAdapter.ViewHolder scrapView,ViewGroup viewGroup){
        viewGroup.removeView(scrapView.itemView);
        viewArrayMap.remove(index);
        scrapViewMap.put(index,scrapView);
    }
}
