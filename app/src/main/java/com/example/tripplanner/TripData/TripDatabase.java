package com.example.tripplanner.TripData;
//create database

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Trip.class}, version = 1, exportSchema = false)
    public abstract class TripDatabase extends RoomDatabase {
        public abstract TripDAO tripDAO();

    }

