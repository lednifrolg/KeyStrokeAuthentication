package com.filip.tomasovych.keystrokeauthentication.app.classification;

import android.util.Log;

import com.filip.tomasovych.keystrokeauthentication.app.model.KeyBuffer;
import com.filip.tomasovych.keystrokeauthentication.app.util.Helper;
import com.filip.tomasovych.keystrokeauthentication.app.util.KeyController;
import com.opencsv.CSVReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import weka.classifiers.Classifier;
import weka.classifiers.functions.SMO;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SerializationHelper;

/**
 * Created by nolofinwe on 01/03/17.
 */

public final class Evaluator {

    private final static String TAG = Evaluator.class.getSimpleName();

    public static int predictTypingStyle(FileInputStream modelInputStream, FileInputStream valuesInputStream, KeyBuffer keyBuffer, int passwordCode) {
        int style = -1;
        ArrayList<Double> entry = new ArrayList<>();
        List<String> labels = preprocessEntry(keyBuffer, valuesInputStream, entry, passwordCode);

        try {
            SMO SVM = (SMO) SerializationHelper.read(modelInputStream);

            ArrayList<Attribute> atts = new ArrayList<>();
            double[] vals;

            List<String> labelValues = new ArrayList<>();
            if (passwordCode == Helper.ALNUM_PASSWORD_CODE) {
                labelValues.add("1.0");
                labelValues.add("2.0");
                labelValues.add("3.0");
            } else if (passwordCode == Helper.NUM_PASSWORD_CODE) {
                labelValues.add("5.0");
                labelValues.add("6.0");
                labelValues.add("7.0");
            }

            for (String label : labels) {
                atts.add(new Attribute(label, false));
            }

            atts.add(new Attribute("state", labelValues));
            Instances dataset = new Instances("whatever", atts, 0);

            //Instances data = new Instances("MyRelation", atts, 0);

            vals = new double[dataset.numAttributes()];

            for (int i = 0; i < entry.size(); i++) {
                vals[i] = entry.get(i);
            }

            dataset.add(new DenseInstance(1.0, vals));
            dataset.setClassIndex(dataset.numAttributes() - 1);

            double pred = SVM.classifyInstance(dataset.instance(0));
//            System.out.println(dataset.classAttribute().value((int) pred));

            Log.d(TAG, "PRED : " + dataset.classAttribute().value((int) pred));
            style = Double.valueOf(dataset.classAttribute().value((int) pred)).intValue();
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }

        return style;
    }

    public static String predictUser(KeyBuffer keyBuffer, FileInputStream modelInputStream, FileInputStream valuesInputStream, List<String> labelValues, int passwordCode) {
        String user;

        ArrayList<Double> entry = new ArrayList<>();
        List<String> labels = preprocessEntry(keyBuffer, valuesInputStream, entry, passwordCode);

        try {
            SMO SVM = (SMO) SerializationHelper.read(modelInputStream);

            ArrayList<Attribute> atts = new ArrayList<>();
            double[] vals;


            for (String label : labels) {
                atts.add(new Attribute(label, false));
            }

            atts.add(new Attribute("state", labelValues));
            Instances dataset = new Instances("whatever", atts, 0);

            vals = new double[dataset.numAttributes()];

            for (int i = 0; i < entry.size(); i++) {
                vals[i] = entry.get(i);
            }

            dataset.add(new DenseInstance(1.0, vals));
            dataset.setClassIndex(dataset.numAttributes() - 1);

            double pred = SVM.classifyInstance(dataset.instance(0));
//            System.out.println(dataset.classAttribute().value((int) pred));

            Log.d(TAG, "PRED : " + dataset.classAttribute().value((int) pred));
            user = dataset.classAttribute().value((int) pred);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return user;
    }

    public static List<String> preprocessEntry(KeyBuffer keyBuffer, FileInputStream inputStream, ArrayList<Double> entry, int passwordCode) {
        List<String> list = KeyController.transformKeyBuffer(keyBuffer, passwordCode);
        ArrayList<Double> means = new ArrayList<>();
        ArrayList<Double> stDevs = new ArrayList<>();

        Evaluator.getStandardizationValues(means, stDevs, inputStream);


        for (String val : list) {
            entry.add(Double.valueOf(val));
        }

        Evaluator.standardize(entry, means, stDevs);

        return KeyController.getLabels(keyBuffer, passwordCode);
    }


    public static void getStandardizationValues(ArrayList<Double> means, ArrayList<Double> stDevs, FileInputStream inputStream) {
        CSVReader csvReader;

        try {
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

    public static void standardize(ArrayList<Double> entry, ArrayList<Double> means, ArrayList<Double> stDevs) {
        for (int i = 0; i < entry.size(); i++) {
            Double val = entry.get(i);

            if (stDevs.get(i) == 0)
                val = val - means.get(i);
            else
                val = (val - means.get(i)) / stDevs.get(i);
            
            entry.set(i, val);
        }
    }

    public static double manhattanDistance(ArrayList<Double> entry, ArrayList<Double> model) {
        double manDist = 0;

        if (entry.size() != model.size())
            return -9999;

        for (int i = 0; i < entry.size(); i++) {
            manDist += Math.abs(entry.get(i) - model.get(i));
        }

        return -manDist;
    }

}
