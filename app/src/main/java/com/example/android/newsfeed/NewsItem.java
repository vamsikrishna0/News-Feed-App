package com.example.android.newsfeed;

/**
 * Created by Vamsi on 7/5/2016.
 */
public class NewsItem {
    private static final String NO_THUMBNAIL = "noString";
    private String webTitle;
    private String webUrl;
    private String thumbnailLink = NO_THUMBNAIL;

    public NewsItem(String webTitle, String webUrl, String thumbnailLink) {
        this.webTitle = webTitle;
        this.webUrl = webUrl;
        this.thumbnailLink = thumbnailLink;
    }

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
