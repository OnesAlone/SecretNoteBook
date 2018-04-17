package com.leaveme.notebook;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

/**
 * Created by m_space on 2017/10/6.
 */

public class ImageDialog extends Dialog{
    private TextView titleTxt;

    private Context mContext;
    private String title;

    public ImageDialog(Context context) {
        super(context);
        this.mContext = context;
    }

    public ImageDialog(Context context, int themeResId, String content) {
        super(context, themeResId);
        this.mContext = context;
    }

    protected ImageDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        this.mContext = context;
    }

    public ImageDialog setTitle(String title){
        this.title = title;
        return this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_image);
        setCanceledOnTouchOutside(false);
        initView();
    }

    private void initView(){
        titleTxt = (TextView)findViewById(R.id.title);

        if(!TextUtils.isEmpty(title)){
            titleTxt.setText(title);
        }

    }
}
