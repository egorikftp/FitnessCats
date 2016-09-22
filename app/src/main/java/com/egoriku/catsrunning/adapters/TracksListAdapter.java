package com.egoriku.catsrunning.adapters;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.egoriku.catsrunning.App;
import com.egoriku.catsrunning.R;
import com.egoriku.catsrunning.utils.ConverterTime;
import com.mikepenz.fastadapter.items.AbstractItem;

import java.util.List;

public class TracksListAdapter extends AbstractItem<TracksListAdapter, TracksListAdapter.ViewHolder> {

    public TracksListAdapter() {
    }

    private int date;
    private int timeRunning;
    private int distance;
    private int id;
    private int liked;

    public int getDate() {
        return date;
    }

    public void setDate(int date) {
        this.date = date;
    }

    public int getTimeRunning() {
        return timeRunning;
    }

    public void setTimeRunning(int timeRunning) {
        this.timeRunning = timeRunning;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getLiked() {
        return liked;
    }

    public void setLiked(int liked) {
        this.liked = liked;
    }


    @Override
    public int getType() {
        return R.id.card_view;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.adapter_fragment_tracks_list;
    }

    @Override
    public void bindView(ViewHolder holder, List payloads) {
        super.bindView(holder, payloads);
        int likedDigit = 0;
        holder.date.setText(ConverterTime.convertUnixDate(date));
        holder.timeRunning.setText(ConverterTime.ConvertTimeToStringWithMill(timeRunning));
        holder.distance.setText(String.format(holder.format, distance));

        Cursor cursor = App.getInstance().getDb().rawQuery(
                "SELECT Tracks.liked as liked FROM Tracks WHERE Tracks._id = ?", new String[]{String.valueOf(id)});

        if (cursor != null) {
            if (cursor.moveToNext()) {
                likedDigit = cursor.getInt(cursor.getColumnIndexOrThrow("liked"));
            }
            cursor.close();
        }

        switch (likedDigit) {
            case 0:
                holder.imageViewLiked.setImageDrawable(App.getInstance().getResources().getDrawable(R.drawable.ic_star_border_black));
                break;

            case 1:
                holder.imageViewLiked.setImageDrawable(App.getInstance().getResources().getDrawable(R.drawable.ic_star_black));
                break;
        }
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView date;
        public TextView timeRunning;
        public TextView distance;
        public ImageView imageViewLiked;
        public String format;


        public ViewHolder(View itemView) {
            super(itemView);
            date = (TextView) itemView.findViewById(R.id.main_fragment_date_text_view);
            timeRunning = (TextView) itemView.findViewById(R.id.main_fragment_time_running_text_view);
            distance = (TextView) itemView.findViewById(R.id.main_fragment_distance_text_view);
            imageViewLiked = (ImageView) itemView.findViewById(R.id.main_fragment_distance_image_view_liked);
            format = itemView.getResources().getString(R.string.tracks_list_fragment_distance_meter);
        }
    }
}
