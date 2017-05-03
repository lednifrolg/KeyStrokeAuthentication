package com.filip.tomasovych.keystrokeauthentication.app.classification;

import android.content.Context;

import com.filip.tomasovych.keystrokeauthentication.app.database.DbHelper;
import com.filip.tomasovych.keystrokeauthentication.app.model.KeyBuffer;
import com.filip.tomasovych.keystrokeauthentication.app.model.User;
import com.filip.tomasovych.keystrokeauthentication.app.util.Helper;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by nolofinwe on 05/03/17.
 */

public class Identificator {

    private final static String TAG = Identificator.class.getSimpleName();

    private Context mContext;
    private DbHelper mDbHelper;

    public Identificator(Context context) {
        mContext = context;
        mDbHelper = DbHelper.getInstance(context);
    }


    public String predict(KeyBuffer keyBuffer, int passwordCode) {
        int style = getStyle(keyBuffer, passwordCode);
        String predictedUser = null;

        ArrayList<Double> entry = new ArrayList<>();
        String modelName = style + "ClassificationSVM";
        String valuesFileName = style + "ClassificationVALUES.csv";
        try {
            FileInputStream modelInputStream = mContext.openFileInput(modelName);
            FileInputStream valuesInputStream = mContext.openFileInput(valuesFileName);

            List<String> labelValues = mDbHelper.getUsersForIdentifiaction();

            predictedUser = Evaluator.predictUser(keyBuffer, modelInputStream, valuesInputStream, labelValues, passwordCode);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


        return predictedUser;
    }

    private int getStyle(KeyBuffer keyBuffer, int passwordCode) {
        try {
            FileInputStream model = null;
            FileInputStream vals = null;

            if (passwordCode == Helper.ALNUM_PASSWORD_CODE) {
                model = mContext.openFileInput("ClassificationSVMALNUM");
                vals = mContext.openFileInput("ClassificationVALUESALNUM.csv");
            } else if (passwordCode == Helper.NUM_PASSWORD_CODE) {
                model = mContext.openFileInput("ClassificationSVMNUM");
                vals = mContext.openFileInput("ClassificationVALUESNUM.csv");
            }

            return Evaluator.predictTypingStyle(model, vals, keyBuffer, passwordCode);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return -1;
    }
}
