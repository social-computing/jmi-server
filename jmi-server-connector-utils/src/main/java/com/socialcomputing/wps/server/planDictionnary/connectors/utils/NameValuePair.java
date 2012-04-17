package com.socialcomputing.wps.server.planDictionnary.connectors.utils;

public class NameValuePair {
    private final String name;
    private final String value;
    private final String defaultValue;

    public NameValuePair(String name) {
        this(name, null, null);
    }
    
    public NameValuePair(String name, String value) {
        this(name, value, null);
    }

    public NameValuePair(String name, String value, String defaultValue) {
        this.name = name;
        this.value = value;
        this.defaultValue = defaultValue;
    }
    
    public String getName() {
        return name;
    }

    public String getValue() {
        return value == null ? defaultValue : value;
    }

    public String getDefaultValue() {
        return defaultValue;
    }
}
