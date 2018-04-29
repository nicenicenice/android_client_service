package com.proj.mqliteclient;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.proj.mqliteclient.db.DbNotificationManager;
import com.proj.mqliteclient.db.DbProvider;
import com.proj.mqliteclient.db.FakeContainer;
import com.proj.mqliteclient.utils.Utils;

import org.json.JSONArray;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_NEW_TAB = 0;

    private AsyncTask<Void, Void, JSONArray> mDataLoadFromServiceTask;

    private DbProvider mDbProvider;
    private DbNotificationManager mNotifier;
    private DbNotificationManager.Listener mDbListener = new DbNotificationManager.Listener() {
        @Override
        public void onDataUpdated() {
            // TODO
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDbProvider = FakeContainer.getProviderInstance(this);
        mNotifier = FakeContainer.getNotificationInstance(this);

        setContentView(R.layout.activity_main);


        Button refreshButton = (Button) findViewById(R.id.refr_button);
        assert refreshButton != null;
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // подключаемся в сервису, скачиваем json array и
                    // обновляем свою таблицу БД в другом потоке
                loadDataFromService();
            }
        });

        Button showButton = (Button) findViewById(R.id.show_button);
        assert showButton != null;
        showButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDataActivity();
            }
        });

        mNotifier.addListener(mDbListener);
    }

    private void showDataActivity() {
        startActivity(new Intent(this, DataActivity.class));
        //startActivityForResult(new Intent(this, DataActivity.class), REQUEST_CODE_NEW_TAB);
    }

    private void loadDataFromService() {

        mDataLoadFromServiceTask = new AsyncTask<Void, Void, JSONArray>() {
            @Override
            protected JSONArray doInBackground(Void... params) {
                return Utils.getDataInJsonArrayFormat();
            }
            @Override
            protected void onPostExecute(JSONArray result) {
                if (isCancelled()) return;
                mDbProvider.refreshDbData(result);
            }
        };
        mDataLoadFromServiceTask.execute();
    }
}
