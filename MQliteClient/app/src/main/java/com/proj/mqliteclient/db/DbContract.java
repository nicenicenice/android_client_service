package com.proj.mqliteclient.db;

/**
 * Created by user on 28/04/2018.
 */

// просто набор констант, которые мы хотим использовать. (таблицы, поля и тп)
public interface DbContract {
    String DB_NAME = "main.sqlite";

    String TEST = "test";
    interface Test {
        String ID = "rowid";
        String NUM1 = "num1";
        String NUM2 = "num2";
        String NUM3 = "num3";
        String NUM4 = "num4";
        String PICTURE = "picture";
    }
}
