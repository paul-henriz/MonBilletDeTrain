package fr.paulhenrizimmerlin.monbilletdetrain.activities;

import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Pair;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import com.android.volley.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import fr.paulhenrizimmerlin.monbilletdetrain.R;
import fr.paulhenrizimmerlin.monbilletdetrain.controllers.BackgroundTask;
import fr.paulhenrizimmerlin.monbilletdetrain.controllers.GetStation;
import fr.paulhenrizimmerlin.monbilletdetrain.database.AppDatabase;
import fr.paulhenrizimmerlin.monbilletdetrain.helpers.AutoSuggestAdapter;
import fr.paulhenrizimmerlin.monbilletdetrain.models.Journey;

import static fr.paulhenrizimmerlin.monbilletdetrain.database.AppDatabase.getInstance;

public class AddJourneyActivity extends AppCompatActivity {
    private static final int TRIGGER_AUTO_COMPLETE = 100;
    private static final long AUTO_COMPLETE_DELAY = 300;

    // UI Elements
    EditText priceInput;
    AutoCompleteTextView departureInput;
    AutoCompleteTextView arrivalInput;
    Spinner travelClassInput;
    DatePicker dateInput;
    Button saveButton;

    // Autocompletion
    String departureId;
    String arrivalId;
    private Handler handlerDeparture;
    private AutoSuggestAdapter autoSuggestAdapterDeparture;
    private Handler handlerArrival;
    private AutoSuggestAdapter autoSuggestAdapterArrival;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_journey);

        // We instantiate UI elements
        departureInput = findViewById(R.id.departure_text);
        arrivalInput = findViewById(R.id.arrival_text);
        priceInput = findViewById(R.id.price_number);

        travelClassInput = findViewById(R.id.class_selector);
        dateInput = findViewById(R.id.date_selector);

        //Setting up the adapter for departure autosuggest
        autoSuggestAdapterDeparture = new AutoSuggestAdapter(this,
                android.R.layout.simple_dropdown_item_1line);
        departureInput.setThreshold(2);
        departureInput.setAdapter(autoSuggestAdapterDeparture);
        departureInput.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        departureId = autoSuggestAdapterDeparture.getObject(position).first;
                    }
                });
        departureInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int
                    count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                handlerDeparture.removeMessages(TRIGGER_AUTO_COMPLETE);
                handlerDeparture.sendEmptyMessageDelayed(TRIGGER_AUTO_COMPLETE,
                        AUTO_COMPLETE_DELAY);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        handlerDeparture = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                if (msg.what == TRIGGER_AUTO_COMPLETE) {
                    if (!TextUtils.isEmpty(departureInput.getText())) {
                        makeApiCall(departureInput.getText().toString(), autoSuggestAdapterDeparture);
                    }
                }
                return false;
            }
        });

        //Setting up the adapter for arrival autosuggest
        autoSuggestAdapterArrival = new AutoSuggestAdapter(this,
                android.R.layout.simple_dropdown_item_1line);
        arrivalInput.setThreshold(2);
        arrivalInput.setAdapter(autoSuggestAdapterArrival);
        arrivalInput.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        arrivalId = autoSuggestAdapterArrival.getObject(position).first;
                    }
                });
        arrivalInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int
                    count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                handlerArrival.removeMessages(TRIGGER_AUTO_COMPLETE);
                handlerArrival.sendEmptyMessageDelayed(TRIGGER_AUTO_COMPLETE,
                        AUTO_COMPLETE_DELAY);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        handlerArrival = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                if (msg.what == TRIGGER_AUTO_COMPLETE) {
                    if (!TextUtils.isEmpty(arrivalInput.getText())) {
                        makeApiCall(arrivalInput.getText().toString(), autoSuggestAdapterArrival);
                    }
                }
                return false;
            }
        });

        // Save button action (insert into DB + update price + send toast)
        saveButton = findViewById(R.id.send_add_journey);
        saveButton.setOnClickListener(v -> {
            if (checkIfFilled()) {
                AppDatabase mDb = getInstance(getApplicationContext());
                mDb.journeyDao().insertJourney(createJourneyFromUI());
                BackgroundTask.updateAllPrice(getApplicationContext(), (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE));
                Toast.makeText(getApplicationContext(), getResources().getText(R.string.add_journey_success_added), Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private Journey createJourneyFromUI() {
        // We retrieve the reduction card from preferences
        SharedPreferences pfs = PreferenceManager.getDefaultSharedPreferences(this);
        String reductionCard = pfs.getString("pref_reduction_card", "26-NO_CARD");

        // We format date
        int day = dateInput.getDayOfMonth();
        int month = dateInput.getMonth();
        int year = dateInput.getYear();

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);

        Journey current = new Journey();

        // We fill the journey with information from the form
        current.setLimitPrice(Float.parseFloat(priceInput.getText().toString()));
        current.setDeparture(departureId);
        current.setArrival(arrivalId);
        current.setDate(calendar.getTime());
        current.setDepartureLabel(departureInput.getText().toString());
        current.setArrivalLabel(arrivalInput.getText().toString());
        current.setTravelClass(getResources().getStringArray(R.array.journey_class_values)[travelClassInput.getSelectedItemPosition()]);
        current.setReductionCard(reductionCard);

        return current;
    }

    // Check if all mandatory field are set
    private Boolean checkIfFilled() {
        if (departureId == null) {
            departureInput.setError(getResources().getString(R.string.add_journey_warning_departure));
            return false;
        }
        if (arrivalId == null) {
            arrivalInput.setError(getResources().getString(R.string.add_journey_warning_arrival));
            return false;
        }
        if (TextUtils.isEmpty(priceInput.getText())) {
            priceInput.setError(getResources().getString(R.string.add_journey_warning_limit));
            return false;
        }
        return true;
    }

    // Call the api for station name suggestion
    private void makeApiCall(String text, AutoSuggestAdapter adapter) {
        GetStation.make(this, text, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                List<Pair<String, String>> results = new ArrayList<>();
                try {
                    JSONArray array = new JSONArray(response);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject row = array.getJSONObject(i);
                        results.add(new Pair<String, String>(row.getString("id"), row.getString("label")));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                adapter.setData(results);
                adapter.notifyDataSetChanged();
            }
        }, error -> {
        });
    }
}
