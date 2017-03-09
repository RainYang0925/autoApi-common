package com.lj.autotest.business;

import com.lj.autotest.util.MySQL;

import java.sql.SQLException;

/**
 * Created by lijing on 16/5/16.
 * Description:
 */
public class DB {
    public MySQL db1;
    public MySQL db2 = null;
    public MySQL db3 = null;


    private DB() {
        try {
            db1 = new MySQL("ljbdata");
            db2 = new MySQL("ljddata");
            db3 = new MySQL("ljreport");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static final DB instance = new DB();

    public static DB getInstance() {
        return instance;
    }

    public void close() throws SQLException {
        db1.close();
        db2.close();
        db3.close();
    }
}
