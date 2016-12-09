package com.egoriku.catsrunning.helpers;

import android.os.AsyncTask;

public abstract class BaseAsyncWriter extends AsyncTask<Void, Void, Void> {

    @Override
    protected abstract Void doInBackground(Void... voids);
}
