package com.example.maps.ui.gallery;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

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
import com.example.maps.InfoActivity;
import com.example.maps.R;
import com.example.maps.databinding.FragmentGalleryBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

public class GalleryFragment extends Fragment {

    private FragmentGalleryBinding binding;

    private ListView listview;
    private ListView listview2;

    private ArrayList<String> names;

    ArrayList<String> ar = new ArrayList<String>();
    ArrayList<String> arr = new ArrayList<String>();
    JSONArray jsonArray;
    JSONObject jsonObject;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gallery, container, false);
        listview = view.findViewById(R.id.gruposActivos);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RequestQueue requestQueue;

        // Instantiate the cache
        Cache cache = new DiskBasedCache(getContext().getCacheDir(), 1024 * 1024); // 1MB cap

        // Set up the network to use HttpURLConnection as the HTTP client.
        Network network = new BasicNetwork(new HurlStack());

        // Instantiate the RequestQueue with the cache and network.
        requestQueue = new RequestQueue(cache, network);

        // Start the queue
        requestQueue.start();

        String url = "https://dgs801.com/socialsecurity/grupos.php?email=braquetesss@gmail.com";

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONArray>(){
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            jsonArray = response;
                            jsonObject = jsonArray.getJSONObject(0);
                            String nombre = null;
                            String id = null;

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject object = jsonArray.getJSONObject(i);
                                nombre = object.getString("nombre");
                                id = object.getString("idGrupo");
                                ar.add(nombre);
                                arr.add(id);
                            }

                            ArrayAdapter<String> adapter =new ArrayAdapter<String>(getActivity(),
                                    android.R.layout.simple_list_item_activated_1,ar);

                            listview.setAdapter(adapter);

                        }catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                        Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
        requestQueue.add(jsonArrayRequest);

        //listView.setClickable(true);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterview, View v, int i, long l) {
                //Toast.makeText(VentasActivity.this, arr.get(i), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(), InfoActivity.class);
                intent.putExtra("id", arr.get(i));
                startActivity(intent);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}