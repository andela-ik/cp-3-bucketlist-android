package ik.cp2.bucketlist;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.Map;

import ik.cp2.bucketlist.adapters.BucketListAdapter;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "MainActivity" ;
    private RecyclerView bucketListRecyclerView;
    private LinearLayoutManager bucketListLayoutManager;
    private BucketListAdapter buckeListAdapter;
    public RequestQueue requestQueue;
    public FloatingActionButton add_bucketlist;
    public CardView bucket;
    FragmentManager fragmentManager = getSupportFragmentManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        add_bucketlist=(FloatingActionButton) findViewById(R.id.add_bucket_list);

        bucketListRecyclerView = (RecyclerView) findViewById(R.id.buckets_recycler);
        bucket = (CardView)findViewById(R.id.card_view);

        // use a linear layout manager
        JSONObject arr=new JSONObject();
        bucketListLayoutManager = new LinearLayoutManager(this);
        bucketListRecyclerView.setLayoutManager(bucketListLayoutManager);
        getBucketLists();
        bucketListRecyclerView.setAdapter(buckeListAdapter);
        add_bucketlist.setOnClickListener(this);



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.logout:
                logout();
                return true;
            case R.id.menu_refresh:
                getBucketLists();
                return true;
            case R.id.home:
                Log.i("Test","Clicked");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View v) {

        switch(v.getId()){
            case R.id.add_bucket_list:
                CreateBucketList create=new CreateBucketList();
                create.show(fragmentManager,"Create Bucket List");
                break;
            case R.id.card_view:
                Toast.makeText(this,"Test",Toast.LENGTH_SHORT).show();
                break;


        }
    }


    public void logout()
    {
        Context context = getBaseContext();
        SharedPreferences prefs = context.getSharedPreferences(getString(R.string.bucket_prefs),Context.MODE_PRIVATE);
        String restoredText = prefs.getString("access_token", null);
        SharedPreferences.Editor editor = prefs.edit();
        if (restoredText != null){

            editor.remove("access_token");
            editor.apply();
        }
        Intent intent = new Intent(getBaseContext(), LoginActivity.class);
        startActivity(intent);
        MainActivity.this.finish();
    }

    public void getBucketLists(){
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = getString(R.string.prod_server)+"/bucketlists";



        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Log.i(TAG,response);
                            buckeListAdapter = new BucketListAdapter(new JSONObject(response));
                            bucketListRecyclerView.setAdapter(buckeListAdapter);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this,error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                Context context = getBaseContext();
                SharedPreferences prefs = context.getSharedPreferences(getString(R.string.bucket_prefs),Context.MODE_PRIVATE);
                String token = prefs.getString("access_token", null);
                params.put("Content-Type","application/json");
                params.put("access_token",token);
                return params;
        }
        };

        queue.add(stringRequest);

    }

    

    public void setActionBarTitle(String title){
        getSupportActionBar().setTitle(title);
    }
}




