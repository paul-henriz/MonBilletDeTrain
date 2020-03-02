package fr.paulhenrizimmerlin.monbilletdetrain.controllers;

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;

import fr.paulhenrizimmerlin.monbilletdetrain.models.Journey;

public class CheckPrice extends AsyncTask<Journey, Integer, Journey> {
    @Override
    protected Journey doInBackground(Journey... journeys) {
        Journey travel = journeys[0];
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            URL url = new URL("https://www.oui.sncf/apim/calendar/train/v4/" + travel.getDeparture() + "/" + travel.getArrival() + "/" + dateFormat.format(travel.getDate()) + "/" + dateFormat.format(travel.getDate()) + "/12-YOUNG/2/fr?extendedToLocality=true&additionalFields=hours");
            HttpURLConnection c = (HttpURLConnection) url.openConnection();
            c.setUseCaches(false);
            c.connect();
            try {
                InputStream in = new BufferedInputStream(c.getInputStream());
                String s = readStream(in);
                try {
                    JSONObject obj = new JSONObject(s.substring(s.indexOf("{"), s.lastIndexOf("}") + 1));
                    travel.setCurrentPrice(obj.getDouble("price") / 100);
                    travel.setHours(obj.getJSONArray("hours").getString(0));
                    return travel;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } finally {
                c.disconnect();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String readStream(InputStream is) {
        try {
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            int i = is.read();
            while (i != -1) {
                bo.write(i);
                i = is.read();
            }
            return bo.toString();
        } catch (IOException e) {
            return "";
        }
    }
}
