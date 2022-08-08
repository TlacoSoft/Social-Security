package com.example.maps;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class UsuarioActivity extends AppCompatActivity {

    ArrayList<String> arr = new ArrayList<String>();
    TextView email;
    String extra;
    int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usuario);

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

        String url = "https://dgs801.com/socialsecurity/getusuario.php?id="+id;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>(){
                    @Override
                    public void onResponse(JSONObject response) {
                        email = (TextView) findViewById(R.id.nombre);
                        String idUsuario = null;

                        try {
                            email.setText(response.getString("email"));
                            idUsuario = response.getString("idUsuario");
                            arr.add(idUsuario);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                        Toast.makeText(UsuarioActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
        requestQueue.add(jsonObjectRequest);
    }

    public void amigo(View v){
        Toast.makeText(UsuarioActivity.this, arr.get(0), Toast.LENGTH_SHORT).show();
        UsuarioActivity createPackageContext;
        Intent intent = new Intent(createPackageContext = UsuarioActivity.this, AmigoActivity.class);
        intent.putExtra("id", arr.get(0));
        startActivity(intent);
    }
}