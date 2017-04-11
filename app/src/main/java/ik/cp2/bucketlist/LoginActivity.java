package ik.cp2.bucketlist;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
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

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{
    RequestQueue requestQueue;
    public static final String TAG = "SignIn";
    private ProgressDialog pd;
    EditText email;
    EditText password;
    Button login;
    String Token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_login);

        pd = new ProgressDialog(LoginActivity.this);
        email =(EditText)findViewById(R.id.email);
        password = (EditText)findViewById(R.id.password);
        login = (Button)findViewById(R.id.button);
        login.setOnClickListener(this);

        requestQueue = Volley.newRequestQueue(this);




    }
    @Override
    protected void onResume(){
        super.onResume();
        Context context = getBaseContext();
        SharedPreferences prefs = context.getSharedPreferences(getString(R.string.bucket_prefs),Context.MODE_PRIVATE);
        String restoredText = prefs.getString("access_token", null);
        if (restoredText != null){
            Intent intent = new Intent(getBaseContext(), MainActivity.class);
            startActivity(intent);
            LoginActivity.this.finish();
        }
    }


    public void showSignUp(View view)
    {
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.button:
                signIn();
                break;
        }
    }


    public void signIn(){
        pd = new ProgressDialog(LoginActivity.this);
        String url = getString(R.string.prod_server)+"/auth/login";
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
                            Toast toast = Toast.makeText(LoginActivity.this, jmessage.getString("message"), Toast.LENGTH_SHORT);
                            toast.show();
                            if(jmessage.has("access_token")){
                                Token = jmessage.getString("access_token");
                                Context context = getBaseContext();
                                SharedPreferences.Editor editor = context.getSharedPreferences(getString(R.string.bucket_prefs),Context.MODE_PRIVATE).edit();
                                editor.putString("access_token", Token);
                                editor.apply();
                                Intent intent = new Intent(getBaseContext(), MainActivity.class);
                                startActivity(intent);
                                LoginActivity.this.finish();
                            }
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
                            if (jmessage.has("message")){
                                Toast toast = Toast.makeText(LoginActivity.this, jmessage.getString("error"), Toast.LENGTH_SHORT);
                                toast.show();
                            }


                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        catch (NullPointerException e){
                            Toast toast = Toast.makeText(LoginActivity.this, "Service Unavailable", Toast.LENGTH_SHORT);
                            toast.show();
                        }

                    }
                }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("email", email.getText().toString());
                params.put("password", password.getText().toString());

                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("Content-Type","application/x-www-form-urlencoded");
                return params;
            }
            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                return super.parseNetworkResponse(response);
            }
        };

        requestQueue.add(postRequest);
   }
}

