package com.example.android.booklisting;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import java.util.List;

/**
 * Created by pilar_000 on 17/07/2017.
 */

class BookLoader extends AsyncTaskLoader<List<Book>> {
    private String mUrl;

    public BookLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<Book> loadInBackground() {
        Log.e("BookLoader", "Loading in background");
        return QueryUtils.fetchData(mUrl);
    }
}
