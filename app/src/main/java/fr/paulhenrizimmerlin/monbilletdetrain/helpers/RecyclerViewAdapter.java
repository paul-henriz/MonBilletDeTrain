package fr.paulhenrizimmerlin.monbilletdetrain.helpers;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.List;

import fr.paulhenrizimmerlin.monbilletdetrain.R;
import fr.paulhenrizimmerlin.monbilletdetrain.models.Journey;

public class RecyclerViewAdapter extends RecyclerView.Adapter<ViewHolder> {
    private List<Journey> journeys;

    public RecyclerViewAdapter(List<Journey> journeysList) {
        this.journeys = journeysList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.journey_item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Journey j = journeys.get(position);
        holder.travel.setText(j.getDepartureLabel() + " - " + j.getArrivalLabel());
        holder.price.setText(j.getCurrentPrice() + "(limit: " + j.getLimitPrice() + ")");
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh'h'mm");
        holder.date.setText(sdf.format(j.getDate()));
    }

    @Override
    public int getItemCount() {
        return journeys.size();
    }
}
