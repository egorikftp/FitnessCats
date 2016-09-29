package com.egoriku.catsrunning.adapters;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.egoriku.catsrunning.App;
import com.egoriku.catsrunning.R;
import com.egoriku.catsrunning.models.Firebase.Point;
import com.egoriku.catsrunning.utils.ConverterTime;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.mikepenz.fastadapter.utils.ViewHolderFactory;

import java.util.ArrayList;
import java.util.List;

public class AllFitnessDataAdapter extends AbstractItem<AllFitnessDataAdapter, AllFitnessDataAdapter.ViewHolder> {

    private static final ViewHolderFactory<? extends ViewHolder> FACTORY = new ItemFactory();

    public AllFitnessDataAdapter() {
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
        return R.layout.adapter_all_fitness_data_fragment;
    }


    @Override
    public void bindView(ViewHolder holder, List payloads) {
        super.bindView(holder, payloads);
        holder.date.setText(ConverterTime.convertUnixDate(beginsAt));
        holder.timeRunning.setText(ConverterTime.ConvertTimeToStringWithMill(time));
        holder.distance.setText(String.format(holder.format, distance));

        Cursor cursor = App.getInstance().getDb().rawQuery(
                "SELECT Tracks.liked as liked FROM Tracks WHERE Tracks._id = ?", new String[]{String.valueOf(getId())});

        if (cursor != null) {
            if (cursor.moveToNext()) {
                liked = cursor.getInt(cursor.getColumnIndexOrThrow("liked"));
            }
            cursor.close();
        }

        switch (liked) {
            case 0:
                holder.imageViewLiked.setImageDrawable(App.getInstance().getResources().getDrawable(R.drawable.ic_star_border_black));
                break;

            case 1:
                holder.imageViewLiked.setImageDrawable(App.getInstance().getResources().getDrawable(R.drawable.ic_star_black));
                break;
        }
    }


    private static class ItemFactory implements ViewHolderFactory<ViewHolder> {
        public ViewHolder create(View v) {
            return new ViewHolder(v);
        }
    }


    @Override
    public ViewHolderFactory<? extends ViewHolder> getFactory() {
        return FACTORY;
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView date;
        public TextView timeRunning;
        public TextView distance;
        public ImageView imageViewLiked;
        public String format;
        public RelativeLayout relativeLayout;


        public ViewHolder(View itemView) {
            super(itemView);
            date = (TextView) itemView.findViewById(R.id.main_fragment_date_text_view);
            timeRunning = (TextView) itemView.findViewById(R.id.main_fragment_time_running_text_view);
            distance = (TextView) itemView.findViewById(R.id.main_fragment_distance_text_view);
            imageViewLiked = (ImageView) itemView.findViewById(R.id.item_image_loved_yes);
            relativeLayout = (RelativeLayout) itemView.findViewById(R.id.adapter_all_fitness_data_relative_layout);
            format = itemView.getResources().getString(R.string.tracks_list_fragment_distance_meter);
        }
    }
}
