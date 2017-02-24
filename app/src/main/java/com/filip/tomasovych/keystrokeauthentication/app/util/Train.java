package com.filip.tomasovych.keystrokeauthentication.app.util;

import android.content.Context;

import com.filip.tomasovych.keystrokeauthentication.app.database.DbHelper;
import com.filip.tomasovych.keystrokeauthentication.app.model.User;
import com.opencsv.CSVReader;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

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
        CSVReader csvReader;
        try {
            FileInputStream inputStream = mContext.openFileInput("csvtest.csv");
            InputStreamReader reader = new InputStreamReader(inputStream);
            csvReader = new CSVReader(reader);

            ArrayList<ArrayList<Double>> columns = new ArrayList<>();

            String[] line;
            while ((line = csvReader.readNext()) != null) {
                if (line[0].equals("xCoordPress0")) {
                    for (String col : line) {
                        columns.add(new ArrayList<Double>());
                    }
                    continue;
                }

                for (int i = 0; i < line.length; i++) {
                    columns.get(i).add(Double.valueOf(line[i]));
                }
            }

            double sd = stdDev(columns.get(0));
            double mean = mean(columns.get(0));
            csvReader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


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

    private double stdDev(ArrayList<Double> column) {
        double mean = mean(column);
        double temp = 0.0;

        for (int i = 0; i < column.size(); i++) {
            double val = column.get(i);

            double squrDiffToMean = Math.pow(val - mean, 2);

            temp += squrDiffToMean;
        }

        double meanOfDiffs = temp / (double) (column.size());

        return Math.sqrt(meanOfDiffs);
    }
}
