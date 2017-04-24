package com.lj.autotest.util;

import com.alibaba.fastjson.JSONObject;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.ReadContext;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;

/**
 * Created by lijing on 16/9/29.
 * Description:
 */
public class ApiRequestParser {

    // 设置每个参数的异常值集合, 从配置文件读取 exception.string
    private static String[] exceptionsString = Config.properties.getProperty("exception.string").split(",");
    private static String[] exceptionsURL = Config.properties.getProperty("exception.URL").split(",");
    private static String[] exceptionsHeadersIgnore = Config.properties.getProperty("exception.headers.ignore").split(",");

    /**
     * 根据一个请求模板,自动生成各参数异常值场景的用例
     *
     * @param apiRequest : 请求模板
     * @return :
     * @throws UnsupportedEncodingException :
     */
    public static List<ApiRequest> parser(ApiRequest apiRequest) throws UnsupportedEncodingException {
        List<ApiRequest> apiList = new ArrayList<>();
//        apiList.add(apiRequest);

        // 针对url部分做异常值测试的集合
        List<ApiRequest> list_url = parser_url(apiRequest);
        apiList.addAll(list_url);

        // 针对headers部分做异常值测试的集合
        List<ApiRequest> list_headers = parser_headers(apiRequest);
        apiList.addAll(list_headers);

        // 针对body部分做异常值测试的集合
        List<ApiRequest> list_body = parser_body(apiRequest);
        apiList.addAll(list_body);

        return apiList;
    }


    /**
     * 对url模板做解析:对每个query对象设置不同的参数,总用例数为: query个数 * 异常值集合的个数
     *
     * @param apiRequest : ApiRequest 请求模板
     * @return : 针对url中path做各种异常值适配的ApiRequest list
     * @throws UnsupportedEncodingException :
     */
    public static List<ApiRequest> parser_url(ApiRequest apiRequest) throws UnsupportedEncodingException {
        List<ApiRequest> apiList = new ArrayList<>();

        String url_sample = apiRequest.getUrl();

        // 截取每个url中的query 参数
        if (url_sample.indexOf("?") > 1) { // 如果不包含url参数,则不做处理
            String query = url_sample.substring(url_sample.indexOf("?") + 1);
            String[] query_list = query.split("&");

            // 对每个query值做异常值集合的赋值
            for (String aQuery_list : query_list) {
                String query_temp = aQuery_list.substring(0, aQuery_list.indexOf("=") + 1);
                for (String item : exceptionsURL) {
                    String url_new = url_sample.replaceAll(aQuery_list, query_temp + URLEncoder.encode(item, "UTF-8"));
                    ApiRequest temp = apiRequest.clone();
                    temp.setUrl(url_new);
                    apiList.add(temp);
                }
            }
        }

        return apiList;
    }


    /**
     * 处理headers
     *
     * @param apiRequest : apiRequest 样例
     * @return : 返回针对每个key 设置不同的异常值后的apiRequest对象集合
     */
    public static List<ApiRequest> parser_headers(ApiRequest apiRequest) {
        List<ApiRequest> apiList = new ArrayList<>();
        JSONObject headers = apiRequest.getHeaders();

        // 忽略的headers列表,即不需要对次header做异常测试
        List<String> ignore_list = Arrays.asList(exceptionsHeadersIgnore);

        // 每个key 设置不同的异常值
        for (String key : headers.keySet()) {
            if (!ignore_list.contains(key)) {
                for (String item : exceptionsString) {
                    JSONObject headers_temp = (JSONObject) headers.clone();
                    headers_temp.put(key, item);
                    ApiRequest apiRequest_temp = apiRequest.clone();
                    apiRequest_temp.setHeaders(headers_temp);
                    apiList.add(apiRequest_temp);
                }
            }
        }

        return apiList;
    }


    /**
     * 针对body部分做异常值测试的集合
     *
     * @param apiRequest : 请求样例
     * @return : 返回针对body部分做异常值测试的集合的apiRequest对象集合
     */
    public static List<ApiRequest> parser_body(ApiRequest apiRequest) {
        List<ApiRequest> apiList = new ArrayList<>();

        JSONObject body = apiRequest.getBody();

        for (String key : body.keySet()) {
            for (String item : exceptionsString) {
                JSONObject body_temp = (JSONObject) body.clone();
                body_temp.put(key, item);
                ApiRequest apiRequest_temp = apiRequest.clone();
                apiRequest_temp.setBody(body_temp);
                apiList.add(apiRequest_temp);
            }
        }

        return apiList;
    }


    /**
     * 循环查询每个测试数据目录下的文件,自动生成所有的接口测试ApiRequest对象
     *
     * @param files: 特定的数据文件名数组; 如果为空,则读取所有文件
     * @return : 返回所有的接口异常值场景的ApiRequest对象
     * @throws IOException :
     */
    public static List<ApiRequest> parser(String[] files) throws IOException {

        List<ApiRequest> apiList = new ArrayList<>();

        // 获取目录下的所有文件名
        String s[] = new String[0];
        if (files.length == 0) {
            // 如果不指定数据文件,则读取目录下的所有文件
            // 规定所有的测试用例json文件位置放在 resources/testcase/ 目录下,格式固定
//            File dirName = new File(this.getClass().getResource("/").getFile() + "testcase");
            File dirName = new File(ApiRequestParser.class.getResource("/").getFile() + "testcase");
            if (dirName.isDirectory()) {
                s = dirName.list();
            }
        } else {
            // 读取特定的文件
            s = files;
        }

        if (s.length > 0) {  // 存在用例文件时才执行

//            String baseDir = this.getClass().getResource("/").getFile() + "testcase";
            String baseDir = ApiRequestParser.class.getResource("/").getFile() + "testcase";
            // 处理每一个文件
            for (String jsonFileName : s) {

                // JsonPath 读取每个文件
                File jsonFile = new File(baseDir + "/" + jsonFileName);
                ReadContext context = JsonPath.parse(jsonFile);

                // 读取每个文件中的所有api请求
                List<Map<String, String>> apis = context.read("$.apis");

                // 对每个api 请求json 做处理,封装成 ApiRequest 对象
                for (int i = 0; i < apis.size(); i++) {
                    String url = context.read("$.apis[" + i + "].url");
                    String method = context.read("$.apis[" + i + "].method");

                    Map<String, String> headersMap = context.read("$.apis[" + i + "].headers");
                    JSONObject headers = new JSONObject();
                    for (String key : headersMap.keySet()) {
                        // 支持变量参数中userId 和 token 以变量形式传入
                        headers.put(key, ApiRequestJsonFileParser.formatUidAndToken(headersMap.get(key)));
                    }

                    Map<String, String> bodyMap = context.read("$.apis[" + i + "].body");
                    JSONObject body = new JSONObject();
                    for (String key : bodyMap.keySet()) {
                        // 支持变量参数中userId 和 token 以变量形式传入
                        body.put(key, ApiRequestJsonFileParser.formatUidAndToken(bodyMap.get(key)));
                    }

                    // 用例文件中的为请求模板
                    ApiRequest apiRequest = new ApiRequest(url, method, headers, body);

                    // TODO: 增加描述、前置、后置、预期结果字段
                    String desc = context.read("$.apis[" + i + "].description");
                    Map<String, String> map_before = parserBeforeMethod(context, i);
                    Map<String, String> map_after = parserAfterMethod(context, i);
                    Object response_expected = context.read("$.apis[" + i + "].response");

                    apiRequest.setDescription(desc);
                    apiRequest.setBeforeMethod(map_before);
                    apiRequest.setAfterMethod(map_after);
                    apiRequest.setResponse_expected(response_expected);



                    // json中设置了url、headers、body中变量ENUM数值,生成特定的ENUM组合结果
                    LinkedHashMap mapUrlEnum = new LinkedHashMap();
                    try {
                        mapUrlEnum = context.read("$.apis[" + i + "].EnumURL");
                    } catch (Exception e) {

                    }
                    LinkedHashMap mapHeadersEnum = new LinkedHashMap();
                    try {
                        mapHeadersEnum = context.read("$.apis[" + i + "].EnumHeaders");
                    } catch (Exception e) {

                    }
                    LinkedHashMap mapBodyEnum = new LinkedHashMap();
                    try {
                        mapBodyEnum = context.read("$.apis[" + i + "].EnumBody");
                    } catch (Exception e) {

                    }
                    List<ApiRequest> apiRequestListEnum = ApiRequestJsonFileParser.enumApiRequest(apiRequest, mapUrlEnum, mapHeadersEnum, mapBodyEnum);
                    apiList.addAll(apiRequestListEnum);

                    try {
                        // 读取配置文件中是否需要做各参数异常值场景的自动生成用例操作的标志, 非必选项
                        // TODO:当采用自动生成异常测试用例时，如何设置预期Response？
                        Boolean isNeedException = context.read("$.apis[" + i + "].isNeedException");
                        if (isNeedException) {
                            List<ApiRequest> apiList_temp = parser(apiList.get(0));
                            apiList.addAll(apiList_temp);
                        }
                    }catch (Exception e){

                    }

                }
            }
        }

        return apiList;
    }

    /**
     * 循环查询每个测试数据目录下的文件,自动生成所有的接口测试ApiRequest对象
     *
     * @return :
     * @throws IOException :
     */
    public static List<ApiRequest> parser() throws IOException {
        String[] files = null;
        return parser(files);
    }

    /**
     * 循环查询特定的文件,自动生成所有的接口测试ApiRequest对象
     *
     * @param file : 特定的文件名
     * @return :
     * @throws IOException :
     */
    public static List<ApiRequest> parser(String file) throws IOException {
        String[] files = {file};
        return parser(files);
    }

    /**
     * 解析json文件中的前置（before）的sql、redis操作：每个sql、redis操作为一个key
     *
     * @param context ：待解析的json内容的 com.jayway.jsonpath.ReadContext
     * @param index   ：apis 数组中的index
     * @return ： 包含各sql、redis的map，每个sql、redis操作为一个key，如sql_0\sql_1\redis_0\redis_1等
     */
    public static Map<String, String> parserBeforeMethod(ReadContext context, int index) {
        Map<String, String> map = new HashMap<>();
        net.minidev.json.JSONArray sqls = context.read("$.apis[" + index + "].sqlBeforeMethod");
        for (int i = 0; i < sqls.size(); i++) {
            System.out.println(sqls.get(i));
            map.put("mysql_" + i, String.valueOf(sqls.get(i)));
        }

        net.minidev.json.JSONArray redis = context.read("$.apis[" + index + "].redisBeforeMethod");
        for (int i = 0; i < redis.size(); i++) {
            map.put("redis_" + i, String.valueOf(redis.get(i)));
        }
        return map;
    }

    /**
     * 解析json文件中的后置（after）的sql、redis操作：每个sql、redis操作为一个key
     *
     * @param context ：待解析的json内容的 com.jayway.jsonpath.ReadContext
     * @param index   ：apis 数组中的index
     * @return ： 包含各sql、redis的map，每个sql、redis操作为一个key，如sql_0\sql_1\redis_0\redis_1等
     */
    public static Map<String, String> parserAfterMethod(ReadContext context, int index) {
        Map<String, String> map = new HashMap<>();
        net.minidev.json.JSONArray sqls = context.read("$.apis[" + index + "].sqlAfterMethod");
        for (int i = 0; i < sqls.size(); i++) {
            System.out.println(sqls.get(i));
            map.put("sql_" + i, String.valueOf(sqls.get(i)));
        }

        net.minidev.json.JSONArray redis = context.read("$.apis[" + index + "].redisAfterMethod");
        for (int i = 0; i < redis.size(); i++) {
            map.put("redis_" + i, String.valueOf(redis.get(i)));
        }
        return map;
    }


//    public static void main(String[] args) throws IOException {
//        String file = "NewTestSample.json";
//        List<ApiRequest> list = parser(file);
//        System.out.println(list.size());
//        for(ApiRequest apiRequest: list){
//            System.out.println(">>>>>>>>");
//            System.out.println("url: "+apiRequest.getUrl());
//            System.out.println("headers: "+ apiRequest.getHeaders());
//            System.out.println("body: "+apiRequest.getBody());
//        }
//    }
}
