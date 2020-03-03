package fr.paulhenrizimmerlin.monbilletdetrain.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import fr.paulhenrizimmerlin.monbilletdetrain.R;
import fr.paulhenrizimmerlin.monbilletdetrain.database.AppDatabase;
import fr.paulhenrizimmerlin.monbilletdetrain.helpers.RecyclerViewAdapter;
import fr.paulhenrizimmerlin.monbilletdetrain.models.Journey;

public class ListJourneys extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        RecyclerView mRecyclerView;
        RecyclerViewAdapter mAdapter;
        List<Journey> journeys;

        mRecyclerView = findViewById(R.id.journey_list);

        AppDatabase mDb = AppDatabase.getInstance(getApplicationContext());
        journeys = mDb.journeyDao().loadAllJourneys();

        mAdapter = new RecyclerViewAdapter(journeys);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        mRecyclerView.setAdapter(mAdapter);
    }
}
