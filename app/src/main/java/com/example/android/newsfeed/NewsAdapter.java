package com.example.android.newsfeed;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by Vamsi on 7/5/2016.
 */
public class NewsAdapter extends ArrayAdapter<NewsItem> {

    // A constructor which takes the List Item
    public NewsAdapter(Context context, ArrayList<NewsItem> newsItems) {
        super(context, 0, newsItems);
    }

    // The convertView is reused.
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item, parent, false);
        }

        //getItem returns the item at the position. Here it is a NewsItem object
        final NewsItem currentNewsItem = getItem(position);

        // The text from the current NewsItem is set to the TextView.
        TextView titleTextView = (TextView) listItemView.findViewById(R.id.title_text_view);
        titleTextView.setText(currentNewsItem.getWebTitle());


        // The thubnail is added to the ImageView, if the NewsItem has a ThumbNail. That happens
        // if the response for that item has a thumbnail.
        ImageView thumbnailImageView = (ImageView) listItemView.findViewById(R.id.image);
        if (currentNewsItem.hasThumbnail()) {
            //The thumbnail is downloaded in a separate AsyncTask from the link we get in the response.
            new DownloadImageTask(thumbnailImageView)
                    .execute(currentNewsItem.getThumbnailLink());
        } else {
            thumbnailImageView.setVisibility(View.GONE);
        }

        return listItemView;
    }


    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            // All the urls sent to the AsyncTask come as "urls" array
            //We take the first link (which is the only one in this case)
            String urldisplay = urls[0];
            Bitmap icon = null;
            try {
                // We use InputStream to create a stream from an URL
                InputStream in = new java.net.URL(urldisplay).openStream();
                // A Bitmap object(android.graphics) is created and the response is given to it.
                icon = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return icon;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}
