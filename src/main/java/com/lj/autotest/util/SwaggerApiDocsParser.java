package com.lj.autotest.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by lijing on 16/9/22.
 * Description: 自动解析SwaggerUI,提炼Api请求,并组织用例。
 */
public class SwaggerApiDocsParser {

    private static Logger logger = LoggerFactory.getLogger(SwaggerApiDocsParser.class);

    /**
     * 解析SwaggerUI 的 api-docs 数据接口
     *
     * @param url : SwaggerUI 数据接口url,如 http://10.0.40.62:9260/v2/api-docs?group=ops-doublecoupon
     * @return : 包含每个path对象的List集合
     * @throws IOException :
     */
    public static List<SwaggerPath> parser(String url) throws IOException {
        Response response = HTTP.get(url);
        JSONObject body = response.getBodyJson();

        List<SwaggerPath> listApis = new ArrayList<>(); // 待返回的对象

        // 解析出所有的tags
//        JSONArray tags = body.getJSONArray("tags");
//        for (int i = 0; i< tags.size(); i++){
//            System.out.println(tags.getJSONObject(i).getString("name"));
//            System.out.println(tags.getJSONObject(i).getString("description"));
//        }

        JSONObject paths = body.getJSONObject("paths");       // 该项目所有的接口 path

        for (String key : paths.keySet()) {
            JSONObject path = paths.getJSONObject(key);       // 解析每个具体的path

//            String path_method = "";                          // 每个path 中具体的方法:post、get、put、delete
            String path_url = key;                             // 每个path中具体的url
            JSONObject path_headers = new JSONObject();     // 每个path中具体的请求headers
            JSONObject path_paramters = new JSONObject();   // 每个path中具体的请求参数,post等

            // 获取请求方式, 目前仅支持post、get、put、delete

            for(String path_method : path.keySet()) {

//                if (path.containsKey("post")) {
//                    path_method = "post";
//                } else if (path.containsKey("get")) {
//                    path_method = "get";
//                } else if (path.containsKey("delete")) {
//                    path_method = "delete";
//                } else if (path.containsKey("put")) {
//                    path_method = "put";
//                }

                //
                JSONObject jsonObject_method = path.getJSONObject(path_method);

                // 每个path中具体的tags, 带[],做个转换
                String path_tags = jsonObject_method.getString("tags").replace("[", "").replace("]", "");

                // 每个path中具体的描述
                String path_description = jsonObject_method.getString("summary");
                String path_operationId = jsonObject_method.getString("operationId");

                // 此方法的返回值类型,带[],需要做个转换
                String path_consumes = jsonObject_method.getString("consumes").replace("[", "").replace("]", "");

                // 此方法的接受参数类型, 对应headers中的Accept,带[],需要做个转换
                String path_produces = jsonObject_method.getString("produces").replace("[", "").replace("]", "");
                path_headers.put("Accept", path_produces);

                JSONArray parameters = jsonObject_method.getJSONArray("parameters");

                List<String> query_param = new ArrayList();    // 每个接口中的in=query的参数集合
                List<String> path_param = new ArrayList<>();   // 每个接口中的in=path 的参数集合

                for (int i = 0; i < parameters.size(); i++) {
                    JSONObject parameter = parameters.getJSONObject(i);
                    String parameter_name = parameter.getString("name");

                /*
                  参数类型:path、query、header
                  path:  参数在url路径的path位置
                  query: 参数在url路径的query位置
                  header:参数在请求的headers中
                  body:  参数在POST请求的data中
                 */
                    String parameter_in = parameter.getString("in");
                    String parameter_description = parameter.getString("description");
                    Boolean parameter_required = parameter.getBoolean("required");
                    String parameter_type = parameter.getString("type");


                    if (Objects.equals(parameter_in, "header")) {
                        path_headers.put(parameter_name, parameter_description);
                    } else if (Objects.equals(parameter_in, "query")) {
                        path_url = path_url + "&" + parameter_name + "=" + parameter_name;
                        query_param.add(parameter_name);
                    } else if (Objects.equals(parameter_in, "path")) {
                        path_url = path_url.replaceAll("\\{" + parameter_name + "\\}", parameter_name);
                        path_param.add(parameter_name);
                    } else if (Objects.equals(parameter_in, "body")) {
                        path_paramters.put(parameter_name, parameter_name);
                    }
                }

                // 解析标准的请求url, 包括url、headers、method
                // 替换第一个& 为 ? , 符合url拼接规则
                path_url = path_url.replaceFirst("&", "?");

                SwaggerPath api = new SwaggerPath(path_method, path_url, path_param, query_param, path_headers, path_paramters, path_description);

                listApis.add(api);
            }

        }
        return listApis;

//        for (SwaggerApiRequest api : listApis) {
//            logger.info(">>>>>>>>>>>>>>>>>>>>>>>>>");
//            logger.info("url     : " + api.getUrl());
//            logger.info("method  : " + api.getMethod());
//            logger.info("headers : " + api.getHeaders().toJSONString());
//            logger.info("params  : " + api.getParamters().toJSONString());
//        }

    }


}
