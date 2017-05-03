package com.filip.tomasovych.keystrokeauthentication.app.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.filip.tomasovych.keystrokeauthentication.R;
import com.filip.tomasovych.keystrokeauthentication.app.classification.Train;
import com.filip.tomasovych.keystrokeauthentication.app.database.DbHelper;
import com.filip.tomasovych.keystrokeauthentication.app.model.User;
import com.filip.tomasovych.keystrokeauthentication.app.util.Helper;

public class StartActivity extends AppCompatActivity {

    private Button mRegisterButton;
    private Button mLoginButton;
    private Button mImposterExperiment;
    private Button mRegisterExperimentButton;
    private Button mIdentificaitonAlnum;
    private Context mContext = this;

    private DbHelper mDbHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        mDbHelper = DbHelper.getInstance(getApplicationContext());

        mRegisterButton = (Button) findViewById(R.id.buttonRegister);
        mLoginButton = (Button) findViewById(R.id.buttonLogin);
        mImposterExperiment = (Button) findViewById(R.id.buttonImposterExperiment);
        mRegisterExperimentButton = (Button) findViewById(R.id.buttonRegisterExperiment);
        mIdentificaitonAlnum = (Button) findViewById(R.id.buttonIdentificationAlnum);


        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StartActivity.this, RegisterActivity.class);
                intent.putExtra(Helper.IS_EXPERIMENT, false);
                startActivity(intent);
            }
        });

        mRegisterExperimentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StartActivity.this, RegisterActivity.class);
                intent.putExtra(Helper.IS_EXPERIMENT, true);
                startActivity(intent);
            }
        });

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater layoutInflaterAndroid = LayoutInflater.from(mContext);
                View mView = layoutInflaterAndroid.inflate(R.layout.user_name_input_dialog, null);
                AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(mContext);
                alertDialogBuilderUserInput.setView(mView);

                final EditText userInputDialogEditText = (EditText) mView.findViewById(R.id.userInputDialog);
                userInputDialogEditText.requestFocus();

                alertDialogBuilderUserInput
                        .setCancelable(false)
                        .setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {
                                String userName = userInputDialogEditText.getText().toString();

                                User user = mDbHelper.getUser(userName);

                                if (user == null) {
                                    Toast.makeText(getBaseContext(), "User does not exist", Toast.LENGTH_SHORT).show();
                                    dialogBox.cancel();
                                    return;
                                }

                                Intent intent = new Intent(StartActivity.this, LoginActivity.class);
                                intent.putExtra(Helper.USER_NAME, userName);
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

        mImposterExperiment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater layoutInflaterAndroid = LayoutInflater.from(mContext);
                View mView = layoutInflaterAndroid.inflate(R.layout.user_name_input_dialog, null);
                AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(mContext);
                alertDialogBuilderUserInput.setView(mView);

                final EditText userInputDialogEditText = (EditText) mView.findViewById(R.id.userInputDialog);
                alertDialogBuilderUserInput
                        .setCancelable(false)
                        .setPositiveButton("Continue", new DialogInterface.OnClickListener() {
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

        mIdentificaitonAlnum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StartActivity.this, IdentificationActivity.class);
                intent.putExtra(Helper.NUM_PASSWORD, false);
                startActivity(intent);
            }
        });


    }

    private <T> void launchActivity(Class<T> loginActivityClass) {
        Intent intent = new Intent(StartActivity.this, loginActivityClass);
        startActivity(intent);
        //finish();
    }


}
