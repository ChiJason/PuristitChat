package com.example.jasonchi.puristitchat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    Button goChat, regisBtn, initBtn;
    TextView showResult;
    EditText uName, uPasswd;
    String registerUrl = "https://api.puristit.com/register";
    String initializeUrl = "https://api.puristit.com/initialize";
    String chatUrl;
    String serverApiKey = "KBc1L02d1il8JyikmOsZlCO0enTEGJl";
    HashMap<String,String> param, paramI;
    Intent intent;
    Customer cs;
    RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        queue = Volley.newRequestQueue(this);

        String regid = FirebaseInstanceId.getInstance().getToken();
        Log.e("REGID", regid);

    }

    public void init() {

        uName = (EditText) findViewById(R.id.uName);
        uPasswd = (EditText) findViewById(R.id.uPasswd);
        showResult = (TextView) findViewById(R.id.regisResult);
        regisBtn = (Button) findViewById(R.id.regisBtn);
        regisBtn.setOnClickListener(this);
        initBtn = (Button) findViewById(R.id.initBtn);
        initBtn.setOnClickListener(this);
        goChat = (Button) findViewById(R.id.goChat);
        goChat.setOnClickListener(this);

        cs = new Customer();
    }

    public void registerAPI(String url, HashMap<String,String> param, final String authKeys) {

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(param),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("Response", response.toString());
                        try {
                            JSONObject object = new JSONObject(response.toString());
                            JSONObject obj = object.getJSONObject("result");
                            cs.setP_username(obj.getString("p_username"));
                            cs.setP_password(obj.getString("p_password"));
                            showResult.setText("P_username: "+ cs.getP_username() + "\n"
                                    +"P_password: " + cs.getP_password());

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("onErrorResponse: ", error.toString());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError{
                String apiKey = authKeys+":"+"";
                String auth = "Basic " + Base64.encodeToString(apiKey.getBytes(), Base64.NO_WRAP);
                HashMap<String,String> headers = new HashMap<>();
                headers.put("Authorization",auth);

                return  headers;
            }
        };

        queue.add(jsonObjectRequest);
    }

    public void initailizeAPI(String url, HashMap<String,String> param, final String authKeys) {

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(param),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("Response", response.toString());

                        try {
                            JSONObject object = new JSONObject(response.toString());
                            JSONObject obj = object.getJSONObject("result");
                            chatUrl = obj.getString("chat_url");
                            showResult.setText(chatUrl);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("onErrorResponse: ", error.toString());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError{
                String apiKey = authKeys+":"+ cs.getP_password();
                String auth = "Basic " + Base64.encodeToString(apiKey.getBytes(), Base64.NO_WRAP);
                HashMap<String,String> headers = new HashMap<>();
                headers.put("Authorization",auth);

                return  headers;
            }
        };

        queue.add(jsonObjectRequest);
    }



    @Override
    public void onClick(View v) {

        switch (v.getId())
        {
            case R.id.regisBtn:
                cs.setName(uName.getText().toString().trim());
                cs.setPassword(uPasswd.getText().toString().trim());
                cs.setUsername(uName.getText().toString().trim());

                param = new HashMap<>();
                param.put("username", cs.getUsername());
                param.put("password", cs.getPassword());
                param.put("name", cs.getName());
                param.put("platform", cs.getPlatform());

                if(cs.getUsername() != "" && cs.getPassword() != "")
                {
                    registerAPI(registerUrl, param, serverApiKey);
                }else
                {
                    showResult.setText("Registration failure");
                }
                break;

            case R.id.initBtn:
                paramI = new HashMap<>();
                paramI.put("p_username",cs.getP_username());
                paramI.put("password", cs.getP_password());
                paramI.put("name", cs.getName());
                paramI.put("platform", cs.getPlatform());

                initailizeAPI(initializeUrl, paramI, serverApiKey);
                break;

            case R.id.goChat:

                intent = new Intent();
                intent.setClass(MainActivity.this, LiveChatActivity.class);
                intent.putExtra("chatURL", chatUrl);
                startActivity(intent);
                break;
        }
    }
}
