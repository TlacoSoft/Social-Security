package com.example.maps;

import androidx.annotation.AnyRes;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.maps.databinding.ActivityMaps2Binding;
import com.example.maps.databinding.ActivityAmigoBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class AmigoActivity extends FragmentActivity implements OnMapReadyCallback{

    private GoogleMap mMap;
    private ActivityMaps2Binding binding;
    String extra;
    int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        if(bundle != null){
            extra = bundle.getString("id");
            id = Integer.parseInt(extra);
        }

        binding = ActivityMaps2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        RequestQueue requestQueue;

        // Instantiate the cache
        Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024); // 1MB cap

        // Set up the network to use HttpURLConnection as the HTTP client.
        Network network = new BasicNetwork(new HurlStack());

        // Instantiate the RequestQueue with the cache and network.
        requestQueue = new RequestQueue(cache, network);

        // Start the queue
        requestQueue.start();

        String url = "https://dgs801.com/socialsecurity/getLocation.php?id="+id;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>(){
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String lat = response.getString("latitude");
                            double str1 = Double.parseDouble(lat);
                            String lng = response.getString("longitude");
                            double str2 = Double.parseDouble(lng);
                            // Add a marker in Sydney and move the camera
                            LatLng ubi = new LatLng(str1, str2);
                            mMap.addMarker(new MarkerOptions().position(ubi).title("Ubicacion de contacto"));
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(ubi));
                            CameraPosition cameraPosition = new CameraPosition.Builder()
                                    .target(ubi)
                                    .zoom(16)
                                    .bearing(0)
                                    .tilt(0)
                                    .build();
                            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                            Toast.makeText(AmigoActivity.this, ubi.toString(), Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error

                    }
                });

        // Access the RequestQueue through your singleton class.
        requestQueue.add(jsonObjectRequest);
    }

}