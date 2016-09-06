package com.example.jasonchi.puristitchat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.iid.FirebaseInstanceId;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    Button goChat, regisBtn;
    TextView showResult;
    EditText uName, uPasswd;
    Intent intent;
    Customer cs;
    String regid;
    MyLiveChat liveChat;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    public void init() {
        uName = (EditText) findViewById(R.id.uName);
        uPasswd = (EditText) findViewById(R.id.uPasswd);
        showResult = (TextView) findViewById(R.id.regisResult);
        regisBtn = (Button) findViewById(R.id.regisBtn);
        regisBtn.setOnClickListener(this);
        goChat = (Button) findViewById(R.id.goChat);
        goChat.setOnClickListener(this);

        regid = FirebaseInstanceId.getInstance().getToken();
        Log.e("REGID", regid);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.regisBtn:
                cs = new Customer();
                cs.setPassword(uPasswd.getText().toString().trim());
                cs.setUsername(uName.getText().toString().trim());

                liveChat = new MyLiveChat(cs.getUsername(), cs.getPassword(), regid, this);

                break;
            case R.id.goChat:
                intent = new Intent();
                intent.setClass(MainActivity.this, LiveChatActivity.class);
                intent.putExtra("chat_url", liveChat.getChatUrl());
                startActivity(intent);
                break;
        }
    }
}
