package com.lj.autotest.util;

import com.alibaba.fastjson.JSONObject;

import java.io.IOException;


/**
 * Created by lijing on 16/9/29.
 * Description: 自定义的HTTPRequest对象
 */
public class ApiRequest {
    private String url;
    private String method;
    private JSONObject headers;
    private JSONObject params;

    private String result_expected;
    private String result_actual;


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

    public String getResult_expected() {
        return result_expected;
    }

    public String getResult_actual() {
        return result_actual;
    }

    public void setResult_actual(String result_actual) {
        this.result_actual = result_actual;
    }

    public void setResult_expected(String result_expected) {
        this.result_expected = result_expected;
    }

    /**
     * 复制clone ，得到一个新的ApiRequest对象
     *
     * @return ： 返回一个新的ApiRequest对象
     */
    public ApiRequest clone() {
        String url_sample = this.getUrl();
        String method = this.getMethod();
        JSONObject headers = this.getHeaders();
        JSONObject params = this.getParams();

        ApiRequest apiRequest1 = new ApiRequest(url_sample, method, headers, params);
        return apiRequest1;
    }


    /**
     * 封装ApiRequest对象的请求操作，目前仅支持4种：get\post\delete\put
     *
     * @return : 返回自定义的response
     * @throws IOException ：
     */
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
