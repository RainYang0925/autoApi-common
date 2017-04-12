package com.lj.autotest.util;

import com.alibaba.druid.pool.DruidDataSourceFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;


/**
 * Created by lijing on 16/5/5.
 * Description: MySQL初始化连接，及简单封装的增删改查
 */
public class MySQL {
    String db;
    DataSource dataSource;
    Connection connection;
    Statement stmt;

    public MySQL(String database) throws Exception {
        db = database;
        HashMap<String, String> properties = new HashMap<>();

        properties.put("url", Config.properties.getProperty(db + ".url"));
        properties.put("username", Config.properties.getProperty(db + ".username"));
        properties.put("password", Config.properties.getProperty(db + ".password"));

        properties.put("initialSize", "2");
        properties.put("minIdle", "2");
        properties.put("maxActive", "5");
        dataSource = DruidDataSourceFactory.createDataSource(properties);
        connection = dataSource.getConnection();
        stmt = connection.createStatement();
    }

    public void close() throws SQLException {
        if (stmt != null) {
            stmt.close();
        }
        if (connection != null) {
            connection.close();
        }
    }


    // 1. ResultSet executeQuery(String sql); 执行SQL查询，并返回ResultSet 对象。
    // 2. int executeUpdate(String sql); 可执行增，删，改，返回执行受到影响的行数。
    // 3. boolean execute(String sql); 可执行任何SQL语句，返回一个布尔值，表示是否返回   ResultSet 。

    /**
     * 执行SQL查询，并返回ResultSet 对象
     * @param sql : 查询sql
     * @return    : 返回ResultSet 对象
     * @throws Exception :
     */
    public ResultSet query(String sql) throws Exception {
        return stmt.executeQuery(sql);
    }

    /**
     * 可执行增，删，改，返回执行受到影响的行数
     * @param sql : 增，删，改 sql
     * @return    : 返回update操作受影响的行数
     * @throws Exception :
     */
    public int update(String sql) throws Exception {
        return stmt.executeUpdate(sql);
    }


    /**
     * 可执行任何SQL语句，返回一个布尔值，表示是否返回   ResultSet
     * @param sql :  行任何SQL语句
     * @return    :  返回一个布尔值，表示是否返回  ResultSet
     * @throws Exception :
     */
    public Boolean execute(String sql) throws Exception {
        return stmt.execute(sql);
    }


//    public static void main(String[] args) throws Exception {
//        System.out.println("Test MySQL Connection...");
//
//        String sql = "select * from T_UserInfo_Ext where UserId = 120000058;";
//        String sql2 = "update T_UserInfo_Ext set Gender=1 where UserId=120000058;";
//        MySQL mysql_ljddata = new MySQL("ljddata");
//        ResultSet resultSet = mysql_ljddata.query(sql);
//
//        while (resultSet.next()) {
//            System.out.println(resultSet.getInt("UserId"));
//            System.out.println(resultSet.getString("Mobile"));
//            System.out.println(resultSet.getString("Gender"));
//        }

//        Boolean resultSet2 = mysql_ljddata.update(sql2);
//
//        System.out.println(resultSet2);
//
//        ResultSet resultSet3 = mysql_ljddata.query(sql);
//
//        while (resultSet3.next()) {
//            System.out.println(resultSet3.getInt("UserId"));
//            System.out.println(resultSet3.getString("Mobile"));
//            System.out.println(resultSet3.getString("Gender"));
////            System.out.println(resultSet);
//        }
//
//        mysql_ljddata.close();
//
//    }
}
