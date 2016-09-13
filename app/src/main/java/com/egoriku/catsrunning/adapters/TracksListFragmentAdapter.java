package com.egoriku.catsrunning.adapters;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.egoriku.catsrunning.App;
import com.egoriku.catsrunning.R;
import com.egoriku.catsrunning.adapters.interfaces.IRecyclerViewListener;
import com.egoriku.catsrunning.models.MainFragmentTracksModel;
import com.egoriku.catsrunning.utils.ConverterTime;

import java.util.ArrayList;

public class TracksListFragmentAdapter extends RecyclerView.Adapter<TracksListFragmentAdapter.ViewHolder> {
    private ArrayList<MainFragmentTracksModel> modelArrayList;
    public static IRecyclerViewListener iRecyclerViewListener;

    public TracksListFragmentAdapter(ArrayList<MainFragmentTracksModel> models) {
        this.modelArrayList = models;
    }


    @Override
    public TracksListFragmentAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflater = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_fragment_tracks_list, parent, false);
        return new ViewHolder(inflater);
    }


    @Override
    public void onBindViewHolder(TracksListFragmentAdapter.ViewHolder holder, int position) {
        int likedDigit = 0;

        holder.date.setText(ConverterTime.convertUnixDate(modelArrayList.get(position).getDate()));
        holder.timeRunning.setText(ConverterTime.ConvertTimeToStringWithMill(modelArrayList.get(position).getTimeRunning()));
        holder.distance.setText(String.format(holder.format, modelArrayList.get(position).getDistance()));

        Cursor cursor = App.getInstance().getDb().rawQuery(
                "SELECT Tracks.liked as liked FROM Tracks WHERE Tracks._id = ?", new String[]{String.valueOf(modelArrayList.get(position).getId())});

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


    @Override
    public int getItemCount() {
        return modelArrayList.size();
    }


    public void clear() {
        modelArrayList.clear();
        notifyDataSetChanged();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
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

            itemView.setOnClickListener(this);
            imageViewLiked.setOnClickListener(this);
        }


        @Override
        public void onClick(View view) {
            if (view instanceof ImageView) {
                iRecyclerViewListener.onLikedClick(getPosition());
            } else {
                iRecyclerViewListener.onItemClick(getPosition());
            }
        }
    }


    public void setOnItemClickListener(IRecyclerViewListener listener) {
        this.iRecyclerViewListener = listener;
    }


    public void setAdapterData(ArrayList<MainFragmentTracksModel> adapterData) {
        this.modelArrayList = adapterData;
        notifyDataSetChanged();
    }
}
