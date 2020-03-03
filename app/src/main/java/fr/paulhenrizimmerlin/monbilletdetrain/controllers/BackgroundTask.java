package fr.paulhenrizimmerlin.monbilletdetrain.controllers;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.preference.PreferenceManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

import fr.paulhenrizimmerlin.monbilletdetrain.database.AppDatabase;
import fr.paulhenrizimmerlin.monbilletdetrain.models.Journey;

public class BackgroundTask extends Service {
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    public static void updateAllPrice(Context c) {
        AppDatabase mDb = AppDatabase.getInstance(c);
        List<Journey> journeys = mDb.journeyDao().loadAllJourneys();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        for (Journey j : journeys) {
            CheckPrice cp = new CheckPrice();
            cp.execute(j);
            try {
                Journey updated = cp.get();
                if (updated != null && updated.getDate().getTime() > Calendar.getInstance().getTimeInMillis()) {
                    mDb.journeyDao().updateJourney(updated);
                    if (updated.getCurrentPrice() < updated.getLimitPrice())
                        Toast.makeText(c, "Price: " + updated.getCurrentPrice(),
                                Toast.LENGTH_SHORT).show();
                } else {
                    mDb.journeyDao().delete(j);
                }
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }
        Log.i("PHZ", mDb.journeyDao().loadAllJourneys().toString());
    }

    public void onCreate() {
        Timer timer = new Timer();
        Toast.makeText(this, "Démarrage du service", Toast.LENGTH_SHORT).show();

        final Handler handler = new Handler();
        TimerTask task = new TimerTask() {
            public void run() {
                handler.post(() -> {
                    updateAllPrice(getApplicationContext());
                });
            }
        };
        SharedPreferences pfs = PreferenceManager.getDefaultSharedPreferences(this);
        Integer freq = Integer.parseInt(pfs.getString("pref_update_freq", "14400"));
        Log.i("PHZ", freq.toString());
        timer.schedule(task, 0, 10 * 1000);
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
