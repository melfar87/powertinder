package com.wgcorp.powertinder.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Photo {
    @JsonProperty("id")
    private String id;

    @JsonProperty("url")
    private String url;

    @JsonProperty("processedFiles")
    private List<ProcessedFile> processedFiles;

}
