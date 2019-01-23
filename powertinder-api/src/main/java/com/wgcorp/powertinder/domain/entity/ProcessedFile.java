package com.wgcorp.powertinder.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by Will on 27/07/2016.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProcessedFile {
    private String url;
    private int height;
    private int width;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }
}
