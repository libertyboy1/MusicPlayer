package com.view.media.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.TextView;

import com.view.media.R;

/**
 * Created by 陈苗辉 on 2016/12/14.
 */
public class ProgressDialog extends Dialog {


    private Context context;
    public ProgressDialog(Context context) {
        super(context);
        this.context = context;
    }

    public ProgressDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    protected ProgressDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }
    private static ProgressDialog progressDialog;
    //创建dialog的样式
    public static ProgressDialog createDialog(Context context){

        progressDialog = new ProgressDialog(context, R.style.ProgressDialogStyle);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setContentView(R.layout.progressdialog);
        progressDialog.getWindow().getAttributes().gravity = Gravity.CENTER;
        return progressDialog ;
    }
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {

        if (progressDialog == null) {
            return;
        }
        //添加控件  执行帧动画
        ImageView imageView = (ImageView) progressDialog.findViewById(R.id.loadingImageView);
        AnimationDrawable animationDrawable = (AnimationDrawable) imageView.getBackground();
        animationDrawable.start();
    }

    public ProgressDialog setTitle(String title) {
        return progressDialog;
    }

    public ProgressDialog setMessage(String strMessage){
        TextView tvMessage = (TextView)progressDialog.findViewById(R.id.id_tv_loadingmsg);

        if (tvMessage != null){
            tvMessage.setText(strMessage);
        }
        return progressDialog;
    }
}
