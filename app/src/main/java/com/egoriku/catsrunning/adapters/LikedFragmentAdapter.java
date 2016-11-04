package com.egoriku.catsrunning.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.egoriku.catsrunning.R;
import com.egoriku.catsrunning.adapters.interfaces.IRecyclerViewListener;
import com.egoriku.catsrunning.models.LikedTracksModel;
import com.egoriku.catsrunning.utils.ConverterTime;

import java.util.ArrayList;

import static com.egoriku.catsrunning.utils.VectorToDrawable.setImageAdapter;

public class LikedFragmentAdapter extends RecyclerView.Adapter<LikedFragmentAdapter.ViewHolder> {
    private ArrayList<LikedTracksModel> modelArrayList;
    public static IRecyclerViewListener iRecyclerViewListener;

    public LikedFragmentAdapter(ArrayList<LikedTracksModel> models) {
        this.modelArrayList = models;
    }


    @Override
    public LikedFragmentAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflater = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_fragment_liked, parent, false);

        return new ViewHolder(inflater);
    }


    @Override
    public void onBindViewHolder(LikedFragmentAdapter.ViewHolder holder, int position) {
        holder.date.setText(ConverterTime.convertDateReminder(modelArrayList.get(position).getDate()));
        holder.timeRunning.setText(ConverterTime.ConvertTimeToString(modelArrayList.get(position).getTimeRunning()));
        holder.distance.setText(String.format(holder.format, modelArrayList.get(position).getDistance()));
        setImageAdapter(holder.imageViewLiked, R.drawable.ic_vec_star_black);

    }


    @Override
    public int getItemCount() {
        return modelArrayList.size();
    }



    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView date;
        public TextView timeRunning;
        public TextView distance;
        public ImageView imageViewLiked;
        public String format;


        public ViewHolder(View itemView) {
            super(itemView);
            date = (TextView) itemView.findViewById(R.id.liked_fragment_date_text_view);
            timeRunning = (TextView) itemView.findViewById(R.id.liked_fragment_time_running_text_view);
            distance = (TextView) itemView.findViewById(R.id.liked_fragment_distance_text_view);
            imageViewLiked = (ImageView) itemView.findViewById(R.id.liked_fragment_distance_image_view_liked);
            format = itemView.getResources().getString(R.string.liked_fragment_adapter_distance_meter);

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


    public void clear() {
        modelArrayList.clear();
        notifyDataSetChanged();
    }
}
