package ik.cp2.bucketlist;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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
import java.util.HashMap;
import java.util.Map;


public class CreateBucketList extends DialogFragment implements View.OnClickListener{


    public static String TAG="CreateBucketList";
    public Context context;
    public EditText new_bl_name;
    public Button create_bl;
    public Button cancel_bl;
    private ProgressDialog pd;
    MainActivity mainactivity;
    public RequestQueue requestQueue;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_create_bucket_list, container,
                false);
        context = getContext();
        new_bl_name=(EditText) rootView.findViewById(R.id.new_bl_name);
        create_bl=(Button) rootView.findViewById(R.id.create_bl);
        cancel_bl=(Button) rootView.findViewById(R.id.cancel_bl);

        create_bl.setOnClickListener(this);
        cancel_bl.setOnClickListener(this);
        pd = new ProgressDialog(context);
        requestQueue = Volley.newRequestQueue(context);
        // Inflate the layout for this fragment


        return rootView;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.create_bl:
                createBucketList();
                break;
            case R.id.cancel_bl:
                getDialog().dismiss();
                break;
        }
    }

    public void createBucketList(){
    //  post method to create new bucket list

        String url = getString(R.string.prod_server)+"/bucketlists";
        pd.setMessage("Please wait");
        pd.show();

        Map<String, String> params = new HashMap<String, String>();



        StringRequest postRequest = new StringRequest( Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.i(TAG, "onResponse: "+ response);
                        try {
                            JSONObject jmessage=new JSONObject(response);
                            Toast toast = Toast.makeText(context, jmessage.getString("message"), Toast.LENGTH_SHORT);
                            toast.show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        pd.dismiss();


                    }


                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //   Handle Error
                        try {
                            pd.dismiss();
                            String message=new String(error.networkResponse.data,"UTF-8");
                            JSONObject jmessage=new JSONObject(message);
                            Toast toast = Toast.makeText(getContext(), jmessage.getString("error"), Toast.LENGTH_SHORT);
                            toast.show();
                            Log.i(TAG, "onError: "+ message);
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("name", new_bl_name.getText().toString());
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                SharedPreferences prefs = context.getSharedPreferences(getString(R.string.bucket_prefs),Context.MODE_PRIVATE);
                String token = prefs.getString("access_token", null);
                params.put("Content-Type","application/x-www-form-urlencoded");
                params.put("access_token",token);
                return params;
            }
            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                int mStatusCode = response.statusCode;
                Log.i(TAG, "Status: "+ mStatusCode);
                if (mStatusCode == 201){
                    getDialog().dismiss();
                    ((MainActivity)getActivity()).getBucketLists();
                }
                return super.parseNetworkResponse(response);
            }
        };

        requestQueue.add(postRequest);

    }
}
