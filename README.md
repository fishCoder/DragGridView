#DragGridView

A ViewGroup that being able to drag the child view

##Sample
![Alt text](./demo.gif)

##How to use

It is similar to the method of RecyclerView

###1 extend ViewHolder
```
class MyVH extends BaseAdapter.ViewHolder{

        public MyVH(View view) {
            super(view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

        }
    }

```

###2 implement BaseAdapter
```
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

```

###3 set adapter
```
dragGridView.setAdapter(baseAdapter);
```



##Dependencies

```
compile 'com.daijia.android:draggridview:1.0.3'
```
