package fr.paulhenrizimmerlin.monbilletdetrain.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

import fr.paulhenrizimmerlin.monbilletdetrain.R;
import fr.paulhenrizimmerlin.monbilletdetrain.database.AppDatabase;
import fr.paulhenrizimmerlin.monbilletdetrain.models.Journey;

import static fr.paulhenrizimmerlin.monbilletdetrain.database.AppDatabase.getInstance;

public class AddJourneyActivity extends AppCompatActivity {
    EditText departureInput;
    EditText arrivalInput;
    EditText priceInput;

    Spinner travelClassInput;
    DatePicker dateInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_journey);

        departureInput = findViewById(R.id.departure_text);
        arrivalInput = findViewById(R.id.arrival_text);
        priceInput = findViewById(R.id.price_number);

        travelClassInput = findViewById(R.id.class_selector);
        dateInput = findViewById(R.id.date_selector);

        Button saveButton = findViewById(R.id.send_add_journey);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkIfFilled()) {
                    AppDatabase mDb = getInstance(getApplicationContext());
                    mDb.journeyDao().insertJourney(createJourneyFromUI());
                    finish();
                }
            }
        });
    }

    private Journey createJourneyFromUI() {
        int day = dateInput.getDayOfMonth();
        int month = dateInput.getMonth();
        int year = dateInput.getYear();

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);

        Journey current = new Journey();
        current.setLimitPrice(Float.parseFloat(priceInput.getText().toString()));
        current.setDeparture(departureInput.getText().toString());
        current.setArrival(arrivalInput.getText().toString());
        current.setDate(calendar.getTime());

        return current;
    }

    private Boolean checkIfFilled() {
        if (TextUtils.isEmpty(departureInput.getText())) {
            departureInput.setError("Departure station is required");
            return false;
        }
        if (TextUtils.isEmpty(arrivalInput.getText())) {
            arrivalInput.setError("Arrival station is required");
            return false;
        }
        if (TextUtils.isEmpty(priceInput.getText())) {
            priceInput.setError("Price is required");
            return false;
        }
        return true;
    }
}
