package com.example.android.booklisting;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Book>> {
    // Base URL to search for 10 books. Needs appending keyword
    private static final String GOOGLE_BOOKS_URL = "https://www.googleapis.com/books/v1/volumes?maxResults=10";

    private BookAdapter mAdapter;
    private String mLastSearchedParameter;
    private int mLastUsedId;
    private TextView mErrorTextView;
    private View mLoadingIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get searchBox
        final EditText searchBox = (EditText) findViewById(R.id.search_box);

        // Get searchButton
        final View searchButton = findViewById(R.id.search_button);

        searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (removeSpaces(s + "").length() == 0) {
                    searchButton.setVisibility(View.GONE);
                } else if (searchButton.getVisibility() == View.GONE && !removeSpaces(s + "").equals(mLastSearchedParameter)) {
                    searchButton.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // Find ListView
        final ListView listView = (ListView) findViewById(R.id.list_view);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String query = searchBox.getText() + ""; // Get text of searchBox
                query = query.replaceAll(" ", ""); // Delete spaces

                searchButton.setVisibility(View.GONE);
                mLoadingIcon.setVisibility(View.VISIBLE);
                mAdapter.clear();

                setErrorMessage("");

                int randomInt = (int) (Math.random() * 10);

                while (randomInt == mLastUsedId) {
                    randomInt = (int) (Math.random() * 10);
                }

                mLastUsedId = randomInt;

                Log.e("RANDOM INT", randomInt + "");

                mLastSearchedParameter = query;
                String searchParameter = "&&q=" + query; // Add characters needed for query
                String completeUrlString = GOOGLE_BOOKS_URL + searchParameter; // Create complete URL

                Bundle bundle = new Bundle();
                bundle.putCharSequence("url", completeUrlString);

                executeLoader(bundle, randomInt);

                Log.e("MainActivity", "Loader initiated with URL: " + completeUrlString);
            }
        });

        // Find error TextView
        mErrorTextView = (TextView) findViewById(R.id.error_text_view);

        listView.setEmptyView(mErrorTextView);

        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();

        if (!(networkInfo != null && networkInfo.isConnectedOrConnecting())) {
            setErrorMessage("No tienes conexión a Internet.\nConéctate y vuelve a iniciar la aplicación.");
            searchBox.setVisibility(View.GONE);
            searchButton.setVisibility(View.GONE);
        }

        // Create adapter and set it on listView
        mAdapter = new BookAdapter(this, new ArrayList<Book>());
        listView.setAdapter(mAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Book currentBook = mAdapter.getItem(position);

                Intent moreInfo = new Intent(Intent.ACTION_VIEW);
                moreInfo.setData(Uri.parse(currentBook.getUrl()));
                startActivity(moreInfo);
            }
        });

        // Find loading icon
        mLoadingIcon = findViewById(R.id.loading_icon);

    }

    @Override
    public void onLoadFinished(Loader<List<Book>> loader, List<Book> data) {
        mLoadingIcon.setVisibility(View.GONE);
        mAdapter.clear();

        if (data != null && !data.isEmpty()) {
            mAdapter.addAll(data);
        } else {
            setErrorMessage(QueryUtils.getErrorMessage());
        }
    }

    @Override
    public Loader<List<Book>> onCreateLoader(int id, Bundle args) {
        Log.e("MainActivity", "Loader created");
        return new BookLoader(this, args.getCharSequence("url") + "");
    }

    @Override
    public void onLoaderReset(Loader<List<Book>> loader) {
        mAdapter.clear();
    }

    private void executeLoader(Bundle args, int id) {
        getLoaderManager().initLoader(id, args, this);
    }

    private String removeSpaces(String string) {
        return string.replaceAll(" ", "");
    }

    private void setErrorMessage(String errorMessage) {
        mErrorTextView.setText(errorMessage);
    }
}
