package com.lj.autotest.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Created by lijing on 2017/4/24.
 * Desc:
 */
public class ApiRequestExecute {
    private static Logger logger = LoggerFactory.getLogger(ApiRequestExecute.class);

    public static void execute(Map<String, String> map) {
        try {
            for (String k : map.keySet()) {
                if (k.startsWith("mysql_")) {
                    execute_mysql(map.get(k));
                }
                if (k.startsWith("redis_")) {
                    execute_redis(map.get(k));
                }
            }
        } catch (Exception e) {

        }
    }

    public static void execute_mysql(String sql) {
        logger.info(">>>>>> 执行mysql： " + sql);
    }


    public static void execute_redis(String redis) {
        logger.info(">>>>>> 执行redis: " + redis);
    }
}
