package com.lj.autotest.util;

import com.alibaba.fastjson.JSONObject;
import org.apache.http.impl.client.CloseableHttpClient;
import org.jsoup.nodes.Document;

//import org.apache.http.client.HttpClient;

/**
 * Created by lijing on 16/5/4.
 */
public class Response {
    public int StatusCode;
    public JSONObject BodyJson;
    public Document BodyHtml;
    public String BodyString;
    public CloseableHttpClient httpClient;


    public Response(int statusCode, JSONObject body, Document bodyHtml, String bodyString, CloseableHttpClient httpClient) {
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
