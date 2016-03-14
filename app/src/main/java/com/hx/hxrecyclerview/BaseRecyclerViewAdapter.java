package com.hx.hxrecyclerview;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;
/**
 * @Description描述:通用RecyclerViewAdapter抽象类
 * @Author作者: hx
 */
public abstract class BaseRecyclerViewAdapter<T> extends RecyclerView.Adapter<RecyclerViewHolder> {
    protected Context context;
    protected List list;
    protected int itemLayoutId;
    private OnItemClickListener onItemClickListener;
    private OnItemLongClickListener onItemLongClickListener;

    private OnLoadMoreListener mOnLoadMoreListener;
    public static final int LOADMORE = 1;
    public static final int NORMAL = 2;
    private boolean loading = false;
    private boolean isLoadMore = true;
    private GridLayoutManager gridManager;
    private int footView_temp = 0 ;

    public BaseRecyclerViewAdapter(Context context, List list, int itemLayoutId) {
        this.context = context;
        this.list = list;
        this.itemLayoutId = itemLayoutId;
    }

    public BaseRecyclerViewAdapter(Context context, List list, int itemLayoutId, RecyclerView recyclerView) {
        this.context = context;
        this.list = list;
        this.itemLayoutId = itemLayoutId;
        initOnScorllListener(recyclerView);
    }

    /*初始化滑动监听器*/
    public void initOnScorllListener(RecyclerView recyclerView) {
        recyclerView.addOnScrollListener(new FamiliarRecyclerViewOnScrollListener(recyclerView.getLayoutManager()) {
            @Override
            public void onScrolledToTop() {

            }

            @Override
            public void onScrolledToBottom() {
                if (isLoadMore) {
                    if (!loading) {
                        if (mOnLoadMoreListener != null) {
                            list.add(null);
                            footView_temp = list.size()-1;
                            notifyItemInserted(footView_temp);
                            mOnLoadMoreListener.loadMore();
                        }
                        loading = true;
                    }
                }
            }
        });
    }

    /*设置点击监听器*/
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    /*设置长按监听器*/
    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View mView = LayoutInflater.from(context).inflate(viewType == NORMAL?itemLayoutId:R.layout.progressbar_item, parent, false);
        return new RecyclerViewHolder(mView, viewType);
    }

    @Override
    public void onBindViewHolder(final RecyclerViewHolder holder, int position) {
        if (list.get(position) == null) {
            holder.mProgressBar.setIndeterminate(true);
        } else {
            convert(holder, position);
        }

        //为item添加click事件
        if (onItemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = holder.getLayoutPosition();
                    onItemClickListener.onItemClick(v, pos);
                }
            });
        }
        //为item添加longClick事件
        if (onItemLongClickListener != null) {
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int pos = holder.getAdapterPosition();
                    onItemLongClickListener.onItemLongClick(v, pos);
                    return true;//返回true则不会触发onclick事件，false则会接着触发onclick事件
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (list.get(position) == null) {
            return LOADMORE;
        } else {
            return NORMAL;
        }
    }

    public abstract <T> void convert(RecyclerViewHolder holder, int position);


    /**
     * 设置下拉加载更多功能
     *
     * @param isLoadMore
     */
    public void setLoadMore(boolean isLoadMore) {
        this.isLoadMore = isLoadMore;
    }

    /**
     * 加载完成
     */
    public void setLoaded() {
        loading = false;
    }

    /*添加上拉加载更多的监听器*/
    public void addOnLoadMoreListener(OnLoadMoreListener mLoadMoreListener) {
        this.mOnLoadMoreListener = mLoadMoreListener;
    }

    /*点击监听的接口*/
    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    /*长点击监听的接口*/
    public interface OnItemLongClickListener {
        void onItemLongClick(View view, int position);
    }

    /*加载更多监听的接口*/
    public interface OnLoadMoreListener {
        void loadMore();
    }

    /*设置recyclerView的Item跨度*/
    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
        if (manager instanceof GridLayoutManager) {
            gridManager = ((GridLayoutManager) manager);
            GridSpanSizeLookup mGridSpanSizeLookup = null;
            if (mGridSpanSizeLookup == null) {
                mGridSpanSizeLookup = new GridSpanSizeLookup();
            }
            gridManager.setSpanSizeLookup(mGridSpanSizeLookup);
        }
    }

    class GridSpanSizeLookup extends GridLayoutManager.SpanSizeLookup {
        @Override
        public int getSpanSize(int position) {
            if (null == list.get(position)) {
                return gridManager.getSpanCount();//如果是底部加载view则为整行
            }
            return 1;
        }
    }
}
