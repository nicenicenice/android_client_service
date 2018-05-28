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
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    // поле для создания нового потока
    private AsyncTask<Void, Void, JSONObject> mDataLoadFromServiceTask;

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

        // обработка кнопки SHOW_SLOTS
        Button showSlotsInfoButton = (Button) findViewById(R.id.show_slots_button);
        assert showSlotsInfoButton != null;
        showSlotsInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSlotsInfoActivity();
            }
        });

        // обработка Spinner
        loadOverlayNamesFromDb();
    }

    // показывает новую активити
    private void showDataActivity() {
        String selectedItem = getSelectedSpinnerItem();
        Intent intent = new Intent(this, MapsActivity.class);
        intent.putExtra("name", selectedItem);
        startActivity(intent);
    }

    // показываем активити с информацией по слотам
    private void showSlotsInfoActivity() {
        String selectedItem = getSelectedSpinnerItem();
        Intent intent = new Intent(this, SlotActivity.class);
        intent.putExtra("name", selectedItem);
        startActivity(intent);
    }

    private String getSelectedSpinnerItem() {
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        assert spinner != null;

        String selectedItem = spinner.getSelectedItem().toString();
        if (selectedItem == null) {
            Toast.makeText(getBaseContext(),
                    getResources().getString(R.string.unexpected_error_occurred), Toast.LENGTH_LONG).show();
            return null;
        }
        return selectedItem;
    }

    // скачиваем данные с сервиса в другом потоке и обновляем таблицу, полученными данными
    private void loadDataFromService() {

        mDataLoadFromServiceTask = new AsyncTask<Void, Void, JSONObject>() {
            @Override
            protected JSONObject doInBackground(Void... params) {
                try {
                    JSONObject jsonResult = Utils.getDataInJsonArrayFormat();
                    return jsonResult;
                } catch (Exception e) {
                    return null;
                }
            }
            @Override
            protected void onPostExecute(JSONObject result) {
                if (isCancelled()) return;

                String messageToShow = "";
                if (result == null) {
                    messageToShow = getResources().getString(R.string.failed_getting_data);
                } else {
                    messageToShow = getResources().getString(R.string.success_getting_data);
                }
                Toast.makeText(getBaseContext(), messageToShow, Toast.LENGTH_LONG).show();

                mDbProvider.refreshDbData(result);
                loadOverlayNamesFromDb();
            }
        };
        mDataLoadFromServiceTask.execute();
    }


    private void setSpinnersAdapter(List<String> spinnerDataList) {
        if (spinnerDataList == null || spinnerDataList.size() <= 0 ) {
            spinnerDataList =  new ArrayList<String>();
            spinnerDataList.add(getResources().getString(R.string.no_overlay_mes));
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, spinnerDataList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        assert spinner != null;
        spinner.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }
}
