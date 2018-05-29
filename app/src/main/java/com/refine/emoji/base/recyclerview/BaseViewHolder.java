package com.refine.emoji.base.recyclerview;

import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by fei on 16/9/28.
 */
public class BaseViewHolder {

    private SparseArray<View> mViewHolder;
    private View mView;

    public static BaseViewHolder getViewHolder(View view) {
        BaseViewHolder viewHolder = (BaseViewHolder) view.getTag();
        if (viewHolder == null) {
            viewHolder = new BaseViewHolder(view);
            view.setTag(viewHolder);
        }
        return viewHolder;
    }

    private BaseViewHolder(View view) {
        this.mView = view;
        this.mViewHolder = new SparseArray<View>();
        view.setTag(mViewHolder);
    }

    public <T extends View> T get(int id) {
        View childView = mViewHolder.get(id);
        if (childView == null) {
            childView = mView.findViewById(id);
            mViewHolder.put(id, childView);
        }
        return (T) childView;
    }

    public View getConvertView() {
        return mView;
    }

    public View getView(int id) {
        return get(id);
    }

    public TextView getTextView(int id) {
        return get(id);
    }

    public Button getButton(int id) {
        return get(id);
    }

    public ImageView getImageView(int id) {
        return get(id);
    }
}
