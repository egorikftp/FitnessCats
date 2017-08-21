package com.egoriku.catsrunning.adapters;

import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.egoriku.catsrunning.R;
import com.egoriku.catsrunning.adapters.interfaces.ILikedClickListener;
import com.egoriku.catsrunning.models.AllFitnessDataModel;
import com.egoriku.catsrunning.utils.ConverterTime;

import java.util.List;

import static com.egoriku.catsrunning.models.Constants.Color.COLOR_NOW_FIT;
import static com.egoriku.catsrunning.utils.VectorToDrawable.setImageAdapter;

public class LikedFragmentAdapter extends AbstractAdapter<AllFitnessDataModel> {
    private List<AllFitnessDataModel> modelList;
    public ILikedClickListener iLikedClickListener;


    public LikedFragmentAdapter() {
    }


    public LikedFragmentAdapter(ILikedClickListener iLikedClickListener) {
        this.iLikedClickListener = iLikedClickListener;
    }


    @Override
    public AbstractViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_fragment_liked, parent, false);
        return new AbstractViewHolder(view);
    }


    @Override
    public int getItemCount() {
        return modelList.size();
    }


    @Override
    public void onBind(AbstractViewHolder holder, AllFitnessDataModel model, final int position, int viewType) {
        final String formatDistance = holder.getString(R.string.liked_fragment_adapter_distance_meter);
        final String formatCalories = holder.getString(R.string.liked_fragment_adapter_calories);
        final ImageView imageViewLiked = holder.<ImageView>get(R.id.adapter_fragment_liked_image_view_liked);
        final ImageView imageViewType = holder.<ImageView>get(R.id.adapter_fragment_liked_data_ic_type);
        final CardView cardView = holder.<CardView>get(R.id.adapter_fragment_liked_root_card_view);
        final TextView textViewDate = holder.<TextView>get(R.id.adapter_fragment_liked_date_text_view);
        final TextView textViewTime = holder.<TextView>get(R.id.adapter_fragment_liked_time_running_text_view);

        if (model.getTime() == 0) {
            textViewDate.setTypeface(null, Typeface.BOLD);
            textViewTime.setTypeface(null, Typeface.BOLD);
            cardView.setCardBackgroundColor(Color.parseColor(COLOR_NOW_FIT));
        }

        textViewDate.setText(ConverterTime.convertUnixDateWithoutHours(model.getBeginsAt()));
        textViewTime.setText(ConverterTime.ConvertTimeAllFitnessData(model.getBeginsAt(), model.getTime()));

        holder.<TextView>get(R.id.adapter_fragment_liked_distance_text_view)
                .setText(String.format(formatDistance, modelList.get(position).getDistance()));

        holder.<TextView>get(R.id.adapter_fragment_liked_calories)
                .setText(String.format(formatCalories, modelList.get(position).getCalories()));

        imageViewLiked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (iLikedClickListener != null) {
                    iLikedClickListener.onLikedClick(modelList.get(position), position);
                }
            }
        });

        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (iLikedClickListener != null) {
                    iLikedClickListener.onItemClick(modelList.get(position), position);
                }
            }
        });

        setImageAdapter(imageViewLiked, R.drawable.ic_vec_star_black);

        switch (model.getTypeFit()) {
            case 1:

                setImageAdapter(imageViewType, R.drawable.ic_vec_directions_walk_40dp);
                break;

            case 2:
                setImageAdapter(imageViewType, R.drawable.ic_vec_directions_run_40dp);
                break;

            case 3:
                setImageAdapter(imageViewType, R.drawable.ic_vec_directions_bike_40dp);
                break;
        }
    }


    @Override
    public AllFitnessDataModel getItem(int position) {
        return modelList.get(position);
    }


    public void setOnItemClickListener(ILikedClickListener listener) {
        this.iLikedClickListener = listener;
    }


    public void setData(List<AllFitnessDataModel> allFitnessDataModels) {
        this.modelList = allFitnessDataModels;
        notifyDataSetChanged();
    }
}
