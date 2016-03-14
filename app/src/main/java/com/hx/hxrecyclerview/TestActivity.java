package com.hx.hxrecyclerview;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description描述:
 * @Author作者: hx
 * @Date日期: 2016/3/12
 */
public class TestActivity extends AppCompatActivity {
    public List<String> list = new ArrayList<>();
    public RecyclerView.LayoutManager layoutManager;
    private Handler myHandler =  new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };
    private MyAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.id_recyclerView);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
           /* 设置刷新时自带的ProgressBar的颜色变化 */
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_light,
                android.R.color.holo_red_light, android.R.color.holo_orange_light,
                android.R.color.holo_green_light);

        recyclerView.setHasFixedSize(true);

        list.addAll(getDatas());
//        recyclerView.setLayoutManager(layoutManager = new LinearLayoutManager(this));
        recyclerView.setLayoutManager(new GridLayoutManager(this,3));
        recyclerView.setAdapter(adapter = new MyAdapter(this,list,recyclerView));

        //设置上拉加载
        adapter.setLoadMore(true);
        adapter.addOnLoadMoreListener(new BaseRecyclerViewAdapter.OnLoadMoreListener() {
            @Override
            public void loadMore() {
                myHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        adapter.list.remove(adapter.list.size() - 1);
                        adapter.notifyItemRemoved(adapter.list.size());
                        adapter.list.addAll(getDatas());
                        adapter.notifyItemInserted(adapter.list.size());
                        adapter.setLoaded();
                    }
                }, 2000);
            }
        });

        //设置下拉刷新
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                adapter.setLoadMore(false);
                myHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        adapter.setLoadMore(true);
                        list.clear();
                        list.addAll(getDatas());
                        adapter.notifyDataSetChanged();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                },2000);
            }
        });

    }


    private class MyAdapter extends BaseRecyclerViewAdapter{
        private  List<String> list;

        public MyAdapter(Context context, List list, RecyclerView recyclerView) {
            super(context, list, R.layout.item_test, recyclerView);
            this.list = list;
        }

        @Override
        public void convert(RecyclerViewHolder holder, int position) {
            TextView textView = holder.getView(R.id.id_textview);
            textView.setText(list.get(position)+"个机器");
        }

    }

    private List<String> getDatas() {
        List<String> tempDatas = new ArrayList<>();
        int curMaxData =  list.size();
        for (int i = 0; i < 30; i++) {
            tempDatas.add("" + (curMaxData + i));
        }

        return tempDatas;
    }
}
