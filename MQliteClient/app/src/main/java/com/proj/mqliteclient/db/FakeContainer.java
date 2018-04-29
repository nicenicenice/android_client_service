package com.proj.mqliteclient.db;

import android.content.Context;

/**
 * Created by user on 28/04/2018.
 */
public class FakeContainer {

    // сингтон. предоставляем объект в единичном экземпляре
    private static DbProvider sDbProviderInstance;
    public static DbProvider getProviderInstance(Context context) {
        context = context.getApplicationContext();
        if (sDbProviderInstance == null) {
            sDbProviderInstance = new DbProvider(context);
        }
        return sDbProviderInstance;
    }
}
