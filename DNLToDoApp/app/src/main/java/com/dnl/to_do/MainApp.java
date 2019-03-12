package com.dnl.to_do;

import android.app.Application;
import android.arch.persistence.room.Room;

import com.dnl.to_do.database.AppDatabase;

public class MainApp extends Application {
    public static MainApp instance;

    public AppDatabase db;

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;
        db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "dnl-todo-db.sqlite").build();
    }
}
