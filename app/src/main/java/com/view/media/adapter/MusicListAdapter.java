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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.view.media.R;
import com.view.media.db.TableMusic;
import com.view.media.utils.StringUtil;

import java.io.File;
import java.util.List;

import static android.R.attr.name;
import static com.view.media.bean.DownLoadBean.position;


/**
 * Created by Destiny on 2016/12/19.
 */

public class MusicListAdapter extends BaseAdapter {
    private Context mContext;
    private List<TableMusic> tb_musics;
    private OnRemoveListener mRemoveListener;


    public MusicListAdapter(Context mContext, List<TableMusic> tb_musics) {
        this.mContext = mContext;
        this.tb_musics = tb_musics;
    }

    @Override
    public int getCount() {
        if (tb_musics != null)
            return tb_musics.size();
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
            holder.iv_mv= (ImageView) view.findViewById(R.id.iv_mv);

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

        if (!StringUtil.isEmpty(tb_musics.get(i).getMvId())){
            holder.iv_mv.setVisibility(View.VISIBLE);
        }else{
            holder.iv_mv.setVisibility(View.GONE);
        }

        holder.tv_name.setText(tb_musics.get(i).getSoungName());
        holder.tv_singer.setText(tb_musics.get(i).getSingerName() + " · " + tb_musics.get(i).getAlbumName());

        return view;
    }

    class ViewHolder {
        TextView tv_name, tv_singer;
        LinearLayout ll_delete;
        ImageView iv_mv;
    }

    public interface OnRemoveListener {
        void onRemoveItem(int position);
    }

    public void setRemoveListener(OnRemoveListener removeListener) {
        this.mRemoveListener = removeListener;
    }

}
