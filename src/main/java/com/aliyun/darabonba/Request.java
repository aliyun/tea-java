package com.aliyun.darabonba;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class Request {
    public final static String URL_ENCODING = "UTF-8";

    public String protocol;

    public Integer port;

    public String method;

    public String pathname;

    public Map<String, String> query;

    public Map<String, String> headers;

    public InputStream body;

    public Request() {
        protocol = "http";
        method = "GET";
        query = new HashMap<String, String>();
        headers = new HashMap<String, String>();
    }

    public static Request create() {
        return new Request();
    }

    @Override
    public String toString() {
        String output = "Protocol: " + this.protocol + "\nPort: " + this.port + "\n" + this.method + " " + this.pathname
                + "\n";
        output += "Query:\n";
        for (Entry<String, String> e : this.query.entrySet()) {
            output += "    " + e.getKey() + ": " + e.getValue() + "\n";
        }
        output += "Headers:\n";
        for (Entry<String, String> e : this.headers.entrySet()) {
            output += "    " + e.getKey() + ": " + e.getValue() + "\n";
        }
        return output;
    }
}