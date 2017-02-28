package com.filip.tomasovych.keystrokeauthentication.app.util;

import android.content.Context;
import android.util.Log;

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

import weka.classifiers.functions.LibSVM;
import weka.classifiers.functions.SMO;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;
import weka.core.SerializationHelper;

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

        createUserTypingModel();

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

//        createDistanceThreshold(columns, state, model);

        return true;
    }

    private void createDistanceThreshold(ArrayList<ArrayList<Double>> columns, int state, ArrayList<Double> model) {
        int r = columns.get(0).size();
        ArrayList<ArrayList<Double>> rows = new ArrayList<>(r);
        ArrayList<Double> distances = new ArrayList<>();
        double threshold = 0;

        for (int i = 0; i < r; i++) {
            rows.add(new ArrayList<Double>());
            for (ArrayList<Double> col : columns) {
                rows.get(i).add(col.get(i));
            }
        }

        double min = 0;

        for (ArrayList<Double> row : rows) {
            double dist = AnomalyDetector.manhattanDistance(row, model);
            distances.add(dist);

            if (dist < min)
                min = dist;
        }

        double mean = mean(distances);
        double std = stDev(distances, mean);

        threshold = min - Math.abs(mean / 2) + std;

        mDbHelper.setThresholdValue(state, mUser, threshold);

        return;
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

    private void standardize(ArrayList<ArrayList<Double>> columns, ArrayList<String> labels) {
        ArrayList<Double> stDevs = new ArrayList<>();
        ArrayList<Double> means = new ArrayList<>();
        String fileName = mUser.getName() + "VALUES.csv";
        int colNum = columns.size() - 1;
        int count = 0;

        for (ArrayList<Double> col : columns) {
            if (count == colNum)
                continue;

            double mean = mean(col);
            double stDev = stDev(col, mean);
            means.add(mean);
            stDevs.add(stDev);

            for (int i = 0; i < col.size(); i++) {
                Double val = col.get(i);
                val = (val - mean) / stDev;
                col.set(i, val);
            }

            count++;
        }

        write(means, fileName);
        write(stDevs, fileName);
    }

    private <T> boolean write(ArrayList<T> row, String fileName) {
        try {
            FileOutputStream outputStream = mContext.openFileOutput(fileName, Context.MODE_PRIVATE | Context.MODE_APPEND);

            List<String> list = new ArrayList<>();

            for (T val : row) {
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


    private void createUserTypingModel() {
        CSVReader csvReader;
        try {
            FileInputStream inputStream = mContext.openFileInput(mUser.getName() + ".csv");
            InputStreamReader reader = new InputStreamReader(inputStream);
            csvReader = new CSVReader(reader);

            ArrayList<ArrayList<Double>> columns = new ArrayList<>();
            ArrayList<String> labels = new ArrayList<>();

            String[] line;
            while ((line = csvReader.readNext()) != null) {
                if (line[0].equals("xCoordPress0")) {
                    for (int i = 0; i < line.length; i++) {
                        columns.add(new ArrayList<Double>());
                        labels.add(line[i]);
                    }

                    continue;
                }

                for (int i = 0; i < line.length; i++) {
                    columns.get(i).add(Double.valueOf(line[i]));
                }
            }

            standardize(columns, labels);
            ArrayList<ArrayList<Double>> rows = createTypingModel(columns, labels);
            trainTypingModel(labels, rows);

            csvReader.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private ArrayList<ArrayList<Double>> createTypingModel(ArrayList<ArrayList<Double>> columns, ArrayList<String> labels) {
        ArrayList<ArrayList<Double>> rowsList = new ArrayList<>();
        String fileName = mUser.getName() + "TYPING.csv";
        int rows = columns.get(0).size();

        write(labels, fileName);

        for (int i = 0; i < rows; i++) {
            ArrayList<Double> row = new ArrayList<>();
            for (ArrayList<Double> col : columns) {
                row.add(col.get(i));
            }

            write(row, fileName);
            rowsList.add(row);
        }

        return rowsList;
    }

    private void trainTypingModel(ArrayList<String> labels, ArrayList<ArrayList<Double>> rows) {
        ArrayList<Attribute> atts = new ArrayList<>();
        double[] vals;

        List<String> labelValues = new ArrayList<>();
        labelValues.add("1.0");
        labelValues.add("2.0");
        labelValues.add("3.0");
        labelValues.add("4.0");

        for (String label : labels) {
            if (label.equals("state"))
                atts.add(new Attribute(label, labelValues));
            else
                atts.add(new Attribute(label, false));
        }

        Instances data = new Instances("MyRelation", atts, 0);

        for (ArrayList<Double> row : rows) {
            vals = new double[data.numAttributes()];

            for (int i = 0; i < row.size(); i++) {
                vals[i] = row.get(i);
            }

            data.add(new DenseInstance(1.0, vals));
        }

        //Log.d(TAG, data.toString());

        data.setClassIndex(data.numAttributes() - 1);
        SMO svm = new SMO();

        try {
            //svm.setOptions(weka.core.Utils.splitOptions("-S 0 -K 0 -D 3 -G 0.0 -R 0.0 -N 0.5 -M 40.0 -C 1.0 -E 0.001 -P 0.1"));
            svm.buildClassifier(data);

//            SerializationHelper.write("model", svm);
        } catch (Exception e) {
            e.printStackTrace();
        }


        Log.d(TAG, "trainTypingModel");
    }
}
