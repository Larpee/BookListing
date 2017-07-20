package com.example.android.booklisting;

/**
 * Created by pilar_000 on 17/07/2017.
 */

class Book {
    private String mTitle;
    private String[] mAuthors;
    private String mDescription;
    private String mUrl;

    public Book(String title, String[] authors, String description, String url) {
        mTitle = title;
        mAuthors = authors;
        mDescription = description;
        mUrl = url;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getAuthors() {
        StringBuilder authors = new StringBuilder(mAuthors[0]); // Append first author

        // If there are any remaining authors, sort through them and add a comma and a line break before each one
        for (int i = 1; i < mAuthors.length; i++) {
            authors.append(",\n" + mAuthors[i]);
        }

        return authors.toString();
    }

    public String getDescription() {
        return mDescription;
    }

    public String getUrl() {
        return mUrl;
    }
}
