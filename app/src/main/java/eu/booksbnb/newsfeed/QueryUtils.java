package eu.booksbnb.newsfeed;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;

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

import static eu.booksbnb.newsfeed.NewsFeedActivity.LOG_TAG;

public class QueryUtils {

    //Create private constructor, QueryUtils should never be created as object
    private QueryUtils() {
    }

    public static List<Article> extractArticlesFromJson(String jsonResponse) {
        //If JSON string is empty or null, return early
        if (TextUtils.isEmpty(jsonResponse)) {
            return null;
        }

        //Create empty ArrayList to add articles to
        List<Article> articles = new ArrayList<>();

        //Try to parse the JSON response, if there is a problem, throw a JSONException
        //Catch the exception so app doesn't crash, print error message to log.
        try {
            //Build a list of Article objects with corresponding data
            JSONObject root = new JSONObject(jsonResponse);
            JSONObject response = root.getJSONObject("response");
            JSONArray results = response.getJSONArray("results");
            for (int idx = 0; idx < results.length(); idx++) {
                Bitmap finalThumb = null;
                JSONObject article = results.getJSONObject(idx);
                String category = article.getString("sectionName");
                String title = article.getString("webTitle");
                String date = article.getString("webPublicationDate");
                String webUrl = article.getString("webUrl");
                JSONObject fields = article.getJSONObject("fields");
                String thumbnail = fields.getString("thumbnail");
                JSONArray contributors = article.getJSONArray("tags");
                String[] authorsList = new String[contributors.length()];
                for (int ith = 0; ith < contributors.length(); ith++) {
                    JSONObject author = contributors.getJSONObject(ith);
                    String authorName = author.getString("webTitle");
                    authorsList[ith] = authorName;
                }
                String[] dateP = date.split("T");
                try {
                    InputStream is = new URL(thumbnail).openStream();
                    finalThumb = BitmapFactory.decodeStream(is);
                } catch (IOException e) {
                    Log.e(LOG_TAG, "Problem fetching thumbnail url.", e);
                }
                articles.add(new Article(category, title, dateP[0], authorsList, webUrl, finalThumb));
            }

        } catch (JSONException e) {
            Log.e("QueryUtils", "Problem parsing Articles JSON results", e);
        }

        return articles;
    }

    //Returns new URL object from String Url
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building URL", e);
        }
        return url;
    }

    //Make an HTTPRequest to URL and return a String as response
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        //If URL is null, return early
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            //If request is successful read Input Stream and parse response
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving articles JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return jsonResponse;
    }

    //Convert input stream to String which contains JSON response
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    public static List<Article> fetchArticleData(String requestUrl) {
        //Create URL object
        URL url = createUrl(requestUrl);

        //Perform HTTPRequest to URL and receive JSON
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the Http Request.", e);
        }
        //Extract relevant fields from JSON to create list
        List<Article> articles = extractArticlesFromJson(jsonResponse);

        return articles;
    }
}
