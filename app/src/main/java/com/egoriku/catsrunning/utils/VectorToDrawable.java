package com.egoriku.catsrunning.utils;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.view.ContextThemeWrapper;
import android.widget.ImageView;

import com.egoriku.catsrunning.App;

public class VectorToDrawable {

    public static Bitmap createBitmapFromVector(Resources resources, int vectorResourceId) {
        VectorDrawableCompat vectorDrawableCompat = VectorDrawableCompat.create(resources, vectorResourceId, null);

        Bitmap bitmap = Bitmap.createBitmap(
                vectorDrawableCompat.getIntrinsicWidth(),
                vectorDrawableCompat.getIntrinsicHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawableCompat.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        vectorDrawableCompat.draw(canvas);
        return bitmap;
    }


    public static Drawable getDrawable(int resId, int navDrawerTheme) {
        ContextThemeWrapper wrapper = new ContextThemeWrapper(App.getInstance(), navDrawerTheme);
        return VectorDrawableCompat.create(
                App.getInstance().getResources(),
                resId,
                wrapper.getTheme()
        );
    }

    public static Drawable getDrawable(int resId) {
        return VectorDrawableCompat.create(App.getInstance().getResources(), resId, null);
    }


    public static void setImageAdapter(ImageView imageViewLiked, int ic_vec) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            imageViewLiked.setImageDrawable(App.getInstance().getResources().getDrawable(ic_vec, App.getInstance().getTheme()));
        } else {
            imageViewLiked.setImageBitmap(createBitmapFromVector(App.getInstance().getResources(), ic_vec));
        }
    }
}
