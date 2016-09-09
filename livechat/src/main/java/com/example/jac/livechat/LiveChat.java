package com.example.jac.livechat;

import android.app.Activity;
import android.content.Context;
import android.util.Base64;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by JasonChi on 2016/9/8.
 */
public abstract class LiveChat {

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
    private WebView mWebView;
    private RequestQueue queue;
    private Context mcontext;

    public LiveChat(String user_name, String user_pwd, String regid, WebView webView, Context context){
        this.username = user_name;
        this.password = user_pwd;
        this.regid = regid;
        this.mWebView = webView;
        this.mcontext = context;

        queue = Volley.newRequestQueue(context);

        param = new HashMap<>();
        param.put("username", this.username);
        param.put("password", this.password);
        param.put("name", this.username);
        param.put("platform", this.platform);

        mWebView.setWebViewClient(mWebViewClient);
        mWebView.setInitialScale(1);
        mWebView.getSettings().setSupportZoom(true);
        mWebView.getSettings().setBuiltInZoomControls(true);
        mWebView.getSettings().setDisplayZoomControls(false);
        mWebView.getSettings().setLoadWithOverviewMode(true);
        mWebView.getSettings().setUseWideViewPort(true);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setDomStorageEnabled(true);

    }

    WebViewClient mWebViewClient = new WebViewClient() {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if(url.contains("logout")){
                Activity activity = (Activity) mcontext;
                activity.finish();
            }else {
                view.loadUrl(url);
            }
            return true;
        }
    };

    public String getChatUrl(){
        return chatUrl + "?platform=Android&registration_id=" + regid + "&roomlist=1";
    }

    public void loadWebView(){

        sendRequest(registerUrl, param);

    }

    protected abstract void beforeRequest();

    protected abstract void afterRequest();


    private void sendRequest(String url, final HashMap<String,String> param) {

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
                                if(chatUrl != null)
                                {
                                    mWebView.loadUrl(getChatUrl());
                                }
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
