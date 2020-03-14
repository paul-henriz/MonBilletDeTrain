package fr.paulhenrizimmerlin.monbilletdetrain.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import fr.paulhenrizimmerlin.monbilletdetrain.helpers.Converters;
import fr.paulhenrizimmerlin.monbilletdetrain.models.Journey;

@Database(entities = {Journey.class}, version = 1, exportSchema = false)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {
    private static final Object LOCK = new Object();
    private static final String DATABASE_NAME = "journeylist";
    private static AppDatabase sInstance;

    public static AppDatabase getInstance(Context context) {
        if (sInstance == null) {
            synchronized (LOCK) {
                sInstance = Room.databaseBuilder(context.getApplicationContext(),
                        AppDatabase.class, AppDatabase.DATABASE_NAME).allowMainThreadQueries()
                        .build();
            }
        }
        return sInstance;
    }

    public abstract JourneyDAO journeyDao();
}