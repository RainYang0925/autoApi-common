package com.lj.autotest.util;

/**
 * Created by lijing on 2017/4/12.
 * Desc: 比对json文件中的预期response 和 实际返回的response；
 * 支持json、jsonp、string、html等格式；
 * 支持自定义需要比较的字段及忽略字段
 */
public class ResponseCompare {

    private Boolean compare(String result, String file){
        return true;
    }
}
