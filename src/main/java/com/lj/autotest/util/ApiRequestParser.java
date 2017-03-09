package com.lj.autotest.util;

import com.alibaba.fastjson.JSONObject;
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

        // 针对params部分做异常值测试的集合
        List<ApiRequest> list_params = parser_params(apiRequest);
        apiList.addAll(list_params);

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
     * 针对params部分做异常值测试的集合
     *
     * @param apiRequest : 请求样例
     * @return : 返回针对params部分做异常值测试的集合的apiRequest对象集合
     */
    public static List<ApiRequest> parser_params(ApiRequest apiRequest) {
        List<ApiRequest> apiList = new ArrayList<>();

        JSONObject params = apiRequest.getParams();

        for (String key : params.keySet()) {
            for (String item : exceptionsString) {
                JSONObject params_temp = (JSONObject) params.clone();
                params_temp.put(key, item);
                ApiRequest apiRequest_temp = apiRequest.clone();
                apiRequest_temp.setParams(params_temp);
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
            // 规定所有的测试用例json文件位置放在 resources/testdata/ 目录下,格式固定
//            File dirName = new File(this.getClass().getResource("/").getFile() + "testdata");
            File dirName = new File(ApiRequestParser.class.getResource("/").getFile() + "testdata");
            if (dirName.isDirectory()) {
                s = dirName.list();
            }
        } else {
            // 读取特定的文件
            s = files;
        }

        if (s.length > 0) {  // 存在用例文件时才执行

//            String baseDir = this.getClass().getResource("/").getFile() + "testdata";
            String baseDir = ApiRequestParser.class.getResource("/").getFile() + "testdata";
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
//                        headers.put(key, headersMap.get(key));

                        // 支持变量参数中userid 和 token 以变量形式传入
                        headers.put(key, ApiRequestJsonFileParser.formatUidAndToken(headersMap.get(key)));
                    }

                    Map<String, String> paramsMap = context.read("$.apis[" + i + "].params");
                    JSONObject params = new JSONObject();
                    for (String key : paramsMap.keySet()) {
//                        params.put(key, paramsMap.get(key));

                        // 支持变量参数中userid 和 token 以变量形式传入
                        params.put(key, ApiRequestJsonFileParser.formatUidAndToken(paramsMap.get(key)));
                    }

                    // 用例文件中的为请求模板
                    ApiRequest apiRequest = new ApiRequest(url, method, headers, params);

                    // json中设置了url、headers、params中变量ENUM数值,生成特定的ENUM组合结果
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
                    LinkedHashMap mapParamsEnum = new LinkedHashMap();
                    try {
                        mapParamsEnum = context.read("$.apis[" + i + "].EnumParams");
                    } catch (Exception e) {

                    }
                    List<ApiRequest> apiRequestListEnum = ApiRequestJsonFileParser.enumApiRequest(apiRequest, mapUrlEnum, mapHeadersEnum, mapParamsEnum);
                    apiList.addAll(apiRequestListEnum);

                    // 读取配置文件中是否需要做各参数异常值场景的自动生成用例操作的标志
                    Boolean isNeedException = context.read("$.apis[" + i + "].isNeedException");
                    if (isNeedException) {
                        List<ApiRequest> apiList_temp = parser(apiList.get(0));
                        apiList.addAll(apiList_temp);
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


//    public static void main(String[] args) throws IOException {
//        String file = "TestSample.json";
//        List<ApiRequest> list = parser(file);
//        System.out.println(list.size());
//        for(ApiRequest apiRequest: list){
//            System.out.println(">>>>>>>>");
//            System.out.println("url: "+apiRequest.getUrl());
//            System.out.println("headers: "+ apiRequest.getHeaders());
//            System.out.println("params: "+apiRequest.getParams());
//        }
//    }
}
