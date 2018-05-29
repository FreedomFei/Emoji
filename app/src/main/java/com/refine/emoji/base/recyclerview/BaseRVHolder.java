package com.refine.emoji.base.recyclerview;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by fei on 16/9/28.
 */
public class BaseRVHolder extends RecyclerView.ViewHolder {

    private BaseViewHolder mViewHolder;

    public BaseRVHolder(View itemView) {
        super(itemView);

        mViewHolder = BaseViewHolder.getViewHolder(itemView);
    }

    public BaseViewHolder getViewHolder() {
        return mViewHolder;
    }
}
