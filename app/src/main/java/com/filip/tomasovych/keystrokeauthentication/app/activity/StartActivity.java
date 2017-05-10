package com.filip.tomasovych.keystrokeauthentication.app.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class StartActivity extends AppCompatActivity {

    private static final String TAG = StartActivity.class.getSimpleName();

    private Button mRegisterButton;
    private Button mLoginButton;
    private Button mImposterExperiment;
    private Button mRegisterExperimentButton;
    private Button mRegisterIdentification;
    private Button mIdentificaitonAlnum;
    private Button mIdentificaitonNum;
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
        mRegisterIdentification = (Button) findViewById(R.id.buttonRegisterIdentification);
        mIdentificaitonNum = (Button) findViewById(R.id.buttonIdentificationNum);

        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StartActivity.this, RegisterActivity.class);
                intent.putExtra(Helper.IS_EXPERIMENT, false);
                intent.putExtra(Helper.IS_IDENTIFY, false);
                startActivity(intent);
            }
        });

        mRegisterExperimentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StartActivity.this, RegisterActivity.class);
                intent.putExtra(Helper.IS_EXPERIMENT, true);
                intent.putExtra(Helper.IS_IDENTIFY, false);
                startActivity(intent);
            }
        });

        mRegisterIdentification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StartActivity.this, RegisterActivity.class);
                intent.putExtra(Helper.IS_EXPERIMENT, false);
                intent.putExtra(Helper.IS_IDENTIFY, true);
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

        mIdentificaitonNum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StartActivity.this, IdentificationActivity.class);
                intent.putExtra(Helper.NUM_PASSWORD, true);
                startActivity(intent);
            }
        });


        SharedPreferences settings = getSharedPreferences(Helper.MY_PREFS_FILE_NAME, 0);

        if (settings.getBoolean(Helper.MY_PREFS_FIRST_USE, true)) {
            Log.d(TAG, "First use");
            copyAssets();
            //trainLegitModels();
            settings.edit().putBoolean("my_first_time", false).commit();
        }

    }

    private void trainLegitModels() {
        new Train(getApplication(), mDbHelper.getUser("h1053055", "na925125TO")).trainUser(Helper.ALNUM_PASSWORD_CODE);
        new Train(getApplication(), mDbHelper.getUser("xvalastiak", "valdy550")).trainUser(Helper.ALNUM_PASSWORD_CODE);
        new Train(getApplication(), mDbHelper.getUser("xgono", "silneheslo321")).trainUser(Helper.ALNUM_PASSWORD_CODE);
        new Train(getApplication(), mDbHelper.getUser("xcagan", "lolecbolec")).trainUser(Helper.ALNUM_PASSWORD_CODE);
        new Train(getApplication(), mDbHelper.getUser("tabora1", "pokemon")).trainUser(Helper.ALNUM_PASSWORD_CODE);
        new Train(getApplication(), mDbHelper.getUser("xmadzik", "lukasmadzik")).trainUser(Helper.ALNUM_PASSWORD_CODE);
        new Train(getApplication(), mDbHelper.getUser("xhagaral", "kalach")).trainUser(Helper.ALNUM_PASSWORD_CODE);
        new Train(getApplication(), mDbHelper.getUser("xGavornik", "kubko23")).trainUser(Helper.ALNUM_PASSWORD_CODE);
        new Train(getApplication(), mDbHelper.getUser("xkollarova", "autovobis")).trainUser(Helper.ALNUM_PASSWORD_CODE);
        new Train(getApplication(), mDbHelper.getUser("xlibantova", "dominika1992")).trainUser(Helper.ALNUM_PASSWORD_CODE);
        new Train(getApplication(), mDbHelper.getUser("xguilisi", "150igs")).trainUser(Helper.ALNUM_PASSWORD_CODE);
        new Train(getApplication(), mDbHelper.getUser("xfarkast", "huawei")).trainUser(Helper.ALNUM_PASSWORD_CODE);
        new Train(getApplication(), mDbHelper.getUser("xwolfm", "mirowolf")).trainUser(Helper.ALNUM_PASSWORD_CODE);
        new Train(getApplication(), mDbHelper.getUser("xtomasova", "pecora")).trainUser(Helper.ALNUM_PASSWORD_CODE);
        new Train(getApplication(), mDbHelper.getUser("xbesedova", "Besedova")).trainUser(Helper.ALNUM_PASSWORD_CODE);
        new Train(getApplication(), mDbHelper.getUser("xvnencak", "stanislav7")).trainUser(Helper.ALNUM_PASSWORD_CODE);
        new Train(getApplication(), mDbHelper.getUser("werther", "password")).trainUser(Helper.ALNUM_PASSWORD_CODE);

        new Train(getApplication(), mDbHelper.getUser("xhagaral", "102016")).trainUser(Helper.NUM_PASSWORD_CODE);
        new Train(getApplication(), mDbHelper.getUser("h1053055", "925125")).trainUser(Helper.NUM_PASSWORD_CODE);
        new Train(getApplication(), mDbHelper.getUser("xkollarova", "240800")).trainUser(Helper.NUM_PASSWORD_CODE);
        new Train(getApplication(), mDbHelper.getUser("xbesedova", "225566")).trainUser(Helper.NUM_PASSWORD_CODE);
        new Train(getApplication(), mDbHelper.getUser("xgono", "170993")).trainUser(Helper.NUM_PASSWORD_CODE);
        new Train(getApplication(), mDbHelper.getUser("xGavornik", "120519")).trainUser(Helper.NUM_PASSWORD_CODE);
        new Train(getApplication(), mDbHelper.getUser("xvalastiak", "550550")).trainUser(Helper.NUM_PASSWORD_CODE);
        new Train(getApplication(), mDbHelper.getUser("xlibantova", "654321")).trainUser(Helper.NUM_PASSWORD_CODE);
        new Train(getApplication(), mDbHelper.getUser("xtomasova", "280690")).trainUser(Helper.NUM_PASSWORD_CODE);
        new Train(getApplication(), mDbHelper.getUser("xmadzik", "1235789510")).trainUser(Helper.NUM_PASSWORD_CODE);
        new Train(getApplication(), mDbHelper.getUser("xwolfm", "2131991")).trainUser(Helper.NUM_PASSWORD_CODE);
        new Train(getApplication(), mDbHelper.getUser("xgulisi", "1502505")).trainUser(Helper.NUM_PASSWORD_CODE);
        new Train(getApplication(), mDbHelper.getUser("werther", "258456")).trainUser(Helper.NUM_PASSWORD_CODE);
        new Train(getApplication(), mDbHelper.getUser("xtabora", "861992")).trainUser(Helper.NUM_PASSWORD_CODE);
        new Train(getApplication(), mDbHelper.getUser("xvnencak", "4014142374")).trainUser(Helper.NUM_PASSWORD_CODE);
        new Train(getApplication(), mDbHelper.getUser("xcagan2", "010593")).trainUser(Helper.NUM_PASSWORD_CODE);
        new Train(getApplication(), mDbHelper.getUser("xfarkast", "193561")).trainUser(Helper.NUM_PASSWORD_CODE);
    }

    private <T> void launchActivity(Class<T> loginActivityClass) {
        Intent intent = new Intent(StartActivity.this, loginActivityClass);
        startActivity(intent);
        //finish();
    }

    private void copyAssets() {
        AssetManager assetManager = getAssets();
        try {
            String[] files = assetManager.list("");
            for (String f : files) {
                Log.d("TAG", f);
                FileOutputStream outputStream = mContext.openFileOutput(f, Context.MODE_PRIVATE);
                InputStream in = assetManager.open(f);
                copyFile(in, outputStream);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);

        }
    }


}
