package com.iotproj.aduino_honeybam;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by office on 2017-12-07.
 */

public class ReceiveWeatherTask extends AsyncTask<String, Void, JSONObject> {


    String description;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected JSONObject doInBackground(String... params) {
        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(params[0]).openConnection();
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);
            conn.connect();

            if(conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream is = conn.getInputStream();
                InputStreamReader reader = new InputStreamReader(is);
                BufferedReader in = new BufferedReader(reader);

                String readed;
                while((readed = in.readLine()) != null) {
                    JSONObject jObject = new JSONObject(readed);
                    return  jObject;
                }
            }else {
                return  null;
            }
            return null;
        }catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    @Override
    protected void onPostExecute(JSONObject jsonObject) {
        Log.d(getClass().getName(), jsonObject.toString());

        if(jsonObject != null) {
            description = "";
        }

        try {
            description = jsonObject.getJSONArray("weather").getJSONObject(0).getString("description");
            Log.d(getClass().getName(), "description : " + description);
        }catch (JSONException e){ e.printStackTrace(); }

        super.onPostExecute(jsonObject);
    }

    public String getDescription() {
        return description;
    }
}
