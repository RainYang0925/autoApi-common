package com.lj.autotest.swagger;

import com.alibaba.fastjson.JSONObject;

/**
 * Created by lijing on 16/9/22.
 * Description:
 */
public class SwaggerApiRequest {
    private String method;
    private String url;
    private JSONObject headers;
    private JSONObject paramters;
    private String description;
    private int statusCode;   // 预期的接口返回statusCode

    public SwaggerApiRequest(String method, String url, JSONObject headers, JSONObject paramters) {
        this.method = method;
        this.url = url;
        this.headers = headers;
        this.paramters = paramters;
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

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }
}
