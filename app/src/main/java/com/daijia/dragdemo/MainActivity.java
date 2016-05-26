package com.daijia.dragdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ViewGroup;

import com.daijia.draggridview.BaseAdapter;
import com.daijia.draggridview.DragGridView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DragGridView dragGridView = (DragGridView) findViewById(R.id.drag_grid_view);
        dragGridView.setAdapter(baseAdapter);
    }


    BaseAdapter baseAdapter = new BaseAdapter() {
        @Override
        public int getCount() {
            return 10;
        }

        @Override
        public Object getItem() {
            return null;
        }

        @Override
        public long getItemId() {
            return 0;
        }

        @Override
        public void onBindView(int position, ViewHolder viewHolder) {

        }

        @Override
        public ViewHolder onCreateView(ViewGroup parent) {
            return new ViewHolder(getLayoutInflater().inflate(R.layout.layout,null));
        }

        @Override
        public void onExchange(int index0, int index1) {

        }
    };
}
