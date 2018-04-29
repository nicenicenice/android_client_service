package com.proj.mqliteclient.db;

import android.content.ContentValues;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 28/04/2018.
 */
public class DbUtils {
    public static List<ContentValues> getResultStringListAndClose(Cursor c) {
        final List<ContentValues> resultStringList = getAllDataFromDB(c);
        closeCursor(c);
        return resultStringList;
    }

    private static List<ContentValues> getAllDataFromDB(Cursor c) {
        List<ContentValues> resList = new ArrayList<>();
        if (c != null && (c.isFirst() || c.moveToFirst())) {
            do {
                ContentValues dBValues = new ContentValues();

                int num1 = c.getColumnIndex(DbContract.Test.NUM1);
                if (!c.isNull(num1)) {
                    dBValues.put(DbContract.Test.NUM1, c.getDouble(num1));
                }

                int num2 = c.getColumnIndex(DbContract.Test.NUM2);
                if (!c.isNull(num2)) {
                    dBValues.put(DbContract.Test.NUM2, c.getDouble(num2));
                }

                int num3 = c.getColumnIndex(DbContract.Test.NUM3);
                if (!c.isNull(num3)) {
                    dBValues.put(DbContract.Test.NUM3, c.getDouble(num3));
                }

                int num4 = c.getColumnIndex(DbContract.Test.NUM4);
                if (!c.isNull(num4)) {
                    dBValues.put(DbContract.Test.NUM4, c.getDouble(num4));
                }

                int pictureCol = c.getColumnIndex(DbContract.Test.PICTURE);
                if (!c.isNull(pictureCol)) {
                    dBValues.put(DbContract.Test.PICTURE, c.getBlob(pictureCol));
                }
                resList.add(dBValues);

                System.out.println(dBValues.toString());
            }
            while (c.moveToNext());
        }

        return resList;

    }

    public static void closeCursor(Cursor c) {
        if (c != null && !c.isClosed()) {
            c.close();
        }
    }
}
