package com.example.jasonchi.puristitchat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    Button goChat;
    String registerUrl = "https://api.puristit.com/register";
    String initializeUrl = "https://api.puristit.com/initialize";
    String ChatUrl = "";
    String serverApiKey = "KBc1L02d1il8JyikmOsZlCO0enTEGJl";
    HashMap<String,String> param;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

        sendRequest(registerUrl, param, serverApiKey);

    }

    WebViewClient mWebViewClient = new WebViewClient() {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    };

    public void init() {

        param = new HashMap<>();
        param.put("username","JC");
        param.put("password","asdfasdf");
        param.put("name","Jason");
        param.put("platform","Android");

        goChat = (Button) findViewById(R.id.goChat);
        goChat.setOnClickListener(this);

    }

    public void sendRequest(String url, HashMap<String,String> param, final String authKeys) {

        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, new JSONObject(param),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("Response", response.toString());
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

    @Override
    public void onClick(View v) {

        if(v.getId() == goChat.getId())
        {
            startActivity(intent = new Intent(this, LiveChatActivity.class));
        }
    }
}
