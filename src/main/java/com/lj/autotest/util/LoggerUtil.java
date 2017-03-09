package com.lj.autotest.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by lijing on 16/6/3.
 * Description:
 */
public class LoggerUtil {
    public static void main(String[] args){
        Logger logger = LoggerFactory.getLogger(LoggerUtil.class);
        logger.info("Test log ... info");
        logger.debug("Test log ... debug");
        logger.warn("Test log ... warn");
        Integer code = 200;
        logger.info(String.valueOf(code));
    }
}
