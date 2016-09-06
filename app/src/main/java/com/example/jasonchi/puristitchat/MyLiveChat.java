package com.example.jasonchi.puristitchat;

import android.content.Context;
import android.util.Base64;
import android.util.Log;

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

/**
 * Created by JasonChi on 2016/9/2.
 */
public class MyLiveChat {

    private HashMap<String,String> param,paramI;
    private String registerUrl = "https://api.puristit.com/register";
    private String initializeUrl = "https://api.puristit.com/initialize";
    private String chatUrl;
    private String clientApiKey = "ydNHyu6SxFBdmzU2QNOvVnIOUKefuQy";
    private String username;
    private String password;
    private String platform = "Android";
    private String regid;
    private String p_username;
    private String p_password = "";
    RequestQueue queue;

    public MyLiveChat(String user_name, String user_pwd, Context context){
        this.username = user_name;
        this.password = user_pwd;

        regid = FirebaseInstanceId.getInstance().getToken();
        Log.e("REGID", regid);

        queue = Volley.newRequestQueue(context);

        param = new HashMap<>();
        param.put("username", this.username);
        param.put("password", this.password);
        param.put("name", this.username);
        param.put("platform", this.platform);

        sendRequest(registerUrl, param);

    }

    public String getChatUrl(){
        return chatUrl;
    }

    public String getRegid() {
        return regid;
    }

    public void sendRequest(String url, final HashMap<String,String> param) {

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(param),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("Response", response.toString());
                        try {
                            JSONObject object = new JSONObject(response.toString());
                            JSONObject obj = object.getJSONObject("result");
                            if(obj.has("chat_url")){
                                chatUrl = obj.getString("chat_url");
                            }else {
                                p_username = obj.getString("p_username");
                                p_password = obj.getString("p_password");

                                paramI = new HashMap<>();
                                paramI.put("p_username", p_username);
                                paramI.put("p_password", p_password);
                                paramI.put("name", username);
                                paramI.put("platform", platform);
                                paramI.put("registration_id", regid);
                                sendRequest(initializeUrl, paramI);
                            }
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
            public Map<String, String> getHeaders() throws AuthFailureError {
                String apiKey = clientApiKey +":"+ p_password;
                String auth = "Basic " + Base64.encodeToString(apiKey.getBytes(), Base64.NO_WRAP);
                HashMap<String,String> headers = new HashMap<>();
                headers.put("Authorization",auth);

                return  headers;
            }
        };
       queue.add(jsonObjectRequest);
    }
}
