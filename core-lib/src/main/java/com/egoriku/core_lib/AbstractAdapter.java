package com.egoriku.core_lib;

import android.content.Context;
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
        private SparseArrayCompat<String> stringSparseArrayCompat;

        public AbstractViewHolder(final View itemView, final int... ids) {
            super(itemView);

            viewSparseArrayCompat = new SparseArrayCompat<>(ids.length);
            for (final int id : ids) {
                viewSparseArrayCompat.append(id, itemView.findViewById(id));
            }

            stringSparseArrayCompat = new SparseArrayCompat<>();
        }

        @SuppressWarnings("unchecked")
        public <T extends View> T get(final int id) {
            View view = viewSparseArrayCompat.get(id);
            if (view == null) {
                view = itemView.findViewById(id);
            }

            return (T) view;
        }

        public String getString(final int id) {
            String srt = stringSparseArrayCompat.get(id);
            if (srt == null) {
                srt = itemView.getResources().getString(id);
                stringSparseArrayCompat.append(id, srt);
            }

            return srt;
        }

        public Context getContext() {
            return itemView.getContext();
        }
    }
}
