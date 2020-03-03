package fr.paulhenrizimmerlin.monbilletdetrain.activities;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;

import fr.paulhenrizimmerlin.monbilletdetrain.R;
import fr.paulhenrizimmerlin.monbilletdetrain.controllers.BackgroundTask;

import static androidx.preference.PreferenceManager.getDefaultSharedPreferences;
import static androidx.preference.PreferenceManager.setDefaultValues;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setDefaultValues(this, R.xml.preferences, false);

        Button addJourney = findViewById(R.id.button_add_journey);
        addJourney.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddJourneyActivity.class);
            startActivity(intent);
        });

        Button listJourney = findViewById(R.id.button_view_journey);
        listJourney.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ListJourneysActivity.class);
            startActivity(intent);
        });

        SharedPreferences spf = getDefaultSharedPreferences(this);
        Boolean bg = spf.getBoolean("pref_run_in_bg", false);

        if (bg) {
            Intent serviceIntent = new Intent(this, BackgroundTask.class);
            startService(serviceIntent);
        }

        // Populate database
        /* AppDatabase mDb = AppDatabase.getInstance(getApplicationContext());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        mDb.journeyDao().deleteAll();
        try {
            Journey j = new Journey("FRAEJ", "FRPAR", dateFormat.parse("2020-03-20"));
            j.setLimitPrice(40);
            mDb.journeyDao().insertJourney(j);
            j = new Journey("FRAEJ", "FRPAR", dateFormat.parse("2020-03-22"));
            j.setLimitPrice(80);
            mDb.journeyDao().insertJourney(j);
        } catch (ParseException e) {
            e.printStackTrace();
        }*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.preferences: {
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            }
            case R.id.about: {
                startActivity(new Intent(this, AboutActivity.class));
                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private void createNotificationChannel() {
        // Créer le NotificationChannel, seulement pour API 26+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Notification channel name";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("UPDATE", name, importance);
            channel.setDescription("Notification channel description");
            // Enregister le canal sur le système : attention de ne plus rien modifier après
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            Objects.requireNonNull(notificationManager).createNotificationChannel(channel);
        }
    }
}
