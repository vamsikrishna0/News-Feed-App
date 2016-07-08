package com.example.android.newsfeed;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {
    private static final String NO_RESPONSE = "No Server Response";
    private static final String NET_CONNECT_PROBLEM = "Network Connectivity problem";
    static ArrayList<NewsItem> newsItems = new ArrayList<>();
    String queryString = "Trump";
    String stringUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http")
                .authority("content.guardianapis.com")
                .appendPath("search")
                .appendQueryParameter("show-fields", "thumbnail")
                .appendQueryParameter("q", queryString).appendQueryParameter("api-key", "test");
        stringUrl = builder.build().toString();

        try {
            getNewsFeed();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void getNewsFeed() throws ExecutionException, InterruptedException {

        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            new GetNewsFeedTask().execute(stringUrl);

        } else {
            Toast.makeText(MainActivity.this, NET_CONNECT_PROBLEM, Toast.LENGTH_SHORT).show();

        }
        stringUrl = null;
        queryString = null;
    }

    class GetNewsFeedTask extends AsyncTask<String, Void, String> {

        protected String doInBackground(String... urls) {

            try {
                URL url = new URL(urls[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                    return stringBuilder.toString();
                } finally {
                    urlConnection.disconnect();
                }
            } catch (Exception e) {
                Toast.makeText(MainActivity.this, NO_RESPONSE, Toast.LENGTH_SHORT).show();
                return null;
            }

        }

        @Override
        protected void onPostExecute(String res) {

            try {
                JSONObject object = new JSONObject(res).getJSONObject("response");
                int numberOfItems = object.getInt("total");
                if (numberOfItems == 0) {
                    Toast.makeText(MainActivity.this, "No search matching your query", Toast.LENGTH_SHORT).show();
                    return;
                }
                JSONArray items = object.getJSONArray("results");


                for (int i = 0; i < items.length(); i++) {
                    JSONObject item = items.getJSONObject(i);

                    String title = item.getString("webTitle");
                    String webLink = item.getString("webUrl");
                    if (item.has("fields") && item.getJSONObject("fields").has("thumbnail")) {
                        String thumbnailLink = item.getJSONObject("fields").getString("thumbnail");

                        newsItems.add(new NewsItem(title, webLink, thumbnailLink));
                    } else
                        newsItems.add(new NewsItem(title, webLink));
                }

                ListView listView = (ListView) findViewById(R.id.news_list);
                NewsAdapter adapter = new NewsAdapter(MainActivity.this, newsItems);

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(newsItems.get(position).getWebUrl()));
                        startActivity(intent);
                    }
                });
                listView.setAdapter(adapter);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
}
