package com.example.serenade.serenade.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.serenade.serenade.R;
import com.example.serenade.serenade.bean.Song;
import com.example.serenade.serenade.utils.TimeParseUtil;

import java.util.List;

/**
 * Created by Serenade on 17/6/18.
 */

public class RecentListAdapter extends BaseAdapter {
    private Context context;
    private List<Song> data;

    public RecentListAdapter(Context context, List<Song> data) {
        this.context = context;
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        RecentHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.recent_list_item, parent, false);
            holder = new RecentHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (RecentHolder) convertView.getTag();
        }
        Song bean = data.get(position);
        String song = bean.getSongname();
        String singer = bean.getSingername();
        int duration = bean.getDuration();
        holder.song.setText(song);
        holder.singer.setText(singer);
        String time = TimeParseUtil.parse(duration);
        holder.duration.setText(time);
        return convertView;
    }

    static class RecentHolder {
        TextView song, duration, singer;

        public RecentHolder(View itemView) {
            song = (TextView) itemView.findViewById(R.id.song);
            duration = (TextView) itemView.findViewById(R.id.duration);
            singer = (TextView) itemView.findViewById(R.id.singer);
        }
    }
}
