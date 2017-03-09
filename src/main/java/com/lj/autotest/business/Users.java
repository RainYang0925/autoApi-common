package com.lj.autotest.business;

import java.util.HashMap;

/**
 * Created by lijing on 16/5/17.
 * Description:
 */
public class Users {

    public static HashMap<String, String> mapUid = new HashMap();
    public static HashMap<String, String> mapToken = new HashMap();

    // 单例模式
    private static Users instance;

    static {
        try {
            instance = new Users();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public static Users getInstance() {
        return instance;
    }

}
