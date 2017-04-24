package com.lj.autotest.util;

import com.alibaba.fastjson.JSONObject;

import java.io.IOException;
import java.util.Map;


/**
 * Created by lijing on 16/9/29.
 * Description: 自定义的HTTPRequest对象
 */
public class ApiRequest {
    private String description;
    private Map<String, String> beforeMethod;   // 包含所有的请求前置动作，如mysql、redis等
    private Map<String, String> afterMethod;    // 包含所有的请求后置动作，如mysql、redis等
    private String url;
    private String method;
    private JSONObject headers;
    private JSONObject body;

    private Object response_expected;
    private ApiResponse apiResponse_actual;
    private int status; // 用例执行状态，初始化为0， 1：测试成功； 2：测试失败； 3：执行失败


    public ApiRequest(String url, String method, JSONObject headers, JSONObject body) {
        this.url = url;
        this.method = method;
        this.headers = headers;
        this.body = body;
        this.status=0;
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

    public ApiResponse getApiResponse_actual() {
        return apiResponse_actual;
    }

    public void setApiResponse_actual(ApiResponse apiResponse_actual) {
        this.apiResponse_actual = apiResponse_actual;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Map<String, String> getBeforeMethod() {
        return beforeMethod;
    }

    public void setBeforeMethod(Map<String, String> beforeMethod) {
        this.beforeMethod = beforeMethod;
    }

    public Map<String, String> getAfterMethod() {
        return afterMethod;
    }

    public void setAfterMethod(Map<String, String> afterMethod) {
        this.afterMethod = afterMethod;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
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
        apiRequest1.setDescription(this.description);
        apiRequest1.setBeforeMethod(this.beforeMethod);
        apiRequest1.setAfterMethod(this.afterMethod);
        apiRequest1.setResponse_expected(this.response_expected);
        apiRequest1.setApiResponse_actual(this.apiResponse_actual);
        apiRequest1.setStatus(this.status);
        return apiRequest1;
    }


    /**
     * 封装ApiRequest对象的请求操作，目前仅支持4种：get\post\delete\put
     *
     * @return : 返回自定义的response
     * @throws IOException ：
     */
    public ApiResponse visit_url() throws IOException {
        ApiResponse apiResponse;
        if (this.getMethod().equalsIgnoreCase("get")) {
            apiResponse = HTTP.get(this.getUrl(), this.getHeaders());
        } else if (this.getMethod().equalsIgnoreCase("post")) {
            apiResponse = HTTP.post(this.getUrl(), this.getBody(), this.getHeaders());
        } else if (this.getMethod().equalsIgnoreCase("delete")) {
            apiResponse = HTTP.delete(this.getUrl(), this.getHeaders());
        } else if (this.getMethod().equalsIgnoreCase("put")) {
            apiResponse = HTTP.put(this.getUrl(), this.getHeaders());
        } else {
            return null;
        }
        this.apiResponse_actual = apiResponse;
        return apiResponse;
    }


    /**
     * 比较预期结果和实际结果
     *
     * @return ：
     * @throws IOException ：
     */
    public ApiRequestStatusEnum compareResponse() throws Exception {
        try {
            boolean result = ApiResponseCompare.compare(this.response_expected, this.apiResponse_actual);
            if (result) {
                this.setStatus(1);
                return ApiRequestStatusEnum.SUCCESS;
            } else {
                this.setStatus(2);
                return ApiRequestStatusEnum.FAIL;
            }
        }catch (Exception e){
            this.setStatus(3);
            return ApiRequestStatusEnum.BLOCK;
        }
    }

    /**
     * 完整的测试用例执行过程，包括3步骤： 请求执行前置、请求执行、请求执行后置
     *
     * @return ： 返回请求执行的response
     * @throws IOException ：
     */
    public ApiRequestStatusEnum execute() throws Exception {
        try {
            // TODO:执行请求前置动作
            ApiRequestExecute.execute(this.beforeMethod);

            // 执行restful请求
            ApiResponse apiResponse = visit_url();

            // TODO:执行请求后置动作
            ApiRequestExecute.execute(this.afterMethod);

            return compareResponse();
        }catch (Exception e){
            this.setStatus(3);
            return ApiRequestStatusEnum.BLOCK;
        }
    }


}
