package com.lj.autotest.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by lijing on 16/5/20.
 * Description:
 */
public class Config {

    public static Properties properties;
    private static Config instance = new Config();


    private Config(){
        properties = new Properties();
        InputStream in = Config.class.getResourceAsStream("/config.properties");
        try {
            properties.load(in);
//            System.out.println(properties.get("domain.appapi"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Config getInstance(){
        return instance;
    }

//    public static void main(String[] args){
//        Config config = Config.getInstance();
//        System.out.println(config.properties.get("domain.appapi"));
//    }
}
