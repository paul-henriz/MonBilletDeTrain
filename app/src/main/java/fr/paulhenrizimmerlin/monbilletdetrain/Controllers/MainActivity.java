package fr.paulhenrizimmerlin.monbilletdetrain.Controllers;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import fr.paulhenrizimmerlin.monbilletdetrain.R;

import static androidx.preference.PreferenceManager.*;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setDefaultValues(this, R.xml.preferences, false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId())
        {
            case R.id.preferences:
            {
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            }
            case R.id.about:
            {
                startActivity(new Intent(this, AboutActivity.class));
                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }
}
