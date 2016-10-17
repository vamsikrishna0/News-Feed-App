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

        //A Uri.Builder object is created. It makes building API query strings easy
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http")
                .authority("content.guardianapis.com")
                .appendPath("search")
                .appendQueryParameter("show-fields", "thumbnail")
                .appendQueryParameter("q", queryString)
                .appendQueryParameter("api-key", "test");
        stringUrl = builder.build().toString();

        try {
            //Here the getNewsFeed() method is called which gets the newsfeed for a particular string
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

        //Check if the network is active
        if (networkInfo != null && networkInfo.isConnected()) {

            // The news feed is retrieved in an AsyncTask class. execute method takes in the stringUrl and starts data retrieval
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

        /* This method puts the returned data onto the listView using an Adapter.
        * */
        @Override
        protected void onPostExecute(String res) {

            try {
                //The response is a String. Its converted to a JSON object.
                JSONObject object = new JSONObject(res).getJSONObject("response");
                int numberOfItems = object.getInt("total");

                //Check if the search doesn't return any results and make a Toast message and return.
                if (numberOfItems == 0) {
                    Toast.makeText(MainActivity.this, "No search matching your query", Toast.LENGTH_SHORT).show();
                    return;
                }

                //A JSONArray of results is created.
                JSONArray items = object.getJSONArray("results");


                //For each item we add it to a NewsItem ArrayList object.
                for (int i = 0; i < items.length(); i++) {
                    JSONObject item = items.getJSONObject(i);

                    String title = item.getString("webTitle");
                    String webLink = item.getString("webUrl");

                    //Check if it has thumbnail and call the appropriate constructor accordingly
                    if (item.has("fields") && item.getJSONObject("fields").has("thumbnail")) {
                        String thumbnailLink = item.getJSONObject("fields").getString("thumbnail");

                        newsItems.add(new NewsItem(title, webLink, thumbnailLink));
                    } else
                        newsItems.add(new NewsItem(title, webLink));
                }

                //Get the ListView item.
                ListView listView = (ListView) findViewById(R.id.news_list);

                //Connect the ArrayList to the Adapter(NewsAdapter).
                //Create a new Adapter class.
                NewsAdapter adapter = new NewsAdapter(MainActivity.this, newsItems);

                //Set a onClickListener on each item of the list.
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        // We use position to get the corresponding item.
                        // We create a  new Intent and start it.
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(newsItems.get(position).getWebUrl()));
                        startActivity(intent);
                    }
                });
                //Actually plug the adapter to the ListView
                listView.setAdapter(adapter);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
}
