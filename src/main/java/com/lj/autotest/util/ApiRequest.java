package com.lj.autotest.util;

import com.alibaba.fastjson.JSONObject;

import java.io.IOException;


/**
 * Created by lijing on 16/9/29.
 * Description:
 */
public class ApiRequest {
    private String url;
    private String method;
    private JSONObject headers;
    private JSONObject params;

    public ApiRequest() {
    }

    public ApiRequest(String url, String method, JSONObject headers, JSONObject params) {
        this.url = url;
        this.method = method;
        this.headers = headers;
        this.params = params;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public JSONObject getHeaders() {
        return headers;
    }

    public void setHeaders(JSONObject headers) {
        this.headers = headers;
    }

    public JSONObject getParams() {
        return params;
    }

    public void setParams(JSONObject params) {
        this.params = params;
    }

    public ApiRequest clone() {
        String url_sample = this.getUrl();
        String method = this.getMethod();
        JSONObject headers = this.getHeaders();
        JSONObject params = this.getParams();

        ApiRequest apiRequest1 = new ApiRequest(url_sample, method, headers, params);
        return apiRequest1;
    }

    public Response visit_url() throws IOException {
        Response response;
        if (this.getMethod().equalsIgnoreCase("get")) {
            response = HTTP.get(this.getUrl(), this.getHeaders());
        } else if (this.getMethod().equalsIgnoreCase("post")) {
            response = HTTP.post(this.getUrl(), this.getParams(), this.getHeaders());
        } else if (this.getMethod().equalsIgnoreCase("delete")) {
            response = HTTP.delete(this.getUrl(), this.getHeaders());
        } else if (this.getMethod().equalsIgnoreCase("put")) {
            response = HTTP.put(this.getUrl(), this.getHeaders());
        } else {
            return null;
        }
        return response;
    }

}
