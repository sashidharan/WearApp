package com.example.a603936.wearapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;
import android.view.View;

public class MainActivity extends Activity {

    private TextView mTextView;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mTextView = (TextView) stub.findViewById(R.id.text);
            }
        });
    }

    public void feedList(View v) {
        addListenerButton();
    }

    public void addListenerButton() {
        final Context context = this;
        Toast.makeText(getApplicationContext(), "You Came to Feed List", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(context, FeedActivity.class);
        startActivity(intent);
    }



}
