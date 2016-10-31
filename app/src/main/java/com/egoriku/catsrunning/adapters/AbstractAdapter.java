package com.egoriku.catsrunning.adapters;

import android.support.v4.util.SparseArrayCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public abstract class AbstractAdapter<Item> extends RecyclerView.Adapter<AbstractAdapter.AbstractViewHolder> {

    public abstract void onBind(AbstractViewHolder holder, Item item, int position, int viewType);


    public abstract Item getItem(int position);


    @Override
    public void onBindViewHolder(AbstractViewHolder holder, int position) {
        onBind(holder, getItem(position), position, getItemViewType(position));
    }


    public static class AbstractViewHolder extends RecyclerView.ViewHolder {

        private SparseArrayCompat<View> viewSparseArrayCompat;

        public AbstractViewHolder(final View itemView, final int... ids) {
            super(itemView);

            viewSparseArrayCompat = new SparseArrayCompat<>(ids.length);
            for (final int id : ids) {
                viewSparseArrayCompat.append(id, itemView.findViewById(id));
            }
        }


        public <T> T get(final int id) {
            View view = viewSparseArrayCompat.get(id);
            if (view == null) {
                view = itemView.findViewById(id);
            }
            return (T) view;
        }


        public String getString(final int id){
            return itemView.getResources().getString(id);
        }
    }
}
