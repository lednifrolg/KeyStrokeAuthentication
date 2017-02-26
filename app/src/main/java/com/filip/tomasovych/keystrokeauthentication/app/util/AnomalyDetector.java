package com.filip.tomasovych.keystrokeauthentication.app.util;

import android.content.Context;
import android.widget.Toast;

import com.filip.tomasovych.keystrokeauthentication.app.model.KeyBuffer;
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

    private String mName;
    private Context mContext;

    public AnomalyDetector(String name, Context context) {
        mContext = context;
        mName = name;
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

        CharSequence toast = "Dist : " + mnDist;
        Toast.makeText(mContext, toast, Toast.LENGTH_SHORT);

        return true;
    }

    private ArrayList<Double> getManhattanModel(int state) {
        String fileName = state + mName + "MODEL.csv";
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
        String fileName = state + mName + "VALUES.csv";

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

    private double manhattanDistance(ArrayList<Double> entry, ArrayList<Double> model) {
        double manDist = 0;

        if (entry.size() != model.size())
            return -9999;

        for (int i = 0; i < entry.size(); i++) {
            manDist += Math.abs(entry.get(i) - model.get(i));
        }

        return manDist;
    }

    private void standardize(ArrayList<Double> entry, ArrayList<Double> means, ArrayList<Double> stDevs) {
        for (int i = 0; i < entry.size(); i++) {
            Double val = entry.get(i);
            val = (val - means.get(i)) / stDevs.get(i);
            entry.set(i, val);
        }
    }
}
