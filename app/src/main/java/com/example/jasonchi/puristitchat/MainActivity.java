package com.example.jasonchi.puristitchat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    TextView resultText;
    String registerUrl = "https://api.puristit.com/register";
    String initializeUrl = "https://api.puristit.com/initialize";
    String ChatUrl = "";
    HashMap<String,String> param;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(registerUrl, new JSONObject(param),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        resultText.setText(response.toString());
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
                String api = "ydNHyu6SxFBdmzU2QNOvVnIOUKefuQy"+":"+"";
                String auth = "Basic " + Base64.encodeToString(api.getBytes(), Base64.NO_WRAP);
                HashMap<String,String> headers = new HashMap<>();
                headers.put("Authorization",auth);

                return  headers;
            }
        };

        queue.add(jsonObjectRequest);

    }

    public void init() {

        param = new HashMap<>();
        param.put("username","JC");
        param.put("password","asdfasdf");
        param.put("name","Jason");
        param.put("platform","Android");

        resultText = (TextView) findViewById(R.id.showResult);

    }
}
