package fr.paulhenrizimmerlin.monbilletdetrain.helpers;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import fr.paulhenrizimmerlin.monbilletdetrain.R;

public class ViewHolder extends RecyclerView.ViewHolder {
    public TextView travel, price, date;

    public ViewHolder(View itemView) {
        super(itemView);
        travel = itemView.findViewById(R.id.travel);
        price = itemView.findViewById(R.id.price);
        date = itemView.findViewById(R.id.date);
    }
}
