package com.filip.tomasovych.keystrokeauthentication.app.classification;

import android.content.Context;
import android.util.Log;

import com.filip.tomasovych.keystrokeauthentication.app.database.DbHelper;
import com.filip.tomasovych.keystrokeauthentication.app.model.User;
import com.filip.tomasovych.keystrokeauthentication.app.util.CSVWriter;
import com.filip.tomasovych.keystrokeauthentication.app.util.Helper;
import com.opencsv.CSVReader;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

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
//        mDbHelper.deleteUser(user);
    }


    /**
     * Crete and train userl model for authentication
     *
     * @param passwordCode NUM or ALNUM password  code
     * @return
     */
    public boolean trainUser(int passwordCode) {
        ArrayList<Integer> states = checkStates(mUser.getName(), passwordCode);
        FileInputStream userValuesInputStream;
        String typingFileName = mUser.getName() + "TYPING.csv";
        String userValuesFileName = null;

        if (passwordCode == Helper.NUM_PASSWORD_CODE)
            userValuesFileName = mUser.getName() + "NUM.csv";
        else if (passwordCode == Helper.ALNUM_PASSWORD_CODE)
            userValuesFileName = mUser.getName() + "ALNUM.csv";


        try {
            userValuesInputStream = mContext.openFileInput(userValuesFileName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }

        for (int state : states) {
            createStateModel(state, mUser.getName());
        }

        createTypingModel(userValuesInputStream, typingFileName, mUser.getName(), passwordCode);

        return true;
    }

    /**
     * Create and train classification model for identification
     *
     * @param passwordCode NUM or ALNUM password  code
     * @return
     */
    public boolean trainIdentification(int passwordCode) {
        ArrayList<Integer> states = checkStates("Classification", passwordCode);
        FileInputStream usersTypingStyleInputStream;
        String typingFileName = "ClassificationTYPING.csv";
        String usersTypingStyleFileName = "Classification.csv";


        if (passwordCode == Helper.NUM_PASSWORD_CODE)
            usersTypingStyleFileName = "ClassificationNUM.csv";
        else if (passwordCode == Helper.ALNUM_PASSWORD_CODE)
            usersTypingStyleFileName = "ClassificationALNUM.csv";


        try {
            usersTypingStyleInputStream = mContext.openFileInput(usersTypingStyleFileName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }

        for (int state : states) {
            createIdentificationStateModel(state, "Classification");
        }

        createTypingModel(usersTypingStyleInputStream, typingFileName, "Classification", passwordCode);

        return true;
    }

    /**
     * Crate identification model for specific typing style
     *
     * @param state identificator of typing style
     * @param name  name of the appropriate file with values from typing
     * @return
     */
    private boolean createIdentificationStateModel(int state, String name) {
        CSVReader csvReader;
        try {
            FileInputStream inputStream = mContext.openFileInput(state + name + ".csv");
            InputStreamReader reader = new InputStreamReader(inputStream);
            csvReader = new CSVReader(reader);

            ArrayList<ArrayList<Double>> columns = new ArrayList<>();
            ArrayList<String> labels = new ArrayList<>();
            ArrayList<String> users = new ArrayList<>();

            String[] line;
            while ((line = csvReader.readNext()) != null) {

                if (line[0].equals("xCoordPress0")) {
                    for (int i = 0; i < line.length - 1; i++) {
                        columns.add(new ArrayList<Double>());
                        labels.add(line[i]);
                    }
                    continue;
                }

                for (int i = 0; i < line.length - 1; i++) {
                    columns.get(i).add(Double.valueOf(line[i]));
                }

                users.add(line[line.length - 1]);
            }

            csvReader.close();
            standardize(columns, state, name);

            createIdentificationModel(columns, state, "Classification", labels, users);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    /**
     * Create authentication model of a user for specific typing style
     *
     * @param state state identificator of typing style
     * @param name  name of the user
     * @return
     */
    private boolean createStateModel(int state, String name) {
        CSVReader csvReader;
        try {
            FileInputStream inputStream = mContext.openFileInput(state + name + ".csv");
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
            standardize(columns, state, name);

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


    /**
     * Get all recorded typing styles for given file
     *
     * @param fileName     name of the file with recorded typing
     * @param passwordCode state identificator of typing style
     * @return List of available typing styles
     */
    private ArrayList<Integer> checkStates(String fileName, int passwordCode) {
        ArrayList<Integer> states = new ArrayList<>();

        if (passwordCode == Helper.ALNUM_PASSWORD_CODE) {
            for (int i = 1; i < 4; i++) {
                if (mContext.getFileStreamPath(i + fileName + ".csv").exists()) {
                    states.add(i);
                }
            }
        }

        if (passwordCode == Helper.NUM_PASSWORD_CODE) {
            for (int i = 5; i < 8; i++) {
                if (mContext.getFileStreamPath(i + fileName + ".csv").exists()) {
                    states.add(i);
                }
            }
        }

        return states;
    }

    /**
     * Create and save Manhattan model used for authentication
     *
     * @param columns list of columns (features)
     * @param state   identificator of typing style
     * @return
     */
    private boolean createManhattanModel(ArrayList<ArrayList<Double>> columns, int state) {
        String fileName = state + mUser.getName() + "MODEL.csv";
        ArrayList<Double> model = new ArrayList<>();

        for (ArrayList<Double> col : columns) {
            model.add(mean(col));
        }

        write(model, fileName, Context.MODE_PRIVATE);

        createDistanceThreshold(columns, state, model);

        return true;
    }

    /**
     * Calculate and save appropriate threshold value for specific authentication model
     *
     * @param columns list of columns (features)
     * @param state   state identificator of typing style
     * @param model   created model for authentication
     */
    private void createDistanceThreshold(ArrayList<ArrayList<Double>> columns, int state, ArrayList<Double> model) {
        int r = columns.get(0).size();
        ArrayList<ArrayList<Double>> rows = new ArrayList<>(r);
        ArrayList<Double> distances = new ArrayList<>();
        double threshold;

        for (int i = 0; i < r; i++) {
            rows.add(new ArrayList<Double>());
            for (ArrayList<Double> col : columns) {
                rows.get(i).add(col.get(i));
            }
        }

        double min = 0;

        for (ArrayList<Double> row : rows) {
            double dist = Evaluator.manhattanDistance(row, model);
            distances.add(dist);

            if (dist < min)
                min = dist;
        }

        double mean = mean(distances);
        double std = stDev(distances, mean);

        threshold = min - Math.abs(mean / 2) + std - 7;

        mDbHelper.setThresholdValue(state, mUser, threshold);
    }

    /**
     * Calculate mean of a column
     *
     * @param column
     * @return mean value
     */
    private double mean(ArrayList<Double> column) {
        double total = 0;

        for (int i = 0; i < column.size(); i++) {
            double currentNum = column.get(i);
            total += currentNum;
        }

        return total / (double) column.size();
    }

    /**
     * Calculate standard deviation of a column
     *
     * @param column
     * @param mean   mean of a given column
     * @return standard deviation of column
     */
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

    /**
     * Standardize list of columns with Z-SCORE standardization
     *
     * @param columns list of columns (features)
     * @param state   state identificator of typing style
     * @param name    name of the user OR classifcation file
     */
    private void standardize(ArrayList<ArrayList<Double>> columns, int state, String name) {
        ArrayList<Double> stDevs = new ArrayList<>();
        ArrayList<Double> means = new ArrayList<>();
        String fileName = state + name + "VALUES.csv";

        for (ArrayList<Double> col : columns) {
            double mean = mean(col);
            double stDev = stDev(col, mean);
            means.add(mean);
            stDevs.add(stDev);

            for (int i = 0; i < col.size(); i++) {
                Double val = col.get(i);
                val = (val - mean) / stDev;

                if (stDev == 0)
                    val = 0.0;

                col.set(i, val);
            }
        }

        write(means, fileName, Context.MODE_PRIVATE);
        write(stDevs, fileName, Context.MODE_APPEND);
    }

    /**
     * Standardize list of columns with Z-SCORE standardization
     *
     * @param columns      list of columns (features)
     * @param name         name of the user OR classifcation file
     * @param passwordCode state identificator of typing style
     */
    private void standardize(ArrayList<ArrayList<Double>> columns, String name, int passwordCode) {
        ArrayList<Double> stDevs = new ArrayList<>();
        ArrayList<Double> means = new ArrayList<>();
        String fileName = null;

        if (passwordCode == Helper.ALNUM_PASSWORD_CODE) {
            fileName = name + "VALUESALNUM.csv";
        } else if (passwordCode == Helper.NUM_PASSWORD_CODE) {
            fileName = name + "VALUESNUM.csv";
        }

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

        write(means, fileName, Context.MODE_PRIVATE);
        write(stDevs, fileName, Context.MODE_APPEND);
    }

    /**
     * Write row into a CSV file
     *
     * @param row      list of values
     * @param fileName name of the CSV file
     * @param mode     APPEND | PRIVATE etc...
     * @param <T>      Type of values - DOUBLE, INTEGER...
     * @return
     */
    private <T> boolean write(ArrayList<T> row, String fileName, int mode) {
        try {
            FileOutputStream outputStream = mContext.openFileOutput(fileName, mode);

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

    /**
     * Create and save ty
     *
     * @param inputStream
     * @param typingFileName
     * @param name
     * @param passwordCode
     */
    private void createTypingModel(FileInputStream inputStream, String typingFileName, String name, int passwordCode) {
        CSVReader csvReader;
        try {
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

            standardize(columns, name, passwordCode);
            ArrayList<ArrayList<Double>> rows = saveTypingModel(columns, labels, typingFileName);
            trainTypingModel(labels, rows, name, passwordCode);

            csvReader.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    private ArrayList<ArrayList<Double>> saveTypingModel(ArrayList<ArrayList<Double>> columns, ArrayList<String> labels, String fileName) {
        ArrayList<ArrayList<Double>> rowsList = new ArrayList<>();
        int rows = columns.get(0).size();

        write(labels, fileName, Context.MODE_PRIVATE);

        for (int i = 0; i < rows; i++) {
            ArrayList<Double> row = new ArrayList<>();
            for (ArrayList<Double> col : columns) {
                row.add(col.get(i));
            }

            write(row, fileName, Context.MODE_APPEND);
            rowsList.add(row);
        }

        return rowsList;
    }


    private void trainTypingModel(ArrayList<String> labels, ArrayList<ArrayList<Double>> rows, String name, int passwordCode) {
        ArrayList<Attribute> atts = new ArrayList<>();
        double[] vals;
        String modelName = null;

        List<String> labelValues = new ArrayList<>();

        if (passwordCode == Helper.ALNUM_PASSWORD_CODE) {
            labelValues.add("1.0");
            labelValues.add("2.0");
            labelValues.add("3.0");

            modelName = name + "SVMALNUM";
        } else if (passwordCode == Helper.NUM_PASSWORD_CODE) {
            labelValues.add("5.0");
            labelValues.add("6.0");
            labelValues.add("7.0");

            modelName = name + "SVMNUM";
        }


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

                if (i == row.size() - 1)
                    vals[i] = labelValues.indexOf(String.valueOf((double) row.get(i)));
            }

            data.add(new DenseInstance(1.0, vals));
        }

        data.setClassIndex(data.numAttributes() - 1);
        SMO svm = new SMO();

        try {
            //svm.setOptions(weka.core.Utils.splitOptions("-S 0 -K 0 -D 3 -G 0.0 -R 0.0 -N 0.5 -M 40.0 -C 1.0 -E 0.001 -P 0.1"));
            svm.buildClassifier(data);

            FileOutputStream outputStream = mContext.openFileOutput(modelName, Context.MODE_APPEND);
            SerializationHelper.write(outputStream, svm);
        } catch (Exception e) {
            e.printStackTrace();
        }


        Log.d(TAG, "trainTypingModel");
    }

    private void createIdentificationModel(ArrayList<ArrayList<Double>> columns, int state, String name, ArrayList<String> labels, ArrayList<String> users) {
        ArrayList<ArrayList<Double>> rows = transformColumnsToRows(columns);

        ArrayList<Attribute> atts = new ArrayList<>();
        double[] vals;
        List<String> labelValues;

        if (state < 4) {
            labelValues = mDbHelper.getUsersForIdentifiaction();
        } else {
            labelValues = mDbHelper.getUsersForIdentifiactionNUM();
        }


        for (String label : labels) {
            atts.add(new Attribute(label, false));
        }

        atts.add(new Attribute("name", labelValues));

        Instances data = new Instances("MyRelation", atts, 0);

        int iter = 0;
        for (ArrayList<Double> row : rows) {
            vals = new double[data.numAttributes()];

            for (int i = 0; i < row.size(); i++) {
                vals[i] = row.get(i);
            }

            vals[row.size()] = labelValues.indexOf(users.get(iter));

            data.add(new DenseInstance(1.0, vals));

            iter++;
        }

        data.setClassIndex(data.numAttributes() - 1);
        SMO svm = new SMO();

        try {
            //svm.setOptions(weka.core.Utils.splitOptions("-S 0 -K 0 -D 3 -G 0.0 -R 0.0 -N 0.5 -M 40.0 -C 1.0 -E 0.001 -P 0.1"));
            svm.buildClassifier(data);

            String modelName = null;
            FileOutputStream outputStream = mContext.openFileOutput(state + name + "SVM", Context.MODE_APPEND);
            SerializationHelper.write(outputStream, svm);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private ArrayList<ArrayList<Double>> transformColumnsToRows(ArrayList<ArrayList<Double>> columns) {
        ArrayList<ArrayList<Double>> rowsList = new ArrayList<>();
        int rows = columns.get(0).size();

        for (int i = 0; i < rows; i++) {
            ArrayList<Double> row = new ArrayList<>();
            for (ArrayList<Double> col : columns) {
                row.add(col.get(i));
            }

            rowsList.add(row);
        }

        return rowsList;
    }
}
