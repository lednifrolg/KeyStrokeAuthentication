package com.filip.tomasovych.keystrokeauthentication.app.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.filip.tomasovych.keystrokeauthentication.R;
import com.filip.tomasovych.keystrokeauthentication.app.classification.AnomalyDetector;
import com.filip.tomasovych.keystrokeauthentication.app.classification.Identificator;
import com.filip.tomasovych.keystrokeauthentication.app.classification.Train;
import com.filip.tomasovych.keystrokeauthentication.app.model.KeyBuffer;
import com.filip.tomasovych.keystrokeauthentication.app.model.KeyObject;
import com.filip.tomasovych.keystrokeauthentication.app.model.User;
import com.filip.tomasovych.keystrokeauthentication.app.util.Helper;
import com.filip.tomasovych.keystrokeauthentication.app.util.KeyController;

public class TrainActivity extends AppCompatActivity {

    private static final String TAG = TrainActivity.class.getSimpleName();

    private String mQWERTY;
    private String mQWERTYWithDot;
    private String mNumbers;
    private EditText mPasswordEditText;
    private Button[] mLetterButtons;
    private Button[] mNumberButtons;
    private KeyObject[] mKeyObjects;
    private int mKeySize;
    private Button mDoneButton;
    private Button mShiftButton;
    //    private Button mBackspaceButton;
    private Button mStartButton;
    private TextView mTrainHintTextView;
    private TextView mCounterTextView;
    private TextView mPasswordHintTextView;
    private ProgressBar mCounterProgressBar;

    private User mUser;
    private KeyBuffer mKeyBuffer;
    private KeyController mKeyController;
    private int mErrorsNum;

    private int mState;
    private int mCounter;
    private boolean mIsShiftPressed;

    private boolean mIsNumPassword;
    private boolean mIsIdentify;

    private Handler mHandler = new Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_train);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mState = 0;
        mCounter = 0;
        mErrorsNum = 0;
        mKeySize = 0;
        mKeyBuffer = new KeyBuffer();

        mTrainHintTextView = (TextView) findViewById(R.id.trainHintTextView);
        mTrainHintTextView.setText(R.string.training_hint_practice);

        mPasswordEditText = (EditText) findViewById(R.id.trainPassword);
        mPasswordEditText.requestFocus();
        mPasswordEditText.setInputType(InputType.TYPE_NULL);

        mCounterTextView = (TextView) findViewById(R.id.counterTextView);
        mCounterProgressBar = (ProgressBar) findViewById(R.id.countProgressBar);

        mPasswordHintTextView = (TextView) findViewById(R.id.passwordHintTextView);

        setUpUser();

        if (mIsNumPassword) {
            LinearLayout keyboard = (LinearLayout) findViewById(R.id.xKeyBoard);
            LinearLayout numKeyboard = (LinearLayout) findViewById(R.id.numKeyBoard);
            keyboard.setVisibility(View.GONE);
            numKeyboard.setVisibility(View.VISIBLE);

            setUpNumberButtons();

            mDoneButton = (Button) findViewById(R.id.numDoneButton);

            mState = 4;
        } else {
            setUpKeyboardButtons();
            setUpNumberButtons();

            mDoneButton = (Button) findViewById(R.id.doneButton);
        }

        mDoneButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    String pw = mPasswordEditText.getText().toString();
                    mPasswordEditText.setText("");

                    if (mUser.getPassword().equals(pw)) {

                        if (mCounter < 10) {

                            if (mState != 0 && mState != 4) {
//                                new Identificator(mUser, getApplicationContext()).predict(mKeyBuffer, Helper.ALNUM_PASSWORD_CODE);
                                AnomalyDetector ad = new AnomalyDetector(mUser, getApplicationContext());
                                boolean isUser = ad.evaluateEntry(mKeyBuffer, Helper.ALNUM_PASSWORD_CODE);

                                Toast.makeText(getApplicationContext(), "Is user : " + isUser, Toast.LENGTH_SHORT).show();

                                mKeyController.save(mKeyBuffer, mState, 0);

                                mCounter++;

                                Log.d(TAG, "Done: Counter: " + mCounter);

                                mCounterTextView.setText(Integer.toString(mCounter) + "/10");
                            }

                            mErrorsNum = 0;
//                            mCounterTextView.setText(Integer.toString(mCounter));
                        }

                        if (mCounter == 10) {
                            changeState();
                        }
                        updateProgressBar();
                    }

                    if (mState != 0 && mState != 4 && mKeyBuffer.getSize() > 4) {
                        mKeyController.save(mKeyBuffer, mState, 1);
                    }

                    mKeyBuffer.clear();
                }

                return false;
            }
        });


        mStartButton = (Button) findViewById(R.id.startButton);
        mStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeState();

                mStartButton.setVisibility(View.GONE);
            }
        });


        mKeyController = new KeyController(getApplicationContext(), mUser);

        showAlertDialog("Vyskusaj si klavesnicu (par krat si napis heslo). Pre potvrdenie hesla stlac DONE vpravo dole, " +
                "v pripade ze sa pomylis takisto stlac DONE a zacni znovu. V pripade ze si pripraveny zacat experiment stlac START");

        startButtonDelay();
    }

    private void startButtonDelay() {
        boolean ret;

        if (mIsNumPassword) {
            ret = mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mStartButton.setVisibility(View.VISIBLE);
                }
            }, 2000);
        } else {
            ret = mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mStartButton.setVisibility(View.VISIBLE);
                }
            }, 3000);
        }
    }


    /**
     * Retrieve User data from Intent
     */
    private void setUpUser() {
        Bundle extras = getIntent().getExtras();
        mUser = new User();
        mUser.setName(extras.getString(Helper.USER_NAME));
        mUser.setId(extras.getLong(Helper.USER_ID));
        mUser.setPassword(extras.getString(Helper.USER_PASSWORD));
        mIsNumPassword = extras.getBoolean(Helper.NUM_PASSWORD);
        mIsIdentify = extras.getBoolean(Helper.IS_IDENTIFY);

        if (mIsIdentify) {
            //mPasswordEditText.setHint(mUser.getPassword());

            mPasswordHintTextView.setVisibility(View.VISIBLE);
            mPasswordHintTextView.setText(mUser.getPassword());
        }

        Log.d(TAG, mUser.toString());
        Log.d(TAG, "IsNumPassword " + mIsNumPassword);
        Log.d(TAG, "IsIdentify" + mIsIdentify);
    }

    /**
     * Find keyboard buttons and set up their onTouchListeners
     */
    private void setUpKeyboardButtons() {
        mQWERTY = "qwertyuiopasdfghjklzxcvbnm_";
        mQWERTYWithDot = "qwertyuiopasdfghjklzxcvbnm.";

        mKeySize = mQWERTY.length();

        mLetterButtons = new Button[mQWERTY.length()];
        mKeyObjects = new KeyObject[mQWERTY.length() + 10];
        mIsShiftPressed = false;


        for (int i = 0; i < mQWERTY.length(); i++) {
            int id = getResources().getIdentifier(mQWERTY.charAt(i) + "Button", "id", getPackageName());
            mLetterButtons[i] = (Button) findViewById(id);

            final int finalI = i;
            mLetterButtons[i].setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    // create KeyObject when button is pressed and assign pressed features
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        mKeyObjects[finalI] = new KeyObject();

                        Rect buttonShape = new Rect();
                        v.getLocalVisibleRect(buttonShape);

                        mKeyObjects[finalI].setPressedPressure(event.getPressure());
                        mKeyObjects[finalI].setPressedTime(event.getEventTime());

                        mKeyObjects[finalI].setCoordXPressed(event.getX());
                        mKeyObjects[finalI].setCoordYPressed(event.getY());

                        mKeyObjects[finalI].setCenterXCoord(buttonShape.exactCenterX());
                        mKeyObjects[finalI].setCenterYCoord(buttonShape.exactCenterY());
                    }

                    // assign release features, check if button is canceled
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        mKeyObjects[finalI].setReleasedPressure(event.getPressure());
                        mKeyObjects[finalI].setReleasedTime(event.getEventTime());

                        mKeyObjects[finalI].setCoordXReleased(event.getX());
                        mKeyObjects[finalI].setCoordYReleased(event.getY());

                        if (mIsShiftPressed) {
                            mKeyObjects[finalI].setKeyChar(Character.toUpperCase(mQWERTYWithDot.charAt(finalI)));
                        } else {
                            mKeyObjects[finalI].setKeyChar(mQWERTYWithDot.charAt(finalI));
                        }

                        Log.d(TAG, mKeyObjects[finalI].toString());


                        // add key to buffer and update EditText
                        if (mKeyBuffer.add(mKeyObjects[finalI]))
                            if (mIsShiftPressed) {
                                mPasswordEditText.append((mQWERTYWithDot.charAt(finalI) + "").toUpperCase());
                                switchToLowerCase();
                            } else {
                                mPasswordEditText.append(mQWERTYWithDot.charAt(finalI) + "");
                            }
                    }

                    return false;
                }
            });
        }

        mShiftButton = (Button) findViewById(R.id.shiftButton);
        mShiftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIsShiftPressed) {
                    switchToLowerCase();
                } else {
                    switchToUpperCase();
                }
            }
        });
    }

    /**
     * Show simple alert dialog
     *
     * @param message message to be shown in dialog
     */
    private void showAlertDialog(String message) {
        AlertDialog alertDialog = new AlertDialog.Builder(TrainActivity.this).create();
        alertDialog.setTitle("Password");
        alertDialog.setMessage(message);
        alertDialog.setCancelable(false);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        alertDialog.show();
    }

    /**
     * Change keyboard to uppercase letters
     */
    private void switchToUpperCase() {
        for (int i = 0; i < mLetterButtons.length; i++) {
            mLetterButtons[i].setText((mQWERTYWithDot.charAt(i) + "").toUpperCase());
        }
        mIsShiftPressed = true;

    }

    /**
     * Change keyboard to lowercase letters
     */
    private void switchToLowerCase() {
        for (int i = 0; i < mLetterButtons.length; i++) {
            mLetterButtons[i].setText((mQWERTYWithDot.charAt(i) + "").toLowerCase());
        }
        mIsShiftPressed = false;
    }

    public void setUpNumberButtons() {
        mNumbers = "1234567890";
        mNumberButtons = new Button[mNumbers.length()];

        if (mIsNumPassword) {
            mKeyObjects = new KeyObject[mNumbers.length()];
        }


        for (int i = mKeySize; i < mKeySize + mNumbers.length(); i++) {
            int id;

            if (mIsNumPassword) {
                id = getResources().getIdentifier("Button" + mNumbers.charAt(i), "id", getPackageName());
            } else {
                id = getResources().getIdentifier("ButtonL" + mNumbers.charAt(i - mKeySize), "id", getPackageName());
            }

            mNumberButtons[i - mKeySize] = (Button) findViewById(id);

            final int finalI = i;

            mNumberButtons[i - mKeySize].setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    // create KeyObject when button is pressed and assign pressed features
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        mKeyObjects[finalI] = new KeyObject();

                        Rect buttonShape = new Rect();
                        v.getLocalVisibleRect(buttonShape);

                        mKeyObjects[finalI].setPressedPressure(event.getPressure());
                        mKeyObjects[finalI].setPressedTime(event.getEventTime());

                        mKeyObjects[finalI].setCoordXPressed(event.getX());
                        mKeyObjects[finalI].setCoordYPressed(event.getY());

                        mKeyObjects[finalI].setCenterXCoord(buttonShape.exactCenterX());
                        mKeyObjects[finalI].setCenterYCoord(buttonShape.exactCenterY());
                    }

                    // assign release features, check if button is canceled
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        mKeyObjects[finalI].setReleasedPressure(event.getPressure());
                        mKeyObjects[finalI].setReleasedTime(event.getEventTime());

                        mKeyObjects[finalI].setCoordXReleased(event.getX());
                        mKeyObjects[finalI].setCoordYReleased(event.getY());

                        mKeyObjects[finalI].setKeyChar(mNumbers.charAt(finalI - mKeySize));

                        Log.d(TAG, mKeyObjects[finalI].toString());


                        // add key to buffer and update EditText
                        if (mKeyBuffer.add(mKeyObjects[finalI]))
                            mPasswordEditText.append(mNumbers.charAt(finalI - mKeySize) + "");
                    }


                    return false;
                }
            });
        }

    }

    /**
     * Change state of application training process
     */
    public void changeState() {
        mState++;

        switch (mState) {
            case 1:
            case 5:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG, "START");
//                        new Train(getApplicationContext(), mUser).trainIdentification(Helper.ALNUM_PASSWORD_CODE);

                        new Train(getApplication(), mUser).trainUser(Helper.ALNUM_PASSWORD_CODE);
                        Log.d(TAG, "STOP");
                    }
                });
                mTrainHintTextView.setText(R.string.training_hint_thumb);
                showAlertDialog("Pis heslo iba s palcom, opakuj 10 krat");
                break;
            case 2:
            case 6:
                mTrainHintTextView.setText(R.string.training_hint_two_thumbs);
                showAlertDialog("Pis heslo s dvoma palcami, opakuj 10 krat");
                break;
            case 3:
            case 7:
                mTrainHintTextView.setText(R.string.training_hint_index);
                showAlertDialog("Pis heslo iba s ukazovakom, opakuj 10 krat");
                break;
            case 4:
//                new Train(getApplicationContext(), mUser).trainUser();
                Intent intent = new Intent(TrainActivity.this, SecondStageActivity.class);
                intent.putExtra(Helper.USER_NAME, mUser.getName());
                intent.putExtra(Helper.IS_IDENTIFY, mIsIdentify);
                startActivity(intent);
                finish();
                return;
            case 8:
                finish();
        }

        if (mCounterTextView.getVisibility() == View.INVISIBLE) {
            mCounterTextView.setVisibility(View.VISIBLE);
            mCounterProgressBar.setVisibility(View.VISIBLE);
        }


        mKeyBuffer.clear();
        mPasswordEditText.setText("");
        mErrorsNum = 0;
        mCounter = 0;


        mCounterTextView.setText(Integer.toString(mCounter) + "/10");
        Log.d(TAG, "Counter " + mCounter);
    }

    /**
     * Update the state of a progressBar
     */
    private void updateProgressBar() {
        new Thread(new Runnable() {
            public void run() {

                // Update the progress bar
                mHandler.post(new Runnable() {
                    public void run() {
                        mCounterProgressBar.setProgress(mCounter);
                    }
                });

            }
        }).start();
    }
}
