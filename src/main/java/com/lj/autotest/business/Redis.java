package com.lj.autotest.business;

import com.lj.autotest.util.Config;
import redis.clients.jedis.Jedis;

/**
 * Created by lijing on 16/5/30.
 * Description:
 * lpush ljb:queue:operatorid:asyn  T_OperatorRecord.operatorid
 * lpush ljb:operator:queue:id  T_OperatorRecord.operatorid  :  sian回调成功通知lj侧处理队列(State=1,logicComplete=0)
 */
public class Redis {
    private static Jedis jedis = null;

    private static Redis instance = new Redis();
    private Redis(){
        String host = Config.properties.getProperty("redis.host");
        Integer port = Integer.parseInt(Config.properties.getProperty("redis.port"));
        Integer timeout = Integer.parseInt(Config.properties.getProperty("redis.timeout"));
        jedis = new Jedis(host, port, timeout);

        // 初始化ljb相关redis开关
        jedis.hset("ljb:switch", "fas_relacc", "1"); //打开用户中心实名认证
        jedis.hset("ljb:switch", "rela_account_register_mode", "1");

    }

    public static Redis getInstance(){
        return instance;
    }

    public static Jedis getJedis() {
        return jedis;
    }

    public static void setJedis(Jedis jedis) {
        Redis.jedis = jedis;
    }

    public static void initUserljScore(String userid, double lj_score){
        jedis.zadd("Credit:PR", lj_score, userid);

    }

    public static void main(String[] args){
        Jedis jedis = Redis.jedis;
        jedis.hset("ljb:switch", "fas_relacc", "1"); //打开用户中心实名认证
        jedis.hset("ljb:switch", "rela_account_register_mode", "1");

    }
}
