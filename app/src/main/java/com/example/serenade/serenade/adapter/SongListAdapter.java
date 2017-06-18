package com.example.serenade.serenade.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.serenade.serenade.R;
import com.example.serenade.serenade.bean.Song;

import java.util.List;

/**
 * Created by Serenade on 17/6/16.
 */

public class SongListAdapter extends RecyclerView.Adapter<SongListAdapter.SongHolder> implements View.OnClickListener {
    private Context context;
    private List<Song> data;
    private OnItemClickListener mOnItemClickListener;

    public SongListAdapter(Context context, List data) {
        this.context = context;
        this.data = data;
    }

    @Override
    public SongHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.song_item, parent, false);
        view.setOnClickListener(this);
        return new SongHolder(view);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public void onBindViewHolder(SongHolder holder, int position) {
        Song bean = data.get(position);
        String song = bean.getSongname();
        String singer = bean.getSingername();
        holder.song.setText(song);
        holder.singer.setText(singer);
        holder.itemView.setTag(position);
    }

    @Override
    public void onClick(View v) {
        if (mOnItemClickListener != null) {
            //注意这里使用getTag方法获取position
            mOnItemClickListener.onItemClick(v, (int) v.getTag());
        }
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    static class SongHolder extends RecyclerView.ViewHolder {
        TextView song, singer;

        public SongHolder(View itemView) {
            super(itemView);
            song = (TextView) itemView.findViewById(R.id.song);
            singer = (TextView) itemView.findViewById(R.id.singer);
        }
    }
}
