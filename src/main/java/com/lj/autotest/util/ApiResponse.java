package com.lj.autotest.util;

import com.alibaba.fastjson.JSONObject;
import org.apache.http.impl.client.CloseableHttpClient;
import org.jsoup.nodes.Document;


/**
 * Created by lijing on 16/5/4.
 * Desc: 自定义的接口Response，支持返回json格式、html格式的结果直接获取格式化内容；
 * 非以上格式化返回可调用BodyString
 */
public class ApiResponse {
    public int StatusCode;
    public JSONObject BodyJson;
    public Document BodyHtml;
    public String BodyString;
    public CloseableHttpClient httpClient;


    public ApiResponse(int statusCode, JSONObject body, Document bodyHtml, String bodyString, CloseableHttpClient httpClient) {
        StatusCode = statusCode;
        BodyJson = body;
        BodyHtml = bodyHtml;
        BodyString = bodyString;
        this.httpClient = httpClient;
    }

    public int getStatusCode() {
        return StatusCode;
    }

    public void setStatusCode(int statusCode) {
        StatusCode = statusCode;
    }

    public JSONObject getBodyJson() {
        return BodyJson;
    }

    public void setBodyJson(JSONObject body) {
        BodyJson = body;
    }

    public Document getBodyHtml() {
        return BodyHtml;
    }

    public void setBodyHtml(Document bodyHtml) {
        BodyHtml = bodyHtml;
    }

    public CloseableHttpClient getHttpClient() {
        return httpClient;
    }

    public void setHttpClient(CloseableHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public String getBodyString() {
        return BodyString;
    }

    public void setBodyString(String bodyString) {
        BodyString = bodyString;
    }
}