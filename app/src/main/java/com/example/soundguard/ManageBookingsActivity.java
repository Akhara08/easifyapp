package com.example.soundguard;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ManageBookingsActivity extends AppCompatActivity {
    private ListView listView;
    private BookingAdapter adapter;
    private List<Booking> bookingList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_bookings);

        listView = findViewById(R.id.listView);
        bookingList = new ArrayList<>();
        adapter = new BookingAdapter(this, bookingList);
        listView.setAdapter(adapter);

        new FetchBookingsTask().execute("http://10.0.2.2/api/get_bookings.php");
    }

    private class FetchBookingsTask extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(ManageBookingsActivity.this);
            progressDialog.setMessage("Fetching bookings...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... urls) {
            String response = "";
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);

                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder result = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }
                    reader.close();
                    response = result.toString();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressDialog.dismiss();

            if (result.isEmpty()) {
                Toast.makeText(ManageBookingsActivity.this, "Failed to fetch data", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                JSONObject jsonObject = new JSONObject(result);
                boolean success = jsonObject.getBoolean("success");

                if (success) {
                    JSONArray bookingsArray = jsonObject.getJSONArray("bookings");
                    bookingList.clear();

                    for (int i = 0; i < bookingsArray.length(); i++) {
                        JSONObject bookingObj = bookingsArray.getJSONObject(i);
                        String id = bookingObj.getString("id");
                        String serviceTitle = bookingObj.getString("service_title");
                        String scheduledDate = bookingObj.getString("scheduled_date");
                        String scheduledTime = bookingObj.getString("scheduled_time");
                        String address = bookingObj.getString("address");
                        String paymentMode = bookingObj.getString("payment_mode");
                        String createdAt = bookingObj.getString("created_at");

                        Booking booking = new Booking(id, serviceTitle, scheduledDate, scheduledTime, address, paymentMode, createdAt);
                        bookingList.add(booking);
                    }

                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(ManageBookingsActivity.this, "No bookings found", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(ManageBookingsActivity.this, "Error parsing JSON", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
