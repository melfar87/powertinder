package com.wgcorp.powertinder.domain.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wgcorp.powertinder.domain.entity.Person;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DetailResponse {

    @JsonProperty("results")
    private Person person;

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }
}
