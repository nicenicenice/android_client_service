package com.proj.mqliteclient.db;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.VisibleForTesting;

import org.json.JSONArray;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by user on 28/04/2018.
 */
public class DbProvider {

    private final DbBackend mDbBackend;
    private final DbNotificationManager mDbNotificationManager;
    private final CustomExecutor mExecutor;
    private final Handler mHandler = new Handler(Looper.getMainLooper());

    public interface ResultCallback<T> {
        void onFinished(T result);
    }

    DbProvider(Context context) {
        mDbBackend = new DbBackend(context);
        mDbNotificationManager = FakeContainer.getNotificationInstance(context);
        mExecutor = new CustomExecutor();
    }

    @VisibleForTesting
    DbProvider(DbBackend dbBackend,
               DbNotificationManager dbNotificationManager,
               CustomExecutor executor) {
        mDbBackend = dbBackend;
        mDbNotificationManager = dbNotificationManager;
        mExecutor = executor;
    }

    public void getDataFromDb(final ResultCallback<Cursor> callback) {
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                final Cursor c =  mDbBackend.getAllDataFromTestTable();
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onFinished(c);
                    }
                });
            }
        });
    }

    public void refreshDbData(final JSONArray response) {
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                mDbBackend.refreshTestTableWithJsonData(response);
                mDbNotificationManager.notifyListeners();
            }
        });
    }

    class CustomExecutor extends ThreadPoolExecutor {
        CustomExecutor() {
            super(1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
        }
    }
}
