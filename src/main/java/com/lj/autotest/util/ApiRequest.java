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
    private JSONObject body;

    private Object response_expected;
    private Response response_actual;


    public ApiRequest(String url, String method, JSONObject headers, JSONObject body) {
        this.url = url;
        this.method = method;
        this.headers = headers;
        this.body = body;
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

    public JSONObject getBody() {
        return body;
    }

    public void setBody(JSONObject body) {
        this.body = body;
    }

    public Object getResponse_expected() {
        return response_expected;
    }

    public void setResponse_expected(Object response_expected) {
        this.response_expected = response_expected;
    }

    public Response getResponse_actual() {
        return response_actual;
    }

    public void setResponse_actual(Response response_actual) {
        this.response_actual = response_actual;
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
        JSONObject params = this.getBody();

        ApiRequest apiRequest1 = new ApiRequest(url_sample, method, headers, params);
        apiRequest1.setResponse_expected(this.response_expected);
        apiRequest1.setResponse_actual(this.response_actual);
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
            response = HTTP.post(this.getUrl(), this.getBody(), this.getHeaders());
        } else if (this.getMethod().equalsIgnoreCase("delete")) {
            response = HTTP.delete(this.getUrl(), this.getHeaders());
        } else if (this.getMethod().equalsIgnoreCase("put")) {
            response = HTTP.put(this.getUrl(), this.getHeaders());
        } else {
            return null;
        }
        this.response_actual = response;
        return response;
    }


    /**
     * 比较预期结果和实际结果
     *
     * @return ：
     * @throws IOException ：
     */
    public boolean compareResponse() throws Exception {
        return ResponseCompare.compare(this.response_expected, this.response_actual);
    }

}
