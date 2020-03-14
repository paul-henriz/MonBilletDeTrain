package fr.paulhenrizimmerlin.monbilletdetrain.controllers;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.preference.PreferenceManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

import fr.paulhenrizimmerlin.monbilletdetrain.R;
import fr.paulhenrizimmerlin.monbilletdetrain.activities.ListJourneysActivity;
import fr.paulhenrizimmerlin.monbilletdetrain.database.AppDatabase;
import fr.paulhenrizimmerlin.monbilletdetrain.models.Journey;

public class BackgroundTask extends Service {
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    public static void updateAllPrice(Context c, NotificationManager nManager) {
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
                    if (updated.getCurrentPrice() < updated.getLimitPrice()) {
                        //Toast.makeText(c, "Price: " + updated.getCurrentPrice(), Toast.LENGTH_SHORT).show();
                        Notification.Builder builder =
                                new Notification.Builder(c)
                                        .setSmallIcon(R.mipmap.ic_launcher_foreground)
                                        .setContentTitle(c.getResources().getString(R.string.app_name))
                                        .setStyle(new Notification.BigTextStyle().bigText(c.getResources().getString(R.string.notification_your_ticket) + " " + updated.getDepartureLabel() + " - " + updated.getArrivalLabel() + " " + c.getResources().getString(R.string.notification_cost_only) + " " + updated.getCurrentPrice() + "€"));
                        int NOTIFICATION_ID = updated.getId();

                        Intent targetIntent = new Intent(c, ListJourneysActivity.class);
                        PendingIntent contentIntent = PendingIntent.getActivity(c, 0, targetIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                        builder.setContentIntent(contentIntent);
                        nManager.notify(NOTIFICATION_ID, builder.build());
                    }

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
        final Handler handler = new Handler();
        TimerTask task = new TimerTask() {
            public void run() {
                handler.post(() -> {
                    updateAllPrice(getApplicationContext(), (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE));
                });
            }
        };
        SharedPreferences pfs = PreferenceManager.getDefaultSharedPreferences(this);
        Integer freq = Integer.parseInt(pfs.getString("pref_update_freq", "14400"));
        timer.schedule(task, 0, 10 * 1000);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
