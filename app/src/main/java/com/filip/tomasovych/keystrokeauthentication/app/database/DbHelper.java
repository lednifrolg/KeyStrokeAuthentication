package com.filip.tomasovych.keystrokeauthentication.app.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.filip.tomasovych.keystrokeauthentication.app.model.KeyObject;
import com.filip.tomasovych.keystrokeauthentication.app.model.User;
import com.filip.tomasovych.keystrokeauthentication.app.util.Helper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nolofinwe on 4.10.2016.
 */

public class DbHelper extends SQLiteOpenHelper {

    private static final String TAG = DbHelper.class.getSimpleName();

    private static DbHelper mDbHelper;

    // Database info
    private static final String DATABASE_NAME = "KeystrokeDynamics.db";
    private static final int DATABASE_VERSION = 7;

    // Table names
    private static final String TABLE_USER = "User";
    private static final String TABLE_EXPERIMENT_TYPE = "ExperimentType";
    private static final String TABLE_EXPERIMENT = "Experiment";
    private static final String TABLE_KEY_DATA = "KeyData";
    private static final String TABLE_OUTLIER_THRESHOLD = "OutlierThreshold";

    // User columns
    private static final String USER_ID = "id";
    private static final String USER_NAME = "name";
    private static final String USER_PASSWORD = "password";

    // ExperimentType columns
    private static final String EXPERIMENT_TYPE_ID = "id";
    private static final String EXPERIMENT_TYPE_NAME = "name";

    // Experiment columns
    private static final String EXPERIMENT_ID = "id";
    private static final String EXPERIMENT_ID_USER = "userId";
    private static final String EXPERIMENT_ID_TYPE = "experimentTypeId";
    private static final String EXPERIMENT_ERRORS = "errors";

    // KeyData columns
    private static final String KEY_DATA_ID = "id";
    private static final String KEY_DATA_ID_EXPERIMENT = "experimentId";

    // OutlierThreshold columns
    private static final String OUTLIER_THRESHOLD_ID = "id";
    private static final String OUTLIER_THRESHOLD_ID_USER = "userId";
    private static final String OUTLIER_THRESHOLD_EXPERIMENT_TYPE_ID = "experimentTypeId";
    private static final String OUTLIER_THRESHOLD_VALUE = "thresholdValue";

    // private static final String KEY_DATA_KEY_NUM = "keyNum";
    private static final String KEY_DATA_KEY_PRESS_TIME = "keyPressTime";
    private static final String KEY_DATA_KEY_RELEASE_TIME = "keyReleaseTime";
    private static final String KEY_DATA_X_COORD_PRESS = "xCoordPress";
    private static final String KEY_DATA_Y_COORD_PRESS = "yCoordPress";
    private static final String KEY_DATA_X_COORD_RELEASE = "xCoordRelease";
    private static final String KEY_DATA_Y_COORD_RELEASE = "yCoordRelease";
    private static final String KEY_DATA_CENTER_X_COORD = "centerXCoord";
    private static final String KEY_DATA_CENTER_Y_COORD = "centerYCoord";
    private static final String KEY_DATA_PRESS_PRESSURE = "pressPressure";
    private static final String KEY_DATA_RELEASE_PRESSURE = "releasePressure";

    /**
     * Create a helper object to create, open, and/or manage a database.
     * This method always returns very quickly.  The database is not actually
     * created or opened until one of {@link #getWritableDatabase} or
     * {@link #getReadableDatabase} is called.
     *
     * @param context to use to open or create the database
     * @param name    of the database file, or null for an in-memory database
     * @param factory to use for creating cursor objects, or null for the default
     * @param version number of the database (starting at 1); if the database is older,
     *                {@link #onUpgrade} will be used to upgrade the database; if the database is
     *                newer, {@link #onDowngrade} will be used to downgrade the database
     */
    public DbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    /**
     * Constructor should be private to prevent direct instantiation.
     * Make a call to the static method "getInstance()" instead.
     */
    private DbHelper(Context context) {
//        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    public static synchronized DbHelper getInstance(Context context) {
        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        if (mDbHelper == null) {
            mDbHelper = new DbHelper(context.getApplicationContext());
        }

        return mDbHelper;
    }

    /**
     * Called when the database is created for the first time. This is where the
     * creation of tables and the initial population of the tables should happen.
     *
     * @param db The database.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_USER_TABLE = "CREATE TABLE " + TABLE_USER +
                "(" +
                USER_ID + " INTEGER PRIMARY KEY ," +
                USER_NAME + " TEXT," +
                USER_PASSWORD + " TEXT" +
                ")";

        String CREATE_EXPERIMENT_TYPE_TABLE = "CREATE TABLE " + TABLE_EXPERIMENT_TYPE +
                "(" +
                EXPERIMENT_TYPE_ID + " INTEGER PRIMARY KEY," +
                EXPERIMENT_TYPE_NAME + " TEXT" +
                ")";

        String CREATE_EXPERIMENT_TABLE = "CREATE TABLE " + TABLE_EXPERIMENT +
                "(" +
                EXPERIMENT_ID + " INTEGER PRIMARY KEY," +
                EXPERIMENT_ID_USER + " INTEGER," +
                EXPERIMENT_ID_TYPE + " INTEGER," +
                EXPERIMENT_ERRORS + " INTEGER," +
                "FOREIGN KEY (" + EXPERIMENT_ID_USER + ") REFERENCES " + TABLE_USER + "(" + USER_ID + ")" +
                "FOREIGN KEY (" + EXPERIMENT_ID_TYPE + ") REFERENCES " + TABLE_EXPERIMENT_TYPE + "(" + EXPERIMENT_TYPE_ID + ")" +
                ")";

        String CREATE_KEY_DATA_TABLE = "CREATE TABLE " + TABLE_KEY_DATA +
                "(" +
                KEY_DATA_ID + " INTEGER PRIMARY KEY," +
                KEY_DATA_ID_EXPERIMENT + " INTEGER," +
                KEY_DATA_KEY_PRESS_TIME + " REAL," +
                KEY_DATA_KEY_RELEASE_TIME + " REAL," +
                KEY_DATA_X_COORD_PRESS + " REAL," +
                KEY_DATA_Y_COORD_PRESS + " REAL," +
                KEY_DATA_X_COORD_RELEASE + " REAL," +
                KEY_DATA_Y_COORD_RELEASE + " REAL," +
                KEY_DATA_CENTER_X_COORD + " REAL," +
                KEY_DATA_CENTER_Y_COORD + " REAL," +
                KEY_DATA_PRESS_PRESSURE + " REAL," +
                KEY_DATA_RELEASE_PRESSURE + " REAL," +
                "FOREIGN KEY (" + KEY_DATA_ID_EXPERIMENT + ") REFERENCES " + TABLE_EXPERIMENT + "(" + EXPERIMENT_ID + ")" +
                ")";

        String CREATE_OUTLIER_THRESHOLD_TABLE = "CREATE TABLE " + TABLE_OUTLIER_THRESHOLD +
                "(" +
                OUTLIER_THRESHOLD_ID_USER + " INTEGER," +
                OUTLIER_THRESHOLD_EXPERIMENT_TYPE_ID + " INTEGER," +
                OUTLIER_THRESHOLD_VALUE + " REAL," +
                "PRIMARY KEY (" + OUTLIER_THRESHOLD_ID_USER + ", " + OUTLIER_THRESHOLD_EXPERIMENT_TYPE_ID + ")" +
                "FOREIGN KEY (" + OUTLIER_THRESHOLD_ID_USER + ") REFERENCES " + TABLE_USER + "(" + USER_ID + ") " +
                "FOREIGN KEY (" + OUTLIER_THRESHOLD_EXPERIMENT_TYPE_ID + ") REFERENCES " + TABLE_EXPERIMENT_TYPE + "(" + EXPERIMENT_TYPE_ID + ")" +
                ")";

        db.execSQL(CREATE_USER_TABLE);
        db.execSQL(CREATE_EXPERIMENT_TYPE_TABLE);
        db.execSQL(CREATE_EXPERIMENT_TABLE);
        db.execSQL(CREATE_KEY_DATA_TABLE);
        db.execSQL(CREATE_OUTLIER_THRESHOLD_TABLE);

        User identAlnum = new User("initialUserAlnum", Helper.STATIC_PASSWORD);
        User identNum = new User("initialUserNum", Helper.STATIC_NUM_PASSWORD);
        inserInitUser(identAlnum, db);
        inserInitUser(identNum, db);
        initExperimentUsers(db);
    }

    /**
     * Called when the database needs to be upgraded. The implementation
     * should use this method to drop tables, add tables, or do anything else it
     * needs to upgrade to the new schema version.
     * <p>
     * <p>
     * The SQLite ALTER TABLE documentation can be found
     * <a href="http://sqlite.org/lang_altertable.html">here</a>. If you add new columns
     * you can use ALTER TABLE to insert them into a live table. If you rename or remove columns
     * you can use ALTER TABLE to rename the old table, then create the new table and then
     * populate the new table with the contents of the old table.
     * </p><p>
     * This method executes within a transaction.  If an exception is thrown, all changes
     * will automatically be rolled back.
     * </p>
     *
     * @param db         The database.
     * @param oldVersion The old database version.
     * @param newVersion The new database version.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        if (oldVersion != newVersion) {
            // Simplest implementation is to drop all old tables and recreate them
            Log.d(TAG, "OLDVERSION DB != NEWVERSION");
//            db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERdETAIL);
//            db.execSQL("CREATE UNIQUE INDEX idx_twocols ON " + TABLE_OUTLIER_THRESHOLD + "(" + OUTLIER_THRESHOLD_ID_USER + ", " + OUTLIER_THRESHOLD_EXPERIMENT_TYPE_ID + ")");
//            db.execSQL("DROP TABLE IF EXISTS " + TABLE_OUTLIER_THRESHOLD);
            onCreate(db);
        }
    }


    /**
     * Get user from database by his name and password
     *
     * @param name User name
     * @param password User password
     * @return User object with name and Id or null if no user was found
     */
    public User getUser(String name, String password) {
        User user = null;

        String query = "SELECT * FROM " + TABLE_USER +
                " WHERE " + USER_NAME + " LIKE '" + name +
                "' AND " + USER_PASSWORD + " LIKE '" + password + "'";

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        try {
            if (cursor.moveToFirst()) {
                user = new User();

                user.setId(cursor.getInt(cursor.getColumnIndex(USER_ID)));
                user.setName(cursor.getString(cursor.getColumnIndex(USER_NAME)));
                user.setPassword(cursor.getString(cursor.getColumnIndex(USER_PASSWORD)));
            } else {
                return null;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.d(TAG, "Error getting user from database");
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        return user;
    }

    /**
     * Get user from database by his name
     *
     * @param name User name
     * @return User object with name and Id or null if no user was found
     */
    public User getUser(String name) {
        User user = null;

        String query = "SELECT * FROM " + TABLE_USER +
                " WHERE " + USER_NAME + " LIKE '" + name + "'";

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        try {
            if (cursor.moveToFirst()) {
                user = new User();

                user.setId(cursor.getInt(cursor.getColumnIndex(USER_ID)));
                user.setName(cursor.getString(cursor.getColumnIndex(USER_NAME)));
            } else {
                return null;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.d(TAG, "Error getting user from database");
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        return user;
    }

    /**
     * Get user from database by his name
     *
     * @param name User name
     * @param isUnique is username unique
     * @return User object with name and Id or null if no user was found
     */
    public User getUser(String name, boolean isUnique) {
        User user = null;

        String query = "SELECT * FROM " + TABLE_USER +
                " WHERE " + USER_NAME + " LIKE '" + name + "'";

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        try {
            if (cursor.moveToFirst()) {
                user = new User();

                user.setId(cursor.getInt(cursor.getColumnIndex(USER_ID)));
                user.setName(cursor.getString(cursor.getColumnIndex(USER_NAME)));
                user.setPassword(cursor.getString(cursor.getColumnIndex(USER_PASSWORD)));
            } else {
                user = null;
            }

            if (cursor.getCount() > 1) {
                user = null;
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            Log.d(TAG, "Error getting user from database");
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        Log.d(TAG, user.toString());

        return user;
    }

    /**
     * Insert User to database
     *
     * @param user to be inserted
     * @return user id if successfully inserted, -1 otherwise
     */
    public long insertUser(User user) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();

        long result = 0;

        try {
            ContentValues values = new ContentValues();

            values.put(USER_NAME, user.getName());
            values.put(USER_PASSWORD, user.getPassword());

            result = db.insertOrThrow(TABLE_USER, null, values);
            db.setTransactionSuccessful();

            Log.d(TAG, "insertUser values : " + values.toString());
            Log.d(TAG, "insertUser result : " + result);
        } catch (SQLException e) {
            e.printStackTrace();
            Log.d(TAG, "Error while trying to add user to database");
            result = -1;
        } finally {
            db.endTransaction();
        }

        return result;
    }

    private void initExperimentUsers(SQLiteDatabase db) {
        inserInitUser(new User("h1053055", "na925125TO"), db);
        inserInitUser(new User("xvalastiak", "valdy550"), db);
        inserInitUser(new User("xgono", "silneheslo321"), db);
        inserInitUser(new User("xcagan", "lolecbolec"), db);
        inserInitUser(new User("tabora1", "pokemon"), db);
        inserInitUser(new User("xmadzik", "lukasmadzik"), db);
        inserInitUser(new User("xhagaral", "kalach"), db);
        inserInitUser(new User("xGavornik", "kubko23"), db);
        inserInitUser(new User("xkollarova", "autovobis"), db);
        inserInitUser(new User("xlibantova", "dominika1992"), db);
        inserInitUser(new User("xguilisi", "150igs"), db);
        inserInitUser(new User("xfarkast", "huawei"), db);
        inserInitUser(new User("xwolfm", "mirowolf"), db);
        inserInitUser(new User("xtomasova", "pecora"), db);
        inserInitUser(new User("xbesedova", "Besedova"), db);
        inserInitUser(new User("xvnencak", "stanislav7"), db);
        inserInitUser(new User("werther", "password"), db);

        inserInitUser(new User("xhagaral", "102016"), db);
        inserInitUser(new User("h1053055", "925125"), db);
        inserInitUser(new User("xkollarova", "240800"), db);
        inserInitUser(new User("xbesedova", "225566"), db);
        inserInitUser(new User("xgono", "170993"), db);
        inserInitUser(new User("xGavornik", "120519"), db);
        inserInitUser(new User("xvalastiak", "550550"), db);
        inserInitUser(new User("xlibantova", "654321"), db);
        inserInitUser(new User("xtomasova", "280690"), db);
        inserInitUser(new User("xmadzik", "1235789510"), db);
        inserInitUser(new User("xwolfm", "2131991"), db);
        inserInitUser(new User("xgulisi", "1502505"), db);
        inserInitUser(new User("werther", "258456"), db);
        inserInitUser(new User("xtabora", "861992"), db);
        inserInitUser(new User("xvnencak", "4014142374"), db);
        inserInitUser(new User("xcagan2", "010593"), db);
        inserInitUser(new User("xfarkast", "193561"), db);
    }

    private long inserInitUser(User user, SQLiteDatabase db) {
        db.beginTransaction();

        long result = 0;

        try {
            ContentValues values = new ContentValues();

            values.put(USER_NAME, user.getName());
            values.put(USER_PASSWORD, user.getPassword());

            result = db.insertOrThrow(TABLE_USER, null, values);
            db.setTransactionSuccessful();

            Log.d(TAG, "insertUser values : " + values.toString());
            Log.d(TAG, "insertUser result : " + result);
        } catch (SQLException e) {
            e.printStackTrace();
            Log.d(TAG, "Error while trying to add user to database");
            result = -1;
        } finally {
            db.endTransaction();
        }

        return result;
    }


    /**
     * Create experiment entry for a user
     *
     * @param user   user doing the experiment
     * @param type   type of experiment
     * @param errors number of errors in experiment
     * @return id of experiment or -1 if the creation failed
     */
    public long insertExperiment(User user, int type, int errors) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();

        long result = -1;

        try {
            ContentValues values = new ContentValues();

            values.put(EXPERIMENT_ID_USER, user.getId());
            values.put(EXPERIMENT_ID_TYPE, type);
            values.put(EXPERIMENT_ERRORS, errors);

            result = db.insertOrThrow(TABLE_EXPERIMENT, null, values);
            db.setTransactionSuccessful();

            Log.d(TAG, "insertExperiment values : " + values.toString());
            Log.d(TAG, "insertExperiment result : " + result);
        } catch (SQLException e) {
            e.printStackTrace();
            Log.d(TAG, "Error while trying to create experiment");
        } finally {
            db.endTransaction();
        }

        return result;
    }

    /**
     * @param experimentId
     * @param keyObject
     * @return
     */
    public long insertKeyData(long experimentId, KeyObject keyObject) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();

        long result = -1;

        try {
            ContentValues values = new ContentValues();

            values.put(KEY_DATA_ID_EXPERIMENT, experimentId);
            values.put(KEY_DATA_KEY_PRESS_TIME, keyObject.getPressedTime());
            values.put(KEY_DATA_KEY_RELEASE_TIME, keyObject.getReleasedTime());
            values.put(KEY_DATA_X_COORD_PRESS, keyObject.getCoordXPressed());
            values.put(KEY_DATA_Y_COORD_PRESS, keyObject.getCoordYPressed());
            values.put(KEY_DATA_X_COORD_RELEASE, keyObject.getCoordXReleased());
            values.put(KEY_DATA_Y_COORD_RELEASE, keyObject.getCoordYReleased());
            values.put(KEY_DATA_CENTER_X_COORD, keyObject.getCenterXCoord());
            values.put(KEY_DATA_CENTER_Y_COORD, keyObject.getCenterYCoord());
            values.put(KEY_DATA_PRESS_PRESSURE, keyObject.getPressedPressure());
            values.put(KEY_DATA_RELEASE_PRESSURE, keyObject.getReleasedPressure());

            result = db.insertOrThrow(TABLE_KEY_DATA, null, values);
            db.setTransactionSuccessful();

            Log.d(TAG, "insertKeyData values : " + values.toString());
            Log.d(TAG, "insertKeyData result : " + result);
        } catch (SQLException e) {
            e.printStackTrace();
            Log.d(TAG, "Error while trying to add KeyData to experiment");
        } finally {
            db.endTransaction();
        }

        return result;
    }

    public List<String> getUsersForIdentifiaction() {
        List<String> userNames = new ArrayList<>();

        String query = "SELECT * FROM " + TABLE_USER +
                " WHERE " + USER_PASSWORD + " LIKE '" + Helper.STATIC_PASSWORD + "'";

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        try {
            while (cursor.moveToNext()) {
                userNames.add(cursor.getString(cursor.getColumnIndex(USER_NAME)));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.d(TAG, "Error getting user names from database");
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        return userNames;
    }

    public List<String> getUsersForIdentifiactionNUM() {
        List<String> userNames = new ArrayList<>();

        String query = "SELECT * FROM " + TABLE_USER +
                " WHERE " + USER_PASSWORD + " LIKE '" + Helper.STATIC_NUM_PASSWORD + "'";

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        try {
            while (cursor.moveToNext()) {
                userNames.add(cursor.getString(cursor.getColumnIndex(USER_NAME)));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.d(TAG, "Error getting user names from database");
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        return userNames;
    }

    /**
     * Set threshold value used for anomaly detection
     *
     * @param type
     * @param user
     * @param thresholdValue
     * @return
     */
    public long setThresholdValue(int type, User user, double thresholdValue) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();

        long result = -1;

        try {
            ContentValues values = new ContentValues();

            values.put(OUTLIER_THRESHOLD_ID_USER, user.getId());
            values.put(OUTLIER_THRESHOLD_EXPERIMENT_TYPE_ID, type);
            values.put(OUTLIER_THRESHOLD_VALUE, thresholdValue);

            result = db.insertWithOnConflict(TABLE_OUTLIER_THRESHOLD, null, values, SQLiteDatabase.CONFLICT_IGNORE);
            if (result == -1) {
               result = db.update(TABLE_OUTLIER_THRESHOLD, values, (OUTLIER_THRESHOLD_ID_USER + " = " + user.getId() + " AND " + OUTLIER_THRESHOLD_EXPERIMENT_TYPE_ID + " = " + type), new String[]{});
            }
            db.setTransactionSuccessful();

            Log.d(TAG, "setThresholdValue value : " + thresholdValue);
            Log.d(TAG, "setThresholdValue result : " + result);
        } catch (SQLException e) {
            e.printStackTrace();
            Log.d(TAG, "Error while setting threshold value");
        } finally {
            db.endTransaction();
        }

        return result;
    }


    public double getThresholdValue(User user, int type) {
        String query = "SELECT * FROM " + TABLE_OUTLIER_THRESHOLD +
                " WHERE " + OUTLIER_THRESHOLD_ID_USER + " = '" + user.getId() +
                "' AND " + OUTLIER_THRESHOLD_EXPERIMENT_TYPE_ID + " = '" + type + "'";

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        double threshold = 0;
        try {
            if (cursor.moveToFirst())
                threshold = cursor.getDouble(cursor.getColumnIndex(OUTLIER_THRESHOLD_VALUE));

        } catch (Exception ex) {
            ex.printStackTrace();
            Log.d(TAG, "Error getting threshold value from database");
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        return threshold;
    }

    public void deleteUser(User user) {
//        String query = "DELETE FROM " + TABLE_USER + " WHERE " + USER_ID + " = " + user.getId();

        SQLiteDatabase database = this.getWritableDatabase();

        //Execute sql query to remove from database
        //NOTE: When removing by String in SQL, value must be enclosed with ''
        database.execSQL("DELETE FROM " + TABLE_USER + " WHERE " + USER_ID + "= '" + user.getId() + "'");

        //Close the database
        database.close();
    }
}
