package com.proj.mqliteclient;

import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.proj.mqliteclient.db.DbProvider;
import com.proj.mqliteclient.db.DbUtils;
import com.proj.mqliteclient.db.FakeContainer;
import com.proj.mqliteclient.utils.Utils;

import org.json.JSONArray;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    // поле для создания нового потока
    private AsyncTask<Void, Void, JSONArray> mDataLoadFromServiceTask;

    private DbProvider mDbProvider;
    private DbProvider.ResultCallback<Cursor> mDataLoadDbCallback;

    private void loadOverlayNamesFromDb() {

        mDataLoadDbCallback = new DbProvider.ResultCallback<Cursor>() {
            @Override
            public void onFinished(Cursor c) {
                if (mDataLoadDbCallback != this) {
                    return;
                }
                onDataLoadedFromDb(c);
            }
        };
        mDbProvider.getOverlayNamesFromDb(mDataLoadDbCallback);
    }

    private void onDataLoadedFromDb(Cursor c) {
        List<String> resList = DbUtils.getStringListWithNamesAndClose(c);
        setSpinnersAdapter(resList);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDbProvider = FakeContainer.getProviderInstance(this);

        setContentView(R.layout.activity_main);

        // обработка кнопки REFRESH
        Button refreshButton = (Button) findViewById(R.id.refr_button);
        assert refreshButton != null;
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // подключаемся в сервису, скачиваем json array и
                    // обновляем свою таблицу БД в другом потоке
                loadDataFromService();
                //Toast.makeText(getBaseContext(),
                  //      "Данные успешно загружены", Toast.LENGTH_LONG).show();
            }
        });

        // обработка кнопки DATA
        Button showButton = (Button) findViewById(R.id.show_button);
        assert showButton != null;
        showButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDataActivity();
            }
        });

        // обработка Spinner
        loadOverlayNamesFromDb();
    }

    // показывает новую активити
    private void showDataActivity() {
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        assert spinner != null;

        String selectedItem = spinner.getSelectedItem().toString();
        if (selectedItem == null) {
            Toast.makeText(getBaseContext(),
                    "произошла непредвиденная ошибка", Toast.LENGTH_LONG).show();
            return;
        }

        Intent intent = new Intent(this, MapsActivity.class);
        intent.putExtra("name", selectedItem);
        startActivity(intent);
    }

    // скачиваем данные с сервиса в другом потоке и обновляем таблицу, полученными данными
    private void loadDataFromService() {

        mDataLoadFromServiceTask = new AsyncTask<Void, Void, JSONArray>() {
            @Override
            protected JSONArray doInBackground(Void... params) {
                try {
                    JSONArray jsonResult = Utils.getDataInJsonArrayFormat();
                    return jsonResult;
                } catch (Exception e) {
                    return null;
                }
            }
            @Override
            protected void onPostExecute(JSONArray result) {
                if (isCancelled()) return;

                if (result == null)
                    Toast.makeText(getBaseContext(),
                            "Данные с сервиса НЕ были получены", Toast.LENGTH_LONG).show();
                else
                    Toast.makeText(getBaseContext(),"Данные с сервиса успешно получены", Toast.LENGTH_LONG).show();

                mDbProvider.refreshDbData(result);
            }
        };
        mDataLoadFromServiceTask.execute();
    }


    private void setSpinnersAdapter(List<String> spinnerDataList) {
        if (spinnerDataList == null) {
            spinnerDataList =  new ArrayList<String>();
            spinnerDataList.add("there're no any overlays");
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, spinnerDataList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        assert spinner != null;
        spinner.setAdapter(adapter);
    }
}
