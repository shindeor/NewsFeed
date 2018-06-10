package eu.booksbnb.newsfeed;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Article {
    private String category;
    private String title;
    private String[] authors;
    private String writDate;
    private String webUrl;
    private Bitmap thumbnail;

    public Article(String cat, String name, String urlW, Bitmap thumb) {
        category = cat;
        title = name;
        webUrl = urlW;
        thumbnail = thumb;
    }

    public Article(String cat, String name, String when, String urlW, Bitmap thumb) {
        category = cat;
        title = name;
        writDate = when;
        webUrl = urlW;
        thumbnail = thumb;
    }

    public Article(String cat, String name, String when, String[] writers, String urlW, Bitmap thumb) {
        category = cat;
        title = name;
        writDate = when;
        authors = writers;
        webUrl = urlW;
        thumbnail = thumb;
    }

    public String getCategory() {
        return category;
    }

    public String getNewsTitle() {
        return title;
    }

    public String[] getAuthor() {
        return authors;
    }

    public String getWritDate() {
        return writDate;
    }

    public String getWebUrl() {
        return webUrl;
    }

    public Bitmap getThumbnail() {
        return thumbnail;
    }
}
