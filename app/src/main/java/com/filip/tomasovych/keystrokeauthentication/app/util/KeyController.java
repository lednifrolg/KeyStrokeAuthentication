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

        saveCSV(keyBuffer, state);
        keyBuffer.clear();

        return true;
    }

    private boolean saveCSV(KeyBuffer keyBuffer, int state) {
        String csvFile = "TESTFIL.csv";
        boolean ex = fileExists(csvFile);
        FileOutputStream outputStream;


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
            columns.add("offSet" + i);

        }

        for (int i = 0; i < bufferSize - 1; i++) {
            columns.add("PPTime" + i);
            columns.add("UDTime" + i);
        }



        try {
            outputStream = mContext.openFileOutput(csvFile, Context.MODE_PRIVATE);


            if (!ex)
                CSVWriter.writeLine(outputStream, columns);



        } catch (IOException e) {

            e.printStackTrace();
            return false;
        }


        return true;
    }


    private boolean fileExists(String fname){
        File file = mContext.getFileStreamPath(fname);
        return file.exists();
    }

}
