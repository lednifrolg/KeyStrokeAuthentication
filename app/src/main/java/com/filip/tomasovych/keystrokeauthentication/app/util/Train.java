package com.filip.tomasovych.keystrokeauthentication.app.util;

import android.content.Context;

import com.filip.tomasovych.keystrokeauthentication.app.database.DbHelper;
import com.filip.tomasovych.keystrokeauthentication.app.model.User;
import com.opencsv.CSVReader;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by nolofinwe on 24/02/17.
 */

public class Train {

    private final static String TAG = Train.class.getSimpleName();

    private DbHelper mDbHelper;
    private Context mContext;
    private User mUser;

    public Train(Context context, User user) {
        mDbHelper = DbHelper.getInstance(context);
        mContext = context;
        mUser = user;
    }


    public boolean trainUser() {
        ArrayList<Integer> states = checkStates();

        for (int state : states) {
            createStateModel(state);
        }

        return true;
    }

    private boolean createStateModel(int state) {
        CSVReader csvReader;
        try {
            FileInputStream inputStream = mContext.openFileInput(state + mUser.getName() + ".csv");
            InputStreamReader reader = new InputStreamReader(inputStream);
            csvReader = new CSVReader(reader);

            ArrayList<ArrayList<Double>> columns = new ArrayList<>();

            String[] line;
            while ((line = csvReader.readNext()) != null) {
                if (line[0].equals("xCoordPress0")) {
                    for (int i = 0; i < line.length; i++) {
                        columns.add(new ArrayList<Double>());
                    }
                    continue;
                }

                for (int i = 0; i < line.length; i++) {
                    columns.get(i).add(Double.valueOf(line[i]));
                }
            }

            csvReader.close();
            standardize(columns, state);

            createManhattanModel(columns, state);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    private ArrayList<Integer> checkStates() {
        ArrayList<Integer> states = new ArrayList<>();
        for (int i = 1; i < 4; i++) {
            if (mContext.getFileStreamPath(i + mUser.getName() + ".csv").exists()) {
                states.add(i);
            }
        }

        for (int i = 5; i < 8; i++) {
            if (mContext.getFileStreamPath(i + mUser.getName() + ".csv").exists()) {
                states.add(i);
            }
        }

        return states;
    }


    private boolean createManhattanModel(ArrayList<ArrayList<Double>> columns, int state) {
        String fileName = state + mUser.getName() + "MODEL.csv";
        ArrayList<Double> model = new ArrayList<>();

        for (ArrayList<Double> col : columns) {
            model.add(mean(col));
        }

        write(model, fileName);

        return true;
    }


    private double mean(ArrayList<Double> column) {
        double total = 0;

        for (int i = 0; i < column.size(); i++) {
            double currentNum = column.get(i);
            total += currentNum;
        }

        return total / (double) column.size();
    }


    private double stDev(ArrayList<Double> column, double mean) {
        double temp = 0.0;

        for (int i = 0; i < column.size(); i++) {
            double val = column.get(i);

            double squrDiffToMean = Math.pow(val - mean, 2);

            temp += squrDiffToMean;
        }

        double meanOfDiffs = temp / (double) (column.size());

        return Math.sqrt(meanOfDiffs);
    }


    private void standardize(ArrayList<ArrayList<Double>> columns, int state) {
        ArrayList<Double> stDevs = new ArrayList<>();
        ArrayList<Double> means = new ArrayList<>();
        String fileName = state + mUser.getName() + "VALUES.csv";

        for (ArrayList<Double> col : columns) {
            double mean = mean(col);
            double stDev = stDev(col, mean);
            means.add(mean);
            stDevs.add(stDev);

            for (int i = 0; i < col.size(); i++) {
                Double val = col.get(i);
                val = (val - mean) / stDev;
                col.set(i, val);
            }
        }

        write(means, fileName);
        write(stDevs, fileName);
    }

    private boolean write(ArrayList<Double> row, String fileName) {
        try {
            FileOutputStream outputStream = mContext.openFileOutput(fileName, Context.MODE_PRIVATE | Context.MODE_APPEND);

            List<String> list = new ArrayList<>();

            for (Double val : row) {
                list.add(String.valueOf(val));
            }

            CSVWriter.writeLine(outputStream, list);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }
}
