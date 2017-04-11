package ik.cp2.bucketlist;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import static android.provider.ContactsContract.CommonDataKinds.Website.URL;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {
    RequestQueue requestQueue;
    public static final String TAG = "Signup";
    private ProgressDialog pd;
    EditText name;
    EditText email;
    EditText password;
    EditText verifyPassword;
    public Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_sign_up);

        Button signup = (Button)findViewById(R.id.si_signup);
        name =(EditText)findViewById(R.id.username);
        email =(EditText)findViewById(R.id.signup_email);
        password =(EditText)findViewById(R.id.signup_password);
        verifyPassword =(EditText)findViewById(R.id.verify);
        pd = new ProgressDialog(SignUpActivity.this);
        context =this.getBaseContext();


        signup.setOnClickListener(this);

        requestQueue = Volley.newRequestQueue(this);
    }


    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.si_signup:
                signUp();
                break;
        }
    }


    public void signUp(){
        String url = getString(R.string.prod_server)+"/auth/register";
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
                            Toast toast = Toast.makeText(SignUpActivity.this, jmessage.getString("message"), Toast.LENGTH_SHORT);
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
                            Toast toast = Toast.makeText(SignUpActivity.this, jmessage.getString("error"), Toast.LENGTH_SHORT);
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
                    params.put("email", email.getText().toString());
                    params.put("name", name.getText().toString());
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
                    int mStatusCode = response.statusCode;
                    Log.i(TAG, "Status: "+ mStatusCode);
                    if (mStatusCode == 201){
                        Intent intent = new Intent(context, LoginActivity.class);
                        startActivity(intent);
                        SignUpActivity.this.finish();
                    }
                    return super.parseNetworkResponse(response);
                }
        };

        requestQueue.add(postRequest);
    }


}
