package com.example.android.newsfeed;

/**
 * Created by Vamsi on 7/5/2016.
 */
public class NewsItem {
// This is the Data object created for each listItem

    private static final String NO_THUMBNAIL = "noString";

    //The 3 variables to store the data
    private String webTitle;
    private String webUrl;
    private String thumbnailLink = NO_THUMBNAIL;

    //Constructor for the case with a thumbnail
    public NewsItem(String webTitle, String webUrl, String thumbnailLink) {
        this.webTitle = webTitle;
        this.webUrl = webUrl;
        this.thumbnailLink = thumbnailLink;
    }

    //Constructor for the case "without" a thumbnail
    public NewsItem(String webTitle, String webUrl) {
        this.webTitle = webTitle;
        this.webUrl = webUrl;
    }

    public String getWebTitle() {
        return webTitle;
    }

    public String getWebUrl() {
        return webUrl;
    }

    public String getThumbnailLink() {
        return thumbnailLink;
    }

    public boolean hasThumbnail() {
        return !this.thumbnailLink.equals(NO_THUMBNAIL);
    }
}
