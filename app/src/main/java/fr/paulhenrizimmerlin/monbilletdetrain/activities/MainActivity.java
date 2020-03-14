package fr.paulhenrizimmerlin.monbilletdetrain.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import fr.paulhenrizimmerlin.monbilletdetrain.R;
import fr.paulhenrizimmerlin.monbilletdetrain.controllers.BackgroundTask;

import static androidx.preference.PreferenceManager.getDefaultSharedPreferences;
import static androidx.preference.PreferenceManager.setDefaultValues;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // If necessary, set default preferences
        setDefaultValues(this, R.xml.preferences, false);

        // Link buttons with activities
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

        // Check if user enabled service. If not started, start it
        SharedPreferences spf = getDefaultSharedPreferences(this);
        Boolean bg = spf.getBoolean("pref_run_in_bg", false);

        if (bg) {
            Intent serviceIntent = new Intent(this, BackgroundTask.class);
            startService(serviceIntent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    // "Three dots" menu in the top right corner
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
}
