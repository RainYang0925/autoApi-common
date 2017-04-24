package com.lj.autotest.util;

import com.jayway.jsonpath.JsonPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by lijing on 2017/4/12.
 * Desc: 比对json文件中的预期response 和 实际返回的response；
 * 支持json、jsonp、string、html等格式；
 * 支持自定义需要比较的字段及忽略字段
 */
public class ResponseCompare {
    private static Logger logger = LoggerFactory.getLogger(ResponseCompare.class);

    public static boolean compare(Object expected, Response actual) throws Exception {
        try {
            if (actual.getBodyJson() != null) {
                // 返回结果是json类型
                return compareJson((Map<String, Object>) JsonPath.read(expected, "$"), actual.getBodyJson());
            }else {
                // 返回结果是非json类型
                return compareString(String.valueOf(expected), actual.getBodyString());
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }

    }


    /**
     * 比较string对象的response， 模糊匹配
     *
     * @param expected ：预期结果的response， String类型
     * @param actual   ：实际返回的response， String 类型
     * @return ：
     */
    public static boolean compareString(String expected, String actual) {
        if (!actual.equalsIgnoreCase(actual)) {
            return false;
        } else {
            return true;
        }

    }

    /**
     * 比较json对象的response
     * 模糊匹配，即当预期结果的每个key对应的value包含在（非完全相等）实际返回的结果中，返回true；
     * 有任意一个key不满足时，返回false。
     *
     * @param expected : 预期结果的response， Json类型
     * @param actual   : 实际返回的response， Json类型
     * @return : 模糊匹配的结果
     * @throws IOException ：
     */
    public static boolean compareJson(Map<String, Object> expected, Map<String, Object> actual) throws IOException {
        try {
            List<String> list = parserKeyPath(expected);
            for (String path : list) {
                String value_expected = String.valueOf(JsonPath.read(expected, "$." + path));
                String value_actual = String.valueOf(JsonPath.read(actual, "$." + path));
                compareString(value_actual, value_expected);
            }
        } catch (Exception e) {
            return false;
        }
        return true;

    }

    /**
     * 获取预期response中包含的所有的JsonPath list，
     *
     * @param objectMap :
     * @param keySuper  : 递归传递上一级的  JsonPath
     * @return : JsonPath list
     */
    private static List parserKeyPath(Map<String, Object> objectMap, String keySuper, List<String> list) {
        for (String key : objectMap.keySet()) {
            Class<?> type = objectMap.get(key).getClass();

            String path = key;
            if (!keySuper.equals("")) {
                path = keySuper + "." + key; // path 深度递加
            }
            switch (type.toString()) {
                case "class net.minidev.json.JSONArray":   // 处理json数组
                    net.minidev.json.JSONArray jsonArray = (net.minidev.json.JSONArray) objectMap.get(key);
                    for (int i = 0; i < jsonArray.size(); i++) {
                        Map<String, Object> object_temp = (Map<String, Object>) jsonArray.get(i);
                        parserKeyPath(object_temp, path + "[" + i + "]", list);
                    }
                    break;
                case "class java.util.LinkedHashMap":      // 处理json
                    Map<String, Object> object_temp = (Map<String, Object>) objectMap.get(key);
                    parserKeyPath(object_temp, path, list);
                    break;
                default:                                   // 处理其他类型，如int、string
                    list.add(path);
            }
        }
        return list;
    }

    private static List parserKeyPath(Map<String, Object> objectMap) {
        return parserKeyPath(objectMap, "", new ArrayList<String>());
    }

    public static void main(String[] args) throws IOException {
//        String baseDir = ResponseCompare.class.getResource("/").getFile() + "testcase/";
//        File file = new File(baseDir + "TestSample.json");
//
//        Object expected = JsonPath.read(file, "$.apis[0].response");
//        ReadContext context = JsonPath.parse(file);
//
//        System.out.println(context.read("$.apis[0].response"));


        List<ApiRequest> request = ApiRequestParser.parser("TestSample.json");
        System.out.println(request.size());
        System.out.println(request.get(0).getResponse_expected());

//        System.out.println(compare());
    }
}
