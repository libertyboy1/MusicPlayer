package com.view.media.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.view.media.R;
import com.view.media.activity.PlayActivity;
import com.view.media.bean.SearchMusicBean;
import com.view.media.utils.StringUtil;

import java.util.ArrayList;

import static android.R.id.list;
import static com.view.media.R.id.ll_main;

/**
 * Created by Destiny on 2016/12/21.
 */

public class SearchMusicListAdapter extends RecyclerView.Adapter<SearchMusicListAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<SearchMusicBean> beans;

    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener{
        void onItemClick(View v,int position);
    }

    public SearchMusicListAdapter(Context mContext, ArrayList<SearchMusicBean> beans) {
        this.mContext = mContext;
        this.beans = beans;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_lv_music, parent, false);
        ViewHolder vh = new ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(SearchMusicListAdapter.ViewHolder holder, final int position) {
        holder.tv_name.setText(beans.get(position).name);
        if (!StringUtil.isEmpty(beans.get(position).albumName)) {
            holder.tv_singer.setText(beans.get(position).singerName + " · " + beans.get(position).albumName);
        } else {
            holder.tv_singer.setText(beans.get(position).singerName);
        }

        if (!StringUtil.isEmpty(beans.get(position).mvId)) {
            holder.iv_mv.setVisibility(View.VISIBLE);
        } else {
            holder.iv_mv.setVisibility(View.GONE);
        }

        if (beans.get(position).isDownload) {
            holder.iv_tag.setImageResource(R.mipmap.ic_lyric_poster_lyric_select);
        } else {
            holder.iv_tag.setImageResource(R.mipmap.icon_down);
        }

        holder.ll_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener!=null){
                    onItemClickListener.onItemClick(v,position);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        if (beans != null)
            return beans.size();
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_name, tv_singer;
        public LinearLayout ll_main;
        public ImageView iv_tag, iv_mv;

        public ViewHolder(View itemView) {
            super(itemView);
            tv_name = (TextView) itemView.findViewById(R.id.tv_name);
            tv_singer = (TextView) itemView.findViewById(R.id.tv_singer);
            ll_main = (LinearLayout) itemView.findViewById(R.id.ll_main);
            iv_tag = (ImageView) itemView.findViewById(R.id.iv_tag);
            iv_mv = (ImageView) itemView.findViewById(R.id.iv_mv);
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener=onItemClickListener;
    }
}
