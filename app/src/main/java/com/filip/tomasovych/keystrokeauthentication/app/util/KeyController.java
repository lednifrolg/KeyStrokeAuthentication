package com.filip.tomasovych.keystrokeauthentication.app.util;

import android.content.Context;

import com.filip.tomasovych.keystrokeauthentication.app.database.DbHelper;
import com.filip.tomasovych.keystrokeauthentication.app.model.KeyBuffer;
import com.filip.tomasovych.keystrokeauthentication.app.model.KeyObject;
import com.filip.tomasovych.keystrokeauthentication.app.model.User;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by nolofinwe on 14.10.2016.
 */

public class KeyController {

    private final static String TAG = KeyController.class.getSimpleName();

    private DbHelper mDbHelper;
    private Context mContext;
    private User mUser;


    public KeyController(Context context, User user) {
        mDbHelper = DbHelper.getInstance(context);
        mContext = context;
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

        if (errors == 0)
            saveCSV(keyBuffer, state);

        keyBuffer.clear();


        return true;
    }

    private boolean saveCSV(KeyBuffer keyBuffer, int state) {
        String csvFile = state + mUser.getName() + ".csv";
        String typingFile = mUser.getName() + ".csv";

        int bufferSize = keyBuffer.getSize();

        List<String> columns = new ArrayList<>();

        for (int i = 0; i < bufferSize; i++) {
            columns.add("xCoordPress" + i);
            columns.add("yCoordPress" + i);
            columns.add("xCoordRelease" + i);
            columns.add("yCoordRelease" + i);
            columns.add("pressPressure" + i);
            columns.add("releasePressure" + i);
            columns.add("holdTime" + i);
            columns.add("HPP" + i);
            columns.add("offSet" + i);

        }

        for (int i = 0; i < bufferSize - 1; i++) {
            columns.add("PPTime" + i);
            columns.add("UDTime" + i);
        }

        write(columns, csvFile, keyBuffer, -1);

        columns.add("state");
        write(columns, typingFile, keyBuffer, state);

        return true;
    }

    private boolean write(List<String> columns, String csvFile, KeyBuffer keyBuffer, int state) {
        boolean exists = fileExists(csvFile);
        int bufferSize = keyBuffer.getSize();

        try {
            FileOutputStream outputStream = mContext.openFileOutput(csvFile, Context.MODE_PRIVATE | Context.MODE_APPEND);

            if (!exists)
                CSVWriter.writeLine(outputStream, columns);

            ArrayList<Long> pressed = new ArrayList<>();
            ArrayList<Long> released = new ArrayList<>();
            List<String> list = new ArrayList<>();

            for (KeyObject key : keyBuffer.getBuffer()) {
                pressed.add(key.getPressedTime());
                released.add(key.getReleasedTime());

                list.add(String.valueOf(key.getCoordXPressed()));
                list.add(String.valueOf(key.getCoordYPressed()));
                list.add(String.valueOf(key.getCoordXReleased()));
                list.add(String.valueOf(key.getCoordYReleased()));
                list.add(String.valueOf(key.getPressedPressure()));
                list.add(String.valueOf(key.getReleasedPressure()));
                list.add(String.valueOf(transformTimeStamp(key.getPressedTime(), key.getReleasedTime())));

                double hpp = transformTimeStamp(key.getPressedTime(), key.getReleasedTime()) * key.getPressedPressure();
                list.add(String.valueOf(hpp));

                double offset = Math.hypot(key.getCoordXReleased() - key.getCenterXCoord(), key.getCoordYReleased() - key.getCenterYCoord());
                list.add(String.valueOf(offset));
            }

            for (int i = 0; i < bufferSize - 1; i++) {
                list.add(String.valueOf(transformTimeStamp(pressed.get(i), pressed.get(i + 1))));
                list.add(String.valueOf(transformTimeStamp(released.get(i), pressed.get(i + 1))));
            }

            if (state != -1)
                list.add(String.valueOf(state));

            CSVWriter.writeLine(outputStream, list);

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    private double transformTimeStamp(long firstTS, long secondTS) {
        double result = secondTS - firstTS;

        return result / 1000;
    }

    private boolean fileExists(String fileName) {
        File file = mContext.getFileStreamPath(fileName);
        return file.exists();
    }
}
