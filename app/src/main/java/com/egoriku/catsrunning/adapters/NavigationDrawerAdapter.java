package com.egoriku.catsrunning.adapters;

import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.egoriku.catsrunning.App;
import com.egoriku.catsrunning.R;
import com.egoriku.catsrunning.adapters.interfaces.OnItemSelecteListener;
import com.egoriku.catsrunning.models.ItemNavigationDrawer;

import java.util.ArrayList;

import static com.egoriku.catsrunning.utils.VectorToBitmap.createBitmapFromVector;


public class NavigationDrawerAdapter extends RecyclerView.Adapter<NavigationDrawerAdapter.ViewHolder> {
    private ArrayList<ItemNavigationDrawer> modelArray;
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_MENY = 1;
    private OnItemSelecteListener onItemSelecteListener;

    public NavigationDrawerAdapter(ArrayList<ItemNavigationDrawer> modelArray) {
        this.modelArray = modelArray;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflater;
        if (viewType == TYPE_HEADER) {
            inflater = LayoutInflater.from(parent.getContext()).inflate(R.layout.navigation_drawer_header, parent, false);
        } else {
            inflater = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_navigation_drawer, parent, false);
        }
        return new ViewHolder(inflater, viewType);
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (position == 0) {
            holder.headerName.setText(modelArray.get(position).getUserName());
            holder.headerEmail.setText(modelArray.get(position).getUserEmail());
        } else {
            if (modelArray.get(position).isSelected()) {
                holder.textViewItem.setTextColor(App.getInstance().getResources().getColor(R.color.colorPrimary));
                setImage(holder.imageView, modelArray.get(position).getImgResId());
                holder.textViewItem.setText(modelArray.get(position).getItemName());
            } else {
                holder.textViewItem.setTextColor(App.getInstance().getResources().getColor(R.color.color_naw_drawer_text));
                setImage(holder.imageView, modelArray.get(position).getImgResId());
                holder.textViewItem.setText(modelArray.get(position).getItemName());
            }
        }
    }


    private void setImage(ImageView image, int imgRes) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            image.setImageDrawable(App.getInstance().getResources().getDrawable(imgRes, App.getInstance().getTheme()));
        } else {
            image.setImageBitmap(createBitmapFromVector(App.getInstance().getResources(), imgRes));
        }
    }


    @Override
    public int getItemCount() {
        return modelArray.size();
    }


    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_HEADER;
        }
        return TYPE_MENY;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewItem;
        public ImageView imageView;
        public TextView headerName;
        public TextView headerEmail;

        public ViewHolder(View itemView, int viewType) {
            super(itemView);
            if (viewType == 0) {
                headerName = (TextView) itemView.findViewById(R.id.nav_drawer_user_name);
                headerEmail = (TextView) itemView.findViewById(R.id.naw_drawer_user_email);
            } else {
                textViewItem = (TextView) itemView.findViewById(R.id.naw_drawer_item_text);
                imageView = (ImageView) itemView.findViewById(R.id.naw_drawer_item_image);
            }

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onItemSelecteListener.onItemSelected(view, getAdapterPosition());
                }
            });
        }
    }


    public void setOnItemSelecteListener(OnItemSelecteListener onItemSelectedListener) {
        this.onItemSelecteListener = onItemSelectedListener;
    }
}
