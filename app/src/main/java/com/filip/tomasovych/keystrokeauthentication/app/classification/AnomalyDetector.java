package com.filip.tomasovych.keystrokeauthentication.app.classification;

import android.content.Context;
import android.util.Log;

import com.filip.tomasovych.keystrokeauthentication.app.database.DbHelper;
import com.filip.tomasovych.keystrokeauthentication.app.model.KeyBuffer;
import com.filip.tomasovych.keystrokeauthentication.app.model.User;
import com.filip.tomasovych.keystrokeauthentication.app.util.Helper;
import com.filip.tomasovych.keystrokeauthentication.app.util.KeyController;
import com.opencsv.CSVReader;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by nolofinwe on 26/02/17.
 */

public class AnomalyDetector {

    private final static String TAG = AnomalyDetector.class.getSimpleName();

    private User mUser;
    private Context mContext;
    private DbHelper mDbHelper;

    public AnomalyDetector(User user, Context context) {
        mContext = context;
        mUser = user;
        mDbHelper = DbHelper.getInstance(context);
    }


    public boolean evaluateEntry(KeyBuffer keyBuffer, int passwordCode) {
        int style = getStyle(keyBuffer, passwordCode);

        if (style == -1)
            return false;

        ArrayList<Double> entry = new ArrayList<>();
        ArrayList<Double> model = getManhattanModel(style);
        String fileName = style + mUser.getName() + "VALUES.csv";
        try {
            FileInputStream inputStream = mContext.openFileInput(fileName);
            List<String> labels = Evaluator.preprocessEntry(keyBuffer, inputStream, entry, passwordCode);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


        double mnDist = Evaluator.manhattanDistance(entry, model);

        double threshold = mDbHelper.getThresholdValue(mUser, style);

        Log.d(TAG, "Style : " + style);
        Log.d(TAG, "Dist : " + mnDist);
        Log.d(TAG, "Threshold : " + threshold);

        if (mnDist < threshold)
            return false;

        return true;
    }

    private int getStyle(KeyBuffer keyBuffer, int passwordCode) {
        try {
            FileInputStream model = null;
            FileInputStream vals = null;

            if (passwordCode == Helper.ALNUM_PASSWORD_CODE) {
                model = mContext.openFileInput(mUser.getName() + "SVMALNUM");
                vals = mContext.openFileInput(mUser.getName() + "VALUESALNUM.csv");
            } else if (passwordCode == Helper.NUM_PASSWORD_CODE) {
                model = mContext.openFileInput(mUser.getName() + "SVMNUM");
                vals = mContext.openFileInput(mUser.getName() + "VALUESNUM.csv");
            }

            return Evaluator.predictTypingStyle(model, vals, keyBuffer, passwordCode);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return -1;
    }


    private ArrayList<Double> getManhattanModel(int state) {
        String fileName = state + mUser.getName() + "MODEL.csv";
        ArrayList<Double> model = new ArrayList<>();
        CSVReader csvReader;

        try {
            FileInputStream inputStream = mContext.openFileInput(fileName);
            InputStreamReader reader = new InputStreamReader(inputStream);
            csvReader = new CSVReader(reader);

            String[] line;
            if ((line = csvReader.readNext()) != null) {
                for (int i = 0; i < line.length; i++) {
                    model.add(Double.valueOf(line[i]));
                }
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return model;
    }


}
