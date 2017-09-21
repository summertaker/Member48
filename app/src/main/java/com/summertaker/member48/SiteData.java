package com.summertaker.member48;

import java.util.ArrayList;
import java.util.List;

public class SiteData {
    private String title;
    private String userAgent;
    private List<String> urls = new ArrayList<>();

    public SiteData(String title, String userAgent, List<String> urls) {
        this.title = title;
        this.userAgent = userAgent;
        this.urls = urls;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public List<String> getUrls() {
        return urls;
    }

    public void setUrls(List<String> urls) {
        this.urls = urls;
    }
}
