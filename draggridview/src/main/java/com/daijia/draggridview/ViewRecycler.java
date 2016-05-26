package com.daijia.draggridview;

import android.support.v4.util.ArrayMap;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by flb on 16/5/24.
 */
public class ViewRecycler {

    BaseAdapter mAdapter;

    public void setAdapter(BaseAdapter adapter){
        mAdapter = adapter;
    }


    ArrayMap<Integer,BaseAdapter.ViewHolder> viewArrayMap = new ArrayMap<>();

    List<BaseAdapter.ViewHolder> viewCache = new ArrayList<>();



    public BaseAdapter.ViewHolder get(int index, ViewGroup parent){
        BaseAdapter.ViewHolder viewHolder = viewArrayMap.get(index);
        if (viewHolder == null){

            if(viewCache.size()!=0){
                viewHolder = viewCache.get(0);
                viewCache.remove(0);
            }else {
                viewHolder = mAdapter.onCreateView(parent);
                parent.addView(viewHolder.mView);
            }

        }else {
//            viewArrayMap.removeAt(index);
        }
        mAdapter.onBindView(index,viewHolder);

        return viewHolder;
    };


    public void recyclerView(ViewGroup viewGroup){
        for (Integer index : viewArrayMap.keySet()){
            viewGroup.removeView(viewArrayMap.get(index).mView);
            viewCache.add(viewArrayMap.get(index));
            viewCache.remove(index);
        }
    }
}
