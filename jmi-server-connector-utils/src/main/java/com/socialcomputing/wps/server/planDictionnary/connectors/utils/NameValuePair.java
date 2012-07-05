package com.socialcomputing.wps.server.planDictionnary.connectors.utils;

public class NameValuePair {
    private final String name;
    private final String value;
    private final String defaultValue;
    private final boolean parse;
    

    public NameValuePair(String name) {
        this(name, null, null);
    }
    
    public NameValuePair(String name, String value) {
        this(name, value, null);
    }

    public NameValuePair(String name, String value, boolean parse) {
        this(name, value, null, parse);
    }
    
    public NameValuePair(String name, String value, String defaultValue) {
        this(name, value, null, true);
    }
    
    public NameValuePair(String name, String value, String defaultValue, boolean parse) {
        this.name = name;
        this.value = value;
        this.defaultValue = defaultValue;
        this.parse = parse;
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
    
    public boolean getParse() {
        return parse;
    }
}
