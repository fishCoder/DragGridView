package com.daijia.dragdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.daijia.draggridview.BaseAdapter;
import com.daijia.draggridview.DragGridView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    List<Integer> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        for(int i=0;i<20;i++){
            list.add(i);
        }

        DragGridView dragGridView = (DragGridView) findViewById(R.id.drag_grid_view);
        dragGridView.setAdapter(baseAdapter);

    }




    BaseAdapter baseAdapter = new BaseAdapter() {
        @Override
        public int getCount() {
            return 20;
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

            ((MyVH)viewHolder).tv.setText(""+list.get(position));
        }

        @Override
        public ViewHolder onCreateView(ViewGroup parent) {
            MyVH vh = new MyVH(getLayoutInflater().inflate(R.layout.layout,null));
            return vh;
        }

        @Override
        public void onExchange(int index0, int index1, boolean isEnd) {

        }


    };


    class MyVH extends BaseAdapter.ViewHolder{

        TextView tv;
        public MyVH(View view) {
            super(view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(MainActivity.this,tv.getText().toString(),Toast.LENGTH_SHORT).show();
                }
            });
            tv = (TextView) view.findViewById(com.daijia.draggridview.R.id.text);
        }
    }
}
