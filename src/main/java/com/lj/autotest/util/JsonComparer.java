package com.lj.autotest.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;

/**
 * Created by jerrychen on 2016/09/18.
 */
public class JsonComparer {

    public final static String SRC_VALUE = "sourceValue";
    public final static String EXPECTED_VALUE = "expectedValue";

    private static Logger logger = LoggerFactory.getLogger(JsonComparer.class);

    public static JSONObject compareJson(JSONObject expectedJson,
                                         JSONObject srcJson,
                                         String key) {
        if (StringUtils.isBlank(key)) {
            logger.debug("the paramter key is " + key + " and rename it to JSONObjectDefaultKey");
            key = "JSONObjectDefaultKey";
        }

        logger.debug("compare JSONObject for key: " + key);
        JSONObject diffDetails = new JSONObject(true);

        if (expectedJson == null) {
            logger.warn("expectedJson is null");
            return diffDetails;
        }

        if (srcJson == null) {
            logger.warn("srcJson is null");
            logger.warn("key: " + key);
            logger.warn("expected: " + expectedJson.toJSONString() );
            logger.warn("actual: null");

            diffDetails.put(EXPECTED_VALUE, expectedJson);
            diffDetails.put(SRC_VALUE, srcJson);
            return diffDetails;
        }

        Iterator<String> iterator = expectedJson.keySet().iterator();
        while (iterator.hasNext()) {
            key = iterator.next();
            JSONObject diffElemt = compareJson(expectedJson.get(key), srcJson.get(key), key);
            if (diffElemt != null && !diffElemt.isEmpty()) {
                diffDetails.put(key, diffElemt);
            }
        }
        return diffDetails;
    }

    public static JSONObject compareJson(JSONArray expectedJSONArray,
                                         JSONArray srcJSONArray,
                                         String key) {
        if (StringUtils.isBlank(key)) {
            logger.debug("the paramter key is " + key + " and rename it to JSONArrayDefaultKey");
            key = "JSONArrayDefaultKey";
        }

        logger.debug("compare JSONArray for key: " + key);
        JSONObject diffDetails = new JSONObject(true);

        if (expectedJSONArray == null) {
            logger.warn("expectedJSONArray is null");
            return diffDetails;
        }

        if (srcJSONArray == null) {
            logger.warn("srcJSONArray is null");
            logger.warn("key: " + key);
            logger.warn("expected: " + expectedJSONArray.toJSONString());
            logger.warn("actual: null");

            diffDetails.put(EXPECTED_VALUE, expectedJSONArray);
            diffDetails.put(SRC_VALUE, srcJSONArray);
            return diffDetails;
        }

        if (expectedJSONArray.size() != srcJSONArray.size()) {
            logger.warn("key: " + key);
            logger.warn("expected: " + expectedJSONArray.toJSONString());
            logger.warn("actual: " + srcJSONArray.toJSONString());

            diffDetails.put(EXPECTED_VALUE, expectedJSONArray);
            diffDetails.put(SRC_VALUE, srcJSONArray);

            return diffDetails;
        }

        Iterator expectedIterator = expectedJSONArray.iterator();
        Iterator srcIterator = srcJSONArray.iterator();
        int i = 0;

        while (expectedIterator.hasNext()) {
            String keyI = key + i;
            JSONObject diffElemt = compareJson(expectedIterator.next(), srcIterator.next(), keyI);
            if (diffElemt != null && !diffElemt.isEmpty()) {
                diffDetails.put(keyI, diffElemt);
            }
            i++;
        }
        return diffDetails;
    }

    public static JSONObject compareJson(String expectedStr,
                                         String srcStr,
                                         String key) {
        if (StringUtils.isBlank(key)) {
            logger.debug("the paramter key is " + key + " and rename it to StringDefaultKey");
            key = "StringDefaultKey";
        }

        logger.debug("compare String for key: " + key);
        JSONObject diffELemt = new JSONObject();

        if (expectedStr == null) {
            logger.warn("expectedStr is null");
            return diffELemt;
        }

        if (srcStr == null) {
            logger.warn("srcStr is null");
            logger.warn("key: " + key);
            logger.warn("expected: " + expectedStr);
            logger.warn("actual: null");

            diffELemt.put(EXPECTED_VALUE, expectedStr);
            diffELemt.put(SRC_VALUE, srcStr);
            return diffELemt;
        }

        if (!expectedStr.equals(srcStr)) {
            logger.warn("key: " + key);
            logger.warn("expected: " + expectedStr);
            logger.warn("actual: "+ srcStr);

            diffELemt.put(EXPECTED_VALUE, expectedStr);
            diffELemt.put(SRC_VALUE, srcStr);
        }
        return diffELemt;
    }

    public static JSONObject compareJson(int expectedInt,
                                         int srcInt,
                                         String key) {
        if (StringUtils.isBlank(key)) {
            logger.debug("the paramter key is " + key + " and rename it to intDefaultKey");
            key = "intDefaultKey";
        }

        logger.debug("compare int for key: " + key);
        JSONObject diffELemt = new JSONObject();

        if (expectedInt != srcInt) {
            logger.warn("key: " + key);
            logger.warn("expected: " + expectedInt);
            logger.warn("actual: " + srcInt);

            diffELemt.put(EXPECTED_VALUE, expectedInt);
            diffELemt.put(SRC_VALUE, srcInt);
        }
        return diffELemt;
    }

    public static JSONObject compareJson(Object expectedJson,
                                         Object srcJson,
                                         String key) {
        JSONObject diffDetails = new JSONObject(true);

        if (expectedJson == null) {
            logger.warn("expectedJson is null");
            return diffDetails;
        }

        if (srcJson == null) {
            logger.warn("srcJson is null");
            logger.warn("key: " + key);
            logger.warn("expected: " + expectedJson.toString());
            logger.warn("actual: null");

            diffDetails.put(EXPECTED_VALUE, expectedJson);
            diffDetails.put(SRC_VALUE, srcJson);
            return diffDetails;
        }

        if (expectedJson instanceof JSONObject) {
            return compareJson((JSONObject) expectedJson, (JSONObject) srcJson, key);
        } else if (expectedJson instanceof JSONArray) {
            return compareJson((JSONArray) expectedJson, (JSONArray) srcJson, key);
        } else if (expectedJson instanceof String) {
            return compareJson((String) expectedJson, (String) srcJson, key);
        } else if (expectedJson instanceof Integer) {
            return compareJson((int) expectedJson, (int) srcJson, key);
        } else {
            return compareJson(expectedJson.toString(), srcJson.toString(), key);
        }
    }

    public static JSONObject compareJsonWithIgnore(JSONObject expectedJson,
                                                   JSONObject srcJson,
                                                   JSONObject ignoreKeys) {
        return ignoreKeysFromJson(compareJson(expectedJson, srcJson, null), ignoreKeys);
    }

    public static JSONObject ignoreKeysFromJson(JSONObject srcJson,
                                                JSONObject ignoreKeys) {
        if (null == srcJson) {
            logger.warn("srcJson is null");
            return null;
        }

        JSONObject result = (JSONObject) srcJson.clone();
        if (null == ignoreKeys) {
            logger.warn("ignoreKeys is null");
            return result;
        }

        Iterator<String> iterator = ignoreKeys.keySet().iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();

            if (!result.containsKey(key)) {
                logger.debug("there is no key : " + key + " in "
                        + result.toJSONString() +" to ignore");
                continue;
            }

            Object value = ignoreKeys.get(key);
            if (value instanceof JSONObject) {
                Object srcValue = result.get(key);

                if (srcValue instanceof JSONObject) {
                    JSONObject diffJson = ignoreKeysFromJson((JSONObject) srcValue, (JSONObject) value);
                    if (diffJson.isEmpty()) {
                        logger.debug("remove key : " + key + " in " + result.toJSONString());
                        logger.debug("the value is " + result.get(key).toString());
                        result.remove(key);
                    } else {
                        result.put(key, diffJson);
                    }
                } else {
                    logger.warn("Value in " + result.toJSONString() + " for key : " + key
                            + " shoud be a JSON according to the ignoreKeys");
                    continue;
                }
            } else {
                logger.debug("remove key : " + key + " in " + result.toJSONString());
                logger.debug("the value is " + result.get(key).toString());
                result.remove(key);
            }
        }
        return result;
    }
}
