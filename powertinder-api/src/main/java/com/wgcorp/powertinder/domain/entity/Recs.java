package com.wgcorp.powertinder.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * Created by Will on 24/07/2016.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Recs {
    private List<Person> results;

    public List<Person> getResults() {
        return results;
    }

    public void setResults(List<Person> results) {
        this.results = results;
    }

}
