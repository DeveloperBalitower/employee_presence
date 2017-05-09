package bts.co.id.employeepresences.Manager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.location.Location;

import com.google.gson.internal.StringMap;
import com.tumblr.remember.Remember;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import bts.co.id.employeepresences.Listener.Identifiable;
import bts.co.id.employeepresences.Model.HistoryDataModel;
import bts.co.id.employeepresences.Model.StaticData;
import bts.co.id.employeepresences.Model.SystemSetting;
import bts.co.id.employeepresences.Model.Workplace;

/**
 * Created by IT on 7/14/2016.
 */
public class DatabaseManager {

    public static final String DATABASE_NAME = "presences_history.sqlite";
    public static final String TABLE_WORKPLACES = "workplaces";
    public static final String TABLE_EMPLOYEE_HISTORY = "employee_precences_history";
    public static final String TABLE_EMPLOYEE_SETTING = "employee_presences_setting";
    private static final String TAG = "DatabaseManager";
    private static final String INIT_DB_SCRIPT_FILENAME = "initdb.sql";
    private static DatabaseManager instance;
    private static GlobalManager globalManager;
    private static SQLiteDatabase db;
    private static int DATABASE_VERSION = 1;
    private static Context context;
    private StringMap<StringMap<String>> databaseSchema;
    private DBHelper helper;

//    private DatabaseManager(Context context) {
//        super(context, DATABASE_NAME, null, DATABASE_VERSION);
//        File root = new File(Environment.getExternalStorageDirectory(), "EmployeePresences/");
//        if (!root.exists()){
//            Log.e("Root Not Exists");
//            if (!root.mkdir()){
//                Log.e("Failed to create direcktory");
//                return;
//            }
//        }
//        super(context, Environment.getExternalStorageDirectory().getPath() + "/EmployeePresences/" + DATABASE_NAME, null, DATABASE_VERSION);
//        Log.d("DB : " + Environment.getExternalStorageDirectory().getPath() + "/" + DATABASE_NAME);
//        databaseSchema = new StringMap<StringMap<String>>();
//        globalManager = new GlobalManager(context.getApplicationContext());
//    }


    private DatabaseManager() {
        databaseSchema = new StringMap<StringMap<String>>();
        globalManager = new GlobalManager(StaticData.applicationContext);
    }

    public static DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

//    public static DatabaseManager getInstance(Context context) {
//        if (instance == null || db == null || !db.isOpen()) {
//            instance = new DatabaseManager(context);
//            instance.createDatabase(context);
//            instance.openDatabase();
//            instance.initDatabase();
//        }
//        return instance;
//    }

    private static String loadDBInitializationScript(String fileName) {
        try {
            StringBuilder result = new StringBuilder();

            BufferedReader reader = new BufferedReader(new InputStreamReader(context.getAssets().open(fileName)));

            String line = "";
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }

            return result.toString();
        } catch (Exception e) {
            return null;
        }
    }

    private static String[] getTransactionsFromSqlScript(String initSql) {
        String[] transactions = initSql.split("\\$");

        return transactions;
    }

    public boolean isDatabaseExists() {
        return db != null;
    }

    public void createDatabase(Context context) {
        helper = new DBHelper(DATABASE_NAME, DATABASE_VERSION);
        this.context = context;
    }

    public void openDatabase() {
//        count++;
        if (db == null || !db.isOpen()) {
            db = helper.getWritableDatabase();

            Log.d("Database %s was opened", db.getPath());
        }

    }

    private Cursor getSchema() {
        Cursor cursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type = 'table' ORDER BY name, type DESC", null);
        return cursor;
    }

    private Cursor getTableSchema(String tableName) {
        Cursor cursor = db.rawQuery(String.format("PRAGMA table_info('%s')", tableName), null);
        return cursor;
    }

    public void initDatabase() {
        databaseSchema.clear();
        Cursor cursor = this.getSchema();
        while (cursor.moveToNext()) {
            String tableName = cursor.getString(cursor.getColumnIndex("name"));
            databaseSchema.put(tableName, new StringMap<String>());
        }
        cursor.close();

        for (String tableName : databaseSchema.keySet()) {
            Cursor cursor2 = this.getTableSchema(tableName);
            StringMap<String> dictionary = databaseSchema.get(tableName);
            while (cursor2.moveToNext()) {
                String columnName = cursor2.getString(cursor2.getColumnIndex("name"));
                String type = cursor2.getString(cursor2.getColumnIndex("type"));
                dictionary.put(columnName, type);
            }
            cursor2.close();
        }
    }

//    private static class DBHelper extends SQLiteOpenHelper {
//
//        private static DBHelper instance = new DBHelper(DATABASE_NAME, DATABASE_VERSION);
//
//        public DBHelper(String name, int version) {
//            super(StaticData.applicationContext, name, null, version);
//        }
//
//        @Override
//        public void onCreate(SQLiteDatabase db) {
//            try {
//                initSql = loadDBInitializationScript(StaticData.DB_INIT_SCRIPT_FILENAME);
//
//                Log.d("Initialization database script is %s", initSql);
//            } catch (Exception e) {
//                Log.w(e);
//            }
//
//            if (initSql.length() != 0) {
//                String[] transactions = getTransactionsFromSqlScript(initSql);
//                for (String transaction : transactions) {
//                    db.execSQL(transaction);
//                }
//            }
//        }
//
//        @Override
//        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//            Log.i("Database Version: OLD: " + oldVersion + " = NEW: " + newVersion);
////            db.execSQL("DROP TABLE IF EXISTS " + TABLE_SITE_LIST);
//            db.execSQL("DROP TABLE IF EXISTS " + TABLE_WORKPLACES);
//
//            globalManager.getSharedPreferencesManager().clear();
//            Remember.clear();
//            onCreate(db);
//        }
//    }

    public void updateData(String tableName, ContentValues contentValues, String whereClause, String[] whereArgs) {
        db.update(tableName, contentValues, whereClause, whereArgs);
    }

    public boolean checkIfUserHaveDataHistory(String idHistory) {
        String query = "SELECT * FROM " + TABLE_EMPLOYEE_HISTORY + " WHERE id=" + idHistory;

        Cursor cursor = db.rawQuery(query, null);

        if (cursor != null && cursor.getCount() > 0) {
//            cursor.moveToFirst();
            return true;
        }
        return false;
    }

    public void checkOutUser(int id, int server_id, String site_id, String checkOutDate, String checkOutTime, int uploadStatus) {
        Log.i("CheckOutUser " + id + " " + server_id + " " + site_id + " " + checkOutDate + " " + checkOutTime + " " + uploadStatus);
        ContentValues dataToInsert = new ContentValues();

//        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
//        SimpleDateFormat tf = new SimpleDateFormat("HH:mm:ss");
//
//        String checkOutDate = df.format(checkOut_date);
//        String checkOutTime = tf.format(checkOut_date);

        dataToInsert.put("check_out_date", checkOutDate);
        dataToInsert.put("check_out_time", checkOutTime);
        dataToInsert.put("upload_status", uploadStatus);


        String where = "server_id=? and site=? and id = ?";
        String[] whereArgs = new String[]{String.valueOf(server_id), String.valueOf(site_id), String.valueOf(id)};

        updateData(TABLE_EMPLOYEE_HISTORY, dataToInsert, where, whereArgs);
    }


    public long checkINUser(String site_id, int server_id, Double lat, Double lng, Date checkin_date, int uploadStatus) {

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat tf = new SimpleDateFormat("HH:mm:ss");

        String checkinDate = df.format(checkin_date);
        String checkinTime = tf.format(checkin_date);


        String where = "id=?";
        String[] whereArgs = new String[]{String.valueOf(0)};

        List<HistoryDataModel> historyDataModelList = this.getDataQueryList(HistoryDataModel.class, TABLE_EMPLOYEE_HISTORY, where, whereArgs);
        if (historyDataModelList != null && historyDataModelList.size() > 0) {
            if (historyDataModelList.get(0).getCheck_in_date().equals(checkinDate)) {

            }
        }
//        return

        Log.i("checkINUser " + site_id + " " + server_id + " " + site_id + " " + checkin_date + " " + uploadStatus);
        String sql = "INSERT INTO " + TABLE_EMPLOYEE_HISTORY + " (server_id,user_nik, check_in_date, check_in_time, site, updated_at, created_at, upload_status,checkin_lat,checkin_long) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?,?,?)";
        SQLiteStatement insert = db.compileStatement(sql);
        db.beginTransaction();

        String dateNow = globalManager.getCurrentDateTime();
        bindValueToSqlStatement(insert, BindAction.INTEGER, 1, server_id);
        bindValueToSqlStatement(insert, BindAction.STRING, 2, globalManager.getSharedPreferencesManager().getUserLogin().getNik());
        bindValueToSqlStatement(insert, BindAction.STRING, 3, checkinDate);
        bindValueToSqlStatement(insert, BindAction.STRING, 4, checkinTime);
        bindValueToSqlStatement(insert, BindAction.STRING, 5, site_id);
        bindValueToSqlStatement(insert, BindAction.STRING, 6, dateNow);
        bindValueToSqlStatement(insert, BindAction.STRING, 7, dateNow);
        bindValueToSqlStatement(insert, BindAction.INTEGER, 8, uploadStatus);
        bindValueToSqlStatement(insert, BindAction.DOUBLE, 9, lat);
        bindValueToSqlStatement(insert, BindAction.DOUBLE, 10, lng);

        insert.execute();
//        android.util.Log.i(TAG,"getUniqueId = "+String.valueOf(insert.getUniqueId()));

        insert.clearBindings();

        db.setTransactionSuccessful();
        db.endTransaction();

        String query = "SELECT id FROM " + TABLE_EMPLOYEE_HISTORY + " order by id desc limit 1;";

        long lastId = 0;

        Cursor c = db.rawQuery(query, null);
        if (c != null && c.moveToFirst()) {
            lastId = c.getLong(0);
//The 0 is the column index, we only have 1 column, so the index is 0
        }
        return lastId;
    }

    public void insertEmployeeSetting(List<SystemSetting> systemSettings) {
        if (systemSettings != null) {
            String sql = "INSERT OR REPLACE INTO " + TABLE_EMPLOYEE_SETTING + "(id,setting_name,value_int,value_string,activated) VALUES (?,?,?,?,?)";
            SQLiteStatement insert = db.compileStatement(sql);
            db.beginTransaction();
            for (SystemSetting systemSetting : systemSettings) {

                bindValueToSqlStatement(insert, BindAction.INTEGER, 1, systemSetting.getId());
                bindValueToSqlStatement(insert, BindAction.STRING, 2, systemSetting.getSettingName());
                bindValueToSqlStatement(insert, BindAction.INTEGER, 3, systemSetting.getValueInt());
                bindValueToSqlStatement(insert, BindAction.STRING, 4, systemSetting.getValueString());
                bindValueToSqlStatement(insert, BindAction.INTEGER, 5, systemSetting.getActivated());

                insert.execute();
                insert.clearBindings();
            }
            db.setTransactionSuccessful();
            db.endTransaction();
        }
    }

    public void insertPresencesHistory(List<HistoryDataModel> historyDataModels, int AllUploadedStat) {
        if (historyDataModels != null) {
            String sql = "INSERT OR REPLACE INTO " + TABLE_EMPLOYEE_HISTORY + " " +
                    "(id,server_id,user_nik,check_in_date,check_in_time,checkin_lat,checkin_long,check_out_date,check_out_time,checkout_lat,checkout_long,site,upload_status) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)";
            SQLiteStatement insert = db.compileStatement(sql);
            db.beginTransaction();
            for (HistoryDataModel historyDataModel : historyDataModels) {

                bindValueToSqlStatement(insert, BindAction.INTEGER, 1, historyDataModel.getId());
                bindValueToSqlStatement(insert, BindAction.INTEGER, 2, historyDataModel.getServer_id());
                bindValueToSqlStatement(insert, BindAction.STRING, 3, historyDataModel.getUser_nik());
                bindValueToSqlStatement(insert, BindAction.STRING, 4, historyDataModel.getCheck_in_date());
                bindValueToSqlStatement(insert, BindAction.STRING, 5, historyDataModel.getCheck_in_time());
                bindValueToSqlStatement(insert, BindAction.DOUBLE, 6, historyDataModel.getCheckin_lat());
                bindValueToSqlStatement(insert, BindAction.DOUBLE, 7, historyDataModel.getCheckin_long());
                bindValueToSqlStatement(insert, BindAction.STRING, 8, historyDataModel.getCheck_out_date());
                bindValueToSqlStatement(insert, BindAction.STRING, 9, historyDataModel.getCheck_out_time());
                bindValueToSqlStatement(insert, BindAction.DOUBLE, 10, historyDataModel.getCheckout_lat());
                bindValueToSqlStatement(insert, BindAction.DOUBLE, 11, historyDataModel.getCheckout_long());
                bindValueToSqlStatement(insert, BindAction.STRING, 12, historyDataModel.getSite());
                bindValueToSqlStatement(insert, BindAction.INTEGER, 13, AllUploadedStat);

                insert.execute();
                insert.clearBindings();
            }
            db.setTransactionSuccessful();
            db.endTransaction();
        }
    }

    public void insertWorkplaces(List<Workplace> workplaces) {
        if (workplaces != null) {
            String sql = "INSERT OR REPLACE INTO " + TABLE_WORKPLACES + " (workplace_id, workplace_name, latitude,longitude, address) VALUES (?, ?, ?,?, ?)";
            SQLiteStatement insert = db.compileStatement(sql);
            db.beginTransaction();
            for (Workplace workplace : workplaces) {

                bindValueToSqlStatement(insert, BindAction.STRING, 1, workplace.getWorkplaceId());
                bindValueToSqlStatement(insert, BindAction.STRING, 2, workplace.getWorkplaceName());
                bindValueToSqlStatement(insert, BindAction.STRING, 3, workplace.getLatitude());
                bindValueToSqlStatement(insert, BindAction.STRING, 4, workplace.getLongitude());
                bindValueToSqlStatement(insert, BindAction.STRING, 5, workplace.getAddress());

                insert.execute();
                insert.clearBindings();
            }
            db.setTransactionSuccessful();
            db.endTransaction();
        }
    }

    public List<Workplace> getWorkplaces(double lat, double lng, double distances) {
        return this.getDataQueryListWorkplaces(Workplace.class, TABLE_WORKPLACES, null, null, lat, lng, distances);
    }

    public Workplace getWorkPlace(String workplace_id) {
        return this.getDataQuery(Workplace.class, TABLE_WORKPLACES, "workplace_id =?", new String[]{String.valueOf(workplace_id)});
    }

    public double getDistancesCheckIn() {
        double distance = 100;
        String where = "setting_name=?";
        String[] whereArgs = new String[]{"check_in_range"};
        List<SystemSetting> systemSettings = this.getDataQueryList(SystemSetting.class, TABLE_EMPLOYEE_SETTING, where, whereArgs);
        if (systemSettings.size() > 0) {
            for (int i = 0; i < systemSettings.size(); i++) {
                if (systemSettings.get(i) != null && systemSettings.get(i).getSettingName() != null && systemSettings.get(i).getValueInt() != null && systemSettings.get(i).getSettingName().equalsIgnoreCase("mysql_db_version")) {
                    distance = Double.valueOf(systemSettings.get(i).getValueInt());
                    break;
                }
            }
        }
        return distance;
    }

    public int getDbServerVersion() {
        String where = "setting_name=?";
        int version = 0;
        String[] whereArgs = new String[]{"mysql_db_version"};
        List<SystemSetting> systemSettings = this.getDataQueryList(SystemSetting.class, TABLE_EMPLOYEE_SETTING, where, whereArgs);
        if (systemSettings.size() > 0) {
            for (int i = 0; i < systemSettings.size(); i++) {
                if (systemSettings.get(i) != null && systemSettings.get(i).getSettingName() != null && systemSettings.get(i).getValueInt() != null && systemSettings.get(i).getSettingName().equalsIgnoreCase("mysql_db_version")) {
                    version = Integer.valueOf(systemSettings.get(i).getValueInt());
                    break;
                }
            }
        }
        return version;
    }

    public List<SystemSetting> getSystemSetting() {
        String where = "upload_status=?";
        String[] whereArgs = new String[]{String.valueOf(0)};
        return this.getDataQueryList(SystemSetting.class, TABLE_EMPLOYEE_SETTING, where, whereArgs);
    }

    public List<HistoryDataModel> getPresencesListToUpload() {
        String where = "upload_status=?";
        String[] whereArgs = new String[]{String.valueOf(0)};
        return this.getDataQueryList(HistoryDataModel.class, TABLE_EMPLOYEE_HISTORY, where, whereArgs);
    }

    public void deleteAllDataSetting() {
        db.execSQL("DELETE FROM " + TABLE_EMPLOYEE_SETTING);
        db.execSQL("VACUUM");
    }


    public void deleteAllDataHistory() {
        db.execSQL("DELETE FROM " + TABLE_EMPLOYEE_HISTORY + " where upload_status > 0");
        db.execSQL("VACUUM");
    }

    public void deleteAllDataWorkPlaces() {
        if (isDatabaseExists()){
            db.execSQL("DELETE FROM " + TABLE_WORKPLACES);
            db.execSQL("DELETE FROM " + TABLE_EMPLOYEE_SETTING);
            db.execSQL("VACUUM");
        }
    }

//    public List<Site> getSiteList() {
//        return this.getDataQueryList(Site.class, TABLE_SITE_LIST, null, null);
//    }

//    public List<Site> getSiteListById(String siteId) {
//        return this.getDataQueryList(Site.class, TABLE_SITE_LIST, "site_id =?", new String[]{String.valueOf(siteId)});
//    }

    private void bindValueToSqlStatement(SQLiteStatement statement, BindAction bindAction, int bindIndex, Object value) {
        if (value != null) {
            switch (bindAction) {
                case INTEGER:
                    if (value instanceof Integer) {
                        statement.bindLong(bindIndex, (Integer) value);
                    } else {
                        statement.bindLong(bindIndex, (Long) value);
                    }
                    break;

                case DOUBLE:
                    statement.bindDouble(bindIndex, (Double) value);
                    break;

                case STRING:
                    statement.bindString(bindIndex, (String) value);
                    break;
            }
        } else {
            statement.bindNull(bindIndex);
        }
    }

    private <T> T instantiateEntityFromCursorWorkPlace(Class<T> entityClass, Cursor cursor, double lat, double lng, double distances) {
        T result = null;
        try {
            Constructor<T> constructor = entityClass.getConstructor(Cursor.class);
            result = constructor.newInstance(cursor, lat, lng, distances);
        } catch (Exception e) {
            Log.e(e);
        }

        return result;
    }

    private <T> T instantiateEntityFromCursor(Class<T> entityClass, Cursor cursor) {
        T result = null;
        try {
            Constructor<T> constructor = entityClass.getConstructor(Cursor.class);
            result = constructor.newInstance(cursor);
        } catch (Exception e) {
            Log.e(e);
        }

        return result;
    }

    public <T> T getDataQuery(Class<T> entityClass, String query) {
        T result = null;
        Cursor cursor = db.rawQuery(query, null);
        while (cursor.moveToNext()) {
            result = instantiateEntityFromCursor(entityClass, cursor);
        }
        cursor.close();
        return result;
    }

    public <T> T getDataQuery(Class<T> entityClass, String tableName, String selection, String[] selectionArgs) {
        T result = null;
        Cursor cursor = db.query(tableName, null, selection, selectionArgs, null, null, null);
        while (cursor.moveToNext()) {
            result = instantiateEntityFromCursor(entityClass, cursor);
        }
        cursor.close();

        return result;
    }

    public <T> List<T> getDataQueryListWorkplaces(Class<T> entityClass, String tableName, String selection, String[] selectionArgs, double lat, double lng, double distances) {
        List<T> result = new ArrayList<T>();
        Cursor cursor = db.query(tableName, null, selection, selectionArgs, null, null, null);
        while (cursor.moveToNext()) {
            double venueLat = Double.valueOf(cursor.getString(cursor.getColumnIndex("latitude")));
            double venueLng = Double.valueOf(cursor.getString(cursor.getColumnIndex("longitude")));
            double distance = distances;
            float[] resultsDistanceByLocation = new float[1];
            Location.distanceBetween(venueLat, venueLng,
                    lat, lng, resultsDistanceByLocation);
            if (resultsDistanceByLocation[0] <= distances) {
                result.add(instantiateEntityFromCursor(entityClass, cursor));
            }
//            if (globalManager.getDistancesPositionOnMeters(lat, lng, venueLat, venueLng) <= distance) {
//                result.add(instantiateEntityFromCursor(entityClass, cursor));
//            }
        }
        cursor.close();

        return result;
    }

    public <T> List<T> getDataQueryList(Class<T> entityClass, String tableName, String selection, String[] selectionArgs) {
        List<T> result = new ArrayList<T>();
        Cursor cursor = db.query(tableName, null, selection, selectionArgs, null, null, null);
        while (cursor.moveToNext()) {
            result.add(instantiateEntityFromCursor(entityClass, cursor));
        }
        cursor.close();

        return result;
    }

    private <T> T getEntityById(Class<T> entityClass, Long id, String tableName) {
        T result = null;

        String selection = "id = ?";
        String[] selectionArgs = new String[]{String.valueOf(id)};
        Cursor cursor = db.query(tableName, null, selection, selectionArgs, null, null, null);
        while (cursor.moveToNext()) {
            result = instantiateEntityFromCursor(entityClass, cursor);
        }
        cursor.close();

        return result;
    }

    private <T> List<T> getEntitiesByColumnList(Class<T> entityClass, String tableName, String columnName, long idValue) {
        List<T> result = new ArrayList<T>();

        String selection = columnName + " = ?";
        String[] selectionArgs = new String[]{String.valueOf(idValue)};
        Cursor cursor = db.query(tableName, null, selection, selectionArgs, null, null, null);
        while (cursor.moveToNext()) {
            Object data = instantiateEntityFromCursor(entityClass, cursor);
            result.add(instantiateEntityFromCursor(entityClass, cursor));
        }
        cursor.close();

        return result;
    }

    public <T> List<T> getEntitiesList(Class<T> entityClass, String tableName) {
        List<T> result = new ArrayList<T>();

        Cursor cursor = db.query(tableName, null, null, null, null, null, null);
        while (cursor.moveToNext()) {
            Object data = instantiateEntityFromCursor(entityClass, cursor);
            result.add(instantiateEntityFromCursor(entityClass, cursor));
        }
        cursor.close();

        return result;
    }

    public <T> T getEntitiWithArguments(Class<T> entityClass, String tableName, String whereClause, String[] whereArgs) {
        T result = null;
        Cursor cursor = db.query(tableName, null, whereClause, whereArgs, null, null, null);
        while (cursor.moveToNext()) {
            Object data = instantiateEntityFromCursor(entityClass, cursor);
            result = instantiateEntityFromCursor(entityClass, cursor);
        }
        cursor.close();

        return result;
    }

    public <T> List<T> getEntitiesListWithArguments(Class<T> entityClass, String tableName, String whereClause, String[] whereArgs, String orderByColumn) {
        List<T> result = new ArrayList<T>();

        Cursor cursor = db.query(tableName, null, whereClause, whereArgs, null, null, orderByColumn);
        while (cursor.moveToNext()) {
            Object data = instantiateEntityFromCursor(entityClass, cursor);
            result.add(instantiateEntityFromCursor(entityClass, cursor));
        }
        cursor.close();

        return result;
    }

    private <T extends Identifiable> Long getDefaultEntityId(Class<T> entityClass, String tableName) {
        Cursor cursor = db.query(tableName, null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            T entity = instantiateEntityFromCursor(entityClass, cursor);
            cursor.close();
            return entity.getId();
        }

        return null;
    }

    public void deleteAllData() {
        //SQLiteDatabase db = this.getWritableDatabase();
        // db.delete(TABLE_NAME,null,null);
        //db.execSQL("delete * from"+ TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EMPLOYEE_HISTORY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WORKPLACES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EMPLOYEE_SETTING);
        String initSql = loadDBInitializationScript(StaticData.DB_INIT_SCRIPT_FILENAME);
        if (initSql.length() != 0) {
            String[] transactions = DatabaseManager.this.getTransactionsFromSqlScript(initSql);
            for (String transaction : transactions) {
                db.execSQL(transaction);
            }
        }
    }

    public String buildDistanceWhereClause(double latitude, double longitude, double distanceInKilometers) {

        // see: http://stackoverflow.com/questions/3126830/query-to-get-records-based-on-radius-in-sqlite

        final double coslat = Math.cos(Math.toRadians(latitude));
        final double sinlat = Math.sin(Math.toRadians(latitude));
        final double coslng = Math.cos(Math.toRadians(longitude));
        final double sinlng = Math.sin(Math.toRadians(longitude));

        final String format = "(%1$s * %2$s * (%3$s * %4$s + %5$s * %6$s) + %7$s * %8$s) > %9$s";
        final String selection = String.format(format,
                coslat, "COLUMN_LATITUDE_COS",
                coslng, "COLUMN_LONGITUDE_COS",
                sinlng, "COLUMN_LONGITUDE_SIN",
                sinlat, "COLUMN_LATITUDE_SIN",
                Math.cos(distanceInKilometers / 6371.0)
        );

        Log.d(selection);
        return selection;
    }

    enum BindAction {
        INTEGER, DOUBLE, STRING
    }

    private class DBHelper extends SQLiteOpenHelper {

        public DBHelper(String name, int version) {
            super(StaticData.applicationContext, name, null, version);
        }

//        @Override
//        public void onCreate(SQLiteDatabase db) {
//            String initSql = "";
//            try {
//                initSql = loadDBInitializationScript(StaticData.DB_INIT_SCRIPT_FILENAME);
//                Log.d("Initialization database script is %s", initSql);
//            } catch (Exception e) {
//                Log.w(e);
//            }
//
//            if (initSql.length() != 0) {
//                String[] transactions = DatabaseManager.this.getTransactionsFromSqlScript(initSql);
//                for (String transaction : transactions) {
//                    db.execSQL(transaction);
//                }
//            }
//        }
//
//        @Override
//        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//            Log.i("Database Version: OLD: " + oldVersion + " = NEW: " + newVersion);
//            db.execSQL("DROP TABLE IF EXISTS " + TABLE_EMPLOYEE_HISTORY);
//            db.execSQL("DROP TABLE IF EXISTS " + TABLE_WORKPLACES);
//            db.execSQL("DROP TABLE IF EXISTS " + TABLE_EMPLOYEE_SETTING);
//
//            globalManager.getSharedPreferencesManager().clear();
//            Remember.clear();
//            onCreate(db);
//        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            String initSql = "";
            try {
                initSql = loadDBInitializationScript(INIT_DB_SCRIPT_FILENAME);

                Log.d("Initialization database script is %s", initSql);
            } catch (Exception e) {
                Log.w(e);
            }

            if (initSql.length() != 0) {
                String[] transactions = DatabaseManager.this.getTransactionsFromSqlScript(initSql);
                for (String transaction : transactions) {
                    db.execSQL(transaction);
                }
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.i(TAG, "Database Version: OLD: " + oldVersion + " = NEW: " + newVersion);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_EMPLOYEE_HISTORY);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_EMPLOYEE_SETTING);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_WORKPLACES);

            globalManager.getSharedPreferencesManager().clear();
            Remember.clear();
            onCreate(db);
        }
    }
}
