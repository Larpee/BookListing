package com.example.android.booklisting;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by pilar_000 on 17/07/2017.
 */

class BookAdapter extends ArrayAdapter<Book> {
    public BookAdapter(Context context, List<Book> books) {
        super(context, -1, books);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;

        if (listItem == null) {
            listItem = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }

        Book currentBook = getItem(position);

        TextView title = (TextView) listItem.findViewById(R.id.title);
        title.setText(currentBook.getTitle());

        TextView author = (TextView) listItem.findViewById(R.id.author);
        author.setText(currentBook.getAuthors());

        TextView description = (TextView) listItem.findViewById(R.id.description);
        description.setText(currentBook.getDescription());

        return listItem;
    }
}
