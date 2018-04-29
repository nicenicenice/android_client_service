package com.proj.mqliteclient.db;

import android.content.Context;

/**
 * Created by user on 28/04/2018.
 */
public class FakeContainer {

    private static DbProvider sDbProviderInstance;
    public static DbProvider getProviderInstance(Context context) {
        context = context.getApplicationContext();
        if (sDbProviderInstance == null) {
            sDbProviderInstance = new DbProvider(context);
        }
        return sDbProviderInstance;
    }

    private static DbNotificationManager sDbNotificationInstance;
    public static DbNotificationManager getNotificationInstance(Context context) {
        context = context.getApplicationContext();
        if (sDbNotificationInstance == null) {
            sDbNotificationInstance = new DbNotificationManager();
        }
        return sDbNotificationInstance;
    }
}
