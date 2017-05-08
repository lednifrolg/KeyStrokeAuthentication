package com.filip.tomasovych.keystrokeauthentication.app.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.filip.tomasovych.keystrokeauthentication.R;
import com.filip.tomasovych.keystrokeauthentication.app.database.DbHelper;
import com.filip.tomasovych.keystrokeauthentication.app.model.User;
import com.filip.tomasovych.keystrokeauthentication.app.util.Helper;

/**
 * A login screen that offers login via email/password.
 */
public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = RegisterActivity.class.getSimpleName();

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private RadioGroup mRadioGroup;

    private DbHelper mDbHelper;
    private User mUser;

    private boolean mIsExperiment;
    private boolean mIsIdentify;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Bundle extras = getIntent().getExtras();
        mIsExperiment = extras.getBoolean(Helper.IS_EXPERIMENT);
        mIsIdentify = extras.getBoolean(Helper.IS_IDENTIFY);

        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);


        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });


        mRadioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                Log.d(TAG, "CheckedId : " + checkedId);
                switch (checkedId) {
//                    case R.id.authRadioButton:
//                        Log.d(TAG, "authRadioButton");
//                        authRadioButtonClicked();
//                        break;
                    case R.id.identifyRadioButton:
                        Log.d(TAG, "identifyRadioButton");
                        identifyRadioButtonClicked();
                        break;
                    case R.id.identifyNumRadioButton:
                        Log.d(TAG, "identifyNumRadioButton");
                        identifyNumericRadioButtonClicked();
                        break;
                    default:
                        Log.d(TAG, "Radiobutton : something else");
                }
            }
        });

        if (!mIsIdentify) {
            mRadioGroup.setVisibility(View.GONE);
        } else {
            identifyRadioButtonClicked();
        }

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        mDbHelper = DbHelper.getInstance(getApplicationContext());

        if (mIsExperiment) {
            showStartupDialog();
        }
    }

    /**
     * set static password for a user, disable edit on password field
     */
    private void identifyRadioButtonClicked() {
        mPasswordView.setText(Helper.STATIC_PASSWORD);
        mPasswordView.setFocusable(false);
        mPasswordView.setFocusableInTouchMode(false);
        mPasswordView.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        mIsIdentify = true;
    }

    /**
     * set static password for a user, disable edit on password field
     */
    private void identifyNumericRadioButtonClicked() {
        mPasswordView.setText(Helper.STATIC_NUM_PASSWORD);
        mPasswordView.setFocusable(false);
        mPasswordView.setFocusableInTouchMode(false);
        mPasswordView.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        mIsIdentify = true;
    }

    /**
     * reset password filed, enable edit
     */
    private void authRadioButtonClicked() {
        mPasswordView.setText("");
        mPasswordView.setFocusable(true);
        mPasswordView.setFocusableInTouchMode(true);
        mPasswordView.getInputType();
        mPasswordView.setInputType(129);
        mIsIdentify = false;
    }

    private void showStartupDialog() {
        AlertDialog alertDialog = new AlertDialog.Builder(RegisterActivity.this).create();
        alertDialog.setTitle("Experiment");
        alertDialog.setCancelable(false);
        alertDialog.setMessage("Experiment spociva z pisania hesla. " +
                "Ako username zadaj AIS meno (xPriezvisko) a ako heslo si zvol heslo ktore si pouzival napriklad pred rokom, " +
                "alebo nieco co vies dostatocne rychlo pisat");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        alertDialog.show();
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password) || !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserLoginTask(email, password);
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        // return email.contains("@");
        return email.length() > 4;
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return (password.length() > 5);
//        return (password.length() > 5 && password.matches(".*[a-zA-Z]+.*"));
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            try {
                // Simulate network access.
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                return false;
            }


            mUser = mDbHelper.getUser(mEmail);

            if (mUser == null) {
                mUser = new User(mEmail, mPassword);

                long id = mDbHelper.insertUser(mUser);

                if (id == -1)
                    return false;

                mUser.setId(id);
            } else {
                //Toast.makeText(getBaseContext(), "User already exists", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "User already exists");
                return false;
            }

            // TODO: register the new account here.
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                Intent intent = new Intent(RegisterActivity.this, TrainActivity.class);

                intent.putExtra(Helper.USER_NAME, mUser.getName());
                intent.putExtra(Helper.USER_ID, mUser.getId());
                intent.putExtra(Helper.USER_PASSWORD, mUser.getPassword());
                intent.putExtra(Helper.IS_IDENTIFY, mIsIdentify);
                intent.putExtra(Helper.IS_EXPERIMENT, mIsExperiment);

                if (Helper.isNumeric(mPassword)) {
                    intent.putExtra(Helper.NUM_PASSWORD, true);
                } else {
                    intent.putExtra(Helper.NUM_PASSWORD, false);
                }

                startActivity(intent);
                mUser = null;
                mEmailView.setText("");
                mPasswordView.setText("");

                finish();
            } else {
                mPasswordView.setError(getString(R.string.error_user_exists));
                mPasswordView.requestFocus();
            }
        }


        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}

