package com.proj.mqliteclient;

import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.proj.mqliteclient.db.DbContract;
import com.proj.mqliteclient.db.DbProvider;
import com.proj.mqliteclient.db.FakeContainer;
import com.proj.mqliteclient.db.DbUtils;


import org.json.JSONArray;

import java.util.List;

public class DataActivity extends AppCompatActivity {

    private DbProvider.ResultCallback<Cursor> mDataLoadDbCallback;
    private DbProvider mDbProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDbProvider = FakeContainer.getProviderInstance(this);

        setContentView(R.layout.activity_data);

        // получаем даннеы из БД и загружаем их в активити
        loadDataFromDb();
    }

    private void loadDataFromDb() {

        mDataLoadDbCallback = new DbProvider.ResultCallback<Cursor>() {
            @Override
            public void onFinished(Cursor result) {
                if (mDataLoadDbCallback != this) {
                    return;
                }
                onDataLoadedFromDb(result);
            }
        };
        mDbProvider.getDataFromDb(mDataLoadDbCallback);
    }

    private void onDataLoadedFromDb(Cursor c) {
        List<ContentValues> resList = DbUtils.getResultStringListAndClose(c);

        TableLayout ll = (TableLayout) findViewById(R.id.outer_table);

        for (int i = 0; i < resList.size(); ++i) {
            ContentValues rowValues = resList.get(i);

            TableRow outerRow = new TableRow(this);
            TableLayout innerTl = new TableLayout(this);

            // col 1
            TableRow row1 = new TableRow(this);
            TextView num1 = new TextView(this);
            num1.setText(rowValues.getAsString(DbContract.Test.NUM1));

            TextView num2 = new TextView(this);
            num2.setText(rowValues.getAsString(DbContract.Test.NUM2));

            // picture
            //ImageView picture = new ImageView(this);
            //byte[] encodeByte = rowValues.getAsByteArray(DbContract.Test.PICTURE);
            //byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            //Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);

            //Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
            //picture.setImageBitmap(Bitmap.createScaledBitmap(bitmap, picture.getWidth(),
            //        picture.getHeight(), false));

            row1.addView(num1);
            row1.addView(num2);
            //row1.addView(picture);


            // col 2
            TableRow row2 = new TableRow(this);
            TextView num3 = new TextView(this);
            num3.setText(rowValues.getAsString(DbContract.Test.NUM3));

            TextView num4 = new TextView(this);
            num4.setText(rowValues.getAsString(DbContract.Test.NUM4));

            row2.addView(num3);
            row2.addView(num4);

            innerTl.addView(row1);
            innerTl.addView(row2);
            outerRow.addView(innerTl);
            ll.addView(outerRow);
        }
    }
}
