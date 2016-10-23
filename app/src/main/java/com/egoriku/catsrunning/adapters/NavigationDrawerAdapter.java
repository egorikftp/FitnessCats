package com.egoriku.catsrunning.adapters;

import android.graphics.Typeface;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.egoriku.catsrunning.App;
import com.egoriku.catsrunning.R;
import com.egoriku.catsrunning.adapters.interfaces.OnItemSelecteListener;
import com.egoriku.catsrunning.models.ItemNavigationDrawer;

import java.util.ArrayList;

import static android.R.color.white;
import static com.egoriku.catsrunning.utils.VectorToDrawable.getDrawable;


public class NavigationDrawerAdapter extends RecyclerView.Adapter<NavigationDrawerAdapter.ViewHolder> {
    private ArrayList<ItemNavigationDrawer> modelArray;
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_MINY = 1;
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
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        if (position == 0) {
            holder.headerName.setText(modelArray.get(position).getUserName());
            holder.headerEmail.setText(modelArray.get(position).getUserEmail());
        } else {
            if (modelArray.get(position).isShowLine()) {
                holder.imageLine.setVisibility(View.VISIBLE);
            }

            if (modelArray.get(position).isSelected()) {
                holder.rootRelativeLayout.setBackgroundColor(App.getInstance().getResources().getColor(R.color.color_background_nav_drawer));
                holder.imageView.setImageDrawable(
                        getDrawable(modelArray.get(position).getImgResId(), R.style.NavDrawerDefaultTheme)
                );
                holder.textViewItem.setTextColor(App.getInstance().getResources().getColor(R.color.colorAccent));
                holder.textViewItem.setTypeface(null, Typeface.BOLD);
                holder.textViewItem.setText(modelArray.get(position).getItemName());
            } else {
                holder.rootRelativeLayout.setBackgroundColor(App.getInstance().getResources().getColor(white));
                holder.imageView.setImageDrawable(
                        getDrawable(modelArray.get(position).getImgResId(), R.style.NavDrawerDefaultThemeUpdate)
                );
                holder.textViewItem.setTextColor(App.getInstance().getResources().getColor(R.color.color_nav_drawer_text));
                holder.textViewItem.setTypeface(null, Typeface.NORMAL);
                holder.textViewItem.setText(modelArray.get(position).getItemName());
            }
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
        return TYPE_MINY;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        protected TextView textViewItem;
        protected ImageView imageView;
        protected ImageView imageLine;
        protected TextView headerName;
        protected TextView headerEmail;
        protected RelativeLayout rootRelativeLayout;


        public ViewHolder(View itemView, int viewType) {
            super(itemView);
            if (viewType == 0) {
                headerName = (TextView) itemView.findViewById(R.id.nav_drawer_user_name);
                headerEmail = (TextView) itemView.findViewById(R.id.naw_drawer_user_email);
            } else {
                textViewItem = (TextView) itemView.findViewById(R.id.naw_drawer_item_text);
                imageView = (ImageView) itemView.findViewById(R.id.naw_drawer_item_image);
                imageLine = (ImageView) itemView.findViewById(R.id.naw_drawer_item_line);
                rootRelativeLayout = (RelativeLayout) itemView.findViewById(R.id.root_layout_nav_drawer_item);
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
