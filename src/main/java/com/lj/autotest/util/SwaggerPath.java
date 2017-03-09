package com.lj.autotest.util;

import com.alibaba.fastjson.JSONObject;

import java.util.List;

/**
 * Created by lijing on 16/9/27.
 * Description:
 */
public class SwaggerPath {
    private String method;
    private String url;
    private List<String> path;
    private List<String> query;
    private JSONObject headers;
    private JSONObject paramters;
    private String description;

    public SwaggerPath(String method, String url, List<String> path, List<String> query, JSONObject headers, JSONObject paramters,
                       String description) {
        this.method = method;
        this.url = url;
        this.path = path;
        this.query = query;
        this.headers = headers;
        this.paramters = paramters;
        this.description = description;
    }


    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public JSONObject getHeaders() {
        return headers;
    }

    public void setHeaders(JSONObject headers) {
        this.headers = headers;
    }

    public JSONObject getParamters() {
        return paramters;
    }

    public void setParamters(JSONObject paramters) {
        this.paramters = paramters;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public List<String> getPath() {
        return path;
    }

    public void setPath(List<String> path) {
        this.path = path;
    }

    public List<String> getQuery() {
        return query;
    }

    public void setQuery(List<String> query) {
        this.query = query;
    }
}
