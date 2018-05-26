package com.proj.mqliteclient;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.proj.mqliteclient.db.DbContract;
import com.proj.mqliteclient.db.DbProvider;
import com.proj.mqliteclient.db.DbUtils;
import com.proj.mqliteclient.db.FakeContainer;

import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private DbProvider.ResultCallback<Cursor> mDataLoadDbCallback;
    private DbProvider mDbProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDbProvider = FakeContainer.getProviderInstance(this);

        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void loadDataFromDb(final GoogleMap googleMap, String nameOfOverlay) {

        mDataLoadDbCallback = new DbProvider.ResultCallback<Cursor>() {
            @Override
            public void onFinished(Cursor result) {
                if (mDataLoadDbCallback != this) {
                    return;
                }
                onDataLoadedFromDb(googleMap, result);
            }
        };
        mDbProvider.getDataFromDb(mDataLoadDbCallback, nameOfOverlay);
    }

    private void onDataLoadedFromDb(GoogleMap googleMap, Cursor c) {
        mMap = googleMap;

        List<ContentValues> resList = DbUtils.getResultStringListAndClose(c);

        for (int i = 0; i < resList.size(); ++i) {
            ContentValues rowValues = resList.get(i);

            LatLngBounds newarkBounds = null;

            try {
                Double latLngBoundNEN = rowValues.getAsDouble(DbContract.GroundOverlays.LAT_LNG_BOUND_NEN);
                Double latLngBoundNEE = rowValues.getAsDouble(DbContract.GroundOverlays.LAT_LNG_BOUND_NEE);
                Double latLngBoundSWN = rowValues.getAsDouble(DbContract.GroundOverlays.LAT_LNG_BOUND_SWN);
                Double latLngBoundSWE = rowValues.getAsDouble(DbContract.GroundOverlays.LAT_LNG_BOUND_SWE);

                // picture
                // получили закодированный массив битов в строку
                String imageString = rowValues.getAsString(DbContract.GroundOverlays.OVERLAY_PIC);
                byte[] encodedPicture = null;
                try {
                    // декодируем строку в массив битов
                    encodedPicture = imageString.getBytes("ISO-8859-1");
                } catch (Exception e) {}

                // из массива битов делаем bitmap
                Bitmap bitmap = BitmapFactory.decodeByteArray(encodedPicture, 0, encodedPicture.length);

                newarkBounds = new LatLngBounds(
                        new LatLng(latLngBoundNEN, latLngBoundNEE),       // South west corner
                        new LatLng(latLngBoundSWN, latLngBoundSWE));      // North east corner

                GroundOverlayOptions newarkMap = new GroundOverlayOptions()
                        .image(BitmapDescriptorFactory.fromBitmap(bitmap))
                        .positionFromBounds(newarkBounds)
                        .transparency(0.1f);

                GroundOverlay imageOverlay = mMap.addGroundOverlay(newarkMap);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Intent intent = getIntent();
        String nameOfWarehouse = intent.getStringExtra("name");
        if (nameOfWarehouse == null)
            return;

        // получаем даннеы из БД и загружаем их в активити
        loadDataFromDb(googleMap, nameOfWarehouse);
    }
}
