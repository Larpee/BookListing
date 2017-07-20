package com.example.android.booklisting;

import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by pilar_000 on 17/07/2017.
 */

class QueryUtils {
    private static final String LOG_TAG = "QueryUtils";
    private static String errorMessage;

    public QueryUtils (){
        makeUrl("");
    }

    private static URL makeUrl(String stringUrl) {
        URL url = null;

        try {
            url = new URL(stringUrl);
        }

        catch (MalformedURLException e) {
            errorMessage = "Problema formando el link al servidor.";
        }

        return url;
    }

    private static String makeHttpRequest(URL url) throws IOException{
        String jsonResponse = "";

        if (url == null) {
            return null;
        }

        HttpURLConnection connection = null;
        InputStream inputStream = null;

        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setReadTimeout(10000);
            connection.setConnectTimeout(15000);
            connection.connect();

            int responseCode = connection.getResponseCode();

            if (responseCode == 200) {
                inputStream = connection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            }

            else {
                errorMessage = "Código de error: " + responseCode + ".";
            }
        }

        catch (IOException e) {
            errorMessage = "Error: " + e.getCause() + ".";
        }

        finally {
            if (connection != null) {
                connection.disconnect();
            }

            if (inputStream != null) {
                inputStream.close();
            }
        }

        return jsonResponse;
    }

    private static String readFromStream(InputStream inputStream) throws IOException{
        StringBuilder output = new StringBuilder();

        InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
        BufferedReader reader = new BufferedReader(inputStreamReader);

        String line = reader.readLine();

        while (line != null) {
            output.append(line);
            line = reader.readLine();
        }

        return output.toString();
    }

    private static List<Book> extractBooks(String json) {
        if (TextUtils.isEmpty(json)) {
            return null;
        }

        List<Book> books = new ArrayList<>();

        try {
            JSONObject root = new JSONObject(json);

            if (root.getInt("totalItems") == 0) {
                errorMessage = "No se encontraron libros.";
                return null;
            }

            JSONArray itemsArray = root.getJSONArray("items");

            for (int i = 0; i < itemsArray.length(); i++) {
                JSONObject currentBook = itemsArray.getJSONObject(i);
                JSONObject bookInfo = currentBook.getJSONObject("volumeInfo");

                String title = bookInfo.optString("title", null);

                JSONArray authors = bookInfo.optJSONArray("authors");
                String[] authorsArray = toArray(authors);

                String description = bookInfo.optString("description", null);

                String url = bookInfo.optString("infoLink", null);

                if (description == null || authors == null || title == null || url == null) {
                    continue;
                }

                books.add(new Book(title, authorsArray, description, url));
            }
        }

        catch (JSONException e) {
            errorMessage = "Error procesando respuesta del servidor.";
        }

        return books;
    }

    private static String[] toArray(JSONArray jsonArray) {
        if (jsonArray == null) {
            return null;
        }

        ArrayList<String> arrayList = new ArrayList<>();

        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                arrayList.add(jsonArray.getString(i));
            }
        }

        catch (JSONException e) {
            errorMessage = "Error procesando la respuesta del servidor";
        }

        return arrayList.toArray(new String[arrayList.size()]);
    }

    public static List<Book> fetchData(String stringUrl) {
        URL url = makeUrl(stringUrl);

        if (url == null) {
            return null;
        }

        String json = null;

        try {
            json = makeHttpRequest(url);
        }

        catch (IOException e) {
            errorMessage = "Error: " + e.getCause();
        }

        if (!TextUtils.isEmpty(json)) {
            return extractBooks(json);
        }

        else {
            return null;
        }
    }

    public static String getErrorMessage() {
        return errorMessage;
    }

}
