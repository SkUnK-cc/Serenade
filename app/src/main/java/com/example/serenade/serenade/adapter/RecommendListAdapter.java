package com.example.serenade.serenade.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.serenade.serenade.R;
import com.example.serenade.serenade.bean.Song;
import com.example.serenade.serenade.callback.OnItemClickListener;
import com.example.serenade.serenade.utils.img.GlideRoundTransform;
import com.example.serenade.serenade.utils.system.SystemUtil;

import java.util.List;

/**
 * Created by Serenade on 2017/8/2.
 */

public class RecommendListAdapter extends RecyclerView.Adapter<RecommendListAdapter.RecommendListHolder> implements View.OnClickListener {
    private Context context;
    private List<Song> data;
    private OnItemClickListener onItemClickListener;

    public RecommendListAdapter(Context context, List<Song> data) {
        this.context = context;
        this.data = data;
    }

    @Override
    public RecommendListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recommend_list_item, parent, false);
        view.setOnClickListener(this);
        RecommendListHolder holder = new RecommendListHolder(view);
        ViewGroup.LayoutParams params = holder.image.getLayoutParams();
        int n = (int) (SystemUtil.getScreenWidth(context) / 2);
        n = n-view.getPaddingRight()-view.getPaddingRight();
        params.height = n;
        params.width = n;
        return holder;
    }

    @Override
    public void onBindViewHolder(RecommendListHolder holder, int position) {
        Song song = data.get(position);
        holder.name.setText(song.getSongname() + " - " + song.getSingername());
        Glide.with(context)
                .load(song.getAlbumpic_big())
                .placeholder(R.mipmap.app_icon)
                .transform(new GlideRoundTransform(context,5))
                .error(R.mipmap.app_icon)
                .into(holder.image);
        holder.itemView.setTag(position);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public void onClick(View v) {
        if (onItemClickListener != null) {
            onItemClickListener.onItemClick(v, (int) v.getTag());
        }
    }

    static class RecommendListHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView name;

        public RecommendListHolder(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.image);
            name = (TextView) itemView.findViewById(R.id.name);
        }
    }
}
