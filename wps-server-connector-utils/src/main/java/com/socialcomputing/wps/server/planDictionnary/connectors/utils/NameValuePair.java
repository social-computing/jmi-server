package com.socialcomputing.wps.server.planDictionnary.connectors.utils;

public class NameValuePair {
    private String name = null;
    private String value = null;

    public NameValuePair(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

}
