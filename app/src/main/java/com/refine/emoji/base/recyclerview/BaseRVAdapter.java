package com.refine.emoji.base.recyclerview;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by fei on 16/9/28.
 */
public abstract class BaseRVAdapter<T> extends RecyclerView.Adapter<BaseRVHolder> {

    private Context mContext;
    private List<T> mList;

    public BaseRVAdapter(Context context, List<T> list) {
        mContext = context;
        mList = list;
    }

    @Override
    public BaseRVHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(onCreateViewLayoutID(viewType), parent, false);

        return new BaseRVHolder(view);
    }

    public abstract int onCreateViewLayoutID(int viewType);

    @Override
    public void onViewRecycled(final BaseRVHolder holder) {
        super.onViewRecycled(holder);
    }

    @Override
    public void onBindViewHolder(final BaseRVHolder holder, final int position) {

        onBindViewHolder(holder.getViewHolder(), position);
        if (mOnItemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onItemClick(mList, v, holder.getLayoutPosition(), holder.getItemId());
                }
            });
        }
    }

    public abstract void onBindViewHolder(BaseViewHolder holder, int position);

    @Override
    public int getItemCount() {
        return mList == null ? -1 : mList.size();
    }

    public boolean addData(T data) {
        return addData(data, getItemCount());
    }

    public boolean addData(T data, int position) {
        if (data != null) {
            if (getItemCount() > 0 && position >= 0 && position <= mList.size()) {
                mList.add(position, data);
                notifyItemInserted(position);

                return true;
            }
        }

        return false;
    }

    public boolean addDataAll(List<T> data) {
        if (data != null) {
            if (getItemCount() >= 0) {
                mList.addAll(data);
                notifyDataSetChanged();

                return true;
            }
        }
        return false;
    }

    public boolean remove(int position) {
        if (getItemCount() > 0) {
            mList.remove(position);
            notifyItemRemoved(position);
//            notifyDataSetChanged();

            return true;
        }
        return false;
    }

    public boolean removeDataAll() {
        if (getItemCount() > 0) {
            mList.clear();
            notifyDataSetChanged();

            return true;
        }
        return false;
    }

    public interface OnItemClickListener<T> {
        void onItemClick(List<T> data, View view, int position, long id);
    }

    private OnItemClickListener mOnItemClickListener;

    public OnItemClickListener getOnItemClickListener() {
        return mOnItemClickListener;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }
}
