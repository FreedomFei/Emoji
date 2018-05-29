package com.refine.emoji;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.NumberFormat;

/**
 * Created by Refine on 2018/5/24/024.
 */

public class UploadPregressDialog extends Dialog {

    private Context mContext;
    private ProgressBar mProgress;
    private TextView mProgressNumber;
    private TextView mProgressPercent;

    private String mProgressNumberFormat;
    private Handler mViewUpdateHandler;

    public UploadPregressDialog(@NonNull Context context) {
        super(context);

        mContext = context;
        initFormats();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View view = LayoutInflater.from(mContext).inflate(R.layout.alert_dialog_progress, null);
        mProgress = view.findViewById(R.id.progress);
        mProgressNumber = view.findViewById(R.id.progress_number);
        mProgressPercent = view.findViewById(R.id.progress_percent);
        setContentView(view);

        mProgress.setMax(100);
        setProgress(0);

        mViewUpdateHandler = new Handler();
    }

    private void initFormats() {
        mProgressNumberFormat = "%1d/%2d";
        mProgressPercentFormat = NumberFormat.getPercentInstance();
        mProgressPercentFormat.setMaximumFractionDigits(0);
    }

    public void setProgress(int value) {
        mProgress.setProgress(value);
        onProgressChanged();
    }

    private NumberFormat mProgressPercentFormat;

    public void setProgressPercentFormat(NumberFormat format) {
        mProgressPercentFormat = format;
        onProgressChanged();
    }

    private void onProgressChanged() {
        if (mViewUpdateHandler != null && !mViewUpdateHandler.hasMessages(0)) {
            mViewUpdateHandler.post(new Runnable() {
                @Override
                public void run() {
                    /* Update the number and percent */
                    int progress = mProgress.getProgress();
                    int max = mProgress.getMax();
                    if (mProgressNumberFormat != null) {
                        String format = mProgressNumberFormat;
                        mProgressNumber.setText(String.format(format, progress, max));
                    } else {
                        mProgressNumber.setText("");
                    }
                    if (mProgressPercentFormat != null) {
                        double percent = (double) progress / (double) max;
                        SpannableString tmp = new SpannableString(mProgressPercentFormat.format(percent));
                        tmp.setSpan(new StyleSpan(android.graphics.Typeface.BOLD),
                                0, tmp.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        mProgressPercent.setText(tmp);
                    } else {
                        mProgressPercent.setText("");
                    }
                }
            });
        }
    }
}
