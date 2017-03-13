package com.filip.tomasovych.keystrokeauthentication.app.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.filip.tomasovych.keystrokeauthentication.R;
import com.filip.tomasovych.keystrokeauthentication.app.util.Helper;

public class StartActivity extends AppCompatActivity {

    private Button mRegisterButton;
    private Button mLoginButton;
    private Button mIdentificationButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);


        mRegisterButton = (Button) findViewById(R.id.buttonRegister);
        mLoginButton = (Button) findViewById(R.id.buttonLogin);
        mIdentificationButton = (Button) findViewById(R.id.buttonIdentification);

        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchActivity(LoginActivity.class);
            }
        });

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        mIdentificationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private <T> void launchActivity(Class<T> loginActivityClass) {
        Intent intent = new Intent(StartActivity.this, loginActivityClass);
        startActivity(intent);
        finish();
    }
}
