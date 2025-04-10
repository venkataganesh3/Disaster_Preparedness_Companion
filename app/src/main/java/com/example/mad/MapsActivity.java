package com.example.mad;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private static final String WEATHER_API_KEY = "f87f9331f1b2c100d015672ae1d4d5f8"; // Your OpenWeatherMap API key
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    private LinearLayout safePlacesLayout; // LinearLayout to display safe places addresses
    private TextView titleTextView; // TextView to display location status

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Initialize fused location client
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Initialize the map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // Initialize the layout to show safe places addresses
        safePlacesLayout = findViewById(R.id.safePlacesLayout);

        // Initialize the title TextView
        titleTextView = findViewById(R.id.statusTitle);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        // Request location permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocation();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    private void getCurrentLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            // Enable the blue dot for current location
            mMap.setMyLocationEnabled(true);

            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, location -> {
                        if (location != null) {
                            LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 12f));

                            // Fetch weather for the current location
                            getWeather(location.getLatitude(), location.getLongitude());
                        } else {
                            Toast.makeText(MapsActivity.this, "Unable to fetch location", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void getWeather(double lat, double lon) {
        String url = "https://api.openweathermap.org/data/2.5/weather?lat=" + lat + "&lon=" + lon + "&appid=" + WEATHER_API_KEY + "&units=metric";

        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        String weather = jsonResponse.getJSONArray("weather").getJSONObject(0).getString("description");
                        double temp = jsonResponse.getJSONObject("main").getDouble("temp");
                        double windSpeed = jsonResponse.getJSONObject("wind").getDouble("speed");

                        // Determine risk level
                        if (temp > 40 || temp < 0 || weather.contains("storm") || windSpeed > 20) {
                            titleTextView.setText("High-Risk Zone 🚨");
                            showHighRiskZone(lat, lon); // Show safe places if high-risk
                        } else if (weather.contains("rain") || windSpeed > 10) {
                            titleTextView.setText("Moderate Risk Zone ⚠️");
                            showModerateRiskZone();
                        } else {
                            titleTextView.setText("Safe Zone 😊");
                            showSafeZone();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error fetching weather data", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Error fetching weather data", Toast.LENGTH_SHORT).show());

        queue.add(request);
    }

    private void showHighRiskZone(double lat, double lon) {
        // Fetch and display nearby safe places dynamically based on the current location
        fetchSafePlaces(lat, lon);
    }

    private void fetchSafePlaces(double lat, double lon) {
        // 1. Fetch weather data using OpenWeatherMap API or any other weather API
        String weatherApiUrl = "https://api.openweathermap.org/data/2.5/weather?lat=" + lat + "&lon=" + lon + "&appid=YOUR_API_KEY";

        // Make an HTTP request to fetch weather data (use a library like Retrofit or OkHttp for network calls)
        // Here we just simulate a good weather condition (sunny) for the sake of demonstration
        String weatherCondition = "sunny"; // This should be dynamically fetched from the weather API

        // 2. Use Google Places API to fetch nearby safe places based on the weather condition (e.g., parks, hospitals)
        String placesApiUrl = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" + lat + "," + lon + "&radius=5000&type=park&key=YOUR_API_KEY";

        // Simulate fetching nearby safe places based on the weather condition and proximity
        List<LatLng> safePlaces = new ArrayList<>();
        // Example: Fetching 3 safe places based on nearby parks
        safePlaces.add(new LatLng(lat + 0.01, lon + 0.01)); // Nearby park
        safePlaces.add(new LatLng(lat - 0.02, lon - 0.02)); // Nearby park
        safePlaces.add(new LatLng(lat + 0.03, lon - 0.03)); // Nearby park

        // 3. Display safe places on the map
        for (LatLng place : safePlaces) {
            mMap.addMarker(new MarkerOptions().position(place).title("Safe Place"));
        }

        // Optionally, zoom to the first safe place
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(safePlaces.get(0), 12f));

        // 4. Display safe place addresses or weather conditions (this can also be dynamic)
        displaySafePlacesAddresses(safePlaces, lat, lon, weatherCondition);
    }

    private void showModerateRiskZone() {
        // Display moderate-risk zone message on screen or map
        titleTextView.setText("Moderate Risk Zone ⚠️");
    }

    private void showSafeZone() {
        // Display safe zone message on screen or map
        titleTextView.setText("Safe Zone 😊");
    }

    private void displaySafePlacesAddresses(List<LatLng> safePlaces, double lat, double lon, String weatherCondition) {
        Geocoder geocoder = new Geocoder(this);

        // Clear previous safe places addresses
        safePlacesLayout.removeAllViews();

        for (LatLng place : safePlaces) {
            try {
                List<Address> addresses = geocoder.getFromLocation(place.latitude, place.longitude, 1);

                if (addresses != null && !addresses.isEmpty()) {
                    Address address = addresses.get(0);
                    StringBuilder fullAddress = new StringBuilder();

                    // Get full address (street, city, country)
                    for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                        fullAddress.append(address.getAddressLine(i)).append("\n");
                    }

                    // Calculate distance from current location to this safe place
                    float[] results = new float[1];
                    Location.distanceBetween(lat, lon, place.latitude, place.longitude, results);
                    float distance = results[0]; // Distance in meters

                    // Create a TextView for each safe place
                    TextView addressTextView = new TextView(this);
                    addressTextView.setText("📍 Safe Place:\n" + fullAddress.toString() +
                            "\n📏 Distance: " + distance + " meters" +
                            "\n☀️ Weather: " + weatherCondition);
                    addressTextView.setPadding(20, 15, 20, 15);
                    addressTextView.setTextSize(16);
                    addressTextView.setTextColor(getResources().getColor(android.R.color.white));
                    addressTextView.setBackground(getResources().getDrawable(R.drawable.ic_launcher_background));

                    // Set margin between safe place entries
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );
                    params.setMargins(0, 10, 0, 10);  // 10dp space between items
                    addressTextView.setLayoutParams(params);

                    // Add the TextView to the LinearLayout
                    safePlacesLayout.addView(addressTextView);
                }
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Error getting address", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
