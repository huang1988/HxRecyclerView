package com.hx.hxrecyclerview;

import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;
import android.widget.ProgressBar;

/**
 * @Description描述:通用的RecyclerViewHolder
 * @Author作者: hx
 */
public class RecyclerViewHolder extends RecyclerView.ViewHolder {
    private SparseArray<View> array;
    public ProgressBar mProgressBar;
    public RecyclerViewHolder(View itemView, int viewType) {
        super(itemView);
        array = new SparseArray<>();
        init(itemView, viewType);
    }

    /**
     * 获取item的childView
     * @param id childView的id
     * @return 返回childView对象
     */
    public <T extends View> T getView(int id){
        if (array.get(id)==null){
            array.put(id,itemView.findViewById(id));
        }
        return (T) array.get(id);
    }

    private void init(View view, int viewType) {
        switch (viewType) {
            case BaseRecyclerViewAdapter.LOADMORE:
                mProgressBar = (ProgressBar) view.findViewById(R.id.progressbar);
                break;
            default:
                break;
        }
    }
}
