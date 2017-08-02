package com.example.serenade.serenade.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.serenade.serenade.R;
import com.example.serenade.serenade.application.MyApplication;
import com.example.serenade.serenade.bean.Song;
import com.example.serenade.serenade.callback.OnItemClickListener;

import java.util.List;

import io.realm.Realm;

/**
 * Created by Serenade on 17/6/18.
 */

public class PlayListAdapter extends RecyclerView.Adapter<PlayListAdapter.RecentHolder> implements View.OnClickListener {
    private Context context;
    private List<Song> data;
    private OnItemClickListener mOnItemClickListener;

    public PlayListAdapter(Context context, List<Song> data) {
        this.context = context;
        this.data = data;
    }

    @Override
    public RecentHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.play_list_item, parent, false);
        view.setOnClickListener(this);
        RecentHolder holder = new RecentHolder(view);
        holder.delete.setOnClickListener(this);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecentHolder holder, int position) {
        Song bean = data.get(position);
        String song = bean.getSongname();
        String singer = bean.getSingername();
        holder.song.setText(song);
        holder.singer.setText(" - " + singer);
        holder.itemView.setTag(position);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public void onClick(View v) {
        int id  = v.getId();
        switch (id){
            case R.id.delete:
                View parent = (View) v.getParent();
                int position = (int) parent.getTag();
                Song song = data.get(position);
                Realm realm = MyApplication.getPlayListRealm();
                realm.beginTransaction();
                song.deleteFromRealm();
                realm.commitTransaction();
                data.remove(position);
                notifyDataSetChanged();
                break;
            default:
                if (mOnItemClickListener != null) {
                    //注意这里使用getTag方法获取position
                    mOnItemClickListener.onItemClick(v, (int) v.getTag());
                }
        }
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    static class RecentHolder extends RecyclerView.ViewHolder {
        TextView song, singer;
        ImageView delete;

        public RecentHolder(View itemView) {
            super(itemView);
            song = (TextView) itemView.findViewById(R.id.song);
            singer = (TextView) itemView.findViewById(R.id.singer);
            delete = (ImageView) itemView.findViewById(R.id.delete);
        }
    }
}
