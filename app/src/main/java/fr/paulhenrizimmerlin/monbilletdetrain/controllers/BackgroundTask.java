package fr.paulhenrizimmerlin.monbilletdetrain.controllers;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.preference.PreferenceManager;

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

        // For each entry in the database
        for (Journey j : journeys) {
            // Start the async task
            CheckPrice cp = new CheckPrice();
            cp.execute(j);
            try {
                Journey updated = cp.get();
                // If the travel is valid and not in the past
                if (updated != null && updated.getDate().getTime() > Calendar.getInstance().getTimeInMillis()) {
                    // Update the database with the new price
                    mDb.journeyDao().updateJourney(updated);
                    // if the price is below the limit
                    if (updated.getCurrentPrice() < updated.getLimitPrice()) {
                        /*Toast.makeText(c, "Price: " + updated.getCurrentPrice(), Toast.LENGTH_SHORT).show();*/
                        // We send a notification
                        Notification.Builder builder = null;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            builder =
                                    new Notification.Builder(c)
                                            .setSmallIcon(R.mipmap.ic_launcher_foreground)
                                            .setContentTitle(c.getResources().getString(R.string.app_name))
                                            .setChannelId(c.getResources().getString(R.string.app_name))
                                            .setStyle(new Notification.BigTextStyle().bigText(c.getResources().getString(R.string.notification_your_ticket) + " " + updated.getDepartureLabel() + " - " + updated.getArrivalLabel() + " " + c.getResources().getString(R.string.notification_cost_only) + " " + updated.getCurrentPrice() + "€"));
                            NotificationChannel channel = new NotificationChannel(c.getResources().getString(R.string.app_name), c.getResources().getString(R.string.app_name), NotificationManager.IMPORTANCE_DEFAULT);
                            nManager.createNotificationChannel(channel);
                        } else {
                            builder =
                                    new Notification.Builder(c)
                                            .setSmallIcon(R.mipmap.ic_launcher_foreground)
                                            .setContentTitle(c.getResources().getString(R.string.app_name))
                                            .setStyle(new Notification.BigTextStyle().bigText(c.getResources().getString(R.string.notification_your_ticket) + " " + updated.getDepartureLabel() + " - " + updated.getArrivalLabel() + " " + c.getResources().getString(R.string.notification_cost_only) + " " + updated.getCurrentPrice() + "€"));

                        }
                        int NOTIFICATION_ID = updated.getId();

                        Intent targetIntent = new Intent(c, ListJourneysActivity.class);
                        PendingIntent contentIntent = PendingIntent.getActivity(c, 0, targetIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                        builder.setContentIntent(contentIntent);
                        nManager.notify(NOTIFICATION_ID, builder.build());
                    }

                } else {
                    // We delete from the db and next check if not valid or in the past
                    mDb.journeyDao().delete(j);
                }
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void onCreate() {
        // We create the scheduled task with the preference of the user
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
        timer.schedule(task, 0, freq * 1000);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
