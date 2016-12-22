package com.view.media.adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.view.media.R;
import com.view.media.api.DownLoadFinishInterface;
import com.view.media.bean.DownLoadBean;
import com.view.media.utils.FileUtil;

import static com.view.media.bean.DownLoadBean.position;

/**
 * Created by Destiny on 2016/12/22.
 */

public class DownLoadAdapter extends BaseAdapter {
    private Context mContext;

    public DownLoadAdapter(Context mContext){
        this.mContext=mContext;
    }

    @Override
    public int getCount() {
        return DownLoadBean.downloadApis.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        final ViewHolder holder;
        if (view==null){
            holder=new ViewHolder();
            view= LayoutInflater.from(mContext).inflate(R.layout.item_lv_download,viewGroup,false);
            holder.ll_download= (LinearLayout) view.findViewById(R.id.ll_download);
            holder.item_main= (LinearLayout) view.findViewById(R.id.item_main);
            holder.tv_name= (TextView) view.findViewById(R.id.tv_name);
            holder.tv_go_on= (TextView) view.findViewById(R.id.tv_go_on);
            holder.tv_size= (TextView) view.findViewById(R.id.tv_size);
            holder.tv_speed= (TextView) view.findViewById(R.id.tv_speed);
            holder.pb_progressbar= (ProgressBar) view.findViewById(R.id.pb_progressbar);

            holder.item_main.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (holder.tv_go_on.getVisibility()==View.VISIBLE){
                        holder.tv_go_on.setVisibility(View.GONE);
                        holder.ll_download.setVisibility(View.VISIBLE);
                        DownLoadBean.downloadApis.get(i).downloadMusic();
                    }else{
                        holder.tv_go_on.setVisibility(View.VISIBLE);
                        holder.ll_download.setVisibility(View.GONE);
                        holder.tv_go_on.setText("点击继续下载");
                        DownLoadBean.downloadApis.get(i).cancel();
                        DownLoadBean.position=i;
                    }
                }
            });

            view.setTag(holder);
        }else{
            holder= (ViewHolder) view.getTag();
        }

        DownLoadBean.downloadApis.get(i).setHandler(new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case 0x1:
                        String current=((String)msg.obj).split(",")[0];
                        String total=((String)msg.obj).split(",")[1];

                        holder.tv_size.setText(FileUtil.formetFileSize(Long.parseLong(current))+" / "+FileUtil.formetFileSize(Long.parseLong(total)));
                        holder.pb_progressbar.setProgress((int)(Long.parseLong(current)*1.0/Long.parseLong(total)*100));
                        Log.e("--progress--",(int)(Long.parseLong(current)*1.0/Long.parseLong(total)*100)+"");
                        break;
                    case 0x2:
                        holder.tv_go_on.setVisibility(View.VISIBLE);
                        holder.ll_download.setVisibility(View.GONE);
                        holder.tv_go_on.setText("下载完成");
                        DownLoadBean.position=-1;
                        break;
                    case 0x3:
                        holder.tv_go_on.setVisibility(View.VISIBLE);
                        holder.ll_download.setVisibility(View.GONE);
                        holder.tv_go_on.setText("下载失败");
                        break;
                }
            }
        });

        holder.tv_name.setText(DownLoadBean.downloadApis.get(i).model.getSongName());


        return view;
    }



    class ViewHolder{
        TextView tv_name,tv_go_on,tv_size,tv_speed;
        LinearLayout ll_download,item_main;
        ProgressBar pb_progressbar;
    }
}
