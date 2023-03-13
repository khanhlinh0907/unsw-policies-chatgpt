package com.example.unswpolicieschatgpt.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import java.util.ArrayList;

//Add database entities
@Database(entities = {Document.class}, version = 1, exportSchema = false)
public abstract class DocumentDatabase extends RoomDatabase {
    //Define database name
    private static final String DATABASE_NAME = "Document_Database";
    //Create database instance
    private static DocumentDatabase database;

    //Create Dao
    public abstract DocumentDao mainDao();

    }
