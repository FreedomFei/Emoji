package com.refine.emoji.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.refine.emoji.R;

/**
 * Created by Refine on 2018/5/23/023.
 */

public class BaseActivity extends AppCompatActivity {

    protected AlertDialog.Builder mProgressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initDialog();
    }

    private void initDialog() {
        View view = getLayoutInflater().inflate(R.layout.progress_dialog, null);
        mProgressDialog = new AlertDialog.Builder(this);
        ProgressBar mProgress = (ProgressBar) view.findViewById(R.id.progress);
        TextView mMessageView = (TextView) view.findViewById(R.id.message);
        mProgressDialog.setView(view);
        mMessageView.setText("Loading");
        //mProgressDialog.show();
    }

    protected void toast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }
}
