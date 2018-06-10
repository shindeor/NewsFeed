package eu.booksbnb.newsfeed;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;

import java.util.ArrayList;
import java.util.List;

public class NewsFeedActivity extends AppCompatActivity implements LoaderCallbacks<List<Article>> {
    //Guardian Query URL
    private static final String GUARDIAN_REQUEST_URL = "https://content.guardianapis.com/search";

    private static final int NEWS_LOADER_ID = 1;

    public static final String LOG_TAG = NewsFeedActivity.class.getName();
    private NewsAdapter adapter;
    private TextView emptyView;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_feed);

        //Declare Views
        ListView articlesView = (ListView) findViewById(R.id.list);
        emptyView = (TextView) findViewById(R.id.empty);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        //Check for connectivity
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            //Get reference to LoaderManager
            LoaderManager loaderManager = getLoaderManager();
            loaderManager.initLoader(NEWS_LOADER_ID, null, this);
        } else {
            progressBar.setVisibility(View.GONE);
            emptyView.setText(getString(R.string.no_connection));
        }

        articlesView.setEmptyView(emptyView);

        //Create the articles ArrayAdapter
        adapter = new NewsAdapter(this, android.R.layout.simple_list_item_1, new ArrayList<Article>());

        //Set the adapter to the ListView to populate the UI
        articlesView.setAdapter(adapter);

        //Set an item click listener on ListView, sending intents to open browser
        articlesView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //Find current article
                Article currentArticle = adapter.getItem(i);

                //Convert string URL to Url Object
                Uri articleUrl = Uri.parse(currentArticle.getWebUrl());

                //Create Intent
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, articleUrl);

                //Send intent to open browser
                startActivity(websiteIntent);
            }
        });
    }

    @Override
    public Loader<List<Article>> onCreateLoader(int i, Bundle bundle) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        //GetString retrieves a string value from prefs. Second param is default value for this pref
        String searchKeyword = sharedPrefs.getString(
                getString(R.string.settings_keyword_key),
                getString(R.string.settings_keyword_default));
        String sectionPref = sharedPrefs.getString(
                getString(R.string.settings_filter_category_key),
                getString(R.string.settings_filter_category_default));
        //Parse breaks apart the Uri string passed into the param
        Uri baseUri = Uri.parse(GUARDIAN_REQUEST_URL);
        //BuildUpon prepares BaseUri so we can add params
        Uri.Builder uriBuilder = baseUri.buildUpon();
        //Append query params and value
        uriBuilder.appendQueryParameter("page-size", "20");
        uriBuilder.appendQueryParameter("show-tags", "contributor");
        uriBuilder.appendQueryParameter("show-fields", "thumbnail");
        if (searchKeyword.length() > 3 ) {
            uriBuilder.appendQueryParameter("q", searchKeyword);
        }
        if (sectionPref.length() > 3) {
            uriBuilder.appendQueryParameter("section", sectionPref);
        }
        uriBuilder.appendQueryParameter("api-key", "09f8c1b2-610f-46ef-89f9-6f5ed35eb4ce");
        //Create new loader for given URL
        return new NewsLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<Article>> loader, List<Article> articles) {
        //Update UI with result
        adapter.clear();
        //If there is a list of articles, add them to adapter's data set, triggering ListView to update
        if (articles != null && !articles.isEmpty()) {
            adapter.addAll(articles);
        }
        emptyView.setText(getString(R.string.no_articles));
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onLoaderReset(Loader<List<Article>> loader) {
        adapter.clear();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
