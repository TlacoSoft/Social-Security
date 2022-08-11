package com.example.maps;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

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
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.GoogleMap;

import org.json.JSONObject;

import java.text.BreakIterator;

public class ServiceUbicacion extends Service{

        private Context thisContext = this;
        private static final String CHANNEL_ID = "Canal";
        private GoogleMap mMap;
        private Object MapsActivity;
        private FusedLocationProviderClient fusedLocationClient;
        private int data = 0;
        private final Handler handler= new Handler();

        @Override
        public void onCreate() {

        }

        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
            //return super.onStartCommand(intent, flags, startId);

            ejecutar();

            return START_STICKY;
        }

        private void ejecutar(){
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    actualizarPosicion();//llamamos nuestro metodo
                    handler.postDelayed(this,15000);//se ejecutara cada 15 segundos
                }
            },5000);//empezara a ejecutarse después de 5 milisegundos
        }

        private void actualizarPosicion() {
//Obtenemos una referencia al LocationManager
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

//Obtenemos la última posición conocida
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            //Mostramos la última posición conocida
            notificacion("Compartiendo ubicacion",2);
            guardar(location);
            verificar(location);

//Nos registramos para recibir actualizaciones de la posición
            LocationListener locationListener = new LocationListener() {
                public void onLocationChanged(Location location) {
                    //notificacion(location);
                }

                public void onProviderDisabled(String provider) {
                    BreakIterator lblEstado = null;
                    lblEstado.setText("Provider OFF");
                }

                public void onProviderEnabled(String provider) {
                    BreakIterator lblEstado = null;
                    lblEstado.setText("Provider ON");
                }

                public void onStatusChanged(String provider, int status, Bundle extras) {
                    Log.i("localizacion", "status: " + status);
                    BreakIterator lblEstado = null;
                    lblEstado.setText("Status: " + status);
                }
            };

            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, 15000, 0, locationListener);
        }

        @Override
        public void onDestroy(){
            notificacion("Ubicación desactivada",3);
            handler.removeCallbacksAndMessages(null);
        }

        public void verificar(Location location) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            if (latitude <= 16.8230 && latitude >= 16.8180 && longitude >= -96.7862 && longitude <= -96.7835) {
                notificacion("Haz llegado a tu destino",4);
                Intent myService = new Intent(this, ServiceUbicacion.class);
                stopService(myService);
            }
        }

        public void guardar(Location location){
            double latitude = location.getLatitude();
            String lat = String.valueOf(latitude);
            double longitude = location.getLongitude();
            String lng = String.valueOf(longitude);

            //Toast.makeText(thisContext, lat+lng, Toast.LENGTH_SHORT).show();

            RequestQueue requestQueue;

            // Instantiate the cache
            Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024); // 1MB cap

            // Set up the network to use HttpURLConnection as the HTTP client.
            Network network = new BasicNetwork(new HurlStack());

            // Instantiate the RequestQueue with the cache and network.
            requestQueue = new RequestQueue(cache, network);

            // Start the queue
            requestQueue.start();

            String url = "https://dgs801.com/socialsecurity/guardarLocation.php?id=4&latitude="+lat+"&longitude="+lng;

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                    (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            Toast.makeText(getApplication(), "OK", Toast.LENGTH_LONG).show();
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

        public void notificacion(String token, int id) {
            String name = "Notifi";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);

            //Intent i = new Intent(this, SantoDomingoActivity.class);
            //i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            //PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, i, 0);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setSmallIcon(R.drawable.logo)
                    .setContentTitle("Social Security")
                    .setContentText(token)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setAutoCancel(true);

            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
            notificationManager.notify(id, builder.build());
        }

        @Nullable
        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }
}
