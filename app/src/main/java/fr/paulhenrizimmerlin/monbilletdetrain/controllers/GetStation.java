package fr.paulhenrizimmerlin.monbilletdetrain.controllers;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class GetStation {
    private static GetStation mInstance;
    private static Context mCtx;
    private RequestQueue mRequestQueue;

    public GetStation(Context ctx) {
        mCtx = ctx;
        mRequestQueue = getRequestQueue();
    }

    public static synchronized GetStation getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new GetStation(context);
        }
        return mInstance;
    }

    public static void make(Context ctx, String query, Response.Listener<String>
            listener, Response.ErrorListener errorListener) {
        String url = "https://www.oui.sncf/booking/autocomplete-d2d?uc=fr-FR&searchField=origin&searchTerm=" + query;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                listener, errorListener);
        GetStation.getInstance(ctx).addToRequestQueue(stringRequest);
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }
}