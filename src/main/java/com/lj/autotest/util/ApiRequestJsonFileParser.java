package com.lj.autotest.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lj.autotest.business.DB;
import com.lj.autotest.business.Users;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.ReadContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by lijing on 16/10/31.
 * Description: 处理resource/testdata/ 下的json 文件
 */
public class ApiRequestJsonFileParser {
    private static Logger logger = LoggerFactory.getLogger(ApiRequestJsonFileParser.class);

    /**
     * 根据ENUM 穷举生成 apiRequest 测试对象
     *
     * @param apiRequest     : 基准apiRequest
     * @param mapEnumUrl     : 包含url中各变量的ENUM值得map对象
     * @param mapEnumHeaders : 包含headers中各变量的ENUM值得map对象
     * @param mapEnumParams  : 包含params中各变量的ENUM值得map对象
     * @return : 穷举的 apiRequest list集合
     */
    public static List<ApiRequest> enumApiRequest(ApiRequest apiRequest, LinkedHashMap mapEnumUrl, LinkedHashMap mapEnumHeaders, LinkedHashMap mapEnumParams) {
        JSONObject jsonEnumUrl = map2JSON(mapEnumUrl);
        JSONObject jsonEnumHeaders = map2JSON(mapEnumHeaders);
        JSONObject jsonEnumParams = map2JSON(mapEnumParams);

        return enumApiRequest(apiRequest, jsonEnumUrl, jsonEnumHeaders, jsonEnumParams);
    }

    public static List<ApiRequest> enumApiRequest(ApiRequest apiRequest, JSONObject jsonEnumUrl, JSONObject jsonEnumHeaders, JSONObject jsonEnumParams) {
        String url_format = apiRequest.getUrl();
        JSONObject headers = apiRequest.getHeaders();
        JSONObject params = apiRequest.getParams();


        List<String> urlList = replaceURLFormatAll(url_format, jsonEnumUrl);
        List<JSONObject> headersList = enumJSON(headers, jsonEnumHeaders);
        List<JSONObject> paramsList = enumJSON(params, jsonEnumParams);

        List<ApiRequest> apiRequestList = enumApiRequest(apiRequest, urlList, headersList, paramsList);
        return apiRequestList;

    }

    public static List<ApiRequest> enumApiRequest(ApiRequest apiRequest, List<String> urlList, List<JSONObject> headerList, List<JSONObject> paramList) {
        List<ApiRequest> list = new ArrayList<>();

        // 首先将每个url中的变量用ENUM数值替换
        List<ApiRequest> list_temp = new ArrayList<>();
        for (String url : urlList) {
            ApiRequest apiRequest_temp = apiRequest.clone();
            apiRequest_temp.setUrl(url);
            list_temp.add(apiRequest_temp);
        }

        list.addAll(list_temp);
        list_temp.clear();

        // 将每个apiRequest 的 headers 变量用ENUM数值替换
        for (ApiRequest apiTemp : list) {
            for (JSONObject header : headerList) {
                ApiRequest apiTemp1 = apiTemp.clone();
                apiTemp1.setHeaders(header);
                list_temp.add(apiTemp1);
            }
        }

        list.clear();
        list.addAll(list_temp);
        list_temp.clear();

        // 将每个apiRequest 的 params 中变量用ENUM数值替换
        for (ApiRequest apiTemp : list) {
            for (JSONObject params : paramList) {
                ApiRequest apiTemp1 = apiTemp.clone();
                apiTemp1.setParams(params);
                list_temp.add(apiTemp1);
            }
        }

        list.clear();
        list.addAll(list_temp);
        list_temp.clear();

        return list;

    }

    /**
     * 将jsonObject对象中的变量用ENUM中的具体值替换
     *
     * @param json       : 包含需要替换变量的json
     * @param enumParams : 包含变量ENUM的json
     * @return :
     */
    public static List<JSONObject> enumJSON(JSONObject json, JSONObject enumParams) {

        List<JSONObject> jsonList_1 = new ArrayList<>();
        List<JSONObject> jsonList_2 = new ArrayList<>();

        jsonList_1.add(json);

        Set<String> keys_list = enumParams.keySet();
        for (String key : keys_list) {
            JSONArray values = enumParams.getJSONArray(key);

            for (int i = 0; i < values.size(); i++) {
                for (JSONObject h_temp : jsonList_1) {
                    // 临时headers对象
                    JSONObject h_temp_enum = (JSONObject) h_temp.clone();

                    // EnumHeaders 和 EnumParams 中的数值支持userId 、token 以变量形式赋值
                    h_temp_enum.put(key, formatUidAndToken(String.valueOf(values.get(i))));
                    jsonList_2.add(h_temp_enum);
                }
            }
            jsonList_1.clear();
            jsonList_1.addAll(jsonList_2);
            jsonList_2.clear();
        }

        return jsonList_1;
    }

    /**
     * 针对url format,分析具体的urlPath 和 urlQuery 中的变量参数
     *
     * @param url : urlformat, format中变量以{}包裹
     * @return : 返回一个HashMap<String, List<String>>, key: path, query, sample
     */
    public static HashMap<String, List<String>> format_url(String url) {
        // 判断url是否包含{}, 即是否为一个有效的url
        // 当url包含{}时, 必须在json的有Enum对应的属性

        // url 格式为: urlPath?urlQuery
        List<String> list_urlPath = new ArrayList<>();
        List<String> list_urlQuery = new ArrayList<>();
        HashMap<String, List<String>> urlMap = new HashMap<>();

        String regStr = "\\{(.*?)\\}";
        Pattern pattern = Pattern.compile(regStr);

        String url_path;
        String url_query = "";
        if (url.indexOf("?") > 0) {
            url_path = url.substring(0, url.indexOf("?"));
            url_query = url.substring(url.indexOf("?") + 1);
        } else {
            url_path = url;
        }

        Matcher matcher_urlPath = pattern.matcher(url_path);
        while (matcher_urlPath.find()) {
            String paramTemp = matcher_urlPath.group(1);
            list_urlPath.add(paramTemp);
        }

        Matcher matcher_urlQuery = pattern.matcher(url_query);
        while (matcher_urlQuery.find()) {
            String paramTemp = matcher_urlQuery.group(1);
            list_urlQuery.add(paramTemp);
        }

        urlMap.put("path", list_urlPath);
        urlMap.put("query", list_urlQuery);

        return urlMap;
    }

    /**
     * 替换url中的{}, 初始化为数组中的第一个值
     *
     * @param url        :原始url format, 带{},或者不带
     * @param enumParams :配置文件中的Enum 数据块
     * @return : 返回一个urlSample
     */
    public static String sampleUrl(String url, JSONObject enumParams) {
//        JSONObject enumParams = (JSONObject) JSONPath.read(json, "$.apis[0].Enum");
        String sample = url;
        HashMap<String, List<String>> urlMap = format_url(url);
        sample = replaceURLFormatSamplePart(sample, urlMap.get("path"), enumParams);
        sample = replaceURLFormatSamplePart(sample, urlMap.get("query"), enumParams);

        return sample;
    }


    /**
     * 替换url中的{}
     *
     * @param url        : 原始url format, 带{},或者不带
     * @param list       : 原始url切分出来的urlPath 、 urlQuery 中的一个list
     * @param enumParams : 配置文件中的Enum 数据块
     * @return : 返回一个urlSample
     */
    public static String replaceURLFormatSamplePart(String url, List<String> list, JSONObject enumParams) {
        String sample = url;
        if (!list.isEmpty()) {
            for (int i = 0; i < list.size(); i++) {
                String param_Temp = list.get(i);

                if (enumParams.keySet().contains(param_Temp)) {
                    String param_Temp_valueSample = enumParams.getJSONArray(param_Temp).getString(0);
                    sample = sample.replace("{" + param_Temp + "}", param_Temp_valueSample);
                }
            }
        }
        return sample;
    }


    /**
     * 根据参数ENUM值,替换url的所有变量,生成组合的url list结果
     *
     * @param url        : url format, path、query中变量用{}包裹
     * @param enumParams : 包含参数ENUM数值的json
     * @return : 返回组合的url list, 假设变量为m1、m2、m3...mi, 每个变量的ENUM个数分别为n1、n2、n3...ni, 则总返回数为n1*n2*n3...ni
     */
    public static List<String> replaceURLFormatAll(String url, JSONObject enumParams) {
        HashMap<String, List<String>> urlMap = format_url(url);

        // 存储包括path、query参数替换后的所有的url list
        List<String> urlEnum_listAll = new ArrayList<>();

        // 存储替换path中参数后的url list
        List<String> urlEnum_path = replaceURLFormatPart(url, urlMap.get("path"), enumParams);

        // 对每一个替换了path中变量的url format,再继续替换query中变量
        for (String urlEnum_pathFormat : urlEnum_path) {
            List<String> urlEnum_QueryTemp = replaceURLFormatPart(urlEnum_pathFormat, urlMap.get("query"), enumParams);
            urlEnum_listAll.addAll(urlEnum_QueryTemp);
        }

        return urlEnum_listAll;
    }

    /**
     * 替换url中所有的{}, 生成组合结果
     *
     * @param url        : url format
     * @param list       : 变量参数list
     * @param enumParams : 参数值ENUM
     * @return : 返回替换后的组合url list
     */
    public static List<String> replaceURLFormatPart(String url, List<String> list, JSONObject enumParams) {

        List<String> url_s_1 = new ArrayList<>();  // 交叉替换存放数组1
        List<String> url_s_2 = new ArrayList<>();  // 交叉替换存放数组2

        url_s_1.add(url);  // 初始化交叉替换数组

        for (int i = 0; i < list.size(); i++) {
            String format = list.get(i);
            if (enumParams.keySet().contains(format)) {
                for (int m = 0; m < url_s_1.size(); m++) {
                    String url_format = url_s_1.get(m);

                    for (int j = 0; j < enumParams.getJSONArray(format).size(); j++) {
                        String value = enumParams.getJSONArray(format).getString(j);
                        String url_sample = url_format.replace("{" + format + "}", value);
                        url_s_2.add(url_sample);
                    }
                }
                url_s_1.clear();
                url_s_1.addAll(url_s_2);
                url_s_2.clear();
            }
        }

        return url_s_1;
    }

    /**
     * 将map对象转换为fastJson对象
     *
     * @param map : map
     * @return : fastJson
     */
    public static JSONObject map2JSON(Map map) {
        JSONObject json = new JSONObject();
        for (Object k : map.keySet()) {
            json.put((String) k, map.get(k));
        }
        return json;
    }

    public static void parserSQL(String file, String jsonPath) throws Exception {
        String baseDir = ApiRequestJsonFileParser.class.getResource("/").getFile();
        File jsonFile = new File(baseDir + "/testdata/" + file);
        ReadContext context = JsonPath.parse(jsonFile);

        net.minidev.json.JSONArray sqls;

        try {
            sqls = context.read(jsonPath);
            for (Object sql : sqls) {
                // json文件中sql标前面加上数据库名称,如 ljbdata.T_SinaCard
                DB.getInstance().db2.execute((String) sql);
                logger.info(" >>>>>> 执行SQL: " + sql);
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }


    public static void parserSQLBeforeClass(String file) throws Exception {
        String jsonPath = "$.sqlBeforeClass";
        parserSQL(file, jsonPath);
    }

    public static void parserSQLAeforeClass(String file) throws Exception {
        String jsonPath = "$.sqlAfterClass";
        parserSQL(file, jsonPath);
    }

    public static void parserSQLBeforeMethod(String file) throws Exception {
        String jsonPath = "$.sqlBeforeMethod";
        parserSQL(file, jsonPath);
    }

    public static void parserSQLAeforeMethod(String file) throws Exception {
        String jsonPath = "$.sqlAfterMethod";
        parserSQL(file, jsonPath);
    }


    /**
     * 替换json文件中的userid 和 token 变量为 Users.mapToken\Users.mapUid 中的具体值
     *
     * @param formatString : 以<%XX%> 包裹的变量, 如<%user_all_over.Token%>,  <%user_not_paypwd.UserId%>
     * @return : 返回替换后的String
     */
    public static String formatUidAndToken(String formatString) {

        String reg = "<%(.*)%>";
        Pattern pattern = Pattern.compile(reg);
        Matcher matcher = pattern.matcher(formatString);
        String formatUIDToken = "";
        while (matcher.find()) {
            formatUIDToken = matcher.group(1);
        }

        if (!formatUIDToken.isEmpty()) {
            try {
                String[] formatUIDTokens = formatUIDToken.split("\\.");
                String user = formatUIDTokens[0];
                String type = formatUIDTokens[1];

                String value = "";
                if (type.equalsIgnoreCase("Token")) {
                    value = Users.mapToken.get(user);
                } else if (type.equalsIgnoreCase("userid")) {
                    value = Users.mapUid.get(user);
                }

                logger.info("UserId&Token 替换前 :" +formatString);
                String valueAll = formatString.replaceAll("<%" + formatUIDToken + "%>", value);
                logger.info("UserId&Token 替换后 :" +valueAll);
                return valueAll;
            } catch (Exception e) {
                logger.error("UserId 或者 Token变量格式非法, 正确格式如<%user_all_over.Token%>,  <%user_not_paypwd.UserId%>");
            }
        }

        return formatString;
    }

//    public static void main(String[] args) throws Exception {
//        String file = "TestSample.json";
//        parserSQLBeforeClass(file);
//        parserSQLAeforeClass(file);
//        parserSQLBeforeMethod(file);
//        parserSQLAeforeMethod(file);
//
//    }

}
