package com.egoriku.catsrunning.adapters;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.egoriku.catsrunning.App;
import com.egoriku.catsrunning.R;
import com.egoriku.catsrunning.models.Point;
import com.egoriku.catsrunning.utils.ConverterTime;
import com.mikepenz.fastadapter.items.AbstractItem;

import java.util.ArrayList;
import java.util.List;

public class TracksListAdapter extends AbstractItem<TracksListAdapter, TracksListAdapter.ViewHolder> {

    public TracksListAdapter() {
    }

    private long beginsAt;
    private long time;
    private long distance;
    private int id;
    private int liked;
    private ArrayList<Point> points;

    public long getBeginsAt() {
        return beginsAt;
    }

    public void setBeginsAt(long beginsAt) {
        this.beginsAt = beginsAt;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public long getDistance() {
        return distance;
    }

    public void setDistance(long distance) {
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

    public ArrayList<Point> getPoints() {
        return points;
    }

    public void setPoints(ArrayList<Point> points) {
        this.points = points;
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
        holder.date.setText(ConverterTime.convertUnixDate(beginsAt));
        holder.timeRunning.setText(ConverterTime.ConvertTimeToStringWithMill(time));
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
