package com.filip.tomasovych.keystrokeauthentication.app.util;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.filip.tomasovych.keystrokeauthentication.app.database.DbHelper;
import com.filip.tomasovych.keystrokeauthentication.app.model.KeyBuffer;
import com.filip.tomasovych.keystrokeauthentication.app.model.User;
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


    public boolean evaluateEntry(KeyBuffer keyBuffer, int state) {
        List<String> list = KeyController.transformKeyBuffer(keyBuffer);
        ArrayList<Double> means = new ArrayList<>();
        ArrayList<Double> stDevs = new ArrayList<>();
        ArrayList<Double> entry = new ArrayList<>();
        ArrayList<Double> model = getManhattanModel(state);
        getNormalValues(means, stDevs, state);

        for (String val : list) {
            entry.add(Double.valueOf(val));
        }

        standardize(entry, means, stDevs);
        double mnDist = manhattanDistance(entry, model);

        double threshold = mDbHelper.getThresholdValue(mUser, state);

        Log.d(TAG, "Dist : " + mnDist);
        Log.d(TAG, "Threshold : " + threshold);

        if (mnDist < threshold)
            return false;

        return true;
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

    private void getNormalValues(ArrayList<Double> means, ArrayList<Double> stDevs, int state) {
        CSVReader csvReader;
        String fileName = state + mUser.getName() + "VALUES.csv";

        try {
            FileInputStream inputStream = mContext.openFileInput(fileName);
            InputStreamReader reader = new InputStreamReader(inputStream);
            csvReader = new CSVReader(reader);

            String[] line;
            if ((line = csvReader.readNext()) != null) {
                for (int i = 0; i < line.length; i++) {
                    means.add(Double.valueOf(line[i]));
                }
            }

            if ((line = csvReader.readNext()) != null) {
                for (int i = 0; i < line.length; i++) {
                    stDevs.add(Double.valueOf(line[i]));
                }
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static double manhattanDistance(ArrayList<Double> entry, ArrayList<Double> model) {
        double manDist = 0;

        if (entry.size() != model.size())
            return -9999;

        for (int i = 0; i < entry.size(); i++) {
            manDist += Math.abs(entry.get(i) - model.get(i));
        }

        return - manDist;
    }

    private void standardize(ArrayList<Double> entry, ArrayList<Double> means, ArrayList<Double> stDevs) {
        for (int i = 0; i < entry.size(); i++) {
            Double val = entry.get(i);
            val = (val - means.get(i)) / stDevs.get(i);
            entry.set(i, val);
        }
    }
}
