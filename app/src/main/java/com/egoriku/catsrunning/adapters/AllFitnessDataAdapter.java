package com.egoriku.catsrunning.adapters;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.egoriku.catsrunning.App;
import com.egoriku.catsrunning.R;
import com.egoriku.catsrunning.models.Firebase.Point;
import com.egoriku.catsrunning.utils.ConverterTime;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.mikepenz.fastadapter.utils.ViewHolderFactory;

import java.util.ArrayList;
import java.util.List;

import static com.egoriku.catsrunning.utils.VectorToDrawable.setImageAdapter;

public class AllFitnessDataAdapter extends AbstractItem<AllFitnessDataAdapter, AllFitnessDataAdapter.ViewHolder> {

    private static final ViewHolderFactory<? extends ViewHolder> FACTORY = new ItemFactory();

    public AllFitnessDataAdapter() {
    }

    private long beginsAt;
    private long time;
    private long distance;
    private int id;
    private int liked;
    private int typeFit;
    private String trackToken;
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

    public String getTrackToken() {
        return trackToken;
    }

    public void setTrackToken(String trackToken) {
        this.trackToken = trackToken;
    }

    public int getTypeFit() {
        return typeFit;
    }

    public void setTypeFit(int typeFit) {
        this.typeFit = typeFit;
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
        holder.distance.setText(String.format(holder.formatMeters, distance));

        Cursor cursor = App.getInstance().getDb().rawQuery(
                "SELECT liked FROM Tracks WHERE _id = ?", new String[]{String.valueOf(getId())});

        if (cursor != null) {
            if (cursor.moveToNext()) {
                liked = cursor.getInt(cursor.getColumnIndexOrThrow("liked"));
            }
            cursor.close();
        }

        switch (liked) {
            case 0:
                setImageAdapter(holder.imageViewLiked, R.drawable.ic_vec_star_border);
                break;

            case 1:
                setImageAdapter(holder.imageViewLiked, R.drawable.ic_vec_star_black);
                break;
        }

        switch (typeFit) {
            case 1:
                setImageAdapter(holder.imageViewType, R.drawable.ic_vec_directions_walk_40dp);
                break;

            case 2:
                setImageAdapter(holder.imageViewType, R.drawable.ic_vec_directions_run_40dp);
                break;

            case 3:
                setImageAdapter(holder.imageViewType, R.drawable.ic_vec_directions_bike_40dp);
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
        public ImageView imageViewType;
        public ImageView imageViewLiked;
        public TextView date;
        public TextView timeRunning;
        public TextView distance;
        public TextView calories;

        public String formatMeters;
        public String formatCalories;

        public ViewHolder(View itemView) {
            super(itemView);
            imageViewType = (ImageView) itemView.findViewById(R.id.adapter_all_fitness_data_ic_type);
            imageViewLiked = (ImageView) itemView.findViewById(R.id.adapter_all_fitness_data_image_liked);
            date = (TextView) itemView.findViewById(R.id.adapter_all_fitness_data_date_text);
            timeRunning = (TextView) itemView.findViewById(R.id.adapter_all_fitness_data_time_text_view);
            distance = (TextView) itemView.findViewById(R.id.adapter_all_fitness_data_distance_text_view);
            calories = (TextView) itemView.findViewById(R.id.adapter_all_fitness_data_calories_text_view);
            formatMeters = itemView.getResources().getString(R.string.adapter_all_fitness_data_distance_meter);
            formatCalories = itemView.getResources().getString(R.string.adapter_all_fitness_data_calories);
        }
    }
}
