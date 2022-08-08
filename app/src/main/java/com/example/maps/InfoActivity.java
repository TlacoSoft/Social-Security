package com.example.maps;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
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
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class InfoActivity extends AppCompatActivity {

    TextView nombre,password,codigo;
    ListView listausuario;
    String extra;
    int id;
    private static final String CHANNEL_ID = "Canal";
    ArrayList<String> ar = new ArrayList<String>();
    ArrayList<String> arr = new ArrayList<String>();
    JSONArray jsonArray;
    JSONObject jsonObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        listausuario = (ListView) findViewById(R.id.listcontact);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        if(bundle != null){
            extra = bundle.getString("id");
            id = Integer.parseInt(extra);
        }

        RequestQueue requestQueue;

        // Instantiate the cache
        Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024); // 1MB cap

        // Set up the network to use HttpURLConnection as the HTTP client.
        Network network = new BasicNetwork(new HurlStack());

        // Instantiate the RequestQueue with the cache and network.
        requestQueue = new RequestQueue(cache, network);

        // Start the queue
        requestQueue.start();

        String url = "https://dgs801.com/socialsecurity/getGrupoID.php?id="+id;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>(){
                    @Override
                    public void onResponse(JSONObject response) {
                        nombre = (TextView) findViewById(R.id.nombre);
                        password = (TextView) findViewById(R.id.password);
                        codigo = (TextView) findViewById(R.id.codigo);

                        try {
                            nombre.setText(response.getString("nombre"));
                            password.setText(response.getString("password"));
                            codigo.setText(response.getString("codigo"));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                        Toast.makeText(InfoActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
        requestQueue.add(jsonObjectRequest);
        contact();
    }

    public void invitar(View v){
        notificacion("Invitación enviada");
        String token = "Hola, te quiero invitar a ser parte de mi grupo en Social Security.\nGrupo: "+nombre.getText().toString()+"\nContraseña: "+password.getText().toString()+"\n"+"Código: "+codigo.getText().toString();
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT,token);
        shareIntent.setType("text/plain");
        startActivity(shareIntent);
    }

    public void notificacion(String token){
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "nofify", importance);

        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,CHANNEL_ID)
                .setSmallIcon(R.drawable.logo)
                .setContentTitle("SocialSecurity")
                .setContentText(token)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        notificationManager.notify(1,builder.build());
    }

    public void contact(){
        RequestQueue requestQueue;

        // Instantiate the cache
        Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024); // 1MB cap

        // Set up the network to use HttpURLConnection as the HTTP client.
        Network network = new BasicNetwork(new HurlStack());

        // Instantiate the RequestQueue with the cache and network.
        requestQueue = new RequestQueue(cache, network);

        // Start the queue
        requestQueue.start();

        String url = "https://dgs801.com/socialsecurity/getUsuariosGrupo.php?id="+id;

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONArray>(){
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            jsonArray = response;
                            jsonObject = jsonArray.getJSONObject(0);
                            String email = null;
                            String idUsuario = null;

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject object = jsonArray.getJSONObject(i);
                                email = object.getString("email");
                                idUsuario = object.getString("idUsuario");
                                ar.add(email);
                                arr.add(idUsuario);
                            }

                            ArrayAdapter<String> adapter =new ArrayAdapter<String>(InfoActivity.this,
                                    android.R.layout.simple_expandable_list_item_1,ar);
                            listausuario.setAdapter(adapter);

                        }catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                        Toast.makeText(InfoActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
        requestQueue.add(jsonArrayRequest);

        listausuario.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(InfoActivity.this, arr.get(i), Toast.LENGTH_SHORT).show();
                InfoActivity createPackageContext;
                Intent intent = new Intent(createPackageContext = InfoActivity.this, UsuarioActivity.class);
                intent.putExtra("id", arr.get(i));
                startActivity(intent);
            }
        });
    }

    public  void activar(View v){
        startService(new Intent(this, ServiceUbicacion.class));
    }
}