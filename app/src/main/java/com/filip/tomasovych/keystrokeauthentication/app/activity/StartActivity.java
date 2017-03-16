package com.filip.tomasovych.keystrokeauthentication.app.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.filip.tomasovych.keystrokeauthentication.R;
import com.filip.tomasovych.keystrokeauthentication.app.util.Helper;

public class StartActivity extends AppCompatActivity {

    private Button mRegisterButton;
    private Button mLoginButton;
    private Button mExperimentButton;
    private Context mContext = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);


        mRegisterButton = (Button) findViewById(R.id.buttonRegister);
        mLoginButton = (Button) findViewById(R.id.buttonLogin);
        mExperimentButton = (Button) findViewById(R.id.buttonExperiment);

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

        mExperimentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater layoutInflaterAndroid = LayoutInflater.from(mContext);
                View mView = layoutInflaterAndroid.inflate(R.layout.user_name_input_dialog, null);
                AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(mContext);
                alertDialogBuilderUserInput.setView(mView);

                final EditText userInputDialogEditText = (EditText) mView.findViewById(R.id.userInputDialog);
                alertDialogBuilderUserInput
                        .setCancelable(false)
                        .setPositiveButton("Send", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {
                                String activeUser = userInputDialogEditText.getText().toString();

                                Intent intent = new Intent(StartActivity.this, ImposterLogin.class);
                                intent.putExtra(Helper.USER_NAME, activeUser);
                                intent.putExtra(Helper.NUM_PASSWORD, false);
                                startActivity(intent);
                            }
                        })

                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialogBox, int id) {
                                        dialogBox.cancel();
                                    }
                                });

                AlertDialog alertDialogAndroid = alertDialogBuilderUserInput.create();
                alertDialogAndroid.show();
            }
        });
    }

    private <T> void launchActivity(Class<T> loginActivityClass) {
        Intent intent = new Intent(StartActivity.this, loginActivityClass);
        startActivity(intent);
        finish();
    }
}
