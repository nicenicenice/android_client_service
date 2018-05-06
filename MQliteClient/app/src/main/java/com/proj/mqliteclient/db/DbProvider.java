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

//  класс, посредством с которым клиентский код(активити) взаимодействует с БД
    // так же отвечает на многопоточность
public class DbProvider {

    private final DbBackend mDbBackend;
    private final CustomExecutor mExecutor;
    private final Handler mHandler = new Handler(Looper.getMainLooper());

    public interface ResultCallback<T> {
        void onFinished(T result);
    }

    // конструктор
    DbProvider(Context context) {
        mDbBackend = new DbBackend(context);
        mExecutor = new CustomExecutor();
    }

    // в новом потоке готовим запрос
    // потом запускаем в основном потоке onFinished() переданной функции-коллбека
    // у нас это onDataLoadedFromDb(), которая делаем выборку из БД и вставляет данные в UI
    public void getDataFromDb(final ResultCallback<Cursor> callback) {
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                final Cursor c =  mDbBackend.getAllDataFromTable();
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onFinished(c);
                    }
                });
            }
        });
    }

    // в новом потоке очищаем и заполняем таблицу
    public void refreshDbData(final JSONArray response) {
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                mDbBackend.refreshTableWithJsonData(response);
            }
        });
    }

    class CustomExecutor extends ThreadPoolExecutor {
        CustomExecutor() {
            super(1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
        }
    }
}
