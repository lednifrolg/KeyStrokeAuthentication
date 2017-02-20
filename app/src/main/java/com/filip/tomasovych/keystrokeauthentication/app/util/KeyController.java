package com.filip.tomasovych.keystrokeauthentication.app.util;

import android.content.Context;

import com.filip.tomasovych.keystrokeauthentication.app.database.DbHelper;
import com.filip.tomasovych.keystrokeauthentication.app.model.KeyBuffer;
import com.filip.tomasovych.keystrokeauthentication.app.model.KeyObject;
import com.filip.tomasovych.keystrokeauthentication.app.model.User;

/**
 * Created by nolofinwe on 14.10.2016.
 */

public class KeyController {

    private final static String TAG = KeyController.class.getSimpleName();

    private DbHelper mDbHelper;
    private User mUser;


    public KeyController(Context context, User user) {
        mDbHelper = DbHelper.getInstance(context);
        mUser = user;
    }

    public boolean save(KeyBuffer keyBuffer, int state, int errors) {
        long experimentID;

        if (keyBuffer.getSize() > 0) {
            experimentID = mDbHelper.insertExperiment(mUser, state, errors);

            if (experimentID != -1) {
                for (KeyObject key : keyBuffer.getBuffer()) {
                    long result = mDbHelper.insertKeyData(experimentID, key);

                    if (result == -1)
                        return false;
                }
            }
        }

        keyBuffer.clear();

        return true;
    }

}
