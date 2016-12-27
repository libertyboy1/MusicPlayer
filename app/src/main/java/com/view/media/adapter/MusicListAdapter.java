package com.view.media.adapter;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.view.media.R;
import com.view.media.utils.StringUtil;

import java.io.File;


/**
 * Created by Destiny on 2016/12/19.
 */

public class MusicListAdapter extends BaseAdapter {
    private Context mContext;
    private File[] files;
    private OnRemoveListener mRemoveListener;


    public MusicListAdapter(Context mContext, File[] files) {
        this.mContext = mContext;
        this.files = files;
    }

    @Override
    public int getCount() {
        if (files != null)
            return files.length;
        return 0;
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
        ViewHolder holder;

        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_lv_music, viewGroup, false);
            holder = new ViewHolder();
            holder.tv_name = (TextView) view.findViewById(R.id.tv_name);
            holder.tv_singer = (TextView) view.findViewById(R.id.tv_singer);
            holder.ll_delete = (LinearLayout) view.findViewById(R.id.ll_delete);

            holder.ll_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mRemoveListener != null)
                        mRemoveListener.onRemoveItem(i);
                }
            });

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        String name = files[i].getName();

        holder.tv_name.setText(name.split("\\|")[1]);
        holder.tv_singer.setText(name.split("\\|")[0] + " · " + name.split("\\|")[2].substring(0, name.split("\\|")[2].indexOf(".")));

        return view;
    }

    class ViewHolder {
        TextView tv_name, tv_singer;
        LinearLayout ll_delete;
    }

    public interface OnRemoveListener {
        void onRemoveItem(int position);
    }

    public void setRemoveListener(OnRemoveListener removeListener) {
        this.mRemoveListener = removeListener;
    }

}
