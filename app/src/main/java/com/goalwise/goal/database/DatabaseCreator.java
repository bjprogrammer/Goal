package com.goalwise.goal.database;

import android.content.Context;

import androidx.room.Room;


public class DatabaseCreator {
    private static AppDatabase appDatabase;
    private static final Object LOCK = new Object();
    public synchronized static AppDatabase getAppDatabase(Context context){
        if(appDatabase == null) {
            synchronized (LOCK) {
                if (appDatabase == null) {
                    appDatabase = Room.databaseBuilder(context,
                            AppDatabase.class, "search.db")
                            .build();
                }
            }
        }
        return appDatabase;
    }

    public static void destroyInstance() {
        appDatabase= null;
    }

}

