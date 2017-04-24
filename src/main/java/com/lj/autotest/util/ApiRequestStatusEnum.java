package com.lj.autotest.util;

/**
 * Created by lijing on 2017/4/24.
 * Desc:
 */
public enum ApiRequestStatusEnum {
    INIT(0, "初始状态"),
    SUCCESS(1, "用例执行成功且结果正常"),
    FAIL(2, "用例执行成功但结果异常"),
    BLOCK(3, "用例执行失败");

    int status;
    String desc;
    ApiRequestStatusEnum(int status, String desc){
        this.status=status;
        this.desc = desc;
    }
}
