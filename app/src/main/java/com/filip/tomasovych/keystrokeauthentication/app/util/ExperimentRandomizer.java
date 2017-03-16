package com.filip.tomasovych.keystrokeauthentication.app.util;

import android.content.Context;

import com.filip.tomasovych.keystrokeauthentication.app.classification.AnomalyDetector;
import com.filip.tomasovych.keystrokeauthentication.app.database.DbHelper;
import com.filip.tomasovych.keystrokeauthentication.app.model.User;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by nolofinwe on 15/03/17.
 */

public class ExperimentRandomizer {

    ArrayList<User> mAlNumUsers;
    ArrayList<User> mNumUsers;

    public ExperimentRandomizer(Context context) {
        DbHelper dbHelper = DbHelper.getInstance(context);
        mAlNumUsers = new ArrayList<>();
        mNumUsers = new ArrayList<>();

        mAlNumUsers.add(dbHelper.getUser("h1053055", "na925125TO"));
        mAlNumUsers.add(dbHelper.getUser("xvalastiak", "valdy550"));
        mAlNumUsers.add(dbHelper.getUser("xgono", "silneheslo321"));
        mAlNumUsers.add(dbHelper.getUser("xcagan", "lolecbolec"));
        mAlNumUsers.add(dbHelper.getUser("tabora1", "pokemon"));
        mAlNumUsers.add(dbHelper.getUser("xmadzik", "lukasmadzik"));
        mAlNumUsers.add(dbHelper.getUser("xhagaral", "kalach"));
        mAlNumUsers.add(dbHelper.getUser("xGavornik", "kubko23"));
        mAlNumUsers.add(dbHelper.getUser("xkollarova", "autovobis"));
        mAlNumUsers.add(dbHelper.getUser("xlibantova", "dominika1992"));
        mAlNumUsers.add(dbHelper.getUser("xguilisi", "150igs"));
        mAlNumUsers.add(dbHelper.getUser("xfarkast", "huawei"));
        mAlNumUsers.add(dbHelper.getUser("xwolfm", "mirowolf"));
        mAlNumUsers.add(dbHelper.getUser("xtomasova", "pecora"));
        mAlNumUsers.add(dbHelper.getUser("xbesedova", "Besedova"));
        mAlNumUsers.add(dbHelper.getUser("xvnencak", "stanislav7"));
        mAlNumUsers.add(dbHelper.getUser("werther", "password"));


        mNumUsers.add(dbHelper.getUser("xhagaral", "102016"));
        mNumUsers.add(dbHelper.getUser("h1053055", "925125"));
        mNumUsers.add(dbHelper.getUser("xkollarova", "240800"));
        mNumUsers.add(dbHelper.getUser("xbesedova", "225566"));
        mNumUsers.add(dbHelper.getUser("xgono", "170993"));
        mNumUsers.add(dbHelper.getUser("xGavornik", "120519"));
        mNumUsers.add(dbHelper.getUser("xvalastiak", "550550"));
        mNumUsers.add(dbHelper.getUser("xlibantova", "654321"));
        mNumUsers.add(dbHelper.getUser("xtomasova", "280690"));
        mNumUsers.add(dbHelper.getUser("xmadzik", "1235789510"));
        mNumUsers.add(dbHelper.getUser("xwolfm", "2131991"));
        mNumUsers.add(dbHelper.getUser("xgulisi", "1502505"));
        mNumUsers.add(dbHelper.getUser("werther", "258456"));
        mNumUsers.add(dbHelper.getUser("xtabora", "861992"));
        mNumUsers.add(dbHelper.getUser("xvnencak", "4014142374"));
        mNumUsers.add(dbHelper.getUser("xcagan2", "010593"));
        mNumUsers.add(dbHelper.getUser("xfarkast", "193561"));
    }

    public User getAlNumUser() {
        int randomNum = ThreadLocalRandom.current().nextInt(0, mAlNumUsers.size());

        User usr = mAlNumUsers.get(randomNum);
        mAlNumUsers.remove(randomNum);
        return usr;
    }

    public User getNumUser() {
        int randomNum = ThreadLocalRandom.current().nextInt(0, mNumUsers.size());

        User usr = mNumUsers.get(randomNum);
        mNumUsers.remove(randomNum);
        return usr;
    }
}
