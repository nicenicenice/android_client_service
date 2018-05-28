package com.proj.mqliteclient;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.proj.mqliteclient.db.DbProvider;
import com.proj.mqliteclient.db.DbUtils;
import com.proj.mqliteclient.db.FakeContainer;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class SlotActivity extends AppCompatActivity {

    private DbProvider mDbProvider;
    private DbProvider.ResultCallback<Cursor> mDataLoadDbCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDbProvider = FakeContainer.getProviderInstance(this);

        setContentView(R.layout.activity_slot);

        Intent intent = getIntent();
        String nameOfWarehouse = intent.getStringExtra("name");

        loadSlotsInfoFromDb(nameOfWarehouse);
    }

    private void loadSlotsInfoFromDb(String nameOfWarehouse) {

        mDataLoadDbCallback = new DbProvider.ResultCallback<Cursor>() {
            @Override
            public void onFinished(Cursor c) {
                if (mDataLoadDbCallback != this) {
                    return;
                }
                onDataLoadedFromDb(c);
            }
        };
        mDbProvider.getSlotsInfoByWarehouseNameFromDb(mDataLoadDbCallback, this, nameOfWarehouse);
    }

    private void onDataLoadedFromDb(Cursor c) {
        Map<String, String> slotToProdMap = DbUtils.getMapWithSlotsProdsAndClose(c);

        if (slotToProdMap.size() <= 0)
            return;

        TableLayout tableLayout = (TableLayout) findViewById(R.id.slot_product_table);

        TableRow trHead = new TableRow(this);
        trHead.setBackgroundColor(Color.GRAY);
        trHead.setLayoutParams(new TableRow.LayoutParams(
                TableRow.LayoutParams.FILL_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT));

        TextView slotHead = new TextView(this);
        slotHead.setText("Слоты");
        slotHead.setTextSize(28);
        slotHead.setTextColor(Color.WHITE);
        slotHead.setPadding(10, 5, 5, 5);
        trHead.addView(slotHead);

        TextView prodHead = new TextView(this);
        prodHead.setText("Продукты");
        prodHead.setTextSize(28);
        prodHead.setTextColor(Color.WHITE);
        prodHead.setPadding(10, 5, 5, 5);
        trHead.addView(prodHead);

        tableLayout.addView(trHead, new TableLayout.LayoutParams(
                TableLayout.LayoutParams.FILL_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT));

        Iterator it = slotToProdMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            String slot = (String)pair.getKey();
            String product = (String)pair.getValue();

            TextView slotView = new TextView(this);
            slotView.setText(slot);
            slotView.setTextSize(24);
            slotView.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
            slotView.setPadding(10, 5, 5, 5);

            TextView prodView = new TextView(this);
            prodView.setText(product);
            prodView.setTextSize(24);
            prodView.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
            prodView.setPadding(10, 5, 5, 5);

            TableRow tableRow = new TableRow(this);
            tableRow.setLayoutParams(new TableRow.LayoutParams(
                    TableRow.LayoutParams.FILL_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT));

            tableRow.addView(slotView);
            tableRow.addView(prodView);
            tableLayout.addView(tableRow);
        }
    }
}
