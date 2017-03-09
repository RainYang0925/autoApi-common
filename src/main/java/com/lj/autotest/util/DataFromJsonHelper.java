package com.lj.autotest.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.Iterator;

/**
 * Created by jerrychen on 2016/09/08.
 */
public class DataFromJsonHelper {

    public final static String API_PATH = "apiPath";

    public final static String ROOT_JSON = "rootJson";
    public final static String DATA_STRUCT = "dataStruct";
    public final static String DATA_ARRAY = "dataArray";

    private String filePath;
    private String stringFromFile;
    private JSONObject jsonObject;

    private static Logger logger = LoggerFactory.getLogger(DataFromJsonHelper.class);

    public DataFromJsonHelper(String filePath) throws IOException {
        this.filePath = filePath;
        readStringFromFile();
        convertStringToJSONObject();
    }

    private void readStringFromFile() throws IOException {
        BufferedReader reader = null;

        try {
            InputStream inputStream = DataFromJsonHelper.class.getResourceAsStream(filePath);
            reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder buffer = new StringBuilder();
            String line  = null;

            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }
            stringFromFile = buffer.toString();

        } catch (NullPointerException ex) {
            logger.warn("File not exist: " + filePath);
            stringFromFile = "";

        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }

    private void convertStringToJSONObject() {
        if (StringUtils.isNotBlank(stringFromFile)) {
            jsonObject = JSON.parseObject(stringFromFile);
        } else {
            logger.warn("source json file is empty");
        }
    }

    public JSONObject getRootJsonObject() {
        return (JSONObject) jsonObject.clone();
    }

    public static JSONObject getJSONObjectFrom(String sourceJsonName, JSONObject sourceJson, String key) {
        JSONObject dataJson = new JSONObject();
        if (StringUtils.isBlank(sourceJsonName)) {
            logger.debug("sourceJsonName is blank, rename it into " + ROOT_JSON);
            sourceJsonName = ROOT_JSON;
        }
        logger.debug("get JSONObject " + key + " from source JSONObject " + sourceJsonName);

        if (null == sourceJson) {
            logger.warn(sourceJsonName + " JSONObject is null");
        } else if (sourceJson.isEmpty()) {
            logger.warn(sourceJsonName + " JSONObject is empty");
        } else if (!sourceJson.containsKey(key)) {
            logger.warn(sourceJsonName + " JSONObject has no key:" + key);
        } else {
            dataJson = sourceJson.getJSONObject(key);
        }

        if (dataJson.isEmpty()) {
            logger.warn("JSONObject from key:" + key + " in " + sourceJsonName + " is empty");
        }
        return dataJson;
    }

    public static JSONArray getJSONArrayFrom(String sourceJsonName, JSONObject sourceJson, String key) {
        JSONArray jsonArray = new JSONArray();
        if (StringUtils.isBlank(sourceJsonName)) {
            logger.debug("sourceJsonName is blank, rename it into " + ROOT_JSON);
            sourceJsonName = ROOT_JSON;
        }
        logger.debug("get JSONArray " + key + " from source JSONObject " + sourceJsonName);

        if (null == sourceJson) {
            logger.warn(sourceJsonName + " JSONObject is null");
        } else if (sourceJson.isEmpty()) {
            logger.warn(sourceJsonName + " JSONObject is empty");
        } else if (!sourceJson.containsKey(key)) {
            logger.warn(sourceJsonName + " JSONObject has no key:" + key);
        } else {
            jsonArray = sourceJson.getJSONArray(key);
        }

        if (jsonArray.isEmpty()) {
            logger.warn("JSONArray from key:" + key + " in " + sourceJsonName + " is empty");
        }
        return jsonArray;
    }

    public static String getStringFrom(String sourceJsonName, JSONObject sourceJson, String key) {
        String resString = null;
        if (StringUtils.isBlank(sourceJsonName)) {
            logger.debug("sourceJsonName is blank, rename it into " + ROOT_JSON);
            sourceJsonName = ROOT_JSON;
        }
        logger.debug("get JSONArray " + key + " from source JSONObject " + sourceJsonName);

        if (null == sourceJson) {
            logger.warn(sourceJsonName + " JSONObject is null");
        } else if (sourceJson.isEmpty()) {
            logger.warn(sourceJsonName + " JSONObject is empty");
        } else if (!sourceJson.containsKey(key)) {
            logger.warn(sourceJsonName + " JSONObject has no key:" + key);
        } else {
            resString = sourceJson.getString(key);
        }

        if (StringUtils.isBlank(resString)) {
            logger.warn("String from key:" + key + " in " + sourceJsonName + " is blank : " + resString);
        }
        return resString;
    }

    public String getApiPath() {
        return getStringFrom(ROOT_JSON, getRootJsonObject(), API_PATH);
    }

    public JSONObject getMethodData(String methodName) {
        return getJSONObjectFrom(ROOT_JSON, getRootJsonObject(), methodName);
    }

    public JSONObject getMethodData(Method method) {
        return getMethodData(method.getName());
    }

    public JSONObject getJSONObjectFromMethodData(String methodName, String key) {
        return getJSONObjectFrom(methodName, getMethodData(methodName), key);
    }

    public JSONObject getJSONObjectFromMethodData(Method method, String key) {
        return getJSONObjectFromMethodData(method.getName(), key);
    }

    public JSONArray getJSONArrayFromMethodData(String methodName, String key) {
        return getJSONArrayFrom(methodName, getMethodData(methodName), key);
    }

    public JSONArray getJSONArrayFromMethodData(Method method, String key) {
        return getJSONArrayFromMethodData(method.getName(), key);
    }

    public static Object getObejctFromJson(JSONObject jsonObject, JSONObject dataStruct) {
        if (null == dataStruct) {
            logger.warn("JSONObject dataStruct is null");
            return null;
        }
        if (dataStruct.isEmpty()) {
            logger.warn("JSONObject dataStruct is empty");
            return null;
        }
        if (null == jsonObject) {
            logger.warn("JSONObject jsonObject is null");
            return null;
        }
        if (jsonObject.isEmpty()) {
            logger.warn("JSONObject jsonObject is empty");
            return null;
        }

        if (dataStruct.size() > 1) {
            logger.error("JSONObject dataStruct has over 1 key");
        }

        Iterator<String> iterator = dataStruct.keySet().iterator();
        String keyShouldBeOnly = iterator.next();
        if (jsonObject.containsKey(keyShouldBeOnly)) {
            Object subDataStruct = dataStruct.get(keyShouldBeOnly);

            if (subDataStruct instanceof String) {
                logger.debug("get class type : " + subDataStruct + " for key : " + keyShouldBeOnly);
                if (String.class.getSimpleName().equals(subDataStruct)) {
                    return jsonObject.getString(keyShouldBeOnly);

                } else if (int.class.getSimpleName().equals(subDataStruct)) {
                    return jsonObject.getIntValue(keyShouldBeOnly);

                } else if (JSONObject.class.getSimpleName().equals(subDataStruct)) {
                    return jsonObject.getJSONObject(keyShouldBeOnly);

                } else if (JSONArray.class.getSimpleName().equals(subDataStruct)) {
                    return jsonObject.getJSONArray(keyShouldBeOnly);

                } else {
                    return jsonObject.get(keyShouldBeOnly);
                }
            } else if (subDataStruct instanceof JSONObject) {
                return getObejctFromJson(jsonObject.getJSONObject(keyShouldBeOnly), (JSONObject) subDataStruct);

            } else {
                logger.error("the class of value : " + subDataStruct.toString() + " for key : "
                        + keyShouldBeOnly + " should be String or JSONObject in dataStruct");
                return null;
            }
        } else {
            logger.warn("jsonObject has no key : " + keyShouldBeOnly);
            return null;
        }
    }

    public static Object[] getObjectsFromJson(JSONObject jsonData,
                                              JSONArray dataStructArray) {
        Object[] objectsArray = new Object[] {};

        if (null == dataStructArray) {
            logger.warn("JSONArray dataStructArray is null");
            return objectsArray;
        } else if (dataStructArray.isEmpty()) {
            logger.warn("JSONArray dataStructArray is empty");
            return objectsArray;
        }
        int arraySize = dataStructArray.size();
        objectsArray = new Object[arraySize];

        if (null == jsonData) {
            logger.warn("JSONObject jsonData is null");
            return objectsArray;
        } else if (jsonData.isEmpty()) {
            logger.warn("JSONObject jsonData is empty");
            return objectsArray;
        }

        for (int i = 0; i < arraySize; i++) {
            objectsArray[i] = getObejctFromJson(jsonData, dataStructArray.getJSONObject(i));
        }

        return objectsArray;
    }

    public Object[][] getTestObjectsFromMethodWithDataStruct(String methodName) {
        JSONArray methodTestDataArray = getJSONArrayFromMethodData(methodName, DATA_ARRAY);
        JSONArray testDataStructArray = getJSONArrayFromMethodData(methodName, DATA_STRUCT);

        Object[][] objectsMatrix;
        int arraySize = methodTestDataArray.size();

        objectsMatrix = new Object[arraySize][];

        for(int i = 0; i < arraySize; i++) {
            JSONObject methodTestData = methodTestDataArray.getJSONObject(i);
            objectsMatrix[i] = getObjectsFromJson(methodTestData, testDataStructArray);
        }

        return objectsMatrix;
    }

    public Object[][] getTestObjectsFromMethodWithDataStruct(Method method) {
        return getTestObjectsFromMethodWithDataStruct(method.getName());
    }

}
