package com.lj.autotest.util;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.PatternSyntaxException;

/**
 * Created by lijing on 16/4/25.
 * Description: 自定义http接口请求类, 返回自定义的 ApiResponse 对象
 */
public class HTTP {

    private static Logger logger = LoggerFactory.getLogger(HTTP.class);

    /**
     * HTTP GET 请求
     *
     * @param httpClient    : 发起请求的HttpClient,为空时自动创建
     * @param closeClient   : 请求结束后是否关闭标志
     * @param baseUrl       :
     * @param queryStrings:
     * @param headers       : HTTP Headers
     * @return :返回自定义Response
     * @throws IOException
     */
    public static ApiResponse get(CloseableHttpClient httpClient,
                                  boolean closeClient,
                                  String baseUrl,
                                  JSONObject queryStrings,
                                  JSONObject headers) throws IOException {
        if (httpClient == null) {
            httpClient = HttpClients.createDefault();
        }

        if (headers == null) {
            headers = new JSONObject();
        }

        String requestUrl = createRequestUrl(baseUrl, queryStrings);

        HttpGet httpGet = new HttpGet(requestUrl);

        String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_5) AppleWebKit/601.5.17 (KHTML, like Gecko) Version/9.1 Safari/601.5.17";
        httpGet.setHeader("User-Agent", USER_AGENT);

        // set headers
        for (String key : headers.keySet()) {
            httpGet.setHeader(key, String.valueOf(headers.get(key)));
        }

        logger.info(">>>>>>>>>> executing request : " + httpGet.toString());
        logger.info(">>>> Request Headers -- BEGIN --:");
        Header[] allHeaders = httpGet.getAllHeaders();
        for (Header eachHeader : allHeaders) {
            logger.info(eachHeader.getName() + ":" + eachHeader.getValue());
        }
        logger.info(">>>> Request Headers -- END --:");

        CloseableHttpResponse response = httpClient.execute(httpGet);
        logger.info(">>>> ApiResponse Code : " + response.getStatusLine().getStatusCode());

        ApiResponse resp = doResponse(httpClient, response);
        response.close();

        if (closeClient) {
            httpClient.close();
        }

        return resp;
    }

    public static ApiResponse get(String url) throws IOException {
        return get(null, false, url, null, null);
    }

    public static ApiResponse get(CloseableHttpClient httpClient, String url) throws IOException {
        return get(httpClient, false, url, null, null);
    }

    public static ApiResponse get(String url, JSONObject headers) throws IOException {
        return get(null, false, url, null, headers);
    }

    public static ApiResponse get(CloseableHttpClient httpClient, String url, JSONObject headers) throws IOException {
        return get(httpClient, false, url, null, headers);
    }

    public static ApiResponse get(String url, JSONObject queryStrings, JSONObject headers) throws IOException {
        return get(null, false, url, queryStrings, headers);
    }

    public static ApiResponse get(CloseableHttpClient httpClient,
                                  String url, JSONObject queryStrings,
                                  JSONObject headers) throws IOException {
        return get(httpClient, false, url, queryStrings, headers);
    }


    /**
     * HTTP POST 请求
     *
     * @param httpClient   :发起请求的HttpClient, 为空时自动创建
     * @param closeClient  :请求是否关闭标志
     * @param baseUrl      :请求Url
     * @param queryStrings :请求Url参数
     * @param headers      :请求头信息
     * @param body       :POST data参数
     * @return :返回自定义Response
     * @throws IOException
     */
    public static ApiResponse post(CloseableHttpClient httpClient,
                                   boolean closeClient,
                                   String baseUrl,
                                   JSONObject queryStrings,
                                   JSONObject headers,
                                   JSONObject body) throws IOException {

        if (httpClient == null) {
            httpClient = HttpClients.createDefault();
        }

        if (headers == null) {
            headers = new JSONObject();
        }

        if (body == null) {
            body = new JSONObject();
        }

        String requestUrl = createRequestUrl(baseUrl, queryStrings);

        HttpPost httpPost = new HttpPost(requestUrl);

        String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_5) AppleWebKit/601.5.17 (KHTML, like Gecko) Version/9.1 Safari/601.5.17";
        httpPost.setHeader("User-Agent", USER_AGENT);

        // set headers
        for (String key : headers.keySet()) {
            httpPost.setHeader(key, String.valueOf(headers.get(key)));
        }

        if (headers.getString("Content-Type") != null && headers.getString("Content-Type").contains("application/json")) {
            // 处理Json格式参数, 其Content-Type:application/json
            StringEntity entity = new StringEntity(body.toString(), "utf-8");//解决中文乱码问题
            entity.setContentEncoding("UTF-8");
            entity.setContentType("application/json");
            httpPost.setEntity(entity);
        } else {
            // 处理表单Form 类型参数, 其Content-Type:application/x-www-form-urlencoded
            httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
            List<NameValuePair> urlParameters = new ArrayList<>();
            for (String key : body.keySet()) {
                urlParameters.add(new BasicNameValuePair(key, String.valueOf(body.get(key))));
            }
            httpPost.setEntity(new UrlEncodedFormEntity(urlParameters));
        }

        logger.info(">>>>>>>>>> executing request : " + httpPost.toString());
        logger.info(">>>> Request Headers -- BEGIN --:");
        Header[] allHeaders = httpPost.getAllHeaders();
        for (Header eachHeader : allHeaders) {
            logger.info(eachHeader.getName() + ":" + eachHeader.getValue());
        }
        logger.info(">>>> Request Headers -- END --:");
        logger.info(">>>> POST Request Form: " + body.toJSONString());

        CloseableHttpResponse response = httpClient.execute(httpPost);
        logger.info(">>>> ApiResponse Code : " + response.getStatusLine().getStatusCode());

        ApiResponse resp = doResponse(httpClient, response);
        response.close();

        if (closeClient) {
            httpClient.close();
        }

        return resp;
    }

    public static ApiResponse post(String url, JSONObject params) throws IOException {
        return post(null, false, url, null, null, params);
    }

    public static ApiResponse post(String url, JSONObject params, JSONObject headers) throws IOException {
        return post(null, false, url, null, headers, params);
    }

    public static ApiResponse post(String url,
                                   JSONObject queryStrings,
                                   JSONObject headers,
                                   JSONObject params) throws IOException {
        return post(null, false, url, queryStrings, headers, params);
    }

    public static ApiResponse post(CloseableHttpClient httpClient, String url, JSONObject params) throws IOException {
        return post(httpClient, false, url, null, null, params);
    }

    public static ApiResponse post(CloseableHttpClient httpClient, String url, JSONObject params, JSONObject headers) throws IOException {
        return post(httpClient, false, url, null, headers, params);
    }


    /**
     * RESTFUL PUT
     *
     * @param httpClient   ：
     * @param closeClient  ：
     * @param baseUrl      ：
     * @param queryStrings ：
     * @param headers      ：
     * @param params       ：
     * @return ：
     * @throws IOException ：
     */
    public static ApiResponse put(CloseableHttpClient httpClient,
                                  boolean closeClient,
                                  String baseUrl,
                                  JSONObject queryStrings,
                                  JSONObject headers,
                                  JSONObject params) throws IOException {

        if (httpClient == null) {
            httpClient = HttpClients.createDefault();
        }

        if (headers == null) {
            headers = new JSONObject();
        }

        if (params == null) {
            params = new JSONObject();
        }

        String requestUrl = createRequestUrl(baseUrl, queryStrings);

        HttpPut httpPut = new HttpPut(requestUrl);

        String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_5) AppleWebKit/601.5.17 (KHTML, like Gecko) Version/9.1 Safari/601.5.17";
        httpPut.setHeader("User-Agent", USER_AGENT);

        // set headers
        for (String key : headers.keySet()) {
            httpPut.setHeader(key, String.valueOf(headers.get(key)));
        }

        if (headers.getString("Content-Type") != null && headers.getString("Content-Type").contains("application/json")) {
            // 处理Json格式参数, 其Content-Type:application/json
            StringEntity entity = new StringEntity(params.toString(), "utf-8");//解决中文乱码问题
            entity.setContentEncoding("UTF-8");
            entity.setContentType("application/json");
            httpPut.setEntity(entity);
        } else {
            // 处理表单Form 类型参数, 其Content-Type:application/x-www-form-urlencoded
            httpPut.setHeader("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
            List<NameValuePair> urlParameters = new ArrayList<>();
            for (String key : params.keySet()) {
                urlParameters.add(new BasicNameValuePair(key, String.valueOf(params.get(key))));
            }
            System.out.println("Content-Type: application/x-www-form-urlencoded;charset=utf-8");
            httpPut.setEntity(new UrlEncodedFormEntity(urlParameters));
        }


        logger.info(">>>>>>>>>> executing PUT request : " + httpPut.toString());
        logger.info(">>>> Request Headers -- BEGIN --:");
        Header[] allHeaders = httpPut.getAllHeaders();
        for (Header eachHeader : allHeaders) {
            logger.info(eachHeader.getName() + ":" + eachHeader.getValue());
        }
        logger.info(">>>> Request Headers -- END --:");
        logger.info(">>>> PUT Request Form: " + params.toJSONString());

        CloseableHttpResponse response = httpClient.execute(httpPut);
        logger.info(">>>> ApiResponse Code : " + response.getStatusLine().getStatusCode());

        ApiResponse resp = doResponse(httpClient, response);
        response.close();

        if (closeClient) {
            httpClient.close();
        }

        return resp;
    }

    public static ApiResponse put(String url,
                                  JSONObject headers) throws IOException {
        return put(null, false, url, null, headers, null);
    }

    public static ApiResponse put(String url,
                                  JSONObject headers,
                                  JSONObject params) throws IOException {
        return put(null, false, url, null, headers, params);
    }

    public static ApiResponse put(CloseableHttpClient httpClient,
                                  String url,
                                  JSONObject headers,
                                  JSONObject params) throws IOException {
        return put(httpClient, false, url, null, headers, params);
    }


    /**
     * RESTFUL Delete
     *
     * @param httpClient    : 请求的HttpClient, 可以为空
     * @param closeClient   : 请求后是否关闭标志, 可为空
     * @param baseUrl       : 请求Url
     * @param queryStrings: 参数
     * @param headers       : 请求头信息, 可为空
     * @return : 返回自定义Response
     * @throws IOException
     */
    public static ApiResponse delete(CloseableHttpClient httpClient,
                                     boolean closeClient,
                                     String baseUrl,
                                     JSONObject queryStrings,
                                     JSONObject headers) throws IOException {

        if (httpClient == null) {
            httpClient = HttpClients.createDefault();
        }

        if (headers == null) {
            headers = new JSONObject();
        }

        String requestUrl = createRequestUrl(baseUrl, queryStrings);

        HttpDelete httpDelete = new HttpDelete(requestUrl);

        String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_5) AppleWebKit/601.5.17 (KHTML, like Gecko) Version/9.1 Safari/601.5.17";
        httpDelete.setHeader("User-Agent", USER_AGENT);

        // set headers
        for (String key : headers.keySet()) {
            httpDelete.setHeader(key, String.valueOf(headers.get(key)));
        }

        logger.info(">>>>>>>>>> executing DELETE request : " + httpDelete.toString());
        logger.info(">>>> Request Headers -- BEGIN --:");
        Header[] allHeaders = httpDelete.getAllHeaders();
        for (Header eachHeader : allHeaders) {
            logger.info(eachHeader.getName() + ":" + eachHeader.getValue());
        }
        logger.info(">>>> Request Headers -- END --:");

        CloseableHttpResponse response = httpClient.execute(httpDelete);
        logger.info(">>>> ApiResponse Code : " + response.getStatusLine().getStatusCode());

        ApiResponse resp = doResponse(httpClient, response);
        response.close();

        if (closeClient) {
            httpClient.close();
        }

        return resp;
    }

    public static ApiResponse delete(String url, JSONObject headers) throws IOException {
        return delete(null, false, url, null, headers);
    }

    public static ApiResponse delete(CloseableHttpClient httpClient, String url, JSONObject headers) throws IOException {
        return delete(httpClient, false, url, null, headers);
    }

    /**
     * @param domainNameOrHost ：
     * @param apiPath          ：
     * @param pathParams       ：
     * @return ：
     */
    public static String createBaseUrl(String domainNameOrHost, String apiPath, String... pathParams) {
        StringBuilder baseUrl = new StringBuilder("");

        baseUrl.append(domainNameOrHost);

        for (String param : pathParams) {
            apiPath = apiPath.replaceFirst("\\{.*\\}", param);
        }
        baseUrl.append(apiPath);

        return baseUrl.toString();
    }

    /**
     * 拼接基础URL与请求参数querStrings生成请求的URL
     *
     * @param baseUrl      基础URL,一般是域名或者IP:port
     * @param queryStrings 请求URL需要的请求参数
     * @return 返回拼接之后的请求URL
     */
    private static String createRequestUrl(String baseUrl, JSONObject queryStrings) throws UnsupportedEncodingException {
        if (queryStrings == null) {
            queryStrings = new JSONObject();
        }

        StringBuilder requestUrlBuffer = new StringBuilder("");

        if (!baseUrl.startsWith("http://") && !baseUrl.startsWith("https://")) {
            requestUrlBuffer.append("http://");
        }

        requestUrlBuffer.append(baseUrl);

        // add query strings
        if (!queryStrings.isEmpty()) {

            if (!baseUrl.endsWith("?")) {
                requestUrlBuffer.append("?");
            }

            boolean needAnd = false;

            logger.debug("Here are the query strings>>>>>");
            for (String key : queryStrings.keySet()) {
                if (needAnd) {
                    requestUrlBuffer.append("&");
                } else {
                    needAnd = true;
                }

                String queryValue = queryStrings.getString(key);
                logger.debug(key + "=" + queryValue);
                String urlEncodeKeyValue = key + "=" + URLEncoder.encode(queryValue, "UTF-8");
                requestUrlBuffer.append(urlEncodeKeyValue);
            }
            logger.debug("<<<<<query strings end");
        }
        String requestUrl = requestUrlBuffer.toString();
        logger.debug("requestUrl:" + requestUrl);

        return requestUrl;

    }

    /**
     * 处理http response
     *
     * @param response : 需要处理的HttpResponse
     * @return : 返回自定义的Response 对象
     * @throws IOException
     */
    public static ApiResponse doResponse(CloseableHttpClient httpClient, HttpResponse response) throws IOException {
        int status_code = response.getStatusLine().getStatusCode();

        JSONObject bodyJson = new JSONObject();
        Document doc = new Document("");
        String bodyString = "";

        HttpEntity entity = response.getEntity();
        logger.info("ApiResponse content length: " + entity.getContentLength());

        String strResult = EntityUtils.toString(entity, "UTF-8");

        bodyString = strResult;

        try {
            bodyJson = jsonp(strResult);
        } catch (Exception e) {
            // 返回是 html 格式
            doc = Jsoup.parse(strResult);
        }

        return new ApiResponse(status_code, bodyJson, doc, bodyString, httpClient);

    }


    /**
     * 解析jsonp格式,返回json数据
     *
     * @param jsonp : 需要解析的Json\Jsonp 字符串
     * @return : 返回 JSONObject
     * @throws PatternSyntaxException
     */
    public static JSONObject jsonp(String jsonp) throws PatternSyntaxException {

        String json = jsonp.substring(jsonp.indexOf('{'), jsonp.lastIndexOf('}') + 1);

        return JSON.parseObject(json);

    }


//    public static void main(String[] args) throws IOException {
//        String url="www.baidu.com";
//        ApiResponse response = HTTP.get(url);
//        System.out.println(response.BodyString);
//
//    }

}

